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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CompletedPatient(
    val patientId: Int,
    val name: String,
    val time: String,
)

@Composable
fun DoctorCompletedSessionsScreen(
    completedCount: Int = 0,
    completedDateLabel: String = "Today",
    completedPatients: List<CompletedPatient> = emptyList(),
    onOpenPatient: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
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
                        text = "Completed Sessions",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DoctorDashboardText,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF3BA1D9), Color(0xFF1E7EB0)),
                                )
                            )
                            .padding(20.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Completed Today", color = Color(0xFFD7ECF9), fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = completedCount.toString(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 58.sp / 1.6f)
                                Text(text = "Sessions", color = Color(0xFFD7ECF9), fontSize = 16.sp)
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.TaskAlt,
                            contentDescription = null,
                            tint = Color(0xFF85C2E4),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(36.dp),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Recent Patients", color = DoctorDashboardText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = completedDateLabel, color = Color(0xFF5E9FC7), letterSpacing = 1.sp, fontSize = 14.sp)
                }
            }

            items(completedPatients) { patient ->
                CompletedPatientCard(patient = patient, onOpenPatient = onOpenPatient)
            }
        }
    }
}

@Composable
private fun CompletedPatientCard(patient: CompletedPatient, onOpenPatient: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E6EE)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = DoctorDashboardPrimary, modifier = Modifier.size(28.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF21C062)),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = patient.name, color = DoctorDashboardText, fontWeight = FontWeight.Bold, fontSize = 18.sp / 1.2f)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null, tint = DoctorDashboardMuted, modifier = Modifier.size(14.dp))
                    Text(text = patient.time, color = DoctorDashboardText, fontSize = 14.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "COMPLETED",
                    color = Color(0xFF117A32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFD4F3DE))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "View",
                        color = DoctorDashboardPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onOpenPatient(patient.patientId) },
                    )
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DoctorDashboardMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
