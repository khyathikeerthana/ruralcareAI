package com.simats.ruralcareai.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val API_BASE_URL = NETWORK_API_BASE_URL
private const val CONNECTION_TIMEOUT_MS = 15000
private const val READ_TIMEOUT_MS = 15000
private const val MAX_RETRIES = 2

data class RegisterPayload(
    val fullName: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: String = "patient",
)

data class AuthSession(
    val userId: Int,
    val patientId: Int?,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val accessToken: String,
    val role: String,
    val expiresInMinutes: Int,
)

sealed interface RegistrationResult {
    data class Success(val session: AuthSession) : RegistrationResult
    data class Error(val message: String) : RegistrationResult
}

data class LoginPayload(
    val identifier: String,
    val password: String,
    val expectedRole: String? = null,
)

data class ProfileSetupPayload(
    val userId: Int,
    val fullName: String,
    val age: Int,
    val gender: String,
    val village: String,
    val bloodType: String,
)

data class ProfileUpdatePayload(
    val fullName: String,
    val email: String,
    val phone: String,
    val village: String,
    val bloodType: String,
    val age: Int? = null,
    val gender: String? = null,
    val weightKg: Double? = null,
    val photoPath: String? = null,
)

data class PatientProfile(
    val userId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val age: Int?,
    val gender: String?,
    val village: String?,
    val bloodType: String?,
    val weightKg: Double?,
    val photoPath: String?,
)

sealed interface LoginResult {
    data class Success(val session: AuthSession) : LoginResult
    data class Error(val message: String) : LoginResult
}

sealed interface ProfileSetupResult {
    data class Success(val profile: PatientProfile) : ProfileSetupResult
    data class Error(val message: String) : ProfileSetupResult
}

sealed interface ProfileFetchResult {
    data class Success(val profile: PatientProfile) : ProfileFetchResult
    data object NotFound : ProfileFetchResult
    data class Error(val message: String) : ProfileFetchResult
}

sealed interface ProfileUpdateResult {
    data class Success(val profile: PatientProfile) : ProfileUpdateResult
    data class Error(val message: String) : ProfileUpdateResult
}

object AuthApiClient {
    suspend fun register(payload: RegisterPayload): RegistrationResult = withContext(Dispatchers.IO) {
        var lastException: Exception? = null
        
        // Retry loop for transient failures
        repeat(MAX_RETRIES) { attempt ->
            try {
                val result = performRegister(payload)
                if (result is RegistrationResult.Success || attempt == MAX_RETRIES - 1) {
                    return@withContext result
                }
                lastException = null
            } catch (e: Exception) {
                lastException = e
                Log.w("AuthApiClient", "Register attempt ${attempt + 1} failed: ${e.javaClass.simpleName} - ${e.message}")
                if (attempt < MAX_RETRIES - 1) {
                    // Exponential backoff: 500ms, 1000ms
                    delay((500L * (attempt + 1)))
                }
            }
        }
        
        // All retries exhausted
        if (lastException != null) {
            return@withContext handleRegisterException(lastException!!)
        }
        RegistrationResult.Error("Unable to reach the server. Please try again.")
    }

    private suspend fun performRegister(payload: RegisterPayload): RegistrationResult {
        val connection = (URL("$API_BASE_URL/auth/register").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECTION_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("phone", payload.phone)
                .put("email", payload.email)
                .put("password", payload.password)
                .put("role", payload.role)
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            
            Log.d("AuthApiClient", "Register response code: $responseCode")

            if (responseCode in 200..299) {
                return parseRegistrationResponse(responseBody)
            } else {
                return RegistrationResult.Error(parseError(responseBody, "Unable to create account."))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseRegistrationResponse(responseBody: String): RegistrationResult {
        return try {
            val json = JSONObject(responseBody)
            
            // Validate required fields
            if (!json.has("user_id") || !json.has("full_name") || !json.has("access_token")) {
                Log.e("AuthApiClient", "Missing required fields in registration response")
                return RegistrationResult.Error("Server sent incomplete response. Please try again.")
            }

            RegistrationResult.Success(
                session = AuthSession(
                    userId = json.getInt("user_id"),
                    patientId = if (json.has("patient_id") && !json.isNull("patient_id")) {
                        try {
                            json.getInt("patient_id")
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    },
                    fullName = json.getString("full_name"),
                    email = json.optString("email").takeIf { it.isNotBlank() },
                    phone = json.optString("phone").takeIf { it.isNotBlank() },
                    accessToken = json.getString("access_token"),
                    role = json.getString("role"),
                    expiresInMinutes = json.getInt("expires_in_minutes"),
                )
            )
        } catch (parseException: Exception) {
            Log.e("AuthApiClient", "Failed to parse registration response: ${parseException.message}")
            RegistrationResult.Error("Server response format error. Please try again.")
        }
    }

    private fun handleRegisterException(e: Exception): RegistrationResult {
        return when (e) {
            is java.net.SocketTimeoutException -> {
                Log.e("AuthApiClient", "Register timeout: ${e.message}", e)
                RegistrationResult.Error("Connection timed out. Please check your internet and try again.")
            }
            is java.net.ConnectException, is java.net.UnknownHostException -> {
                Log.e("AuthApiClient", "Cannot connect to server: ${e.message}", e)
                RegistrationResult.Error("Cannot reach the server. Please check your internet connection.")
            }
            is org.json.JSONException -> {
                Log.e("AuthApiClient", "Invalid server response format: ${e.message}", e)
                RegistrationResult.Error("Server sent invalid response. Please try again.")
            }
            else -> {
                Log.e("AuthApiClient", "Unexpected registration error: ${e.javaClass.simpleName} - ${e.message}", e)
                RegistrationResult.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    suspend fun login(payload: LoginPayload): LoginResult = withContext(Dispatchers.IO) {
        var lastException: Exception? = null
        
        // Retry loop for transient failures
        repeat(MAX_RETRIES) { attempt ->
            try {
                val result = performLogin(payload)
                if (result is LoginResult.Success || attempt == MAX_RETRIES - 1) {
                    return@withContext result
                }
                // If error, retry unless it's the last attempt
                lastException = null
            } catch (e: Exception) {
                lastException = e
                Log.w("AuthApiClient", "Login attempt ${attempt + 1} failed: ${e.javaClass.simpleName} - ${e.message}")
                if (attempt < MAX_RETRIES - 1) {
                    // Exponential backoff: 500ms, 1000ms
                    delay((500L * (attempt + 1)))
                }
            }
        }
        
        // All retries exhausted
        if (lastException != null) {
            return@withContext handleLoginException(lastException!!)
        }
        LoginResult.Error("Unable to reach the server. Please try again.")
    }

    private suspend fun performLogin(payload: LoginPayload): LoginResult {
        val connection = (URL("$API_BASE_URL/auth/login").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECTION_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val requestJson = JSONObject()
                .put("identifier", payload.identifier)
                .put("password", payload.password)
                .apply {
                    if (!payload.expectedRole.isNullOrBlank()) {
                        put("expected_role", payload.expectedRole)
                    }
                }
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            
            Log.d("AuthApiClient", "Login response code: $responseCode")

            if (responseCode in 200..299) {
                return parseLoginResponse(responseBody)
            } else {
                return LoginResult.Error(parseError(responseBody, "Invalid email/phone or password."))
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseLoginResponse(responseBody: String): LoginResult {
        return try {
            val json = JSONObject(responseBody)
            
            // Validate required fields
            if (!json.has("user_id") || !json.has("full_name") || !json.has("access_token")) {
                Log.e("AuthApiClient", "Missing required fields in login response")
                return LoginResult.Error("Server sent incomplete response. Please try again.")
            }

            LoginResult.Success(
                session = AuthSession(
                    userId = json.getInt("user_id"),
                    patientId = if (json.has("patient_id") && !json.isNull("patient_id")) {
                        try {
                            json.getInt("patient_id")
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    },
                    fullName = json.getString("full_name"),
                    email = json.optString("email").takeIf { it.isNotBlank() },
                    phone = json.optString("phone").takeIf { it.isNotBlank() },
                    accessToken = json.getString("access_token"),
                    role = json.getString("role"),
                    expiresInMinutes = json.getInt("expires_in_minutes"),
                )
            )
        } catch (parseException: Exception) {
            Log.e("AuthApiClient", "Failed to parse login response: ${parseException.message}")
            LoginResult.Error("Server response format error. Please try again.")
        }
    }

    private fun handleLoginException(e: Exception): LoginResult {
        return when (e) {
            is java.net.SocketTimeoutException -> {
                Log.e("AuthApiClient", "Login timeout: ${e.message}", e)
                LoginResult.Error("Connection timed out. Please check your internet and try again.")
            }
            is java.net.ConnectException, is java.net.UnknownHostException -> {
                Log.e("AuthApiClient", "Cannot connect to server: ${e.message}", e)
                LoginResult.Error("Cannot reach the server. Please check your internet connection.")
            }
            is java.net.SocketException, is java.io.IOException -> {
                Log.e("AuthApiClient", "Login I/O error: ${e.message}", e)
                LoginResult.Error("Cannot reach the server. Ensure backend is running and try again.")
            }
            is org.json.JSONException -> {
                Log.e("AuthApiClient", "Invalid server response format: ${e.message}", e)
                LoginResult.Error("Server sent invalid response. Please try again.")
            }
            else -> {
                Log.e("AuthApiClient", "Unexpected login error: ${e.javaClass.simpleName} - ${e.message}", e)
                LoginResult.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    suspend fun setupPatientProfile(payload: ProfileSetupPayload): ProfileSetupResult = withContext(Dispatchers.IO) {
        val connection = (URL("$API_BASE_URL/auth/profile/setup").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val requestJson = JSONObject()
                .put("user_id", payload.userId)
                .put("full_name", payload.fullName)
                .put("age", payload.age)
                .put("gender", payload.gender)
                .put("village", payload.village)
                .put("blood_type", payload.bloodType)
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                ProfileSetupResult.Success(parsePatientProfile(JSONObject(responseBody)))
            } else {
                ProfileSetupResult.Error(parseError(responseBody, "Unable to save profile setup."))
            }
        } catch (_: Exception) {
            ProfileSetupResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun fetchPatientProfile(userId: Int): ProfileFetchResult = withContext(Dispatchers.IO) {
        val connection = (URL("$API_BASE_URL/auth/profile/$userId").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = false
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseBody = readBody(connection)
            when {
                connection.responseCode in 200..299 -> {
                    ProfileFetchResult.Success(parsePatientProfile(JSONObject(responseBody)))
                }
                connection.responseCode == 404 -> {
                    ProfileFetchResult.NotFound
                }
                else -> {
                    ProfileFetchResult.Error(parseError(responseBody, "Unable to fetch profile details."))
                }
            }
        } catch (_: Exception) {
            ProfileFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun updatePatientProfile(userId: Int, payload: ProfileUpdatePayload): ProfileUpdateResult = withContext(Dispatchers.IO) {
        val connection = (URL("$API_BASE_URL/auth/profile/$userId").openConnection() as HttpURLConnection).apply {
            requestMethod = "PUT"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("email", payload.email)
                .put("phone", payload.phone)
                .put("village", payload.village)
                .put("blood_type", payload.bloodType)
                .apply {
                    if (payload.age != null) {
                        put("age", payload.age)
                    }
                    if (!payload.gender.isNullOrBlank()) {
                        put("gender", payload.gender)
                    }
                    if (payload.weightKg != null) {
                        put("weight_kg", payload.weightKg)
                    }
                    if (!payload.photoPath.isNullOrBlank()) {
                        put("photo_path", payload.photoPath)
                    }
                }
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                ProfileUpdateResult.Success(parsePatientProfile(JSONObject(responseBody)))
            } else {
                ProfileUpdateResult.Error(parseError(responseBody, "Unable to update profile."))
            }
        } catch (_: Exception) {
            ProfileUpdateResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    private fun readBody(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }

        return BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
            reader.readText()
        }
    }

    private fun parseError(body: String, fallback: String): String {
        return runCatching {
            val json = JSONObject(body)
            when (val detail = json.opt("detail")) {
                is String -> detail
                null -> fallback
                else -> detail.toString()
            }
        }.getOrElse { fallback }
    }

    private fun parsePatientProfile(json: JSONObject): PatientProfile {
        return PatientProfile(
            userId = json.getInt("user_id"),
            fullName = json.getString("full_name"),
            email = json.optNullableString("email"),
            phone = json.optNullableString("phone"),
            age = json.optIntOrNull("age"),
            gender = json.optNullableString("gender"),
            village = json.optNullableString("village"),
            bloodType = json.optNullableString("blood_type"),
            weightKg = json.optDoubleOrNull("weight_kg"),
            photoPath = json.optNullableString("photo_path"),
        )
    }

    private fun JSONObject.optNullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotBlank() }
    }

    private fun JSONObject.optIntOrNull(key: String): Int? {
        if (!has(key) || isNull(key)) return null
        return optInt(key)
    }

    private fun JSONObject.optDoubleOrNull(key: String): Double? {
        if (!has(key) || isNull(key)) return null
        return optDouble(key, Double.NaN).takeIf { !it.isNaN() }
    }
}
