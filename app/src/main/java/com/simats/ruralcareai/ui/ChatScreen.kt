package com.simats.ruralcareai.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.ChatApiClient
import com.simats.ruralcareai.network.ChatMessageDto
import com.simats.ruralcareai.network.ChatMessagesResult
import com.simats.ruralcareai.network.ChatThreadDto
import com.simats.ruralcareai.network.ChatThreadsResult
import com.simats.ruralcareai.network.DoctorChatThreadDto
import com.simats.ruralcareai.network.DoctorChatThreadsResult
import com.simats.ruralcareai.network.SendChatMessageResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ChatBackground = Color(0xFFF3F6FB)
private val ChatPrimary = Color(0xFF2D9CDB)
private val ChatText = Color(0xFF0F1730)
private val ChatMuted = Color(0xFF7B8798)
private val ChatCard = Color.White
private val ChatIncoming = Color(0xFFEDEFF4)
private val ChatOutgoing = Color(0xFF2F9ADB)
private val ChatOutline = Color(0xFFE4EAF2)

private data class ChatThreadUi(
    val appointmentId: Int?,
    val doctorId: Int,
    val name: String,
    val specialization: String,
    val preview: String,
    val timeLabel: String,
    val unreadCount: Int,
)

private data class ChatMessageUi(
    val id: Int,
    val content: String,
    val timeLabel: String,
    val isMine: Boolean,
)

private data class DoctorChatThreadUi(
    val appointmentId: Int?,
    val patientId: Int,
    val patientName: String,
    val patientLocation: String,
    val preview: String,
    val timeLabel: String,
    val unreadCount: Int,
)

@Composable
fun PatientChatHubScreen(
    patientId: Int?,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val threads = remember { mutableStateOf<List<ChatThreadUi>>(emptyList()) }
    val isLoadingThreads = remember { mutableStateOf(false) }
    val threadsError = remember { mutableStateOf<String?>(null) }
    var selectedThread by remember { mutableStateOf<ChatThreadUi?>(null) }
    val scope = rememberCoroutineScope()

    fun loadThreads() {
        if (patientId == null) {
            threads.value = emptyList()
            isLoadingThreads.value = false
            threadsError.value = "Patient session not found. Please log in again."
            return
        }

        scope.launch {
            isLoadingThreads.value = true
            threadsError.value = null
            when (val result = ChatApiClient.getThreads(patientId)) {
                is ChatThreadsResult.Success -> {
                    threads.value = result.threads.map { it.toUiThread() }
                }

                is ChatThreadsResult.Error -> {
                    threads.value = emptyList()
                    threadsError.value = result.message
                }
            }
            isLoadingThreads.value = false
        }
    }

    LaunchedEffect(patientId) {
        selectedThread = null
        loadThreads()
    }

    BackHandler(onBack = {
        if (selectedThread == null) {
            onBackToHome()
        } else {
            selectedThread = null
        }
    })

    if (selectedThread == null) {
        ChatInboxScreen(
            threads = threads.value,
            isLoading = isLoadingThreads.value,
            errorMessage = threadsError.value,
            onBack = onBackToHome,
            onRetry = ::loadThreads,
            onOpenThread = { selectedThread = it },
            modifier = modifier,
        )
    } else {
        ChatConversationScreen(
            thread = selectedThread!!,
            patientId = patientId,
            onBack = { selectedThread = null },
            modifier = modifier,
        )
    }
}

@Composable
fun DoctorChatHubScreen(
    doctorId: Int?,
    initialPatientId: Int?,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val threads = remember { mutableStateOf<List<DoctorChatThreadUi>>(emptyList()) }
    val isLoadingThreads = remember { mutableStateOf(false) }
    val threadsError = remember { mutableStateOf<String?>(null) }
    var selectedThread by remember { mutableStateOf<DoctorChatThreadUi?>(null) }
    val scope = rememberCoroutineScope()

    fun loadThreads() {
        if (doctorId == null) {
            threads.value = emptyList()
            isLoadingThreads.value = false
            threadsError.value = "Doctor session not found. Please log in again."
            return
        }

        scope.launch {
            isLoadingThreads.value = true
            threadsError.value = null
            when (val result = ChatApiClient.getDoctorThreads(doctorId)) {
                is DoctorChatThreadsResult.Success -> {
                    threads.value = result.threads.map { it.toUiDoctorThread() }
                }

                is DoctorChatThreadsResult.Error -> {
                    threads.value = emptyList()
                    threadsError.value = result.message
                }
            }

            if (selectedThread == null && initialPatientId != null) {
                selectedThread = threads.value.firstOrNull { it.patientId == initialPatientId }
            }
            isLoadingThreads.value = false
        }
    }

    LaunchedEffect(doctorId, initialPatientId) {
        selectedThread = null
        loadThreads()
    }

    BackHandler(onBack = {
        if (selectedThread == null) {
            onBackToHome()
        } else {
            selectedThread = null
        }
    })

    if (selectedThread == null) {
        DoctorChatInboxScreen(
            threads = threads.value,
            isLoading = isLoadingThreads.value,
            errorMessage = threadsError.value,
            onBack = onBackToHome,
            onRetry = ::loadThreads,
            onOpenThread = { selectedThread = it },
            modifier = modifier,
        )
    } else {
        DoctorChatConversationScreen(
            thread = selectedThread!!,
            doctorId = doctorId,
            onBack = { selectedThread = null },
            modifier = modifier,
        )
    }
}

@Composable
private fun DoctorChatInboxScreen(
    threads: List<DoctorChatThreadUi>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenThread: (DoctorChatThreadUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredThreads = remember(threads, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) {
            threads
        } else {
            threads.filter {
                it.patientName.lowercase().contains(query) ||
                    it.patientLocation.lowercase().contains(query) ||
                    it.preview.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ChatBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )

            Text(
                text = "Patient Chats",
                color = ChatText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            CircleIconButton(
                icon = Icons.Filled.Refresh,
                contentDescription = "Refresh chats",
                onClick = onRetry,
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search patient conversations...",
                    color = Color(0xFF9AA6B6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFFA2ADBC),
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ChatCard,
                unfocusedContainerColor = ChatCard,
                focusedBorderColor = ChatOutline,
                unfocusedBorderColor = ChatOutline,
            ),
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CircularProgressIndicator(color = ChatPrimary)
                        Text(
                            text = "Loading conversations...",
                            color = ChatMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            !errorMessage.isNullOrBlank() -> {
                EmptyState(
                    title = "Unable to load chats",
                    subtitle = errorMessage,
                    actionLabel = "Try Again",
                    onAction = onRetry,
                )
            }

            filteredThreads.isEmpty() -> {
                EmptyState(
                    title = "No Conversations",
                    subtitle = "No patient chats available yet.",
                    actionLabel = "Refresh",
                    onAction = onRetry,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(filteredThreads, key = { thread -> "${thread.patientId}-${thread.appointmentId ?: 0}" }) { thread ->
                        DoctorChatThreadRow(thread = thread, onClick = { onOpenThread(thread) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorChatThreadRow(
    thread: DoctorChatThreadUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ChatOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val initials = buildInitials(thread.patientName)

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF4FA0A8), Color(0xFF1F6570)))),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = initials, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = thread.patientName,
                    color = ChatText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = thread.timeLabel,
                    color = ChatMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = thread.patientLocation,
                color = ChatPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = thread.preview,
                    color = Color(0xFF707E93),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (thread.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ChatPrimary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = thread.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorChatConversationScreen(
    thread: DoctorChatThreadUi,
    doctorId: Int?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var messages by remember(thread.patientId, thread.appointmentId, doctorId) { mutableStateOf<List<ChatMessageUi>>(emptyList()) }
    var isLoading by remember(thread.patientId, thread.appointmentId, doctorId) { mutableStateOf(false) }
    var errorMessage by remember(thread.patientId, thread.appointmentId, doctorId) { mutableStateOf<String?>(null) }
    var outgoingMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadMessages() {
        if (doctorId == null) {
            messages = emptyList()
            isLoading = false
            errorMessage = "Doctor session not found. Please log in again."
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = null
            when (
                val result = ChatApiClient.getMessages(
                    patientId = thread.patientId,
                    doctorId = doctorId,
                    appointmentId = thread.appointmentId,
                    viewerRole = "doctor",
                )
            ) {
                is ChatMessagesResult.Success -> {
                    messages = result.messages.map { it.toUiMessage() }
                }

                is ChatMessagesResult.Error -> {
                    messages = emptyList()
                    errorMessage = result.message
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(thread.patientId, thread.appointmentId, doctorId) {
        loadMessages()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ChatBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = thread.patientName,
                        color = ChatText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = thread.patientLocation,
                        color = ChatMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                CircleIconButton(
                    icon = Icons.Filled.Refresh,
                    contentDescription = "Refresh messages",
                    onClick = ::loadMessages,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFDDE5F0))
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CircularProgressIndicator(color = ChatPrimary)
                            Text(
                                text = "Loading messages...",
                                color = ChatMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                messages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = "No Messages Yet",
                            subtitle = "Start the conversation with ${thread.patientName}.",
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(messages, key = { it.id }) { message ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.82f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (message.isMine) ChatOutgoing else ChatIncoming)
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                ) {
                                    Text(
                                        text = message.content,
                                        color = if (message.isMine) Color.White else ChatText,
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }

                                Text(
                                    text = message.timeLabel,
                                    color = ChatMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                )
                            }
                        }
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color(0xFFB42318),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE1E7F0))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = outgoingMessage,
                    onValueChange = { outgoingMessage = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = {
                        Text(text = "Type your message...", color = Color(0xFF8F9CAF), fontSize = 14.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF7FAFD),
                        unfocusedContainerColor = Color(0xFFF7FAFD),
                        focusedBorderColor = Color(0xFFD9E1EC),
                        unfocusedBorderColor = Color(0xFFD9E1EC),
                    ),
                    singleLine = true,
                    enabled = !isSending,
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSending) Color(0xFF96CAE9) else ChatPrimary)
                        .clickable(
                            enabled = outgoingMessage.trim().isNotBlank() && !isSending && doctorId != null,
                            onClick = {
                                val messageText = outgoingMessage.trim()
                                if (messageText.isBlank() || doctorId == null) {
                                    return@clickable
                                }

                                scope.launch {
                                    isSending = true
                                    when (
                                        val result = ChatApiClient.sendMessage(
                                            patientId = thread.patientId,
                                            doctorId = doctorId,
                                            appointmentId = thread.appointmentId,
                                            text = messageText,
                                            senderRole = "doctor",
                                        )
                                    ) {
                                        is SendChatMessageResult.Success -> {
                                            loadMessages()
                                            outgoingMessage = ""
                                            errorMessage = null
                                        }

                                        is SendChatMessageResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                    isSending = false
                                }
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInboxScreen(
    threads: List<ChatThreadUi>,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenThread: (ChatThreadUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredThreads = remember(threads, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) {
            threads
        } else {
            threads.filter {
                it.name.lowercase().contains(query) ||
                    it.specialization.lowercase().contains(query) ||
                    it.preview.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ChatBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )

            Text(
                text = "Chats",
                color = ChatText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            CircleIconButton(
                icon = Icons.Filled.Refresh,
                contentDescription = "Refresh chats",
                onClick = onRetry,
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search conversations...",
                    color = Color(0xFF9AA6B6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFFA2ADBC),
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ChatCard,
                unfocusedContainerColor = ChatCard,
                focusedBorderColor = ChatOutline,
                unfocusedBorderColor = ChatOutline,
            ),
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        CircularProgressIndicator(color = ChatPrimary)
                        Text(
                            text = "Loading conversations...",
                            color = ChatMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            !errorMessage.isNullOrBlank() -> {
                EmptyState(
                    title = "Unable to load chats",
                    subtitle = errorMessage,
                    actionLabel = "Try Again",
                    onAction = onRetry,
                )
            }

            filteredThreads.isEmpty() -> {
                val subtitle = if (searchQuery.isBlank()) {
                    "No real chat conversations yet. Once appointments and messages are available, they will appear here."
                } else {
                    "No conversations match your search."
                }

                EmptyState(
                    title = "No Conversations",
                    subtitle = subtitle,
                    actionLabel = if (searchQuery.isBlank()) "Refresh" else null,
                    onAction = if (searchQuery.isBlank()) onRetry else null,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(filteredThreads, key = { thread -> "${thread.doctorId}-${thread.appointmentId ?: 0}" }) { thread ->
                        ChatThreadRow(
                            thread = thread,
                            onClick = { onOpenThread(thread) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThreadRow(
    thread: ChatThreadUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ChatOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val initials = buildInitials(thread.name)

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF4FA0A8), Color(0xFF1F6570))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = thread.name,
                    color = ChatText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = thread.timeLabel,
                    color = ChatMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = thread.specialization,
                color = ChatPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = thread.preview,
                    color = Color(0xFF707E93),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (thread.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ChatPrimary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = thread.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatConversationScreen(
    thread: ChatThreadUi,
    patientId: Int?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var messages by remember(thread.doctorId, thread.appointmentId, patientId) { mutableStateOf<List<ChatMessageUi>>(emptyList()) }
    var isLoading by remember(thread.doctorId, thread.appointmentId, patientId) { mutableStateOf(false) }
    var errorMessage by remember(thread.doctorId, thread.appointmentId, patientId) { mutableStateOf<String?>(null) }
    var outgoingMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadMessages() {
        if (patientId == null) {
            messages = emptyList()
            isLoading = false
            errorMessage = "Patient session not found. Please log in again."
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = null
            when (
                val result = ChatApiClient.getMessages(
                    patientId = patientId,
                    doctorId = thread.doctorId,
                    appointmentId = thread.appointmentId,
                    viewerRole = "patient",
                )
            ) {
                is ChatMessagesResult.Success -> {
                    messages = result.messages.map { it.toUiMessage() }
                }

                is ChatMessagesResult.Error -> {
                    messages = emptyList()
                    errorMessage = result.message
                }
            }
            isLoading = false
        }
    }

    LaunchedEffect(thread.doctorId, thread.appointmentId, patientId) {
        loadMessages()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ChatBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = thread.name,
                        color = ChatText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = thread.specialization,
                        color = ChatMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                CircleIconButton(
                    icon = Icons.Filled.Refresh,
                    contentDescription = "Refresh messages",
                    onClick = ::loadMessages,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFDDE5F0))
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CircularProgressIndicator(color = ChatPrimary)
                            Text(
                                text = "Loading messages...",
                                color = ChatMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                messages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = "No Messages Yet",
                            subtitle = "Start the conversation with ${thread.name}.",
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(messages, key = { it.id }) { message ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.82f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (message.isMine) ChatOutgoing else ChatIncoming)
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                ) {
                                    Text(
                                        text = message.content,
                                        color = if (message.isMine) Color.White else ChatText,
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }

                                Text(
                                    text = message.timeLabel,
                                    color = ChatMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                )
                            }
                        }
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = Color(0xFFB42318),
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE1E7F0))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = outgoingMessage,
                    onValueChange = { outgoingMessage = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = {
                        Text(
                            text = "Type your message...",
                            color = Color(0xFF8F9CAF),
                            fontSize = 14.sp,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF7FAFD),
                        unfocusedContainerColor = Color(0xFFF7FAFD),
                        focusedBorderColor = Color(0xFFD9E1EC),
                        unfocusedBorderColor = Color(0xFFD9E1EC),
                    ),
                    singleLine = true,
                    enabled = !isSending,
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isSending) Color(0xFF96CAE9) else ChatPrimary)
                        .clickable(
                            enabled = outgoingMessage.trim().isNotBlank() && !isSending && patientId != null,
                            onClick = {
                                val messageText = outgoingMessage.trim()
                                if (messageText.isBlank() || patientId == null) {
                                    return@clickable
                                }

                                scope.launch {
                                    isSending = true
                                    when (
                                        val result = ChatApiClient.sendMessage(
                                            patientId = patientId,
                                            doctorId = thread.doctorId,
                                            appointmentId = thread.appointmentId,
                                            text = messageText,
                                            senderRole = "patient",
                                        )
                                    ) {
                                        is SendChatMessageResult.Success -> {
                                            messages = messages + result.message.toUiMessage()
                                            outgoingMessage = ""
                                            errorMessage = null
                                        }

                                        is SendChatMessageResult.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                    isSending = false
                                }
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, ChatOutline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ChatPrimary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, ChatOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = ChatText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                color = ChatMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = ChatPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun ChatThreadDto.toUiThread(): ChatThreadUi {
    return ChatThreadUi(
        appointmentId = appointmentId,
        doctorId = doctorId,
        name = doctorName,
        specialization = specialization,
        preview = lastMessage.ifBlank { "No messages yet" },
        timeLabel = formatChatPreviewTime(lastMessageAt),
        unreadCount = unreadCount,
    )
}

private fun DoctorChatThreadDto.toUiDoctorThread(): DoctorChatThreadUi {
    return DoctorChatThreadUi(
        appointmentId = appointmentId,
        patientId = patientId,
        patientName = patientName,
        patientLocation = patientLocation ?: "Unknown",
        preview = lastMessage.ifBlank { "No messages yet" },
        timeLabel = formatChatPreviewTime(lastMessageAt),
        unreadCount = unreadCount,
    )
}

private fun ChatMessageDto.toUiMessage(): ChatMessageUi {
    return ChatMessageUi(
        id = id,
        content = messageText,
        timeLabel = formatChatMessageTime(sentAt),
        isMine = isMine,
    )
}

private fun buildInitials(name: String): String {
    val chunks = name
        .removePrefix("Dr. ")
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    val initials = chunks
        .take(2)
        .mapNotNull { word -> word.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")

    return initials.ifBlank { "DR" }
}

private fun parseChatDate(rawValue: String): Date? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
    )

    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.US)
            val parsed = parser.parse(rawValue)
            if (parsed != null) {
                return parsed
            }
        } catch (_: Exception) {
            // Try next format.
        }
    }

    return null
}

private fun formatChatPreviewTime(rawValue: String): String {
    val parsed = parseChatDate(rawValue) ?: return rawValue
    val now = Date()

    val todayFormatter = SimpleDateFormat("yyyyMMdd", Locale.US)
    val nowTag = todayFormatter.format(now)
    val parsedTag = todayFormatter.format(parsed)

    return if (nowTag == parsedTag) {
        SimpleDateFormat("hh:mm a", Locale.US).format(parsed)
    } else {
        SimpleDateFormat("MMM dd", Locale.US).format(parsed)
    }
}

private fun formatChatMessageTime(rawValue: String): String {
    val parsed = parseChatDate(rawValue) ?: return rawValue
    return SimpleDateFormat("hh:mm a", Locale.US).format(parsed)
}
