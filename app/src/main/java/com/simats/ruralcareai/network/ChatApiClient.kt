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

private const val API_BASE_URL = NETWORK_API_BASE_URL

data class ChatThreadDto(
    val appointmentId: Int?,
    val doctorId: Int,
    val doctorName: String,
    val specialization: String,
    val lastMessage: String,
    val lastMessageAt: String,
    val unreadCount: Int,
)

data class DoctorChatThreadDto(
    val appointmentId: Int?,
    val patientId: Int,
    val patientName: String,
    val patientLocation: String?,
    val lastMessage: String,
    val lastMessageAt: String,
    val unreadCount: Int,
)

data class ChatMessageDto(
    val id: Int,
    val appointmentId: Int?,
    val senderUserId: Int,
    val receiverUserId: Int,
    val messageText: String,
    val sentAt: String,
    val isRead: Boolean,
    val isMine: Boolean,
)

sealed interface ChatThreadsResult {
    data class Success(val threads: List<ChatThreadDto>) : ChatThreadsResult
    data class Error(val message: String) : ChatThreadsResult
}

sealed interface DoctorChatThreadsResult {
    data class Success(val threads: List<DoctorChatThreadDto>) : DoctorChatThreadsResult
    data class Error(val message: String) : DoctorChatThreadsResult
}

sealed interface ChatMessagesResult {
    data class Success(val messages: List<ChatMessageDto>) : ChatMessagesResult
    data class Error(val message: String) : ChatMessagesResult
}

sealed interface SendChatMessageResult {
    data class Success(val message: ChatMessageDto) : SendChatMessageResult
    data class Error(val message: String) : SendChatMessageResult
}

object ChatApiClient {
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

    suspend fun getThreads(patientId: Int): ChatThreadsResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/chat/threads/$patientId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val array = JSONArray(responseBody)
                    val threads = mutableListOf<ChatThreadDto>()
                    for (i in 0 until array.length()) {
                        val json = array.getJSONObject(i)
                        threads += ChatThreadDto(
                            appointmentId = if (json.has("appointment_id") && !json.isNull("appointment_id")) json.getInt("appointment_id") else null,
                            doctorId = json.getInt("doctor_id"),
                            doctorName = json.optString("doctor_name", "Doctor"),
                            specialization = json.optString("specialization", "Specialist"),
                            lastMessage = json.optString("last_message", "No messages yet"),
                            lastMessageAt = json.optString("last_message_at", ""),
                            unreadCount = json.optInt("unread_count", 0),
                        )
                    }
                    ChatThreadsResult.Success(threads)
                } else {
                    ChatThreadsResult.Error(parseError(responseBody, "Unable to load chat conversations."))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            ChatThreadsResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getDoctorThreads(doctorId: Int): DoctorChatThreadsResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/chat/threads/doctor/$doctorId").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val array = JSONArray(responseBody)
                    val threads = mutableListOf<DoctorChatThreadDto>()
                    for (i in 0 until array.length()) {
                        val json = array.getJSONObject(i)
                        threads += DoctorChatThreadDto(
                            appointmentId = if (json.has("appointment_id") && !json.isNull("appointment_id")) json.getInt("appointment_id") else null,
                            patientId = json.getInt("patient_id"),
                            patientName = json.optString("patient_name", "Patient"),
                            patientLocation = json.optString("patient_location").takeIf { it.isNotBlank() },
                            lastMessage = json.optString("last_message", "No messages yet"),
                            lastMessageAt = json.optString("last_message_at", ""),
                            unreadCount = json.optInt("unread_count", 0),
                        )
                    }
                    DoctorChatThreadsResult.Success(threads)
                } else {
                    DoctorChatThreadsResult.Error(parseError(responseBody, "Unable to load chat conversations."))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            DoctorChatThreadsResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun getMessages(
        patientId: Int,
        doctorId: Int,
        appointmentId: Int?,
        viewerRole: String = "patient",
    ): ChatMessagesResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val appointmentQuery = if (appointmentId != null) "&appointment_id=$appointmentId" else ""
            val roleQuery = "&viewer_role=${java.net.URLEncoder.encode(viewerRole, StandardCharsets.UTF_8.name())}"
            val connection = (
                URL("$API_BASE_URL/chat/messages?patient_id=$patientId&doctor_id=$doctorId$appointmentQuery$roleQuery").openConnection() as HttpURLConnection
            ).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    val array = JSONArray(responseBody)
                    val messages = mutableListOf<ChatMessageDto>()
                    for (i in 0 until array.length()) {
                        val json = array.getJSONObject(i)
                        messages += parseMessage(json)
                    }
                    ChatMessagesResult.Success(messages)
                } else {
                    ChatMessagesResult.Error(parseError(responseBody, "Unable to load messages."))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            ChatMessagesResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    suspend fun sendMessage(
        patientId: Int,
        doctorId: Int,
        appointmentId: Int?,
        text: String,
        senderRole: String = "patient",
    ): SendChatMessageResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = (URL("$API_BASE_URL/chat/messages").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10000
                readTimeout = 10000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            try {
                val payload = JSONObject()
                    .put("patient_id", patientId)
                    .put("doctor_id", doctorId)
                    .put("sender_role", senderRole)
                    .put("message_text", text)
                    .apply {
                        if (appointmentId != null) {
                            put("appointment_id", appointmentId)
                        }
                    }
                    .toString()

                connection.outputStream.use { stream ->
                    stream.write(payload.toByteArray(StandardCharsets.UTF_8))
                }

                val responseBody = readBody(connection)
                if (connection.responseCode in 200..299) {
                    SendChatMessageResult.Success(parseMessage(JSONObject(responseBody)))
                } else {
                    SendChatMessageResult.Error(parseError(responseBody, "Unable to send message."))
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            SendChatMessageResult.Error(e.message ?: "Unable to reach the server")
        }
    }

    private fun parseMessage(json: JSONObject): ChatMessageDto {
        return ChatMessageDto(
            id = json.getInt("id"),
            appointmentId = if (json.has("appointment_id") && !json.isNull("appointment_id")) json.getInt("appointment_id") else null,
            senderUserId = json.getInt("sender_user_id"),
            receiverUserId = json.getInt("receiver_user_id"),
            messageText = json.optString("message_text", ""),
            sentAt = json.optString("sent_at", ""),
            isRead = json.optBoolean("is_read", false),
            isMine = json.optBoolean("is_mine", false),
        )
    }
}
