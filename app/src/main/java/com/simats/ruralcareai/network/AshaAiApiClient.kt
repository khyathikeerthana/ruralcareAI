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

data class SymptomAnalysisDto(
    val severityLabel: String,
    val severityScore: Int,
    val triageNote: String,
    val possibleCauses: List<String>,
    val matchedSymptoms: List<String>,
    val immediateActions: List<String>,
    val homeCare: List<String>,
    val redFlags: List<String>,
    val disclaimer: String,
    val provider: String,
)

sealed interface SymptomAnalysisResult {
    data class Success(val analysis: SymptomAnalysisDto) : SymptomAnalysisResult
    data class Error(val message: String) : SymptomAnalysisResult
}

private const val API_BASE_URL = NETWORK_API_BASE_URL

object AshaAiApiClient {
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

    private fun parseStringList(array: JSONArray?): List<String> {
        if (array == null) {
            return emptyList()
        }
        val values = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val value = array.optString(i).trim()
            if (value.isNotBlank()) {
                values += value
            }
        }
        return values
    }

    suspend fun analyzeSymptoms(
        patientId: Int?,
        symptoms: String,
        language: String? = null,
    ): SymptomAnalysisResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/ai/analyze-symptoms").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 20000
                readTimeout = 60000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            try {
                val requestJson = JSONObject()
                    .put("symptoms", symptoms)
                    .apply {
                        if (patientId != null) {
                            put("patient_id", patientId)
                        }
                        if (!language.isNullOrBlank()) {
                            put("language", language)
                        }
                    }
                    .toString()

                connection.outputStream.use { stream ->
                    stream.write(requestJson.toByteArray(StandardCharsets.UTF_8))
                }

                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val json = JSONObject(responseBody)
                    SymptomAnalysisResult.Success(
                        analysis = SymptomAnalysisDto(
                            severityLabel = json.optString("severity_label", "Moderate Severity"),
                            severityScore = json.optInt("severity_score", 50),
                            triageNote = json.optString("triage_note", "Consult a doctor if symptoms worsen."),
                            possibleCauses = parseStringList(json.optJSONArray("possible_causes")),
                            matchedSymptoms = parseStringList(json.optJSONArray("matched_symptoms")),
                            immediateActions = parseStringList(json.optJSONArray("immediate_actions")),
                            homeCare = parseStringList(json.optJSONArray("home_care")),
                            redFlags = parseStringList(json.optJSONArray("red_flags")),
                            disclaimer = json.optString("disclaimer", "AI guidance is informational only."),
                            provider = json.optString("provider", "gemini"),
                        )
                    )
                } else {
                    SymptomAnalysisResult.Error(parseError(responseBody, "Unable to analyze symptoms right now."))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            SymptomAnalysisResult.Error(e.message ?: "Unable to reach the server")
        }
    }
}
