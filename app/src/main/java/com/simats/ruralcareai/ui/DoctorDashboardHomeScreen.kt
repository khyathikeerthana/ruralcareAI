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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class HomeQueuePatient(
    val id: Int,
    val name: String,
    val scheduledTime: String,
    val status: String,
)

@Composable
fun DoctorDashboardHomeScreen(
    doctorName: String = "Dr. Sarah Johnson",
    doctorPhoto: String? = null,
    totalAppointments: Int = 14,
    waitingAppointments: Int = 4,
    nextPatientId: Int? = null,
    nextPatientName: String = "Jaspreet Kaur",
    nextPatientTime: String = "10:15 AM",
    nextPatientLocation: String = "Rohti Chhapara Village",
    queuePreview: List<HomeQueuePatient> = listOf(
        HomeQueuePatient(id = 1, name = "Kulwinder Kaur", scheduledTime = "11:30 AM", status = "In Lobby"),
        HomeQueuePatient(id = 2, name = "Aaliyah Smith", scheduledTime = "11:45 AM", status = "Waiting"),
        HomeQueuePatient(id = 3, name = "Marcus Chen", scheduledTime = "12:15 PM", status = "Waiting"),
    ),
    onOpenQueue: () -> Unit = {},
    onOpenWaiting: () -> Unit = {},
    onOpenCompleted: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenChats: () -> Unit = {},
    onOpenChat: (Int) -> Unit = {},
    onOpenQueuePatient: (Int) -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    fun decodePhoto(photoBase64: String?): ImageBitmap? {
        return if (photoBase64 != null) {
            try {
                val decodedBytes = Base64.decode(photoBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                bitmap?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    val todayLabel = remember {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
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
                    .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Consultations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DoctorDashboardText,
                    )
                    Text(
                        text = doctorName,
                        fontSize = 11.sp,
                        color = DoctorDashboardMuted.copy(alpha = 0.75f),
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chats",
                    tint = DoctorDashboardMuted,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onOpenChats() },
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 124.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Today, $todayLabel",
                            fontSize = 12.sp,
                            color = DoctorDashboardMuted,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = "Dashboard Overview",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DoctorDashboardText,
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DashboardStatCard(
                            title = "Appointments",
                            value = totalAppointments.toString(),
                            containerColor = DoctorDashboardCard,
                            valueColor = DoctorDashboardText,
                            onClick = onOpenQueue,
                            modifier = Modifier.weight(1f),
                        )
                        DashboardStatCard(
                            title = "Waiting",
                            value = waitingAppointments.toString(),
                            containerColor = Color(0xFFD3E8F9),
                            valueColor = DoctorDashboardPrimary,
                            onClick = onOpenWaiting,
                            modifier = Modifier.weight(1f),
                        )
                        DashboardStatCard(
                            title = "Completed",
                            value = (totalAppointments - waitingAppointments).coerceAtLeast(0).toString(),
                            containerColor = DoctorDashboardCard,
                            valueColor = DoctorDashboardText,
                            onClick = onOpenCompleted,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "UP NEXT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoctorDashboardMuted,
                            letterSpacing = 0.5.sp,
                        )
                        Text(
                            text = nextPatientTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoctorDashboardPrimary,
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCDEAF6)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Patient",
                                        tint = DoctorDashboardPrimary,
                                        modifier = Modifier.size(26.dp),
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = nextPatientName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DoctorDashboardText,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = "Location",
                                            tint = DoctorDashboardMuted.copy(alpha = 0.65f),
                                            modifier = Modifier.size(12.dp),
                                        )
                                        Text(
                                            text = nextPatientLocation,
                                            fontSize = 13.sp,
                                            color = DoctorDashboardMuted.copy(alpha = 0.65f),
                                        )
                                    }
                                }

                                Text(
                                    text = "URGENT",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(DoctorDashboardPrimaryContainer)
                                    .clickable {
                                        if (nextPatientId != null) {
                                            onOpenChat(nextPatientId)
                                        } else {
                                            onOpenChats()
                                        }
                                    }
                                    .padding(vertical = 13.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Go to Chat",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "PATIENT QUEUE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DoctorDashboardMuted,
                            letterSpacing = 0.4.sp,
                        )
                        Text(
                            text = "View All",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DoctorDashboardPrimary,
                            modifier = Modifier.clickable { onOpenQueue() },
                        )
                    }
                }

                items(queuePreview) { patient ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1E8F9)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Patient",
                                    tint = DoctorDashboardPrimary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = patient.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DoctorDashboardText,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Scheduled for ${patient.scheduledTime}",
                                    fontSize = 13.sp,
                                    color = DoctorDashboardMuted,
                                )
                            }

                            Text(
                                text = patient.status,
                                fontSize = 12.sp,
                                color = if (patient.status == "In Lobby") DoctorDashboardText else DoctorDashboardMuted,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (patient.status == "In Lobby") Color(0xFFE8EBEF) else Color.Transparent)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                textAlign = TextAlign.Center,
                            )

                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More",
                                tint = DoctorDashboardMuted,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onOpenQueuePatient(patient.id) },
                            )
                        }
                    }
                }
            }
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
                        isSelected = true,
                        onClick = { },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Dashboard,
                        label = "Appointments",
                        isSelected = false,
                        onClick = onOpenQueue,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Group,
                        label = "Patients",
                        isSelected = false,
                        onClick = onOpenPatients,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.BarChart,
                        label = "Analytics",
                        isSelected = false,
                        onClick = onOpenAnalytics,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Person,
                        label = "Profile",
                        isSelected = false,
                        onClick = onOpenProfile,
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: String,
    containerColor: Color,
    valueColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DoctorDashboardMuted,
            )
            Text(
                text = value,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
        }
    }
}
