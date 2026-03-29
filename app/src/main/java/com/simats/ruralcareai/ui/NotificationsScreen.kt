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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Sync
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

private data class NotificationEntry(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val message: String,
    val timeText: String,
    val unread: Boolean,
)

private val NotificationsBackground = Color(0xFFF2F4F8)
private val NotificationsSurface = Color(0xFFFFFFFF)
private val NotificationsMuted = Color(0xFF6F7A89)
private val NotificationsText = Color(0xFF131A22)
private val NotificationsPrimary = Color(0xFF0B6FA2)
private val NotificationsOutline = Color(0xFFD6DEE9)

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    val todayItems = remember {
        listOf(
            NotificationEntry(
                icon = Icons.Filled.Medication,
                iconBg = Color(0xFFE5F0FA),
                iconTint = NotificationsPrimary,
                title = "Vaccination Reminder",
                message = "It's time for Baby Malik's polio booster. Visit the clinic before 4 PM today.",
                timeText = "2h ago",
                unread = true,
            ),
            NotificationEntry(
                icon = Icons.Filled.Campaign,
                iconBg = Color(0xFFF2EBDD),
                iconTint = Color(0xFFCA850C),
                title = "Dr. Aris Thorne",
                message = "The lab results for your iron levels are back. They look better than last month.",
                timeText = "5h ago",
                unread = true,
            ),
            NotificationEntry(
                icon = Icons.Filled.Sync,
                iconBg = Color(0xFFE9EDF3),
                iconTint = Color(0xFF7A8598),
                title = "System Sync Complete",
                message = "Offline records for the Mwanga Village outreach have been successfully synced.",
                timeText = "8h ago",
                unread = false,
            ),
        )
    }

    val earlierItems = remember {
        listOf(
            NotificationEntry(
                icon = Icons.Filled.Emergency,
                iconBg = Color(0xFFF9E8E8),
                iconTint = Color(0xFFD92D20),
                title = "Emergency Dispatch",
                message = "A critical alert was cleared for Sector 7. Thank you for your swift response.",
                timeText = "Yesterday",
                unread = false,
            ),
            NotificationEntry(
                icon = Icons.Filled.Inventory2,
                iconBg = Color(0xFFE5EEF9),
                iconTint = Color(0xFF4E5D73),
                title = "Stock Inventory Update",
                message = "The quarterly medication inventory report for RuralCareAI is now available for review.",
                timeText = "Yesterday",
                unread = false,
            ),
            NotificationEntry(
                icon = Icons.Filled.AutoAwesome,
                iconBg = Color(0xFFE9EDF3),
                iconTint = Color(0xFF7A8598),
                title = "AI Model Updated",
                message = "Diagnostic accuracy for respiratory screenings improved by 14% with the latest core update.",
                timeText = "2 days ago",
                unread = false,
            ),
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NotificationsBackground)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    NotificationTopAction(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )
                    Text(
                        text = "Notifications",
                        color = NotificationsText,
                        fontSize = if (isCompact) 22.sp / 1.2f else 23.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = "Mark all",
                    color = NotificationsPrimary,
                    fontSize = if (isCompact) 16.sp / 1.12f else 17.sp / 1.12f,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = {}),
                )
            }
        }

        item {
            SectionLabel("TODAY")
        }

        items(todayItems.size) { index ->
            NotificationCard(item = todayItems[index], compact = isCompact)
        }

        item {
            SectionLabel("EARLIER")
        }

        items(earlierItems.size) { index ->
            NotificationCard(item = earlierItems[index], compact = isCompact)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsOff,
                    contentDescription = null,
                    tint = Color(0xFFB8BDC6),
                    modifier = Modifier.size(34.dp),
                )
                Text(
                    text = "END OF NOTIFICATIONS",
                    color = Color(0xFFB0B6C1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationEntry,
    compact: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NotificationsSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.title,
                        color = NotificationsText,
                        fontSize = if (compact) 18.sp / 1.2f else 19.sp / 1.2f,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = item.timeText,
                            color = NotificationsMuted,
                            fontSize = if (compact) 14.sp / 1.15f else 15.sp / 1.15f,
                            fontWeight = FontWeight.Medium,
                        )
                        if (item.unread) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(Color(0xFF2D9CDB)),
                            )
                        }
                    }
                }

                Text(
                    text = item.message,
                    color = NotificationsText.copy(alpha = 0.9f),
                    fontSize = if (compact) 17.sp / 1.18f else 18.sp / 1.18f,
                    lineHeight = if (compact) 28.sp / 1.18f else 29.sp / 1.18f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun NotificationTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, NotificationsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NotificationsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = NotificationsText.copy(alpha = 0.85f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
    )
}
