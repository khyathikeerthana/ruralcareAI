package com.simats.ruralcareai.ui

import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal val DoctorDashboardBackground = Color(0xFFF7F9FC)
internal val DoctorDashboardCard = Color.White
internal val DoctorDashboardText = Color(0xFF191C1E)
internal val DoctorDashboardMuted = Color(0xFF3F4850)
internal val DoctorDashboardPrimary = Color(0xFF006492)
internal val DoctorDashboardPrimaryContainer = Color(0xFF2D9CDB)
internal val DoctorDashboardPrimaryFixed = Color(0xFF8CCDFF)
internal val DoctorDashboardSecondary = Color(0xFF41627A)
internal val DoctorDashboardSecondaryContainer = Color(0xFFBFE1FE)
internal val DoctorDashboardTertiary = Color(0xFF835400)
internal val DoctorDashboardErrorContainer = Color(0xFFFFE7E7)

data class DoctorConsultation(
    val id: Int,
    val patientName: String,
    val patientPhoto: String?,
    val location: String,
    val isUrgent: Boolean,
    val reason: String,
    val consultationType: String,
    val scheduledTime: String,
)

data class PatientInQueue(
    val id: Int,
    val name: String,
    val age: Int,
    val gender: String,
    val location: String,
    val photo: String?,
    val scheduledTime: String,
    val status: String,
    val isVideoConsultation: Boolean,
    val primaryActionLabel: String,
    val secondaryActionLabel: String? = null,
)

data class DoctorStats(
    val totalAppointments: Int,
    val waitingAppointments: Int,
    val completedAppointments: Int,
)

@Composable
fun DoctorDashboardScreen(
    doctorName: String = "Dr. Sarah Johnson",
    doctorPhoto: String? = null,
    stats: DoctorStats = DoctorStats(
        totalAppointments = 14,
        waitingAppointments = 4,
        completedAppointments = 8,
    ),
    queuePatients: List<PatientInQueue> = listOf(
        PatientInQueue(
            id = 2,
            name = "Jaspreet Kaur",
            age = 42,
            gender = "Female",
            location = "Rohti Chhapara Village",
            photo = null,
            scheduledTime = "10:15 AM",
            status = "In-Progress",
            isVideoConsultation = true,
            primaryActionLabel = "Resume",
            secondaryActionLabel = "Prescription",
        ),
        PatientInQueue(
            id = 3,
            name = "Amit Sharma",
            age = 28,
            gender = "Male",
            location = "Nabha Block",
            photo = null,
            scheduledTime = "11:00 AM",
            status = "Waiting",
            isVideoConsultation = false,
            primaryActionLabel = "Start Clinic Visit",
            secondaryActionLabel = "Notify",
        ),
    ),
    onOpenChat: (Int) -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenSchedule: () -> Unit = {},
    onPrimaryQueueAction: (Int) -> Unit = {},
    onSecondaryQueueAction: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Appointments") }

    val todayLabel = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
    }
    var selectedQueueFilter by remember { mutableStateOf("All") }

    val filteredQueuePatients = remember(selectedQueueFilter, queuePatients) {
        when (selectedQueueFilter) {
            "Video" -> queuePatients.filter { it.isVideoConsultation }
            "Clinic" -> queuePatients.filterNot { it.isVideoConsultation }
            "Waiting" -> queuePatients.filter { it.status == "Waiting" }
            else -> queuePatients
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 36.dp, end = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Clinical Sanctuary",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DoctorDashboardPrimaryContainer,
                    )
                    Text(
                        text = doctorName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DoctorDashboardMuted.copy(alpha = 0.7f),
                    )
                }

            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Daily Appointments",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DoctorDashboardText,
                        )
                        Text(
                            text = "$todayLabel • Rural Health Center",
                            fontSize = 12.sp,
                            color = DoctorDashboardMuted.copy(alpha = 0.72f),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(999.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAEFF4)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf("All", "Video", "Clinic", "Waiting").forEach { filter ->
                                val selected = selectedQueueFilter == filter
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(if (selected) Color.White else Color.Transparent)
                                        .clickable { selectedQueueFilter = filter }
                                        .padding(vertical = 11.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = filter,
                                        color = if (selected) DoctorDashboardPrimary else DoctorDashboardMuted,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatCard(
                            label = "TOTAL",
                            value = stats.totalAppointments.toString(),
                            backgroundColor = DoctorDashboardCard,
                            valueColor = DoctorDashboardPrimary,
                            modifier = Modifier.weight(1f),
                            textSize = 13.sp,
                            valueSize = 28.sp,
                        )
                        StatCard(
                            label = "WAITING",
                            value = stats.waitingAppointments.toString(),
                            backgroundColor = DoctorDashboardCard,
                            valueColor = DoctorDashboardTertiary,
                            modifier = Modifier.weight(1f),
                            textSize = 13.sp,
                            valueSize = 28.sp,
                        )
                        StatCard(
                            label = "DONE",
                            value = stats.completedAppointments.toString(),
                            backgroundColor = DoctorDashboardCard,
                            valueColor = DoctorDashboardSecondary,
                            modifier = Modifier.weight(1f),
                            textSize = 13.sp,
                            valueSize = 28.sp,
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Upcoming Sessions",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoctorDashboardText,
                        )
                        Text(
                            text = "View Timeline",
                            fontSize = 12.sp,
                            color = DoctorDashboardPrimary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                if (filteredQueuePatients.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "No appointments in this category.",
                                color = DoctorDashboardMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(18.dp),
                            )
                        }
                    }
                }

                items(filteredQueuePatients) { patient ->
                    PatientQueueItem(
                        patient = patient,
                        onPrimaryAction = {
                            onPrimaryQueueAction(patient.id)
                        },
                        onSecondaryAction = {
                            onSecondaryQueueAction(patient.id)
                        },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 126.dp)
                .size(58.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DoctorDashboardPrimaryContainer,
                            DoctorDashboardPrimary,
                        )
                    )
                )
                .clickable { onOpenSchedule() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Appointment",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Home,
                        label = "Home",
                        isSelected = currentNavigation == "Home",
                        onClick = {
                            currentNavigation = "Home"
                            onOpenQueue()
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Dashboard,
                        label = "Appointments",
                        isSelected = currentNavigation == "Appointments",
                        onClick = {
                            currentNavigation = "Appointments"
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Group,
                        label = "Patients",
                        isSelected = currentNavigation == "Patients",
                        onClick = {
                            currentNavigation = "Patients"
                            onOpenPatients()
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.BarChart,
                        label = "Analytics",
                        isSelected = currentNavigation == "Analytics",
                        onClick = {
                            currentNavigation = "Analytics"
                            onOpenAnalytics()
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Person,
                        label = "Profile",
                        isSelected = currentNavigation == "Profile",
                        onClick = {
                            currentNavigation = "Profile"
                            onOpenProfile()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    backgroundColor: Color,
    valueColor: Color = DoctorDashboardText,
    textSize: TextUnit = 12.sp,
    valueSize: TextUnit = 32.sp,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(106.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                fontSize = textSize,
                fontWeight = FontWeight.Medium,
                color = DoctorDashboardMuted,
            )
            Text(
                text = value,
                fontSize = valueSize,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
        }
    }
}

@Composable
private fun PatientQueueItem(
    patient: PatientInQueue,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    val statusBackground = when (patient.status) {
        "In-Progress" -> Color(0xFFE5F0FB)
        "Waiting" -> Color(0xFFFFEDCC)
        else -> Color(0xFFE4E7EB)
    }
    val statusColor = when (patient.status) {
        "In-Progress" -> DoctorDashboardPrimary
        "Waiting" -> DoctorDashboardTertiary
        else -> Color(0xFF6F7881)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCDEAF6)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Patient",
                        tint = DoctorDashboardPrimary,
                        modifier = Modifier.size(28.dp),
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (patient.isVideoConsultation) DoctorDashboardPrimary else DoctorDashboardSecondary)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (patient.isVideoConsultation) Icons.AutoMirrored.Filled.Chat else Icons.Filled.Group,
                            contentDescription = "Consultation Type",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DoctorDashboardText,
                    )
                    Text(
                        text = "${patient.age}y • ${patient.gender}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DoctorDashboardMuted.copy(alpha = 0.75f),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            tint = DoctorDashboardMuted.copy(alpha = 0.65f),
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = patient.location,
                            fontSize = 10.sp,
                            color = DoctorDashboardMuted.copy(alpha = 0.65f),
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = patient.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(statusBackground)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = patient.scheduledTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DoctorDashboardText,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (patient.status == "Confirmed") Color(0xFFE4E7EB) else DoctorDashboardPrimaryContainer)
                        .clickable { onPrimaryAction() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = patient.primaryActionLabel,
                        fontSize = 13.sp,
                        color = if (patient.status == "Confirmed") DoctorDashboardMuted else Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                patient.secondaryActionLabel?.let { secondaryLabel ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFE6E8EB))
                            .clickable { onSecondaryAction() }
                            .padding(horizontal = 18.dp, vertical = 11.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = secondaryLabel,
                            fontSize = 13.sp,
                            color = DoctorDashboardText,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DoctorBottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    color = if (isSelected) Color(0xFFE5F0FB) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) DoctorDashboardPrimary else Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) DoctorDashboardPrimary else Color(0xFF9CA3AF),
        )
    }
}
