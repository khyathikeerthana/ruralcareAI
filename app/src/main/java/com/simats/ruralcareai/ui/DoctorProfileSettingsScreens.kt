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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
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
fun DoctorSecurityScreen(
    onBack: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Profile") }

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
                        text = "Security",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 14.dp),
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCEAF5)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = DoctorDashboardPrimary,
                            modifier = Modifier.size(34.dp),
                        )
                    }

                    Text(
                        text = "Your account is secure",
                        color = DoctorDashboardText,
                        fontSize = 34.sp / 2,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Last security check: Today, 08:42 AM",
                        color = DoctorDashboardMuted,
                        fontSize = 15.sp,
                    )
                }
            }

            item {
                Text(
                    text = "AUTHENTICATION",
                    color = DoctorDashboardText,
                    fontSize = 13.sp,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE2F1FB)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Security,
                                    contentDescription = null,
                                    tint = DoctorDashboardPrimary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Change Password",
                                    color = DoctorDashboardText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Last changed 3 months ago",
                                    color = DoctorDashboardMuted,
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFFAEB7C4),
                        )
                    }
                }
            }
        }

        DoctorSettingsBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentNavigation = currentNavigation,
            onOpenHome = {
                currentNavigation = "Home"
                onOpenHome()
            },
            onOpenQueue = {
                currentNavigation = "Appointments"
                onOpenQueue()
            },
            onOpenPatients = {
                currentNavigation = "Patients"
                onOpenPatients()
            },
            onOpenAnalytics = {
                currentNavigation = "Analytics"
                onOpenAnalytics()
            },
            onOpenProfile = {
                currentNavigation = "Profile"
                onOpenProfile()
            },
        )
    }
}

@Composable
fun DoctorHelpSupportScreen(
    onBack: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Profile") }

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
                        text = "Settings",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 14.dp),
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Help & Support",
                        color = DoctorDashboardText,
                        fontSize = 40.sp / 2,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "How can we assist your clinical practice today?",
                        color = DoctorDashboardMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFDEE3EA))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = "Search help topics...",
                        color = Color(0xFFB4BDC9),
                        fontSize = 16.sp,
                    )
                }
            }

            item {
                Text(
                    text = "QUICK CONTACT",
                    color = Color(0xFF8094AB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ContactCard(
                        icon = Icons.Filled.Phone,
                        title = "Call Support",
                        subtitle = "24/7 Clinical Line",
                        modifier = Modifier.weight(1f),
                    )
                    ContactCard(
                        icon = Icons.Filled.Mail,
                        title = "Email Us",
                        subtitle = "Reply in 2 hours",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Text(
                    text = "BROWSE TOPICS",
                    color = Color(0xFF8094AB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }

            item {
                TopicCard(title = "App Guide", subtitle = "Mastering the RuralCare interface")
            }
            item {
                TopicCard(title = "Consultation Tips", subtitle = "Optimizing rural patient interactions")
            }
            item {
                TopicCard(title = "Technical Issues", subtitle = "Troubleshooting connectivity and sync")
            }
        }

        DoctorSettingsBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentNavigation = currentNavigation,
            onOpenHome = {
                currentNavigation = "Home"
                onOpenHome()
            },
            onOpenQueue = {
                currentNavigation = "Appointments"
                onOpenQueue()
            },
            onOpenPatients = {
                currentNavigation = "Patients"
                onOpenPatients()
            },
            onOpenAnalytics = {
                currentNavigation = "Analytics"
                onOpenAnalytics()
            },
            onOpenProfile = {
                currentNavigation = "Profile"
                onOpenProfile()
            },
        )
    }
}

@Composable
fun DoctorLanguageSelectionScreen(
    onBack: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currentNavigation by remember { mutableStateOf("Profile") }

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
                        text = "Settings",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 14.dp),
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(124.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAF1F7)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(DoctorDashboardPrimary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(38.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color(0xFFC98712))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "COMING SOON",
                            color = Color(0xFF1E1E1E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }

                    Text(
                        text = "Language Selection",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "We are currently working on bringing more languages to RuralCareAI. Stay tuned for upcoming updates!",
                        color = DoctorDashboardMuted,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )
                }
            }

            item {
                Text(
                    text = "AVAILABLE SOON",
                    color = DoctorDashboardMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            item {
                LanguageCard(
                    icon = Icons.Filled.Translate,
                    title = "English",
                    subtitle = "Default System Language",
                    status = "selected",
                )
            }
            item {
                LanguageCard(
                    icon = Icons.Filled.Translate,
                    title = "Hindi (हिन्दी)",
                    subtitle = "In Development",
                    status = "locked",
                )
            }
            item {
                LanguageCard(
                    icon = Icons.Filled.Translate,
                    title = "Punjabi (ਪੰਜਾਬੀ)",
                    subtitle = "In Development",
                    status = "locked",
                )
            }
        }

        DoctorSettingsBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentNavigation = currentNavigation,
            onOpenHome = {
                currentNavigation = "Home"
                onOpenHome()
            },
            onOpenQueue = {
                currentNavigation = "Appointments"
                onOpenQueue()
            },
            onOpenPatients = {
                currentNavigation = "Patients"
                onOpenPatients()
            },
            onOpenAnalytics = {
                currentNavigation = "Analytics"
                onOpenAnalytics()
            },
            onOpenProfile = {
                currentNavigation = "Profile"
                onOpenProfile()
            },
        )
    }
}

@Composable
private fun ContactCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCEAF5)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DoctorDashboardPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = title,
                color = DoctorDashboardText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = DoctorDashboardMuted,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun TopicCard(title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = DoctorDashboardText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    color = DoctorDashboardMuted,
                    fontSize = 13.sp,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFAEB7C4),
            )
        }
    }
}

@Composable
private fun LanguageCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    status: String,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF2F4F8)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DoctorDashboardMuted,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = subtitle,
                        color = DoctorDashboardMuted,
                        fontSize = 14.sp,
                    )
                }
            }

            if (status == "selected") {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF5AA3CC)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = Color(0xFFB7BEC9),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun DoctorSettingsBottomBar(
    modifier: Modifier = Modifier,
    currentNavigation: String,
    onOpenHome: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenPatients: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter,
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
                    onClick = onOpenHome,
                )
                DoctorBottomNavItem(
                    icon = Icons.Filled.Dashboard,
                    label = "Appointments",
                    isSelected = currentNavigation == "Appointments",
                    onClick = onOpenQueue,
                )
                DoctorBottomNavItem(
                    icon = Icons.Filled.Group,
                    label = "Patients",
                    isSelected = currentNavigation == "Patients",
                    onClick = onOpenPatients,
                )
                DoctorBottomNavItem(
                    icon = Icons.Filled.BarChart,
                    label = "Analytics",
                    isSelected = currentNavigation == "Analytics",
                    onClick = onOpenAnalytics,
                )
                DoctorBottomNavItem(
                    icon = Icons.Filled.Person,
                    label = "Profile",
                    isSelected = currentNavigation == "Profile",
                    onClick = onOpenProfile,
                )
            }
        }
    }
}
