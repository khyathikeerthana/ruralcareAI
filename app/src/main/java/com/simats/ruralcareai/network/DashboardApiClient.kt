package com.simats.ruralcareai.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.nio.charset.StandardCharsets

private const val API_BASE_URL = NETWORK_API_BASE_URL

data class DoctorSummaryDto(
    val id: Int,
    val fullName: String,
    val specialization: String,
    val yearsExperience: Int,
    val isVerified: Boolean,
    val bio: String?,
    val rating: Float? = null,
    val distance: Float? = null,
)

data class AppointmentDto(
    val id: Int,
    val patientId: Int,
    val doctorId: Int,
    val scheduledAt: String,
    val status: String,
    val consultationMode: String,
    val reason: String?,
    val doctorName: String?,
    val specialization: String?,
)

data class PrescriptionDto(
    val id: Int,
    val patientId: Int,
    val medicationName: String,
    val dosage: String,
    val instructions: String?,
    val issuedAt: String,
)

sealed interface SearchResult {
    data class Success(val doctors: List<DoctorSummaryDto>) : SearchResult
    data class Error(val message: String) : SearchResult
}

sealed interface AppointmentsResult {
    data class Success(val appointments: List<AppointmentDto>) : AppointmentsResult
    data class Error(val message: String) : AppointmentsResult
}

sealed interface PrescriptionsResult {
    data class Success(val prescriptions: List<PrescriptionDto>) : PrescriptionsResult
    data class Error(val message: String) : PrescriptionsResult
}

sealed interface UpcomingAppointmentResult {
    data class Success(val appointment: AppointmentDto?) : UpcomingAppointmentResult
    data class Error(val message: String) : UpcomingAppointmentResult
}

object DashboardApiClient {
    private fun readBody(connection: HttpURLConnection): String {
        return if (connection.responseCode in 200..299) {
            BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use { it.readText() }
        } else {
            BufferedReader(InputStreamReader(connection.errorStream, StandardCharsets.UTF_8)).use { it.readText() }
        }
    }

    private fun parseError(body: String, defaultMessage: String): String {
        return try {
            val json = JSONObject(body)
            json.optString("detail", defaultMessage)
        } catch (_: Exception) {
            defaultMessage
        }
    }

    suspend fun searchDoctors(query: String, specialization: String? = null): SearchResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val params = mutableListOf<String>()
            if (query.isNotBlank()) {
                params += "search=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name())
            }
            if (!specialization.isNullOrBlank()) {
                params += "specialization=" + URLEncoder.encode(specialization, StandardCharsets.UTF_8.name())
            }

            val urlString = if (params.isEmpty()) {
                "$API_BASE_URL/doctors"
            } else {
                "$API_BASE_URL/doctors?${params.joinToString("&") }"
            }
            
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(responseBody)
                    val doctors = mutableListOf<DoctorSummaryDto>()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        doctors.add(
                            DoctorSummaryDto(
                                id = json.getInt("id"),
                                fullName = json.getString("full_name"),
                                specialization = json.getString("specialization"),
                                yearsExperience = json.optInt("years_experience", 0),
                                isVerified = json.optBoolean("is_verified", false),
                                bio = json.optString("bio").takeIf { it.isNotBlank() },
                                rating = if (json.has("rating")) json.optDouble("rating").toFloat() else null,
                                distance = if (json.has("distance")) json.optDouble("distance").toFloat() else null,
                            )
                        )
                    }
                    SearchResult.Success(doctors)
                } else {
                    SearchResult.Error(parseError(responseBody, "Search failed"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            SearchResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getAppointmentsByPatient(patientId: Int): AppointmentsResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/appointments?patient_id=$patientId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(responseBody)
                    val appointments = mutableListOf<AppointmentDto>()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        appointments.add(
                            AppointmentDto(
                                id = json.getInt("id"),
                                patientId = json.getInt("patient_id"),
                                doctorId = json.getInt("doctor_id"),
                                scheduledAt = json.getString("scheduled_at"),
                                status = json.getString("status"),
                                consultationMode = json.getString("consultation_mode"),
                                reason = json.optString("reason").takeIf { it.isNotBlank() },
                                doctorName = json.optString("doctor_name").takeIf { it.isNotBlank() },
                                specialization = json.optString("specialization").takeIf { it.isNotBlank() },
                            )
                        )
                    }
                    AppointmentsResult.Success(appointments)
                } else {
                    AppointmentsResult.Error(parseError(responseBody, "Failed to fetch appointments"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            AppointmentsResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getPrescriptionsByPatient(patientId: Int): PrescriptionsResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/prescriptions/$patientId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(responseBody)
                    val prescriptions = mutableListOf<PrescriptionDto>()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        prescriptions.add(
                            PrescriptionDto(
                                id = json.getInt("id"),
                                patientId = json.getInt("patient_id"),
                                medicationName = json.getString("medication_name"),
                                dosage = json.getString("dosage"),
                                instructions = json.optString("instructions"),
                                issuedAt = json.getString("issued_at"),
                            )
                        )
                    }
                    PrescriptionsResult.Success(prescriptions)
                } else {
                    PrescriptionsResult.Error(parseError(responseBody, "Failed to fetch prescriptions"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            PrescriptionsResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getUpcomingAppointment(patientId: Int): UpcomingAppointmentResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/appointments/upcoming/$patientId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    if (responseBody.isBlank() || responseBody == "null") {
                        UpcomingAppointmentResult.Success(null)
                    } else {
                        val json = JSONObject(responseBody)
                        val appointment = AppointmentDto(
                            id = json.getInt("id"),
                            patientId = json.getInt("patient_id"),
                            doctorId = json.getInt("doctor_id"),
                            scheduledAt = json.getString("scheduled_at"),
                            status = json.getString("status"),
                            consultationMode = json.getString("consultation_mode"),
                            reason = json.optString("reason").takeIf { it.isNotBlank() },
                            doctorName = json.optString("doctor_name").takeIf { it.isNotBlank() },
                            specialization = json.optString("specialization").takeIf { it.isNotBlank() },
                        )
                        UpcomingAppointmentResult.Success(appointment)
                    }
                } else {
                    UpcomingAppointmentResult.Error(parseError(responseBody, "Failed to fetch upcoming appointment"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            UpcomingAppointmentResult.Error(e.message ?: "Unable to reach the server")
        }
    }
}
