package com.simats.ruralcareai.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class WaitingPatient(
    val patientId: Int,
    val name: String,
    val time: String,
    val status: String,
    val action: String,
)

@Composable
fun DoctorWaitingQueueScreen(
    waitingCount: Int = 0,
    avgWaitMinutes: Int = 0,
    queueDateLabel: String = "Today",
    waitingPatients: List<WaitingPatient> = emptyList(),
    onOpenPatient: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DoctorDashboardPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onBack() },
                )
                Text(
                    text = "Daily Appointments",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DoctorDashboardText,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(text = "OVERVIEW", color = DoctorDashboardPrimary, fontSize = 14.sp, letterSpacing = 1.sp)
                    Text(
                        text = "Waiting Queue",
                        color = DoctorDashboardText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(text = "Currently Waiting", color = DoctorDashboardText, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = waitingCount.toString(), fontSize = 58.sp / 1.6f, fontWeight = FontWeight.ExtraBold, color = DoctorDashboardText)
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(text = "Patients", fontSize = 16.sp, color = DoctorDashboardText)
                                }
                            }
                            Icon(imageVector = Icons.Filled.Group, contentDescription = null, tint = DoctorDashboardPrimary, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(text = "Avg. Wait Time", color = DoctorDashboardText, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = avgWaitMinutes.toString(), fontSize = 58.sp / 1.6f, fontWeight = FontWeight.ExtraBold, color = DoctorDashboardText)
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text(text = "mins", fontSize = 16.sp, color = DoctorDashboardText)
                                }
                            }
                            Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null, tint = DoctorDashboardTertiary, modifier = Modifier.size(24.dp))
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Queue Details", color = DoctorDashboardText, fontSize = 20.sp / 1.2f, fontWeight = FontWeight.Bold)
                        Text(text = queueDateLabel, color = DoctorDashboardMuted, fontSize = 16.sp / 1.2f)
                    }
                }

                items(waitingPatients) { patient ->
                    WaitingPatientCard(patient = patient, onOpenPatient = onOpenPatient)
                }
            }
        }
    }
}

@Composable
private fun WaitingPatientCard(patient: WaitingPatient, onOpenPatient: (Int) -> Unit) {
    val isInLobby = patient.status == "IN LOBBY"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E6EE)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = DoctorDashboardPrimary, modifier = Modifier.size(30.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF21C062))
                            .border(2.dp, Color.White, CircleShape),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = patient.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DoctorDashboardText)
                    Text(text = "Scheduled: ${patient.time}", fontSize = 14.sp, color = DoctorDashboardMuted)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = patient.status,
                    color = if (isInLobby) Color(0xFF31607F) else Color(0xFF5E6670),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isInLobby) Color(0xFFD6E7F7) else Color(0xFFE7EBEF))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isInLobby) DoctorDashboardPrimaryContainer else Color(0xFFF0F3F7))
                        .clickable { onOpenPatient(patient.patientId) }
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = patient.action,
                            color = if (isInLobby) Color.White else Color(0xFF98A4B3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                        if (isInLobby) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
