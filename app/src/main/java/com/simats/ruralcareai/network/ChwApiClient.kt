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

private const val CHW_API_BASE_URL = NETWORK_API_BASE_URL

data class ChwWorkerProfileDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val workerCode: String,
    val assignedVillage: String?,
)

data class ChwDashboardStatsDto(
    val totalPatients: Int,
    val visitsToday: Int,
    val vitalsChecked: Int,
    val assignedVillage: String?,
)

data class ChwRegisterPatientPayload(
    val fullName: String,
    val age: Int,
    val gender: String,
    val aadhaarId: String,
    val heightCm: Double?,
    val weightKg: Double?,
    val bloodType: String?,
    val photoBase64: String?,
)

data class ChwRegisteredPatientDto(
    val patientId: Int,
    val userId: Int,
    val fullName: String,
    val village: String,
    val message: String,
)

sealed interface ChwWorkerProfileResult {
    data class Success(val profile: ChwWorkerProfileDto) : ChwWorkerProfileResult
    data class Error(val message: String) : ChwWorkerProfileResult
}

sealed interface ChwDashboardStatsResult {
    data class Success(val stats: ChwDashboardStatsDto) : ChwDashboardStatsResult
    data class Error(val message: String) : ChwDashboardStatsResult
}

data class ChwRecordVitalsPayload(
    val patientId: Int,
    val systolic: Int,
    val diastolic: Int,
    val glucoseMode: String,
    val glucoseReading: Int,
    val temperature: Double,
    val spo2: Int,
    val notes: String?,
)

sealed interface ChwRecordVitalsResult {
    object Success : ChwRecordVitalsResult
    data class Error(val message: String) : ChwRecordVitalsResult
}


data class ChwCampSchedulePayload(
    val village: String,
    val primaryFocus: String,
    val subFocus: List<String> = emptyList(),
    val scheduledDate: String,
    val slot: String,
)

data class ChwCampScheduleResponseDto(
    val id: Int,
    val workerId: Int,
    val village: String,
    val primaryFocus: String,
    val subFocus: List<String>?,
    val scheduledAt: String,
    val slot: String,
    val status: String,
)

data class ChwAppointmentDto(
    val id: Int,
    val patientId: Int,
    val doctorId: Int?,
    val scheduledAt: String,
    val consultationMode: String,
    val reason: String?,
    val status: String,
    val patientName: String?,
    val patientLocation: String?,
)

sealed interface ChwUpcomingVisitsResult {
    data class Success(val appointments: List<ChwAppointmentDto>) : ChwUpcomingVisitsResult
    data class Error(val message: String) : ChwUpcomingVisitsResult
}

sealed interface ChwScheduleCampResult {
    data class Success(val schedule: ChwCampScheduleResponseDto) : ChwScheduleCampResult
    data class Error(val message: String) : ChwScheduleCampResult
}

sealed interface ChwRegisterPatientResult {
    data class Success(val patient: ChwRegisteredPatientDto) : ChwRegisterPatientResult
    data class Error(val message: String) : ChwRegisterPatientResult
}

object ChwApiClient {
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

    suspend fun getWorkerByUser(userId: Int): ChwWorkerProfileResult = withContext(Dispatchers.IO) {
        val connection = (URL("$CHW_API_BASE_URL/community-health-workers/by-user/$userId").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                val json = JSONObject(responseBody)
                ChwWorkerProfileResult.Success(
                    ChwWorkerProfileDto(
                        id = json.getInt("id"),
                        userId = json.getInt("user_id"),
                        fullName = json.optString("full_name", ""),
                        workerCode = json.optString("worker_code", ""),
                        assignedVillage = json.optString("assigned_village").takeIf { it.isNotBlank() },
                    )
                )
            } else {
                ChwWorkerProfileResult.Error(parseError(responseBody, "Unable to load health worker profile."))
            }
        } catch (_: Exception) {
            ChwWorkerProfileResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun getDashboardStats(workerId: Int): ChwDashboardStatsResult = withContext(Dispatchers.IO) {
        val connection = (URL("$CHW_API_BASE_URL/community-health-workers/$workerId/dashboard").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                val json = JSONObject(responseBody)
                ChwDashboardStatsResult.Success(
                    ChwDashboardStatsDto(
                        totalPatients = json.optInt("total_patients", 0),
                        visitsToday = json.optInt("visits_today", 0),
                        vitalsChecked = json.optInt("vitals_checked", 0),
                        assignedVillage = json.optString("assigned_village").takeIf { it.isNotBlank() },
                    )
                )
            } else {
                ChwDashboardStatsResult.Error(parseError(responseBody, "Unable to load dashboard stats."))
            }
        } catch (_: Exception) {
            ChwDashboardStatsResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun getUpcomingAppointments(): ChwUpcomingVisitsResult = withContext(Dispatchers.IO) {
        val connection = (URL("$CHW_API_BASE_URL/appointments").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }

        try {
            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                val array = org.json.JSONArray(responseBody)
                val appointments = mutableListOf<ChwAppointmentDto>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    appointments.add(
                        ChwAppointmentDto(
                            id = obj.getInt("id"),
                            patientId = obj.optInt("patient_id", 0),
                            doctorId = obj.opt("doctor_id")?.let { if (it is Int) it else null },
                            scheduledAt = obj.optString("scheduled_at", ""),
                            consultationMode = obj.optString("consultation_mode", ""),
                            reason = obj.optString("reason", "").takeIf { it.isNotBlank() },
                            status = obj.optString("status", ""),
                            patientName = obj.optString("patient_name", "").takeIf { it.isNotBlank() },
                            patientLocation = obj.optString("patient_location", "").takeIf { it.isNotBlank() },
                        )
                    )
                }
                ChwUpcomingVisitsResult.Success(appointments)
            } else {
                ChwUpcomingVisitsResult.Error(parseError(responseBody, "Unable to load upcoming appointments."))
            }
        } catch (_: Exception) {
            ChwUpcomingVisitsResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun registerPatient(workerId: Int, payload: ChwRegisterPatientPayload): ChwRegisterPatientResult = withContext(Dispatchers.IO) {
        val connection = (URL("$CHW_API_BASE_URL/community-health-workers/$workerId/patients").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 15000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val requestJson = JSONObject()
                .put("full_name", payload.fullName)
                .put("age", payload.age)
                .put("gender", payload.gender)
                .put("aadhaar_id", payload.aadhaarId)
                .put("height_cm", payload.heightCm)
                .put("weight_kg", payload.weightKg)
                .put("blood_type", payload.bloodType)
                .put("photo_base64", payload.photoBase64)
                .toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                val json = JSONObject(responseBody)
                ChwRegisterPatientResult.Success(
                    ChwRegisteredPatientDto(
                        patientId = json.getInt("patient_id"),
                        userId = json.getInt("user_id"),
                        fullName = json.optString("full_name", ""),
                        village = json.optString("village", ""),
                        message = json.optString("message", "Patient registered successfully."),
                    )
                )
            } else {
                ChwRegisterPatientResult.Error(parseError(responseBody, "Unable to register patient."))
            }
        } catch (_: Exception) {
            ChwRegisterPatientResult.Error("Unable to reach the server. Please try again.")
        } finally {
            connection.disconnect()
        }
    }

    suspend fun submitVitalsRecord(payload: ChwRecordVitalsPayload): ChwRecordVitalsResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL("$CHW_API_BASE_URL/records").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val requestJson = JSONObject().apply {
                put("patient_id", payload.patientId)
                put("doctor_id", JSONObject.NULL)
                put("record_type", "vitals_capture")
                put("title", "Vitals record captured by CHW")
                put(
                    "description",
                    JSONObject().apply {
                        put("systolic", payload.systolic)
                        put("diastolic", payload.diastolic)
                        put("glucose_mode", payload.glucoseMode)
                        put("glucose_reading", payload.glucoseReading)
                        put("temperature", payload.temperature)
                        put("spo2", payload.spo2)
                        put("notes", payload.notes ?: "")
                    }.toString(),
                )
            }.toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                ChwRecordVitalsResult.Success
            } else {
                ChwRecordVitalsResult.Error(parseError(responseBody, "Unable to save vitals record."))
            }
        } catch (_: Exception) {
            ChwRecordVitalsResult.Error("Unable to reach the server. Please try again.")
        }
    }

    suspend fun scheduleCamp(workerId: Int, payload: ChwCampSchedulePayload): ChwScheduleCampResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL("$CHW_API_BASE_URL/community-health-workers/$workerId/camp-schedules").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val subFocusArray = JSONArray().apply {
                payload.subFocus.forEach { put(it) }
            }

            val requestJson = JSONObject().apply {
                put("village", payload.village)
                put("primary_focus", payload.primaryFocus)
                put("sub_focus", subFocusArray)
                put("scheduled_date", payload.scheduledDate)
                put("slot", payload.slot)
            }.toString()

            connection.outputStream.use { stream ->
                stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
            }

            val responseCode = connection.responseCode
            val responseBody = readBody(connection)
            if (responseCode in 200..299) {
                val json = JSONObject(responseBody)
                ChwScheduleCampResult.Success(
                    ChwCampScheduleResponseDto(
                        id = json.getInt("id"),
                        workerId = json.getInt("worker_id"),
                        village = json.optString("village", ""),
                        primaryFocus = json.optString("primary_focus", ""),
                        subFocus = json.optJSONArray("sub_focus")?.let { arr ->
                            List(arr.length()) { idx -> arr.getString(idx) }
                        },
                        scheduledAt = json.optString("scheduled_at", ""),
                        slot = json.optString("slot", ""),
                        status = json.optString("status", ""),
                    )
                )
            } else {
                ChwScheduleCampResult.Error(parseError(responseBody, "Unable to schedule camp."))
            }
        } catch (_: Exception) {
            ChwScheduleCampResult.Error("Unable to reach the server. Please try again.")
        }
    }
}

