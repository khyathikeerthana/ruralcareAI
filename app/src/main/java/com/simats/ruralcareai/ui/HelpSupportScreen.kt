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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class HelpTopic(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

private val HelpBackground = Color(0xFFF2F4F8)
private val HelpSurface = Color(0xFFFFFFFF)
private val HelpText = Color(0xFF131A22)
private val HelpMuted = Color(0xFF6F7A89)
private val HelpPrimary = Color(0xFF0B6FA2)
private val HelpOutline = Color(0xFFD6DEE9)

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    val topics = remember {
        listOf(
            HelpTopic(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "FAQs",
                subtitle = "Find quick answers about appointments, reports and records",
            ),
            HelpTopic(
                icon = Icons.Filled.Description,
                title = "User Guide",
                subtitle = "Step-by-step help for patient and caregiver workflows",
            ),
            HelpTopic(
                icon = Icons.Filled.Policy,
                title = "Privacy & Data",
                subtitle = "Understand how RuralCareAI handles your health information",
            ),
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HelpBackground)
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
                HelpTopAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                )
                Text(
                    text = "Help & Support",
                    color = HelpText,
                    fontSize = if (isCompact) 22.sp / 1.2f else 23.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
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
                                .size(30.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(HelpPrimary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SupportAgent,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Text(
                            text = "Need help now?",
                            color = HelpPrimary,
                            fontSize = if (isCompact) 19.sp / 1.15f else 20.sp / 1.15f,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "Our support team can help with account access, app navigation, and telemedicine coordination.",
                        color = HelpText.copy(alpha = 0.88f),
                        fontSize = if (isCompact) 16.sp / 1.15f else 17.sp / 1.15f,
                        lineHeight = if (isCompact) 28.sp / 1.15f else 29.sp / 1.15f,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        item {
            Text(
                text = "POPULAR TOPICS",
                color = HelpText.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
            )
        }

        items(topics.size) { index ->
            val topic = topics[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {}),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HelpSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFEAF2FA)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = topic.icon,
                                contentDescription = topic.title,
                                tint = HelpPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = topic.title,
                                color = HelpText,
                                fontSize = 33.sp / 1.8f,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = topic.subtitle,
                                color = HelpMuted,
                                fontSize = 15.sp / 1.12f,
                                lineHeight = 21.sp / 1.12f,
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
        }

        item {
            Text(
                text = "CONTACT SUPPORT",
                color = HelpText.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HelpSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SupportActionRow(
                        icon = Icons.Filled.Call,
                        title = "Call Us",
                        subtitle = "+91 1800 123 455",
                    )
                    HelpDivider()
                    SupportActionRow(
                        icon = Icons.Filled.Email,
                        title = "Email",
                        subtitle = "support@ruralcareai.org",
                    )
                    HelpDivider()
                    SupportActionRow(
                        icon = Icons.Filled.Forum,
                        title = "Live Chat",
                        subtitle = "Usually replies within 5 minutes",
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, HelpOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = HelpPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun SupportActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(horizontal = 12.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFEAF2FA)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = HelpPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = HelpText,
                    fontSize = 33.sp / 1.8f,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    color = HelpMuted,
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
private fun HelpDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HelpOutline),
    )
}
