package com.simats.ruralcareai.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class SettingRowUi(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val subtitle: String,
)

private val SettingsBackground = Color(0xFFF2F4F8)
private val SettingsSurface = Color(0xFFFFFFFF)
private val SettingsText = Color(0xFF131A22)
private val SettingsMuted = Color(0xFF6F7A89)
private val SettingsPrimary = Color(0xFF0B6FA2)
private val SettingsOutline = Color(0xFFD6DEE9)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    var biometricEnabled by rememberSaveable { mutableStateOf(true) }
    var twoFactorEnabled by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SettingsBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            top = if (isCompact) 10.dp else 12.dp,
            bottom = 22.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsTopAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )
                Text(
                    text = "Settings",
                    color = SettingsText,
                    fontSize = if (isCompact) 22.sp / 1.2f else 23.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Security",
                    color = SettingsText,
                    fontSize = if (isCompact) 42.sp / 1.6f else 44.sp / 1.6f,
                    lineHeight = if (isCompact) 48.sp / 1.6f else 50.sp / 1.6f,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Manage your account access and data protection preferences.",
                    color = SettingsText.copy(alpha = 0.9f),
                    fontSize = if (isCompact) 17.sp / 1.15f else 18.sp / 1.15f,
                    lineHeight = if (isCompact) 30.sp / 1.15f else 31.sp / 1.15f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        item {
            SectionTag("ACCOUNT ACCESS")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SettingsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    StaticSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.Key,
                            iconBg = Color(0xFFE5F0FA),
                            iconTint = SettingsPrimary,
                            title = "Change Password",
                            subtitle = "Last updated 3 months ago",
                        ),
                    )
                    DividerLine()
                    ToggleSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.Badge,
                            iconBg = Color(0xFFEAF0F7),
                            iconTint = Color(0xFF4E6A84),
                            title = "Biometric Login",
                            subtitle = "Use FaceID or TouchID",
                        ),
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it },
                    )
                }
            }
        }

        item {
            SectionTag("PRIVACY")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SettingsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ToggleSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.VerifiedUser,
                            iconBg = Color(0xFFF2EBDD),
                            iconTint = Color(0xFF835400),
                            title = "Two-Factor Authentication",
                            subtitle = "Secure your account with a code",
                        ),
                        checked = twoFactorEnabled,
                        onCheckedChange = { twoFactorEnabled = it },
                    )
                    DividerLine()
                    StaticSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.Lock,
                            iconBg = Color(0xFFF9E8E8),
                            iconTint = Color(0xFFD92D20),
                            title = "App Lock",
                            subtitle = "Require PIN on startup",
                        ),
                    )
                }
            }
        }

        item {
            SectionTag("ACTIVITY")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SettingsSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    StaticSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.History,
                            iconBg = Color(0xFFE9EDF3),
                            iconTint = Color(0xFF4E5D73),
                            title = "Login History",
                            subtitle = "View your recent sign-ins",
                        ),
                    )
                    DividerLine()
                    DeviceSettingRow(
                        row = SettingRowUi(
                            icon = Icons.Filled.Smartphone,
                            iconBg = Color(0xFFE9EDF3),
                            iconTint = Color(0xFF4E5D73),
                            title = "Authorized Devices",
                            subtitle = "3 devices currently active",
                        ),
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDE7F0)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF2D9CDB)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "i",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "Security Recommendation",
                            color = SettingsPrimary,
                            fontSize = if (isCompact) 17.sp / 1.15f else 18.sp / 1.15f,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "Enable Two-Factor Authentication to add an extra layer of protection to your patient records and sensitive health information.",
                        color = SettingsText.copy(alpha = 0.88f),
                        fontSize = if (isCompact) 16.sp / 1.15f else 17.sp / 1.15f,
                        lineHeight = if (isCompact) 28.sp / 1.15f else 29.sp / 1.15f,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = "Learn more about health data safety",
                        color = SettingsPrimary,
                        fontSize = if (isCompact) 16.sp / 1.15f else 17.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, SettingsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = SettingsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun SectionTag(text: String) {
    Text(
        text = text,
        color = SettingsText.copy(alpha = 0.85f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
    )
}

@Composable
private fun StaticSettingRow(row: SettingRowUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingIcon(icon = row.icon, bg = row.iconBg, tint = row.iconTint)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.title,
                    color = SettingsText,
                    fontSize = 33.sp / 1.8f,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = row.subtitle,
                    color = SettingsMuted,
                    fontSize = 15.sp / 1.12f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Text(
            text = ">",
            color = Color(0xFFB4BCC8),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ToggleSettingRow(
    row: SettingRowUi,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingIcon(icon = row.icon, bg = row.iconBg, tint = row.iconTint)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.title,
                    color = SettingsText,
                    fontSize = 33.sp / 1.8f,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = row.subtitle,
                    color = SettingsMuted,
                    fontSize = 15.sp / 1.12f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2D9CDB),
                uncheckedThumbColor = Color(0xFFF5F6F8),
                uncheckedTrackColor = Color(0xFFD0D6DE),
            ),
        )
    }
}

@Composable
private fun DeviceSettingRow(row: SettingRowUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingIcon(icon = row.icon, bg = row.iconBg, tint = row.iconTint)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = row.title,
                    color = SettingsText,
                    fontSize = 33.sp / 1.8f,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = row.subtitle,
                    color = SettingsMuted,
                    fontSize = 15.sp / 1.12f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                    .background(Color(0xFFDDEFFD))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "SECURE",
                    color = SettingsPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = ">",
                color = Color(0xFFB4BCC8),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SettingIcon(
    icon: ImageVector,
    bg: Color,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SettingsOutline),
    )
}
