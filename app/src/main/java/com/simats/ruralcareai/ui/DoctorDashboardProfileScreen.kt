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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
fun DoctorDashboardProfileScreen(
    doctorName: String = "Dr. Harpreet Singh",
    doctorSpecialty: String = "Senior Cardiologist",
    experienceLabel: String = "12+\nYears",
    ratingLabel: String = "4.9",
    patientsLabel: String = "2,500+",
    clinicName: String = "Rural Heart Center",
    clinicAddress: String = "Plot 45, Community Road, Ludhiana, Punjab",
    clinicHours: String = "Mon - Fri: 09:00 AM - 06:00 PM\nSat: 09:00 AM - 01:00 PM",
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenEditProfile: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenHelp: () -> Unit = {},
    onOpenLanguage: () -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Profile") }

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
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Doctor Profile",
                    color = DoctorDashboardText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "Edit",
                    color = DoctorDashboardPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenEditProfile() },
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 118.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF195D86), Color(0xFF6EAED0)),
                                        )
                                    )
                                    .border(3.dp, Color(0xFF1A87C4), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "HS",
                                    color = Color.White,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(2.dp, Color.White, CircleShape),
                            )
                        }

                        Text(
                            text = doctorName,
                            color = DoctorDashboardText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )

                        Text(
                            text = doctorSpecialty,
                            color = DoctorDashboardPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ProfileMetricCard(
                            icon = Icons.Filled.AccessTime,
                            label = "EXPERIENCE",
                            value = experienceLabel,
                            modifier = Modifier.weight(1f),
                        )
                        ProfileMetricCard(
                            icon = Icons.Filled.Star,
                            label = "RATING",
                            value = ratingLabel,
                            modifier = Modifier.weight(1f),
                            iconTint = Color(0xFFC98712),
                        )
                        ProfileMetricCard(
                            icon = Icons.Filled.Group,
                            label = "PATIENTS",
                            value = patientsLabel,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF3F8)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCFE6F8)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = null,
                                        tint = DoctorDashboardPrimary,
                                    )
                                }
                                Text(
                                    text = "Clinic Details",
                                    color = DoctorDashboardText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = DoctorDashboardMuted,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = clinicName,
                                        color = DoctorDashboardText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = clinicAddress,
                                        color = DoctorDashboardMuted,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = DoctorDashboardMuted,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Operating Hours",
                                        color = DoctorDashboardText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = clinicHours,
                                        color = DoctorDashboardMuted,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Text(
                                text = "Settings",
                                color = DoctorDashboardText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            )

                            SettingRow(icon = Icons.Filled.Security, label = "Security", onClick = onOpenSecurity)
                            SettingRow(icon = Icons.Filled.Language, label = "Language", value = "English", onClick = onOpenLanguage)
                            SettingRow(icon = Icons.AutoMirrored.Filled.Help, label = "Help & Support", onClick = onOpenHelp)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable { onSignOut() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFD81E1E),
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Logout",
                            color = Color(0xFFD81E1E),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
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
                            onOpenAnalytics()
                        },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Person,
                        label = "Profile",
                        isSelected = currentNavigation == "Profile",
                        onClick = {
                            currentNavigation = "Profile"
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = DoctorDashboardPrimary,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                color = DoctorDashboardMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = DoctorDashboardText,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String = "",
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DoctorDashboardMuted,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                color = DoctorDashboardText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    color = DoctorDashboardMuted,
                    fontSize = 14.sp,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFAAB4C2),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
