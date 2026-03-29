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

data class AdminActivityDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val timestamp: String,
)

data class AdminDashboardOverviewDto(
    val totalPatients: Int,
    val totalDoctors: Int,
    val activeConsultations: Int,
    val completedToday: Int,
    val recentActivity: List<AdminActivityDto>,
)

data class AdminDoctorDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val specialty: String,
    val joinDate: String,
    val experienceYears: Int,
    val qualification: String?,
    val languages: List<String>,
    val hospital: String?,
    val location: String?,
    val photoPath: String?,
    val status: String,
    val isVerified: Boolean,
)

data class AdminPatientDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val village: String?,
    val gender: String?,
    val age: Int?,
    val joinDate: String,
    val photoPath: String?,
)

data class AdminWorkerDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val workerCode: String,
    val assignedVillage: String?,
    val roleTitle: String,
    val joinDate: String,
    val photoPath: String?,
    val status: String,
)

data class AdminWorkersSummaryDto(
    val totalWorkers: Int,
    val activeNow: Int,
    val onField: Int,
)

data class AdminConsultationTrendDto(
    val label: String,
    val value: Int,
)

data class AdminClinicPerformanceDto(
    val clinicName: String,
    val totalAppointments: Int,
    val completedAppointments: Int,
    val performancePercent: Double,
)

data class AdminAnalyticsDto(
    val range: String,
    val consultationGrowthPercent: Double,
    val consultationCount: Int,
    val patientSatisfactionScore: Double,
    val patientSatisfactionSamples: Int,
    val efficiencyPercent: Double,
    val activeChws: Int,
    val activeChwsPercent: Double,
    val consultationTrends: List<AdminConsultationTrendDto>,
    val regionalClinicPerformance: List<AdminClinicPerformanceDto>,
)

data class AdminDoctorUpsertPayload(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String?,
    val specialty: String,
    val experienceYears: Int,
    val qualification: String,
    val hospital: String,
    val location: String,
    val languages: List<String>,
    val status: String,
    val isVerified: Boolean,
    val photoPath: String? = null,
)

data class AdminWorkerUpsertPayload(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String?,
    val workerCode: String,
    val assignedVillage: String,
    val roleTitle: String,
    val status: String,
    val photoPath: String? = null,
)

sealed interface AdminDashboardFetchResult {
    data class Success(val overview: AdminDashboardOverviewDto) : AdminDashboardFetchResult
    data class Error(val message: String) : AdminDashboardFetchResult
}

sealed interface AdminDoctorsFetchResult {
    data class Success(val doctors: List<AdminDoctorDto>) : AdminDoctorsFetchResult
    data class Error(val message: String) : AdminDoctorsFetchResult
}

sealed interface AdminPatientsFetchResult {
    data class Success(val patients: List<AdminPatientDto>) : AdminPatientsFetchResult
    data class Error(val message: String) : AdminPatientsFetchResult
}

sealed interface AdminWorkersFetchResult {
    data class Success(val workers: List<AdminWorkerDto>) : AdminWorkersFetchResult
    data class Error(val message: String) : AdminWorkersFetchResult
}

sealed interface AdminWorkersSummaryResult {
    data class Success(val summary: AdminWorkersSummaryDto) : AdminWorkersSummaryResult
    data class Error(val message: String) : AdminWorkersSummaryResult
}

sealed interface AdminDoctorMutationResult {
    data class Success(val doctor: AdminDoctorDto) : AdminDoctorMutationResult
    data class Error(val message: String) : AdminDoctorMutationResult
}

sealed interface AdminWorkerMutationResult {
    data class Success(val worker: AdminWorkerDto) : AdminWorkerMutationResult
    data class Error(val message: String) : AdminWorkerMutationResult
}

sealed interface AdminOperationResult {
    data class Success(val message: String) : AdminOperationResult
    data class Error(val message: String) : AdminOperationResult
}

sealed interface AdminAnalyticsFetchResult {
    data class Success(val analytics: AdminAnalyticsDto) : AdminAnalyticsFetchResult
    data class Error(val message: String) : AdminAnalyticsFetchResult
}

object AdminApiClient {
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

    private fun buildUrl(path: String, query: List<Pair<String, String>> = emptyList()): String {
        if (query.isEmpty()) {
            return "$API_BASE_URL$path"
        }

        val queryText = query.joinToString("&") {
            "${URLEncoder.encode(it.first, StandardCharsets.UTF_8.name())}=${URLEncoder.encode(it.second, StandardCharsets.UTF_8.name())}"
        }
        return "$API_BASE_URL$path?$queryText"
    }

    private fun parseActivity(json: JSONObject): AdminActivityDto {
        return AdminActivityDto(
            id = json.getString("id"),
            title = json.getString("title"),
            subtitle = json.getString("subtitle"),
            category = json.optString("category", "system"),
            timestamp = json.optString("timestamp", ""),
        )
    }

    private fun parseDoctor(json: JSONObject): AdminDoctorDto {
        val languageArray = json.optJSONArray("languages") ?: JSONArray()
        val languages = mutableListOf<String>()
        for (i in 0 until languageArray.length()) {
            languages.add(languageArray.optString(i))
        }

        return AdminDoctorDto(
            id = json.getInt("id"),
            userId = json.getInt("user_id"),
            fullName = json.getString("full_name"),
            email = json.optString("email").takeIf { it.isNotBlank() },
            phone = json.optString("phone").takeIf { it.isNotBlank() },
            specialty = json.getString("specialty"),
            joinDate = json.optString("join_date", ""),
            experienceYears = json.optInt("experience_years", 0),
            qualification = json.optString("qualification").takeIf { it.isNotBlank() },
            languages = languages,
            hospital = json.optString("hospital").takeIf { it.isNotBlank() },
            location = json.optString("location").takeIf { it.isNotBlank() },
            photoPath = json.optString("photo_path").takeIf { it.isNotBlank() },
            status = json.optString("status", "pending"),
            isVerified = json.optBoolean("is_verified", false),
        )
    }

    private fun parsePatient(json: JSONObject): AdminPatientDto {
        val ageValue = if (json.isNull("age")) null else json.optInt("age")

        return AdminPatientDto(
            id = json.getInt("id"),
            userId = json.getInt("user_id"),
            fullName = json.getString("full_name"),
            email = json.optString("email").takeIf { it.isNotBlank() },
            phone = json.optString("phone").takeIf { it.isNotBlank() },
            village = json.optString("village").takeIf { it.isNotBlank() },
            gender = json.optString("gender").takeIf { it.isNotBlank() },
            age = ageValue,
            joinDate = json.optString("join_date", ""),
            photoPath = json.optString("photo_path").takeIf { it.isNotBlank() },
        )
    }

    private fun parseWorker(json: JSONObject): AdminWorkerDto {
        return AdminWorkerDto(
            id = json.getInt("id"),
            userId = json.getInt("user_id"),
            fullName = json.getString("full_name"),
            email = json.optString("email").takeIf { it.isNotBlank() },
            phone = json.optString("phone").takeIf { it.isNotBlank() },
            workerCode = json.getString("worker_code"),
            assignedVillage = json.optString("assigned_village").takeIf { it.isNotBlank() },
            roleTitle = json.optString("role_title", "Community Health Worker"),
            joinDate = json.optString("join_date", ""),
            photoPath = json.optString("photo_path").takeIf { it.isNotBlank() },
            status = json.optString("status", "active"),
        )
    }

    private fun parseAnalytics(json: JSONObject): AdminAnalyticsDto {
        val trends = mutableListOf<AdminConsultationTrendDto>()
        val trendsJson = json.optJSONArray("consultation_trends") ?: JSONArray()
        for (index in 0 until trendsJson.length()) {
            val item = trendsJson.getJSONObject(index)
            trends.add(
                AdminConsultationTrendDto(
                    label = item.optString("label", ""),
                    value = item.optInt("value", 0),
                )
            )
        }

        val clinics = mutableListOf<AdminClinicPerformanceDto>()
        val clinicsJson = json.optJSONArray("regional_clinic_performance") ?: JSONArray()
        for (index in 0 until clinicsJson.length()) {
            val item = clinicsJson.getJSONObject(index)
            clinics.add(
                AdminClinicPerformanceDto(
                    clinicName = item.optString("clinic_name", ""),
                    totalAppointments = item.optInt("total_appointments", 0),
                    completedAppointments = item.optInt("completed_appointments", 0),
                    performancePercent = item.optDouble("performance_percent", 0.0),
                )
            )
        }

        return AdminAnalyticsDto(
            range = json.optString("range", "month"),
            consultationGrowthPercent = json.optDouble("consultation_growth_percent", 0.0),
            consultationCount = json.optInt("consultation_count", 0),
            patientSatisfactionScore = json.optDouble("patient_satisfaction_score", 0.0),
            patientSatisfactionSamples = json.optInt("patient_satisfaction_samples", 0),
            efficiencyPercent = json.optDouble("efficiency_percent", 0.0),
            activeChws = json.optInt("active_chws", 0),
            activeChwsPercent = json.optDouble("active_chws_percent", 0.0),
            consultationTrends = trends,
            regionalClinicPerformance = clinics,
        )
    }

    suspend fun fetchDashboardOverview(limit: Int = 6): AdminDashboardFetchResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/dashboard", listOf("limit" to limit.toString()))).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                val activitiesJson = json.optJSONArray("recent_activity") ?: JSONArray()
                val activities = mutableListOf<AdminActivityDto>()
                for (index in 0 until activitiesJson.length()) {
                    activities.add(parseActivity(activitiesJson.getJSONObject(index)))
                }

                AdminDashboardFetchResult.Success(
                    AdminDashboardOverviewDto(
                        totalPatients = json.optInt("total_patients", 0),
                        totalDoctors = json.optInt("total_doctors", 0),
                        activeConsultations = json.optInt("active_consultations", 0),
                        completedToday = json.optInt("completed_today", 0),
                        recentActivity = activities,
                    )
                )
            } else {
                AdminDashboardFetchResult.Error(parseError(responseBody, "Unable to load dashboard data."))
            }
        } catch (_: Exception) {
            AdminDashboardFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun listDoctors(status: String? = null, search: String? = null): AdminDoctorsFetchResult = withContext(Dispatchers.IO) {
        val query = mutableListOf<Pair<String, String>>()
        if (!status.isNullOrBlank()) {
            query.add("status" to status)
        }
        if (!search.isNullOrBlank()) {
            query.add("search" to search)
        }

        val connection = (URL(buildUrl("/admin/doctors", query)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val jsonArray = JSONArray(responseBody)
                val doctors = mutableListOf<AdminDoctorDto>()
                for (index in 0 until jsonArray.length()) {
                    doctors.add(parseDoctor(jsonArray.getJSONObject(index)))
                }
                AdminDoctorsFetchResult.Success(doctors)
            } else {
                AdminDoctorsFetchResult.Error(parseError(responseBody, "Unable to load doctors."))
            }
        } catch (_: Exception) {
            AdminDoctorsFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun listPatients(search: String? = null): AdminPatientsFetchResult = withContext(Dispatchers.IO) {
        val query = mutableListOf<Pair<String, String>>()
        if (!search.isNullOrBlank()) {
            query.add("search" to search)
        }

        val connection = (URL(buildUrl("/admin/patients", query)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val jsonArray = JSONArray(responseBody)
                val patients = mutableListOf<AdminPatientDto>()
                for (index in 0 until jsonArray.length()) {
                    patients.add(parsePatient(jsonArray.getJSONObject(index)))
                }
                AdminPatientsFetchResult.Success(patients)
            } else {
                AdminPatientsFetchResult.Error(parseError(responseBody, "Unable to load patients."))
            }
        } catch (_: Exception) {
            AdminPatientsFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun createDoctor(payload: AdminDoctorUpsertPayload): AdminDoctorMutationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/doctors")).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("email", payload.email)
                .put("phone", payload.phone)
                .put("password", payload.password ?: "Doctor@123")
                .put("specialty", payload.specialty)
                .put("experience_years", payload.experienceYears)
                .put("qualification", payload.qualification)
                .put("hospital", payload.hospital)
                .put("location", payload.location)
                .put("languages", JSONArray(payload.languages))
                .put("status", payload.status)
                .put("is_verified", payload.isVerified)
                .put("photo_path", payload.photoPath)
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                AdminDoctorMutationResult.Success(parseDoctor(JSONObject(responseBody)))
            } else {
                AdminDoctorMutationResult.Error(parseError(responseBody, "Unable to create doctor."))
            }
        } catch (_: Exception) {
            AdminDoctorMutationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun updateDoctor(doctorId: Int, payload: AdminDoctorUpsertPayload): AdminDoctorMutationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/doctors/$doctorId")).openConnection() as HttpURLConnection).apply {
            requestMethod = "PUT"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("email", payload.email)
                .put("phone", payload.phone)
                .put("specialty", payload.specialty)
                .put("experience_years", payload.experienceYears)
                .put("qualification", payload.qualification)
                .put("hospital", payload.hospital)
                .put("location", payload.location)
                .put("languages", JSONArray(payload.languages))
                .put("status", payload.status)
                .put("is_verified", payload.isVerified)
                .put("photo_path", payload.photoPath)
                .apply {
                    if (!payload.password.isNullOrBlank()) {
                        put("password", payload.password)
                    }
                }
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                AdminDoctorMutationResult.Success(parseDoctor(JSONObject(responseBody)))
            } else {
                AdminDoctorMutationResult.Error(parseError(responseBody, "Unable to update doctor."))
            }
        } catch (_: Exception) {
            AdminDoctorMutationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun deactivateDoctor(doctorId: Int): AdminOperationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/doctors/$doctorId/deactivate")).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminOperationResult.Success(json.optString("message", "Doctor deactivated."))
            } else {
                AdminOperationResult.Error(parseError(responseBody, "Unable to deactivate doctor."))
            }
        } catch (_: Exception) {
            AdminOperationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun deleteDoctor(doctorId: Int): AdminOperationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/doctors/$doctorId")).openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminOperationResult.Success(json.optString("message", "Doctor deleted."))
            } else {
                AdminOperationResult.Error(parseError(responseBody, "Unable to delete doctor."))
            }
        } catch (_: Exception) {
            AdminOperationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun listWorkers(status: String? = null, search: String? = null): AdminWorkersFetchResult = withContext(Dispatchers.IO) {
        val query = mutableListOf<Pair<String, String>>()
        if (!status.isNullOrBlank()) {
            query.add("status" to status)
        }
        if (!search.isNullOrBlank()) {
            query.add("search" to search)
        }

        val connection = (URL(buildUrl("/admin/workers", query)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val jsonArray = JSONArray(responseBody)
                val workers = mutableListOf<AdminWorkerDto>()
                for (index in 0 until jsonArray.length()) {
                    workers.add(parseWorker(jsonArray.getJSONObject(index)))
                }
                AdminWorkersFetchResult.Success(workers)
            } else {
                AdminWorkersFetchResult.Error(parseError(responseBody, "Unable to load workers."))
            }
        } catch (_: Exception) {
            AdminWorkersFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun getWorkersSummary(): AdminWorkersSummaryResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/workers/summary")).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminWorkersSummaryResult.Success(
                    AdminWorkersSummaryDto(
                        totalWorkers = json.optInt("total_workers", 0),
                        activeNow = json.optInt("active_now", 0),
                        onField = json.optInt("on_field", 0),
                    )
                )
            } else {
                AdminWorkersSummaryResult.Error(parseError(responseBody, "Unable to load worker stats."))
            }
        } catch (_: Exception) {
            AdminWorkersSummaryResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun createWorker(payload: AdminWorkerUpsertPayload): AdminWorkerMutationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/workers")).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("email", payload.email)
                .put("phone", payload.phone)
                .put("password", payload.password ?: "Worker@123")
                .put("worker_code", payload.workerCode)
                .put("assigned_village", payload.assignedVillage)
                .put("role_title", payload.roleTitle)
                .put("status", payload.status)
                .put("photo_path", payload.photoPath)
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                AdminWorkerMutationResult.Success(parseWorker(JSONObject(responseBody)))
            } else {
                AdminWorkerMutationResult.Error(parseError(responseBody, "Unable to create worker."))
            }
        } catch (_: Exception) {
            AdminWorkerMutationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun updateWorker(workerId: Int, payload: AdminWorkerUpsertPayload): AdminWorkerMutationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/workers/$workerId")).openConnection() as HttpURLConnection).apply {
            requestMethod = "PUT"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("email", payload.email)
                .put("phone", payload.phone)
                .put("worker_code", payload.workerCode)
                .put("assigned_village", payload.assignedVillage)
                .put("role_title", payload.roleTitle)
                .put("status", payload.status)
                .put("photo_path", payload.photoPath)
                .apply {
                    if (!payload.password.isNullOrBlank()) {
                        put("password", payload.password)
                    }
                }
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                AdminWorkerMutationResult.Success(parseWorker(JSONObject(responseBody)))
            } else {
                AdminWorkerMutationResult.Error(parseError(responseBody, "Unable to update worker."))
            }
        } catch (_: Exception) {
            AdminWorkerMutationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun deactivateWorker(workerId: Int): AdminOperationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/workers/$workerId/deactivate")).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminOperationResult.Success(json.optString("message", "Worker deactivated."))
            } else {
                AdminOperationResult.Error(parseError(responseBody, "Unable to deactivate worker."))
            }
        } catch (_: Exception) {
            AdminOperationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun deleteWorker(workerId: Int): AdminOperationResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/workers/$workerId")).openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminOperationResult.Success(json.optString("message", "Worker deleted."))
            } else {
                AdminOperationResult.Error(parseError(responseBody, "Unable to delete worker."))
            }
        } catch (_: Exception) {
            AdminOperationResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun fetchAnalytics(range: String): AdminAnalyticsFetchResult = withContext(Dispatchers.IO) {
        val connection = (URL(buildUrl("/admin/analytics", listOf("range" to range))).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
        }

        return@withContext try {
            val responseBody = readBody(connection)
            if (connection.responseCode in 200..299) {
                val json = JSONObject(responseBody)
                AdminAnalyticsFetchResult.Success(parseAnalytics(json))
            } else {
                AdminAnalyticsFetchResult.Error(parseError(responseBody, "Unable to load analytics."))
            }
        } catch (_: Exception) {
            AdminAnalyticsFetchResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }
}
