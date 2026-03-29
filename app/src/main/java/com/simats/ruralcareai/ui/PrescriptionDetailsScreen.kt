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
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PrescriptionScheduleUi(
    val label: String,
    val time: String,
)

data class PrescriptionDetailsUi(
    val patientName: String,
    val prescribedDate: String,
    val medicationName: String,
    val medicationStatus: String,
    val instructions: String,
    val duration: String,
    val durationNote: String,
    val schedule: List<PrescriptionScheduleUi>,
    val physicianNote: String,
    val doctorName: String,
    val doctorDesignation: String,
    val doctorClinic: String,
)

private val PrescriptionDetailsBackground = Color(0xFFF2F4F8)
private val PrescriptionDetailsSurface = Color(0xFFFFFFFF)
private val PrescriptionDetailsText = Color(0xFF131A22)
private val PrescriptionDetailsMuted = Color(0xFF6F7A89)
private val PrescriptionDetailsPrimary = Color(0xFF0B6FA2)
private val PrescriptionDetailsOutline = Color(0xFFD6DEE9)

@Composable
fun PrescriptionDetailsScreen(
    details: PrescriptionDetailsUi,
    onBack: () -> Unit,
    onTopDownload: () -> Unit,
    onBottomDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrescriptionDetailsBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isCompact) 10.dp else 12.dp,
                bottom = 112.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
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
                        TopIconAction(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                        )

                        Text(
                            text = "Prescription Details",
                            color = PrescriptionDetailsText,
                            fontSize = if (isCompact) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    TopIconAction(
                        icon = Icons.Filled.Download,
                        contentDescription = "Download",
                        onClick = onTopDownload,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "PATIENT FILE",
                            color = PrescriptionDetailsPrimary.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = details.patientName,
                            color = PrescriptionDetailsText,
                            fontSize = if (isCompact) 34.sp / 1.4f else 36.sp / 1.4f,
                            lineHeight = if (isCompact) 40.sp / 1.4f else 42.sp / 1.4f,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "Prescribed on",
                            color = PrescriptionDetailsMuted,
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = details.prescribedDate,
                            color = PrescriptionDetailsText,
                            fontSize = if (isCompact) 18.sp else 19.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrescriptionDetailsSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(126.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(PrescriptionDetailsPrimary.copy(alpha = 0.07f)),
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Text(
                                    text = details.medicationName,
                                    color = PrescriptionDetailsText,
                                    fontSize = if (isCompact) 37.sp / 1.5f else 39.sp / 1.5f,
                                    lineHeight = if (isCompact) 43.sp / 1.5f else 45.sp / 1.5f,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.weight(1f),
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(Color(0xFFBFE1FE))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = details.medicationStatus,
                                        color = Color(0xFF31556E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.2.sp,
                                    )
                                }
                            }

                            PrescriptionInfoRow(
                                icon = Icons.Filled.Medication,
                                iconBg = Color(0xFFE5F0FA),
                                iconTint = PrescriptionDetailsPrimary,
                                title = "INSTRUCTIONS",
                                body = details.instructions,
                            )

                            PrescriptionInfoRow(
                                icon = Icons.Filled.CalendarToday,
                                iconBg = Color(0xFFF2EBDD),
                                iconTint = Color(0xFF835400),
                                title = "DURATION",
                                body = details.duration,
                                trailingHighlight = details.durationNote,
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DAILY SCHEDULE",
                        color = PrescriptionDetailsText.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EDF3)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 27.dp)
                                    .size(width = 1.dp, height = (details.schedule.size * 76).dp)
                                    .background(Color(0xFFC7D1DE)),
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                details.schedule.forEach { slot ->
                                    ScheduleRow(slot = slot, compact = isCompact)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6DDB8)),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.StickyNote2,
                            contentDescription = null,
                            tint = Color(0xFFB4833A).copy(alpha = 0.2f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(64.dp),
                        )

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
                                    imageVector = Icons.Filled.AssignmentInd,
                                    contentDescription = "Physician note",
                                    tint = Color(0xFF2F2110),
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "PHYSICIAN'S NOTE",
                                    color = Color(0xFF2F2110),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                )
                            }

                            Text(
                                text = "\"${details.physicianNote}\"",
                                color = PrescriptionDetailsText,
                                fontSize = if (isCompact) 18.sp / 1.12f else 19.sp / 1.12f,
                                lineHeight = if (isCompact) 32.sp / 1.12f else 33.sp / 1.12f,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PrescriptionDetailsSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDF3)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                                    .background(Color(0xFFD7E6F3)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = doctorInitials(details.doctorName),
                                    color = PrescriptionDetailsPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(PrescriptionDetailsPrimary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = details.doctorName,
                                color = PrescriptionDetailsText,
                                fontSize = if (isCompact) 33.sp / 1.65f else 34.sp / 1.65f,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = "${details.doctorDesignation} • ${details.doctorClinic}",
                                color = PrescriptionDetailsText.copy(alpha = 0.85f),
                                fontSize = if (isCompact) 16.sp / 1.2f else 17.sp / 1.2f,
                                lineHeight = if (isCompact) 23.sp / 1.2f else 24.sp / 1.2f,
                                fontWeight = FontWeight.Medium,
                            )

                            Row(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Call,
                                        contentDescription = null,
                                        tint = PrescriptionDetailsPrimary,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = "CONTACT",
                                        color = PrescriptionDetailsPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = PrescriptionDetailsPrimary,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Text(
                                        text = "LOCATION",
                                        color = PrescriptionDetailsPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xF2F2F4F8))
                .padding(horizontal = horizontalPadding, vertical = 14.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFF0B6FA2), Color(0xFF2D9CDB))))
                .clickable(onClick = onBottomDownload)
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = "Download prescription PDF",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "  Download Digital Prescription (PDF)",
                color = Color.White,
                fontSize = if (isCompact) 30.sp / 1.75f else 31.sp / 1.75f,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun TopIconAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, PrescriptionDetailsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PrescriptionDetailsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun PrescriptionInfoRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    body: String,
    trailingHighlight: String? = null,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                color = PrescriptionDetailsMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )

            if (trailingHighlight.isNullOrBlank()) {
                Text(
                    text = body,
                    color = PrescriptionDetailsText,
                    fontSize = 32.sp / 1.7f,
                    lineHeight = 36.sp / 1.7f,
                    fontWeight = FontWeight.Medium,
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$body ",
                        color = PrescriptionDetailsText,
                        fontSize = 32.sp / 1.7f,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = trailingHighlight,
                        color = PrescriptionDetailsPrimary,
                        fontSize = 32.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    slot: PrescriptionScheduleUi,
    compact: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color.White)
                .border(2.dp, Color(0xFFE0E6EE), androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(PrescriptionDetailsPrimary),
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(PrescriptionDetailsSurface)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = slot.label,
                color = PrescriptionDetailsText,
                fontSize = if (compact) 17.sp / 1.15f else 18.sp / 1.15f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = slot.time,
                color = PrescriptionDetailsPrimary,
                fontSize = if (compact) 17.sp / 1.15f else 18.sp / 1.15f,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun doctorInitials(name: String): String {
    val parts = name
        .replace("Dr.", "", ignoreCase = true)
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    if (parts.isEmpty()) {
        return "DR"
    }

    return parts.take(2).joinToString(separator = "") { it.take(1).uppercase() }
}