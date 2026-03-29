package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorAnalyticsScreen(
    consultationsDone: Int = 42,
    avgConsultationMinutes: Double = 14.0,
    waitingNow: Int = 4,
    doneToday: Int = 8,
    commonConditions: List<Pair<String, Int>> = listOf(
        "Fever & Flu" to 48,
        "Seasonal Cough" to 32,
        "Dust Allergy" to 15,
        "Others" to 5,
    ),
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Analytics") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = "Analytics",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "OVERVIEW",
                        color = DoctorDashboardPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    )
                    Text(
                        text = "Daily Performance",
                        color = DoctorDashboardText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFE8EDF3))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(
                            text = "Last 24h",
                            color = DoctorDashboardMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                StatInfoCard(
                    icon = Icons.Filled.QueryStats,
                    title = "Consultations Done",
                    value = consultationsDone.toString(),
                    trailing = "",
                    footnote = "Done today: $doneToday",
                )
            }

            item {
                StatInfoCard(
                    icon = Icons.Filled.Timer,
                    title = "Avg Consultation Time",
                    value = avgConsultationMinutes.toString(),
                    trailing = "mins",
                    footnote = "Waiting now: $waitingNow",
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Patient Trends",
                                color = DoctorDashboardText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "● Volume",
                                color = DoctorDashboardPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFE8EDF3)),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                                Text(
                                    text = day,
                                    color = DoctorDashboardMuted,
                                    fontSize = 9.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Common Conditions",
                            color = DoctorDashboardText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        val total = commonConditions.sumOf { it.second }.coerceAtLeast(1)
                        commonConditions.forEach { (label, count) ->
                            val percentage = ((count.toDouble() / total.toDouble()) * 100.0).toInt().coerceIn(1, 100)
                            ConditionRow(label = label, percentage = percentage)
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.93f)),
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
                            onOpenHome()
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Dashboard,
                        label = "Appointments",
                        isSelected = currentNavigation == "Appointments",
                        onClick = {
                            currentNavigation = "Appointments"
                            onOpenQueue()
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
private fun StatInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    trailing: String,
    footnote: String,
    showRatingBars: Boolean = false,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DoctorDashboardPrimary,
                modifier = Modifier.size(16.dp),
            )

            Text(
                text = title,
                color = DoctorDashboardMuted,
                fontSize = 13.sp,
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = DoctorDashboardText,
                    fontSize = 44.sp / 2,
                    fontWeight = FontWeight.ExtraBold,
                )
                if (trailing.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trailing,
                        color = DoctorDashboardMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
            }

            if (showRatingBars) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(52.dp, 32.dp, 32.dp, 32.dp, 32.dp).forEachIndexed { index, width ->
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(width)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (index < 4) Color(0xFFC98712) else Color(0xFFB9C2CC)),
                        )
                    }
                }
            }

            if (footnote.isNotBlank()) {
                Text(
                    text = footnote,
                    color = if (footnote.contains("+")) DoctorDashboardPrimary else DoctorDashboardMuted,
                    fontSize = 12.sp,
                    fontWeight = if (footnote.contains("+")) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun ConditionRow(label: String, percentage: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = DoctorDashboardText,
                fontSize = 13.sp,
            )
            Text(
                text = "$percentage%",
                color = DoctorDashboardMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFD5DBE3)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (percentage > 20) DoctorDashboardPrimary else Color(0xFFB5C3D2)),
            )
        }
    }
}
