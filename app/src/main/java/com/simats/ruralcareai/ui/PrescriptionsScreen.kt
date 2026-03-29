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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RxBackground = Color(0xFFF2F4F8)
private val RxSurface = Color(0xFFFFFFFF)
private val RxCardMuted = Color(0xFFE8EDF3)
private val RxText = Color(0xFF131A22)
private val RxMuted = Color(0xFF6F7A89)
private val RxPrimary = Color(0xFF0B6FA2)
private val RxOutline = Color(0xFFD6DEE9)
private val RxBluePill = Color(0xFFBFE1FE)
private val RxTopBadge = Color(0xFFF4D8CC)

private data class MedicationItem(
    val name: String,
    val instructions: String,
    val daysLeft: String? = null,
)

private data class ActivePrescriptionCard(
    val doctorName: String,
    val meta: String,
    val icon: ImageVector,
    val iconContainer: Color,
    val iconTint: Color,
    val items: List<MedicationItem>,
    val doctorNote: String,
)

private data class PastPrescriptionRow(
    val doctorName: String,
    val meta: String,
)

private enum class RxScreenMode {
    LIST,
    DETAILS,
}

@Composable
fun PrescriptionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    val horizontalPadding = if (isCompact) 14.dp else 16.dp
    val topGap = if (isCompact) 10.dp else 12.dp

    val activeCards = remember {
        listOf(
            ActivePrescriptionCard(
                doctorName = "Dr. Aris Thorne",
                meta = "Oct 24, 2023 • Community Clinic",
                icon = Icons.Filled.Medication,
                iconContainer = Color(0xFFD8ECFF),
                iconTint = RxPrimary,
                items = listOf(
                    MedicationItem(
                        name = "Amoxicillin 500mg",
                        instructions = "Take 1 capsule by mouth every 8 hours",
                        daysLeft = "7 Days Left",
                    ),
                    MedicationItem(
                        name = "Ibuprofen 400mg",
                        instructions = "Take 1 tablet every 6 hours as needed for pain",
                    ),
                ),
                doctorNote = "Complete the full course of antibiotics even if feeling better. Avoid alcohol. Take with food to minimize stomach upset.",
            ),
            ActivePrescriptionCard(
                doctorName = "Dr. Sarah Jenkins",
                meta = "Oct 12, 2023 • General Practice",
                icon = Icons.Filled.Healing,
                iconContainer = Color(0xFFF4E5CF),
                iconTint = Color(0xFF8C5D00),
                items = listOf(
                    MedicationItem(
                        name = "Lisinopril 10mg",
                        instructions = "Take 1 tablet daily in the morning for blood pressure",
                    ),
                ),
                doctorNote = "Monitor blood pressure daily at home. Report any persistent dry cough or dizziness.",
            ),
        )
    }

    var screenMode by rememberSaveable { mutableStateOf(RxScreenMode.LIST.name) }
    var selectedDetails by remember {
        mutableStateOf(buildPrescriptionDetails(activeCards.first()))
    }

    val currentMode = RxScreenMode.valueOf(screenMode)

    BackHandler(
        onBack = {
            if (currentMode == RxScreenMode.LIST) {
                onBack()
            } else {
                screenMode = RxScreenMode.LIST.name
            }
        },
    )

    if (currentMode == RxScreenMode.DETAILS) {
        PrescriptionDetailsScreen(
            details = selectedDetails,
            onBack = { screenMode = RxScreenMode.LIST.name },
            onTopDownload = {},
            onBottomDownload = {},
        )
        return
    }

    val pastRows = remember {
        listOf(
            PastPrescriptionRow(
                doctorName = "Dr. Michael Chen",
                meta = "Sep 05, 2023 • Viral Infection",
            ),
            PastPrescriptionRow(
                doctorName = "Dr. Aris Thorne",
                meta = "Aug 12, 2023 • Seasonal Allergy",
            ),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RxBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = topGap,
                bottom = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        RxTopCircleButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                        )

                        Text(
                            text = "Prescriptions",
                            color = RxText,
                            fontSize = if (isCompact) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ongoing Medication",
                        color = RxText,
                        fontSize = if (isCompact) 18.sp else 19.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "2 Active",
                        color = RxMuted,
                        fontSize = if (isCompact) 16.sp / 1.15f else 17.sp / 1.15f,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            items(activeCards.size) { index ->
                ActivePrescriptionPanel(
                    card = activeCards[index],
                    compact = isCompact,
                    onViewDetails = {
                        selectedDetails = buildPrescriptionDetails(activeCards[index])
                        screenMode = RxScreenMode.DETAILS.name
                    },
                )
            }

            item {
                Text(
                    text = "Past Records",
                    color = RxText,
                    fontSize = if (isCompact) 18.sp else 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            items(pastRows.size) { index ->
                PastPrescriptionPanel(
                    row = pastRows[index],
                    compact = isCompact,
                )
            }
        }
    }
}

@Composable
private fun RxTopCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, RxOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = RxPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun ActivePrescriptionPanel(
    card: ActivePrescriptionCard,
    compact: Boolean,
    onViewDetails: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RxSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 12.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(card.iconContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = card.icon,
                            contentDescription = card.doctorName,
                            tint = card.iconTint,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = card.doctorName,
                            color = RxText,
                            fontSize = if (compact) 18.sp else 19.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = card.meta,
                            color = RxMuted,
                            fontSize = if (compact) 13.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                StatusPill(text = "ACTIVE")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RxCardMuted),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    card.items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = item.name,
                                    color = RxPrimary,
                                    fontSize = if (compact) 16.sp else 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = item.instructions,
                                    color = RxText.copy(alpha = 0.9f),
                                    fontSize = if (compact) 13.sp else 14.sp,
                                    lineHeight = if (compact) 18.sp else 19.sp,
                                )
                            }

                            if (!item.daysLeft.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = item.daysLeft,
                                        color = RxPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        if (index != card.items.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(RxOutline),
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DOCTOR'S NOTES",
                    color = RxMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "\"${card.doctorNote}\"",
                    color = RxText.copy(alpha = 0.85f),
                    fontSize = if (compact) 14.sp else 15.sp,
                    lineHeight = if (compact) 20.sp else 21.sp,
                    fontStyle = FontStyle.Italic,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFF2D9CDB))
                        .clickable(onClick = onViewDetails)
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "View details",
                        tint = Color(0xFF003049),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "  View Details",
                        color = Color(0xFF003049),
                        fontSize = if (compact) 15.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White)
                        .border(1.dp, RxOutline, androidx.compose.foundation.shape.CircleShape)
                        .clickable(onClick = {}),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Download prescription",
                        tint = RxText,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PastPrescriptionPanel(
    row: PastPrescriptionRow,
    compact: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8ECF1)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "History",
                    tint = RxMuted,
                    modifier = Modifier.size(20.dp),
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = row.doctorName,
                        color = RxText.copy(alpha = 0.75f),
                        fontSize = if (compact) 16.sp else 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = row.meta,
                        color = RxMuted,
                        fontSize = if (compact) 12.sp else 13.sp,
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
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .background(Color(0xFFDDE3EA))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "COMPLETED",
                        color = RxMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp,
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open record",
                    tint = RxMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(RxBluePill)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color(0xFF234B69),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.sp,
        )
    }
}

private fun buildPrescriptionDetails(card: ActivePrescriptionCard): PrescriptionDetailsUi {
    val issuedDate = card.meta.substringBefore("•").trim().ifBlank { "Oct 24, 2023" }
    val clinic = card.meta.substringAfter("•", "Community Clinic").trim().ifBlank { "Community Clinic" }
    val medicine = card.items.firstOrNull()

    val duration = medicine?.daysLeft?.replace(" Left", "") ?: "As advised"
    val durationNote = if (medicine?.daysLeft.isNullOrBlank()) {
        "(Follow doctor's schedule)"
    } else {
        "(Finish entire course)"
    }

    return PrescriptionDetailsUi(
        patientName = "Elias Vance",
        prescribedDate = issuedDate,
        medicationName = medicine?.name ?: "Medication",
        medicationStatus = "ACTIVE",
        instructions = medicine?.instructions ?: "Follow physician instructions",
        duration = duration,
        durationNote = durationNote,
        schedule = listOf(
            PrescriptionScheduleUi(label = "Morning", time = "08:00 AM"),
            PrescriptionScheduleUi(label = "Afternoon", time = "04:00 PM"),
            PrescriptionScheduleUi(label = "Evening", time = "12:00 AM"),
        ),
        physicianNote = card.doctorNote,
        doctorName = card.doctorName,
        doctorDesignation = designationForDoctor(card.doctorName),
        doctorClinic = clinic,
    )
}

private fun designationForDoctor(name: String): String {
    return when (name) {
        "Dr. Aris Thorne" -> "Chief of Medicine"
        "Dr. Sarah Jenkins" -> "General Physician"
        "Dr. Michael Chen" -> "Internal Medicine"
        else -> "Consultant"
    }
}
