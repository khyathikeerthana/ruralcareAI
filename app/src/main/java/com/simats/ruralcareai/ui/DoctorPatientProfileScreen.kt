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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoctorPatientProfileScreen(
    patientName: String,
    patientLocation: String,
    patientStatus: String,
    patientIdLabel: String,
    patientAgeGender: String = "45M",
    bloodType: String = "B+",
    allergyLabel: String = "None",
    primaryReason: String = "No active complaint",
    recordsCount: Int = 0,
    prescriptionsCount: Int = 0,
    onBack: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onAddPrescription: () -> Unit = {},
    onSavePatientUpdate: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val statusColors = when (patientStatus) {
        "URGENT" -> Pair(Color(0xFFF6D6D2), Color(0xFFC62828))
        "STABLE" -> Pair(Color(0xFFCDE3F7), Color(0xFF355A77))
        "NEW" -> Pair(Color(0xFFCDE3F7), Color(0xFF006EA4))
        else -> Pair(Color(0xFFE2E6EA), Color(0xFF6F7881))
    }

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
                    text = "Patient Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DoctorDashboardText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF5EA7BA)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Patient",
                                    tint = Color(0xFFE6F3F8),
                                    modifier = Modifier.size(52.dp),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(2.dp, Color.White, CircleShape),
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = patientName,
                                fontSize = 34.sp / 2,
                                fontWeight = FontWeight.ExtraBold,
                                color = DoctorDashboardText,
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = patientAgeGender,
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFE5E7EB))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                            )
                        }

                        Text(
                            text = "ID: $patientIdLabel  •  Last Visit: 2d ago",
                            fontSize = 13.sp,
                            color = DoctorDashboardMuted,
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TinyStatChip(label = "BLOOD", value = bloodType, valueColor = Color(0xFFC62828))
                            TinyStatChip(label = "ALLERGY", value = allergyLabel, valueColor = Color(0xFF9A5C00))
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.HealthAndSafety,
                                        contentDescription = null,
                                        tint = DoctorDashboardPrimary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = "Chief\nComplaint",
                                        fontSize = 18.sp / 1.4f,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DoctorDashboardText,
                                    )
                                }
                            }

                            Text(
                                text = "Latest clinical note",
                                color = DoctorDashboardText,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = primaryReason,
                                color = DoctorDashboardPrimary,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Review and update this summary after each consultation.",
                                color = DoctorDashboardText,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
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
                        Text(
                            text = "Recent Vitals",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp / 1.4f,
                            color = DoctorDashboardText,
                        )
                        Text(
                            text = "Measured 10:45 AM",
                            color = DoctorDashboardMuted,
                            fontSize = 12.sp,
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        VitalsCard(label = "TEMP", value = "99.4", unit = "°F", modifier = Modifier.weight(1f))
                        VitalsCard(
                            label = "BP (HIGH)",
                            value = "145/92",
                            unit = "",
                            modifier = Modifier.weight(1f),
                            valueColor = Color(0xFFC62828),
                            containerColor = Color(0xFFFDF1F1),
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        VitalsCard(label = "SPO2", value = "98", unit = "%", modifier = Modifier.weight(1f))
                        VitalsCard(label = "WEIGHT", value = "78.2", unit = "kg", modifier = Modifier.weight(1f))
                    }
                }

                item {
                    Text(
                        text = "Medical History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DoctorDashboardText,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HistoryChip("Type 2 Diabetes")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HistoryChip("Hypertension")
                        HistoryChip("Dust Allergy")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HistoryChip("+ Add Condition", muted = true)
                }

                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Clinical Records",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DoctorDashboardText,
                                )

                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(DoctorDashboardPrimaryContainer)
                                        .clickable { onAddPrescription() },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Prescription",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }

                            ClinicalRow(title = "Past Prescriptions", subtitle = "$prescriptionsCount records available")
                            ClinicalRow(title = "Lab Reports", subtitle = "$recordsCount records available")
                            ClinicalRow(title = "Vaccination Record", subtitle = "Completed (2023)")

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, Color(0xFFB6C2CF), RoundedCornerShape(14.dp))
                                    .clickable { },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Upload New Document",
                                    color = Color(0xFF586472),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 13.dp),
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE5EE)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "EMERGENCY CONTACT",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF004D84),
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "Priya Singh (Wife)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DoctorDashboardText,
                            )
                            Text(
                                text = "+91 98765 43210",
                                color = Color(0xFF004D84),
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onSavePatientUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DoctorDashboardPrimaryContainer,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Save Patient Update",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp / 1.4f,
            )
        }
    }
}

@Composable
private fun TinyStatChip(label: String, value: String, valueColor: Color) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = label, color = Color(0xFF6B7280), fontSize = 10.sp, letterSpacing = 1.sp)
            Text(text = value, color = valueColor, fontSize = 28.sp / 2, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun VitalsCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    valueColor: Color = DoctorDashboardText,
    containerColor: Color = Color(0xFFF1F3F7),
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = label, color = DoctorDashboardMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, color = valueColor, fontSize = 28.sp / 1.5f, fontWeight = FontWeight.ExtraBold)
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = unit, color = DoctorDashboardMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun HistoryChip(text: String, muted: Boolean = false) {
    val background = if (muted) Color(0xFFE4E7EB) else Color(0xFFD5E8F9)
    val foreground = if (muted) Color(0xFF6B7280) else Color(0xFF255C82)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text = text, color = foreground, fontSize = 14.sp)
    }
}

@Composable
private fun ClinicalRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = DoctorDashboardText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = subtitle, color = DoctorDashboardMuted, fontSize = 12.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = DoctorDashboardMuted,
        )
    }
}
