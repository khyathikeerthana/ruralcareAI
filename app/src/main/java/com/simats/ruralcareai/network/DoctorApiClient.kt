package com.simats.ruralcareai.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val DOCTOR_API_BASE_URL = NETWORK_API_BASE_URL

data class DoctorIdentityDto(
    val doctorId: Int,
    val fullName: String,
    val specialization: String,
    val yearsExperience: Int,
)

data class DoctorQueueItemDto(
    val appointmentId: Int,
    val patientId: Int,
    val patientName: String,
    val patientLocation: String?,
    val scheduledAt: String,
    val status: String,
    val consultationMode: String,
    val reason: String?,
)

data class DoctorDashboardDto(
    val doctorId: Int,
    val doctorName: String,
    val totalAppointments: Int,
    val waitingAppointments: Int,
    val completedAppointments: Int,
    val doneAppointments: Int,
    val upcomingCount: Int,
    val upNext: DoctorQueueItemDto?,
    val queue: List<DoctorQueueItemDto>,
)

data class DoctorPatientSummaryDto(
    val patientId: Int,
    val fullName: String,
    val location: String?,
    val gender: String?,
    val age: Int?,
    val status: String,
    val upcomingAt: String?,
)

data class DoctorPatientDetailDto(
    val patientId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val location: String?,
    val gender: String?,
    val age: Int?,
    val bloodType: String?,
    val weightKg: Double?,
    val emergencyContact: String?,
    val lastVisitAt: String?,
    val primaryReason: String?,
    val recordsCount: Int,
    val prescriptionsCount: Int,
)

data class DoctorAnalyticsDto(
    val consultationsDone: Int,
    val avgConsultationMinutes: Double,
    val waitingNow: Int,
    val doneToday: Int,
    val commonConditions: List<Pair<String, Int>>,
)

data class DoctorAppointmentItemDto(
    val appointmentId: Int,
    val patientId: Int,
    val patientName: String,
    val patientLocation: String?,
    val scheduledAt: String,
    val status: String,
    val consultationMode: String,
    val reason: String?,
)

data class DoctorProfileDto(
    val doctorId: Int,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val specialization: String,
    val yearsExperience: Int,
    val qualification: String?,
    val hospitalName: String?,
    val assignedLocation: String?,
    val clinicHours: String?,
    val languages: List<String>,
    val profilePhotoPath: String?,
)

data class DoctorRecordDto(
    val id: Int,
    val patientId: Int,
    val recordType: String,
    val title: String,
    val description: String?,
    val fileUrl: String?,
    val createdAt: String,
)

sealed interface DoctorApiResult<out T> {
    data class Success<T>(val data: T) : DoctorApiResult<T>
    data class Error(val message: String) : DoctorApiResult<Nothing>
}

object DoctorApiClient {
    private fun readBody(connection: HttpURLConnection): String {
        return if (connection.responseCode in 200..299) {
            BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use { it.readText() }
        } else {
            BufferedReader(InputStreamReader(connection.errorStream, StandardCharsets.UTF_8)).use { it.readText() }
        }
    }

    private fun parseError(body: String, defaultMessage: String): String {
        return try {
            JSONObject(body).optString("detail", defaultMessage)
        } catch (_: Exception) {
            defaultMessage
        }
    }

    private fun openConnection(path: String, method: String = "GET"): HttpURLConnection {
        return (URL("$DOCTOR_API_BASE_URL$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 10000
            readTimeout = 10000
            setRequestProperty("Accept", "application/json")
            if (method != "GET") {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
        }
    }

    private fun parseQueueItem(json: JSONObject): DoctorQueueItemDto {
        return DoctorQueueItemDto(
            appointmentId = json.getInt("appointment_id"),
            patientId = json.getInt("patient_id"),
            patientName = json.optString("patient_name", "Patient"),
            patientLocation = json.optString("patient_location").takeIf { it.isNotBlank() },
            scheduledAt = json.getString("scheduled_at"),
            status = json.getString("status"),
            consultationMode = json.optString("consultation_mode", "video"),
            reason = json.optString("reason").takeIf { it.isNotBlank() },
        )
    }

    suspend fun getDoctorByUser(userId: Int): DoctorApiResult<DoctorIdentityDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/by-user/$userId")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    DoctorApiResult.Success(
                        DoctorIdentityDto(
                            doctorId = json.getInt("id"),
                            fullName = json.getString("full_name"),
                            specialization = json.getString("specialization"),
                            yearsExperience = json.optInt("years_experience", 0),
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load doctor profile"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorDashboard(doctorId: Int): DoctorApiResult<DoctorDashboardDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/dashboard")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    val queueJson = json.optJSONArray("queue") ?: JSONArray()
                    val queue = buildList {
                        for (index in 0 until queueJson.length()) {
                            add(parseQueueItem(queueJson.getJSONObject(index)))
                        }
                    }

                    val upNext = if (json.has("up_next") && !json.isNull("up_next")) {
                        parseQueueItem(json.getJSONObject("up_next"))
                    } else {
                        null
                    }

                    DoctorApiResult.Success(
                        DoctorDashboardDto(
                            doctorId = json.getInt("doctor_id"),
                            doctorName = json.getString("doctor_name"),
                            totalAppointments = json.optInt("total_appointments", 0),
                            waitingAppointments = json.optInt("waiting_appointments", 0),
                            completedAppointments = json.optInt("completed_appointments", 0),
                            doneAppointments = json.optInt("done_appointments", 0),
                            upcomingCount = json.optInt("upcoming_count", 0),
                            upNext = upNext,
                            queue = queue,
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load doctor dashboard"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorPatients(doctorId: Int, query: String = ""): DoctorApiResult<List<DoctorPatientSummaryDto>> = withContext(Dispatchers.IO) {
        try {
            val suffix = if (query.isBlank()) "" else "?query=${java.net.URLEncoder.encode(query, StandardCharsets.UTF_8.name())}"
            val connection = openConnection("/doctors/$doctorId/patients$suffix")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(body)
                    val patients = buildList {
                        for (index in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(index)
                            add(
                                DoctorPatientSummaryDto(
                                    patientId = json.getInt("patient_id"),
                                    fullName = json.getString("full_name"),
                                    location = json.optString("location").takeIf { it.isNotBlank() },
                                    gender = json.optString("gender").takeIf { it.isNotBlank() },
                                    age = if (json.has("age") && !json.isNull("age")) json.getInt("age") else null,
                                    status = json.optString("status", "STABLE"),
                                    upcomingAt = json.optString("upcoming_at").takeIf { it.isNotBlank() },
                                )
                            )
                        }
                    }
                    DoctorApiResult.Success(patients)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load patients"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getPatientDetail(doctorId: Int, patientId: Int): DoctorApiResult<DoctorPatientDetailDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/patients/$patientId")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    DoctorApiResult.Success(
                        DoctorPatientDetailDto(
                            patientId = json.getInt("patient_id"),
                            fullName = json.getString("full_name"),
                            email = json.optString("email").takeIf { it.isNotBlank() },
                            phone = json.optString("phone").takeIf { it.isNotBlank() },
                            location = json.optString("location").takeIf { it.isNotBlank() },
                            gender = json.optString("gender").takeIf { it.isNotBlank() },
                            age = if (json.has("age") && !json.isNull("age")) json.getInt("age") else null,
                            bloodType = json.optString("blood_type").takeIf { it.isNotBlank() },
                            weightKg = if (json.has("weight_kg") && !json.isNull("weight_kg")) json.getDouble("weight_kg") else null,
                            emergencyContact = json.optString("emergency_contact").takeIf { it.isNotBlank() },
                            lastVisitAt = json.optString("last_visit_at").takeIf { it.isNotBlank() },
                            primaryReason = json.optString("primary_reason").takeIf { it.isNotBlank() },
                            recordsCount = json.optInt("records_count", 0),
                            prescriptionsCount = json.optInt("prescriptions_count", 0),
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load patient details"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun updatePatient(
        doctorId: Int,
        patientId: Int,
        fullName: String,
        location: String,
        gender: String,
        bloodType: String,
        weightKg: Double?,
        emergencyContact: String,
    ): DoctorApiResult<DoctorPatientDetailDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/patients/$patientId", "PUT")
            val payload = JSONObject().apply {
                put("full_name", fullName)
                put("location", location)
                put("gender", gender)
                put("blood_type", bloodType)
                if (weightKg != null) put("weight_kg", weightKg)
                put("emergency_contact", emergencyContact)
            }
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    DoctorApiResult.Success(
                        DoctorPatientDetailDto(
                            patientId = json.getInt("patient_id"),
                            fullName = json.getString("full_name"),
                            email = json.optString("email").takeIf { it.isNotBlank() },
                            phone = json.optString("phone").takeIf { it.isNotBlank() },
                            location = json.optString("location").takeIf { it.isNotBlank() },
                            gender = json.optString("gender").takeIf { it.isNotBlank() },
                            age = if (json.has("age") && !json.isNull("age")) json.getInt("age") else null,
                            bloodType = json.optString("blood_type").takeIf { it.isNotBlank() },
                            weightKg = if (json.has("weight_kg") && !json.isNull("weight_kg")) json.getDouble("weight_kg") else null,
                            emergencyContact = json.optString("emergency_contact").takeIf { it.isNotBlank() },
                            lastVisitAt = json.optString("last_visit_at").takeIf { it.isNotBlank() },
                            primaryReason = json.optString("primary_reason").takeIf { it.isNotBlank() },
                            recordsCount = json.optInt("records_count", 0),
                            prescriptionsCount = json.optInt("prescriptions_count", 0),
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to update patient"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorAnalytics(doctorId: Int): DoctorApiResult<DoctorAnalyticsDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/analytics")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    val conditionRows = json.optJSONArray("common_conditions") ?: JSONArray()
                    val conditions = buildList {
                        for (index in 0 until conditionRows.length()) {
                            val row = conditionRows.getJSONObject(index)
                            add(row.getString("name") to row.getInt("count"))
                        }
                    }

                    DoctorApiResult.Success(
                        DoctorAnalyticsDto(
                            consultationsDone = json.optInt("consultations_done", 0),
                            avgConsultationMinutes = json.optDouble("avg_consultation_minutes", 0.0),
                            waitingNow = json.optInt("waiting_now", 0),
                            doneToday = json.optInt("done_today", 0),
                            commonConditions = conditions,
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load analytics"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorAppointments(doctorId: Int, status: String? = null): DoctorApiResult<List<DoctorAppointmentItemDto>> = withContext(Dispatchers.IO) {
        try {
            val suffix = if (status.isNullOrBlank()) "" else "?status=${java.net.URLEncoder.encode(status, StandardCharsets.UTF_8.name())}"
            val connection = openConnection("/doctors/$doctorId/appointments$suffix")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(body)
                    val appointments = buildList {
                        for (index in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(index)
                            add(
                                DoctorAppointmentItemDto(
                                    appointmentId = json.getInt("appointment_id"),
                                    patientId = json.getInt("patient_id"),
                                    patientName = json.optString("patient_name", "Patient"),
                                    patientLocation = json.optString("patient_location").takeIf { it.isNotBlank() },
                                    scheduledAt = json.getString("scheduled_at"),
                                    status = json.optString("status", "scheduled"),
                                    consultationMode = json.optString("consultation_mode", "video"),
                                    reason = json.optString("reason").takeIf { it.isNotBlank() },
                                )
                            )
                        }
                    }
                    DoctorApiResult.Success(appointments)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load appointments"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorProfile(doctorId: Int): DoctorApiResult<DoctorProfileDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/profile")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    val languagesArray = json.optJSONArray("languages") ?: JSONArray()
                    val languages = buildList {
                        for (index in 0 until languagesArray.length()) {
                            add(languagesArray.optString(index))
                        }
                    }
                    DoctorApiResult.Success(
                        DoctorProfileDto(
                            doctorId = json.getInt("doctor_id"),
                            fullName = json.optString("full_name", "Doctor"),
                            email = json.optString("email").takeIf { it.isNotBlank() },
                            phone = json.optString("phone").takeIf { it.isNotBlank() },
                            specialization = json.optString("specialization", "General Physician"),
                            yearsExperience = json.optInt("years_experience", 0),
                            qualification = json.optString("qualification").takeIf { it.isNotBlank() },
                            hospitalName = json.optString("hospital_name").takeIf { it.isNotBlank() },
                            assignedLocation = json.optString("assigned_location").takeIf { it.isNotBlank() },
                            clinicHours = json.optString("clinic_hours").takeIf { it.isNotBlank() },
                            languages = languages,
                            profilePhotoPath = json.optString("profile_photo_path").takeIf { it.isNotBlank() },
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load profile"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun updateDoctorProfile(
        doctorId: Int,
        fullName: String,
        email: String,
        phone: String,
        specialization: String,
        yearsExperience: Int,
        qualification: String,
        hospitalName: String,
        assignedLocation: String,
        clinicHours: String,
        languages: List<String>,
        profilePhotoPath: String,
    ): DoctorApiResult<DoctorProfileDto> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/doctors/$doctorId/profile", "PUT")
            val payload = JSONObject().apply {
                put("full_name", fullName)
                put("email", email)
                put("phone", phone)
                put("specialization", specialization)
                put("years_experience", yearsExperience)
                put("qualification", qualification)
                put("hospital_name", hospitalName)
                put("assigned_location", assignedLocation)
                put("clinic_hours", clinicHours)
                put("languages", JSONArray(languages))
                put("profile_photo_path", profilePhotoPath)
            }
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(body)
                    val languagesArray = json.optJSONArray("languages") ?: JSONArray()
                    val parsedLanguages = buildList {
                        for (index in 0 until languagesArray.length()) {
                            add(languagesArray.optString(index))
                        }
                    }
                    DoctorApiResult.Success(
                        DoctorProfileDto(
                            doctorId = json.getInt("doctor_id"),
                            fullName = json.optString("full_name", "Doctor"),
                            email = json.optString("email").takeIf { it.isNotBlank() },
                            phone = json.optString("phone").takeIf { it.isNotBlank() },
                            specialization = json.optString("specialization", "General Physician"),
                            yearsExperience = json.optInt("years_experience", 0),
                            qualification = json.optString("qualification").takeIf { it.isNotBlank() },
                            hospitalName = json.optString("hospital_name").takeIf { it.isNotBlank() },
                            assignedLocation = json.optString("assigned_location").takeIf { it.isNotBlank() },
                            clinicHours = json.optString("clinic_hours").takeIf { it.isNotBlank() },
                            languages = parsedLanguages,
                            profilePhotoPath = json.optString("profile_photo_path").takeIf { it.isNotBlank() },
                        )
                    )
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to update profile"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun createAppointment(
        patientId: Int,
        doctorId: Int,
        scheduledAt: String,
        consultationMode: String,
        reason: String,
    ): DoctorApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/appointments", "POST")
            val payload = JSONObject().apply {
                put("patient_id", patientId)
                put("doctor_id", doctorId)
                put("scheduled_at", scheduledAt)
                put("consultation_mode", consultationMode)
                put("reason", reason)
            }
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    DoctorApiResult.Success(Unit)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to create appointment"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String): DoctorApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/appointments/$appointmentId", "PUT")
            val payload = JSONObject().apply { put("status", status) }
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    DoctorApiResult.Success(Unit)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to update appointment"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getRecords(patientId: Int): DoctorApiResult<List<DoctorRecordDto>> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/records/$patientId")
            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val jsonArray = JSONArray(body)
                    val records = buildList {
                        for (index in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(index)
                            add(
                                DoctorRecordDto(
                                    id = json.getInt("id"),
                                    patientId = json.getInt("patient_id"),
                                    recordType = json.getString("record_type"),
                                    title = json.getString("title"),
                                    description = json.optString("description").takeIf { it.isNotBlank() },
                                    fileUrl = json.optString("file_url").takeIf { it.isNotBlank() },
                                    createdAt = json.getString("created_at"),
                                )
                            )
                        }
                    }
                    DoctorApiResult.Success(records)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to load records"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun createRecord(
        patientId: Int,
        doctorId: Int,
        recordType: String,
        title: String,
        description: String,
    ): DoctorApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val connection = openConnection("/records", "POST")
            val payload = JSONObject().apply {
                put("patient_id", patientId)
                put("doctor_id", doctorId)
                put("record_type", recordType)
                put("title", title)
                put("description", description)
            }
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(StandardCharsets.UTF_8))
            }

            try {
                val body = readBody(connection)
                if (connection.responseCode in 200..299) {
                    DoctorApiResult.Success(Unit)
                } else {
                    DoctorApiResult.Error(parseError(body, "Unable to save record"))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorApiResult.Error(e.message ?: "Unable to reach the server")
        }
    }
}
