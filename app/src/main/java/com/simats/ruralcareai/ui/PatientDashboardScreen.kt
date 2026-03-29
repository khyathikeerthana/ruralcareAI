package com.simats.ruralcareai.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AppointmentDto
import com.simats.ruralcareai.network.DashboardApiClient
import com.simats.ruralcareai.network.PrescriptionDto
import com.simats.ruralcareai.network.PrescriptionsResult
import com.simats.ruralcareai.network.UpcomingAppointmentResult
import com.simats.ruralcareai.viewmodel.AppUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DashboardBackground = Color(0xFFF3F6FB)
private val DashboardCard = Color.White
private val DashboardText = Color(0xFF0F1730)
private val DashboardMuted = Color(0xFF5A6A82)
private val DashboardPrimary = Color(0xFF1F9BE6)
private val DashboardPrimaryDark = Color(0xFF1A87D3)
private val DashboardOutline = Color(0xFFE0E9F2)

private data class SpecialtyShortcut(
    val label: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
)

@Composable
fun PatientDashboardScreen(
    uiState: AppUiState,
    onSpecialtyClick: (String) -> Unit,
    onOpenConsults: () -> Unit,
    onOpenAshaAI: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenMyAppointments: () -> Unit = {},
    onOpenMedicineReminders: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val patientName = uiState.patientProfile?.fullName ?: uiState.currentUserName ?: "Alex Harrison"
    val patientId = uiState.currentPatientId

    val upcomingAppointment = remember { mutableStateOf<AppointmentDto?>(null) }
    val reminders = remember { mutableStateOf<List<PrescriptionDto>>(emptyList()) }
    val isUpcomingLoading = remember { mutableStateOf(false) }
    val isRemindersLoading = remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        if (patientId == null) {
            upcomingAppointment.value = null
            reminders.value = emptyList()
            isUpcomingLoading.value = false
            isRemindersLoading.value = false
            return@LaunchedEffect
        }

        isUpcomingLoading.value = true
        when (val upcomingResult = DashboardApiClient.getUpcomingAppointment(patientId)) {
            is UpcomingAppointmentResult.Success -> {
                upcomingAppointment.value = upcomingResult.appointment
            }
            is UpcomingAppointmentResult.Error -> {
                upcomingAppointment.value = null
            }
        }
        isUpcomingLoading.value = false

        isRemindersLoading.value = true
        when (val reminderResult = DashboardApiClient.getPrescriptionsByPatient(patientId)) {
            is PrescriptionsResult.Success -> {
                reminders.value = reminderResult.prescriptions
            }
            is PrescriptionsResult.Error -> {
                reminders.value = emptyList()
            }
        }
        isRemindersLoading.value = false
    }

    val specialties = listOf(
        SpecialtyShortcut(
            label = "General Physician",
            icon = Icons.Filled.MedicalServices,
            iconTint = Color(0xFF2F7AF5),
            iconBg = Color(0xFFDCE8FF),
        ),
        SpecialtyShortcut(
            label = "Orthopedic",
            icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
            iconTint = Color(0xFF1BAFD8),
            iconBg = Color(0xFFDFF5FB),
        ),
        SpecialtyShortcut(
            label = "Pediatrics",
            icon = Icons.Filled.Favorite,
            iconTint = Color(0xFFE63946),
            iconBg = Color(0xFFFCE4E7),
        ),
        SpecialtyShortcut(
            label = "Gynecology",
            icon = Icons.Filled.Science,
            iconTint = Color(0xFF8B5CF6),
            iconBg = Color(0xFFEDE9FE),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(DashboardBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 34.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                HeaderSection(
                    name = patientName,
                    photoPath = uiState.patientProfile?.photoPath ?: uiState.currentUserPhotoPath,
                )
            }

            item {
                SearchBox(onClick = onOpenConsults)
            }

            item {
                SectionHeader(title = "Find Specialists", actionText = "Browse", onActionClick = onOpenConsults)
            }

            item {
                SpecialtyShortcutRow(
                    specialties = specialties,
                    onSpecialtyClick = onSpecialtyClick,
                )
            }

            item {
                SectionHeader(
                    title = "Upcoming Appointment",
                    actionText = "See all",
                    onActionClick = onOpenMyAppointments,
                )
            }

            item {
                UpcomingAppointmentCard(
                    appointment = upcomingAppointment.value,
                    isLoading = isUpcomingLoading.value,
                )
            }

            item {
                SectionHeader(
                    title = "Medicine Reminders",
                    actionText = "See all",
                    onActionClick = onOpenMedicineReminders,
                )
            }

            if (isRemindersLoading.value) {
                item {
                    EmptyDashboardCard(text = "Loading reminders...")
                }
            } else if (reminders.value.isEmpty()) {
                item {
                    EmptyDashboardCard(text = "No reminders set right now. Add reminders to get started.")
                }
            } else {
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    ) {
                        items(reminders.value.take(6)) { reminder ->
                            MedicineReminderCard(reminder = reminder)
                        }
                    }
                }
            }

            item {
                DailyTipCard()
            }
        }

        BottomNavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            onConsultsClick = onOpenConsults,
            onAshaAiClick = onOpenAshaAI,
            onChatClick = onOpenChat,
            onProfileClick = onOpenProfile,
        )
    }
}

@Composable
private fun HeaderSection(name: String, photoPath: String? = null) {
    val avatar = remember(photoPath) { decodeDashboardPhoto(photoPath) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFC39A))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (avatar != null) {
                    Image(
                        bitmap = avatar,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Good morning,",
                    color = DashboardMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = name,
                    color = DashboardText,
                    fontSize = 36.sp / 1.75f,
                    lineHeight = 42.sp / 1.75f,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF3F8)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = Color(0xFF56627A),
                modifier = Modifier.size(23.dp),
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .offset(x = 9.dp, y = (-9).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4A4A))
            )
        }
    }
}

@Composable
private fun SearchBox(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DashboardCard)
            .border(1.dp, DashboardOutline, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = Color(0xFF9AA9BC),
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = "Search symptoms, doctors, or AI help",
            color = DashboardMuted,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SpecialtyShortcutRow(
    specialties: List<SpecialtyShortcut>,
    onSpecialtyClick: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 12.dp),
    ) {
        items(specialties) { specialty ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(1.dp, DashboardOutline, RoundedCornerShape(18.dp))
                    .clickable { onSpecialtyClick(specialty.label) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(specialty.iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = specialty.icon,
                        contentDescription = specialty.label,
                        tint = specialty.iconTint,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = specialty.label,
                    color = DashboardText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = DashboardText,
            fontSize = 36.sp / 1.75f,
            fontWeight = FontWeight.Bold,
        )

        if (actionText != null) {
            Text(
                text = actionText,
                color = DashboardPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(enabled = onActionClick != null) { onActionClick?.invoke() }
            )
        }
    }
}

@Composable
private fun UpcomingAppointmentCard(
    appointment: AppointmentDto?,
    isLoading: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(24.dp),
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Loading upcoming appointment...",
                    color = DashboardMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            return@Card
        }

        if (appointment == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No upcoming appointments right now.",
                    color = DashboardMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            return@Card
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF3A8F95)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (appointment.doctorName ?: "Doctor")
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                            .take(2)
                            .joinToString("")
                            .ifBlank { "DR" },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = appointment.doctorName ?: "Doctor #${appointment.doctorId}",
                        color = DashboardText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = appointment.specialization ?: "Specialist",
                        color = DashboardMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = DashboardPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = formatDashboardDateTime(appointment.scheduledAt),
                            color = DashboardPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicineReminderCard(reminder: PrescriptionDto) {
    Card(
        modifier = Modifier
            .width(196.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EEF6)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = reminder.medicationName.take(1).uppercase(),
                    color = DashboardPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = reminder.medicationName,
                color = DashboardText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )

            Text(
                text = reminder.dosage,
                color = DashboardMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )

            Text(
                text = reminder.instructions?.takeIf { it.isNotBlank() } ?: "As prescribed",
                color = DashboardMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8EEF6))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Issued ${formatIssuedDate(reminder.issuedAt)}",
                    color = Color(0xFF3D4E66),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun EmptyDashboardCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = DashboardCard),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = DashboardMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DailyTipCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2D836B),
                        Color(0xFF1A4D43),
                        Color(0xFF0C1E22),
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.14f),
                radius = size.minDimension * 0.35f,
                center = Offset(size.width * 0.60f, size.height * 0.12f),
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.20f),
                radius = size.minDimension * 0.50f,
                center = Offset(size.width * 0.72f, size.height * 0.82f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DashboardPrimary)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "DAILY TIP",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "The Benefits of Morning\nSunlight in Rural Areas",
                color = Color.White,
                fontSize = 32.sp / 1.75f,
                lineHeight = 40.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Exposure to natural light early in the day can help regulate your circadian rhythm and...",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = "Read Article  ->",
                color = Color.White,
                fontSize = 24.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onConsultsClick: () -> Unit,
    onAshaAiClick: () -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Home",
                selected = true,
                onClick = {},
            )
            BottomNavItem(
                icon = Icons.Filled.MedicalServices,
                label = "Consults",
                selected = false,
                onClick = onConsultsClick,
            )

            Spacer(modifier = Modifier.width(56.dp))

            BottomNavItem(
                icon = Icons.Outlined.Forum,
                label = "Chat",
                selected = false,
                onClick = onChatClick,
            )
            BottomNavItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                selected = false,
                onClick = onProfileClick,
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
                .clickable(onClick = onAshaAiClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(DashboardPrimary, DashboardPrimaryDark))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) DashboardPrimary else Color(0xFF94A3B8)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun parseDate(rawValue: String): Date? {
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
            // Try next pattern.
        }
    }

    return null
}

private fun formatDashboardDateTime(rawValue: String): String {
    val parsed = parseDate(rawValue) ?: return rawValue
    return SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(parsed)
}

private fun formatIssuedDate(rawValue: String): String {
    val parsed = parseDate(rawValue) ?: return rawValue
    return SimpleDateFormat("MMM dd", Locale.US).format(parsed)
}

private fun decodeDashboardPhoto(value: String?): ImageBitmap? {
    if (value.isNullOrBlank()) return null
    val base64Payload = value.substringAfter("base64,", value)

    return try {
        val bytes = Base64.decode(base64Payload, Base64.DEFAULT)
        if (bytes.isEmpty()) {
            null
        } else {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    } catch (_: Exception) {
        null
    }
}
