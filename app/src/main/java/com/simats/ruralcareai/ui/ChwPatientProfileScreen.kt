package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AdminPatientDto

private val PatientProfileBg = Color(0xFFF2F4F8)
private val PatientProfileSurface = Color(0xFFFDFEFF)
private val PatientProfileText = Color(0xFF121820)
private val PatientProfileMuted = Color(0xFF6D7886)
private val PatientProfilePrimary = Color(0xFF0D6796)

@Composable
fun ChwPatientProfileScreen(
    patient: AdminPatientDto,
    onBack: () -> Unit,
    onAddClinicalNote: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PatientProfileBg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 14.dp,
                bottom = 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PatientProfilePrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onBack),
                    )
                    Text(
                        text = "Patient Profile",
                        color = PatientProfileText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC1E7E6)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF3E627C),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD6E5EE)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Patient",
                            tint = Color(0xFF3E627C),
                            modifier = Modifier.size(64.dp),
                        )
                    }

                    Text(
                        text = patient.fullName,
                        color = PatientProfileText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE5F0F9))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "Age: ${patient.age ?: "N/A"}",
                                color = PatientProfilePrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE9ECF1))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "ID: #${patient.id}",
                                color = PatientProfileMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PatientProfileSurface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Contact Info",
                            color = PatientProfileText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Address",
                                tint = PatientProfilePrimary,
                                modifier = Modifier.size(22.dp),
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = patient.village ?: "N/A",
                                    color = PatientProfileText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "Village",
                                    color = PatientProfileMuted,
                                    fontSize = 12.sp,
                                )
                            }
                        }

                        if (!patient.phone.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Phone",
                                    tint = PatientProfilePrimary,
                                    modifier = Modifier.size(22.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = patient.phone,
                                        color = PatientProfileText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "Phone",
                                        color = PatientProfileMuted,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PatientProfilePrimary,
                            ),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    "Contact Family",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PatientProfileSurface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Current Vitals",
                                color = PatientProfileText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "LAST 24H",
                                color = PatientProfileMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            VitalCard(
                                label = "BLOOD PRESSURE",
                                value = "128/84",
                                labelColor = Color(0xFFB8860B),
                                modifier = Modifier.weight(1f),
                            )
                            VitalCard(
                                label = "HEART RATE",
                                value = "92 bpm",
                                labelColor = Color(0xFFDC143C),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            VitalCard(
                                label = "SPO2",
                                value = "98%",
                                labelColor = Color(0xFF0D6796),
                                modifier = Modifier.weight(1f),
                            )
                            VitalCard(
                                label = "TEMP",
                                value = "36.7°C",
                                labelColor = Color(0xFF999999),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PatientProfileSurface),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Recent Visit History",
                                color = PatientProfileText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "View All",
                                color = PatientProfilePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            VisitHistoryItem(
                                icon = Icons.Filled.MedicalServices,
                                title = "Routine Vaccination",
                                description = "Completed Tdap booster and annual flu shot. Patient reported minor soreness at injection site.",
                                date = "OCT 14, 2023",
                                iconBg = Color(0xFFB8860B),
                            )

                            VisitHistoryItem(
                                icon = Icons.Filled.MedicalServices,
                                title = "Hypertension Review",
                                description = "Adjusted dosage of Amlodipine. Recommended reduced sodium intake. Next checkup in 2 weeks.",
                                date = "SEP 28, 2023",
                                iconBg = Color(0xFF8B4513),
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddClinicalNote),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PatientProfileSurface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5F0F9)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                tint = PatientProfilePrimary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Text(
                            text = "Add Clinical Note",
                            color = PatientProfileText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        FloatingBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onNavigate = { },
        )
    }
}

@Composable
private fun VitalCard(
    label: String,
    value: String,
    labelColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (labelColor) {
                Color(0xFFB8860B) -> Color(0xFFFFF0DB)
                Color(0xFFDC143C) -> Color(0xFFFFE8E8)
                Color(0xFF0D6796) -> Color(0xFFE5F0F9)
                else -> Color(0xFFF5F5F5)
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                color = labelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
            )
            Text(
                text = value,
                color = PatientProfileText,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun VisitHistoryItem(
    icon: ImageVector,
    title: String,
    description: String,
    date: String,
    iconBg: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconBg,
                modifier = Modifier.size(20.dp),
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
                    text = title,
                    color = PatientProfileText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = date,
                    color = PatientProfileMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Text(
                text = description,
                color = PatientProfileMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun FloatingBottomNavigation(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color.White.copy(alpha = 0.97f))
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(
                icon = Icons.Filled.Person,
                label = "PATIENTS",
                selected = true,
            )
            NavItem(
                icon = Icons.Filled.PersonAdd,
                label = "ADD",
                selected = false,
            )
            NavItem(
                icon = Icons.Filled.MedicalServices,
                label = "VITALS",
                selected = false,
            )
            NavItem(
                icon = Icons.Filled.Person,
                label = "SETTINGS",
                selected = false,
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFE0F1FF) else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PatientProfilePrimary else Color(0xFF94A1B4),
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            color = if (selected) PatientProfilePrimary else Color(0xFF94A1B4),
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
