package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AppointmentDto
import com.simats.ruralcareai.network.AppointmentsResult
import com.simats.ruralcareai.network.DashboardApiClient
import com.simats.ruralcareai.viewmodel.AppUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MABackground = Color(0xFFF3F6FB)
private val MACardBg = Color.White
private val MAText = Color(0xFF0F1730)
private val MAMuted = Color(0xFF5A6A82)
private val MAPrimary = Color(0xFF1F9BE6)
private val MAOutline = Color(0xFFE0E9F2)
private val MASuccess = Color(0xFF4CAF50)
private val MAError = Color(0xFFEF4A4A)

@Composable
fun MyAppointmentsScreen(
    uiState: AppUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val patientId = uiState.currentPatientId
    val appointments = remember { mutableStateOf<List<AppointmentDto>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val selectedTab = remember { mutableStateOf("Upcoming") }

    LaunchedEffect(patientId) {
        if (patientId == null) {
            appointments.value = emptyList()
            isLoading.value = false
            errorMessage.value = null
            return@LaunchedEffect
        }

        when (val result = DashboardApiClient.getAppointmentsByPatient(patientId)) {
            is AppointmentsResult.Success -> {
                appointments.value = result.appointments
                isLoading.value = false
            }
            is AppointmentsResult.Error -> {
                errorMessage.value = result.message
                isLoading.value = false
            }
        }
    }

    val upcomingAppointments = appointments.value.filter {
        it.status == "scheduled" || it.status == "rescheduled"
    }
    val pastAppointments = appointments.value.filter {
        it.status != "scheduled" && it.status != "rescheduled"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MABackground)
            .statusBarsPadding(),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MAText,
                )
            }
            Text(
                text = "My Appointments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MAText,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TabButton(
                label = "Upcoming",
                isSelected = selectedTab.value == "Upcoming",
                onClick = { selectedTab.value = "Upcoming" },
            )
            TabButton(
                label = "Past",
                isSelected = selectedTab.value == "Past",
                onClick = { selectedTab.value = "Past" },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading appointments...", color = MAMuted)
            }
        } else if (errorMessage.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Error: ${errorMessage.value}", color = MAError)
            }
        } else {
            val displayAppointments = if (selectedTab.value == "Upcoming") upcomingAppointments else pastAppointments

            if (displayAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No ${selectedTab.value.lowercase()} appointments",
                        color = MAMuted,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(displayAppointments) { appointment ->
                        AppointmentCard(appointment)
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MAPrimary else Color.Transparent,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MAMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun AppointmentCard(appointment: AppointmentDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MACardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Doctor info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Dr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MAPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctorName?.takeIf { it.isNotBlank() } ?: "Doctor #${appointment.doctorId}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MAText,
                    )
                    Text(
                        text = appointment.specialization ?: "Specialist",
                        fontSize = 12.sp,
                        color = MAMuted,
                    )
                }
                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (appointment.status) {
                                "completed" -> Color(0xFFE8F5E9)
                                "cancelled" -> Color(0xFFFFEBEE)
                                else -> Color(0xFFFFF9C4)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = appointment.status.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (appointment.status) {
                            "completed" -> MASuccess
                            "cancelled" -> MAError
                            else -> Color(0xFFF57F17)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date and time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F7FA))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Date",
                    tint = MAMuted,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = formatDateForDisplay(appointment.scheduledAt),
                    fontSize = 13.sp,
                    color = MAText,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = "Time",
                    tint = MAMuted,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = formatTimeForDisplay(appointment.scheduledAt),
                    fontSize = 13.sp,
                    color = MAText,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Consultation mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F7FA))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Consultation: ",
                    fontSize = 13.sp,
                    color = MAMuted,
                )
                Text(
                    text = appointment.consultationMode.uppercase(),
                    fontSize = 13.sp,
                    color = MAPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (appointment.status == "scheduled") {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MAPrimary),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Reschedule", fontSize = 13.sp)
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Cancel", fontSize = 13.sp, color = MAError)
                    }
                }
            } else if (appointment.status == "completed") {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MAPrimary),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Download Summary", fontSize = 13.sp)
                }
            }
        }
    }
}

private fun formatDateForDisplay(dateTimeString: String): String {
    val date = parseDate(dateTimeString) ?: return dateTimeString
    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
}

private fun formatTimeForDisplay(dateTimeString: String): String {
    val date = parseDate(dateTimeString) ?: return dateTimeString
    return SimpleDateFormat("hh:mm a", Locale.US).format(date)
}

private fun parseDate(dateTimeString: String): Date? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
    )

    for (pattern in patterns) {
        try {
            val inputFormat = SimpleDateFormat(pattern, Locale.US)
            val date = inputFormat.parse(dateTimeString)
            if (date != null) {
                return date
            }
        } catch (_: Exception) {
            // Try next supported format.
        }
    }

    return null
}
