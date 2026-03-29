package com.simats.ruralcareai.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ReportDetailsUi(
    val title: String,
    val reportDate: String,
    val referenceId: String,
    val statusLabel: String,
    val isAttentionNeeded: Boolean,
    val primaryReading: String,
    val unit: String,
    val readingTier: Int,
    val fastingNote: String,
    val aiInsight: String,
    val doctorNotes: String,
    val doctorName: String,
)

private val ReportDetailsBackground = Color(0xFFF2F4F8)
private val ReportDetailsSurface = Color(0xFFFFFFFF)
private val ReportDetailsPrimary = Color(0xFF0B6FA2)
private val ReportDetailsText = Color(0xFF131A22)
private val ReportDetailsMuted = Color(0xFF6F7A89)
private val ReportDetailsOutline = Color(0xFFD6DEE9)

@Composable
fun ReportDetailsScreen(
    details: ReportDetailsUi,
    onBack: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ReportDetailsBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            top = if (isCompact) 10.dp else 12.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DetailsTopAction(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Text(
                        text = "RuralCareAI",
                        color = ReportDetailsPrimary,
                        fontSize = if (isCompact) 22.sp / 1.15f else 23.sp / 1.15f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "REPORT DETAILS",
                    color = ReportDetailsPrimary.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
                Text(
                    text = details.title,
                    color = ReportDetailsText,
                    fontSize = if (isCompact) 42.sp / 1.55f else 44.sp / 1.55f,
                    lineHeight = if (isCompact) 48.sp / 1.55f else 50.sp / 1.55f,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "${details.reportDate} • Reference ID: ${details.referenceId}",
                    color = ReportDetailsMuted,
                    fontSize = if (isCompact) 15.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                )

                StatusPill(
                    label = details.statusLabel,
                    attention = details.isAttentionNeeded,
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ReportDetailsSurface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "PRIMARY READING",
                        color = ReportDetailsMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = details.primaryReading,
                                color = ReportDetailsPrimary,
                                fontSize = if (isCompact) 64.sp / 1.6f else 66.sp / 1.6f,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = " ${details.unit}",
                                color = ReportDetailsText,
                                fontSize = if (isCompact) 22.sp / 1.2f else 23.sp / 1.2f,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 3.dp),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFE9EEF3)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Science,
                                contentDescription = "Primary reading",
                                tint = Color(0xFFBCC4CE),
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(99.dp)),
                    ) {
                        val segmentWidth = size.width / 3f
                        val palette = listOf(
                            Color(0xFF8EC8E9),
                            Color(0xFF2D9CDB),
                            Color(0xFFC62828),
                        )

                        for (index in 0..2) {
                            val color = if (details.readingTier == index) {
                                palette[index]
                            } else {
                                palette[index].copy(alpha = 0.35f)
                            }

                            drawRect(
                                color = color,
                                topLeft = Offset(segmentWidth * index, 0f),
                                size = Size(segmentWidth, size.height),
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "NORMAL (70-99)",
                            color = ReportDetailsText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "PREDIABETIC (100-125)",
                            color = ReportDetailsText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "HIGH (126+)",
                            color = Color(0xFFC62828),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDEFFD)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC4DCF0)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Fasting",
                            tint = ReportDetailsPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Fasting",
                            color = ReportDetailsText,
                            fontSize = 34.sp / 2f,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = details.fastingNote,
                        color = ReportDetailsText.copy(alpha = 0.85f),
                        fontSize = 16.sp / 1.12f,
                        lineHeight = 22.sp / 1.12f,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EEF6)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI insights",
                            tint = ReportDetailsPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Insights",
                            color = ReportDetailsPrimary,
                            fontSize = if (isCompact) 21.sp / 1.2f else 22.sp / 1.2f,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "\"${details.aiInsight}\"",
                            color = ReportDetailsText.copy(alpha = 0.88f),
                            fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                            lineHeight = if (isCompact) 32.sp / 1.12f else 33.sp / 1.12f,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ReportDetailsSurface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Summarize,
                            contentDescription = "Doctor notes",
                            tint = ReportDetailsText,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = "Doctor's Notes",
                            color = ReportDetailsText,
                            fontSize = if (isCompact) 22.sp / 1.2f else 23.sp / 1.2f,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = details.doctorNotes,
                        color = ReportDetailsText.copy(alpha = 0.86f),
                        fontSize = if (isCompact) 18.sp / 1.12f else 19.sp / 1.12f,
                        lineHeight = if (isCompact) 32.sp / 1.12f else 33.sp / 1.12f,
                        fontWeight = FontWeight.Medium,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFBFE1FE)),
                        )
                        Text(
                            text = details.doctorName,
                            color = ReportDetailsText.copy(alpha = 0.84f),
                            fontSize = if (isCompact) 15.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFF1683BE))
                        .clickable(onClick = onDownload)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Download PDF",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "  Download PDF",
                        color = Color.White,
                        fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White)
                        .border(1.dp, ReportDetailsOutline, androidx.compose.foundation.shape.CircleShape)
                        .clickable(onClick = onShare)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share with doctor",
                        tint = ReportDetailsText,
                        modifier = Modifier.size(19.dp),
                    )
                    Text(
                        text = "  Share with Doctor",
                        color = ReportDetailsText,
                        fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, ReportDetailsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ReportDetailsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun StatusPill(
    label: String,
    attention: Boolean,
) {
    val background = if (attention) Color(0xFFF9D8D4) else Color(0xFFDDF0FF)
    val textColor = if (attention) Color(0xFF9A1D1A) else Color(0xFF145E86)

    Row(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (attention) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(16.dp),
            )
        }

        Text(
            text = label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

