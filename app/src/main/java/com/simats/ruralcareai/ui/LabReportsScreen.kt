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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Science
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ReportsBackground = Color(0xFFF2F4F8)
private val ReportsSurface = Color(0xFFFFFFFF)
private val ReportsText = Color(0xFF131A22)
private val ReportsMuted = Color(0xFF6F7A89)
private val ReportsPrimary = Color(0xFF0B6FA2)
private val ReportsOutline = Color(0xFFD6DEE9)
private val ReportsTabContainer = Color(0xFFE1E6EC)
private val ReportsTabSelected = Color(0xFFFAFCFF)
private val ReportsFabBlue = Color(0xFF0E74A8)
private val ReportsFabBlueLight = Color(0xFF2EA2E3)

private enum class ReportFilter {
    ALL,
    RECENT,
    CRITICAL,
}

private enum class ReportStatus {
    ATTENTION_NEEDED,
    NORMAL,
    REVIEW_REQUIRED,
}

private enum class ReportsScreenMode {
    LIST,
    DETAILS,
    UPLOAD,
}

private data class LabReportRecord(
    val title: String,
    val dateText: String,
    val center: String,
    val icon: ImageVector,
    val iconContainer: Color,
    val iconTint: Color,
    val status: ReportStatus,
)

@Composable
fun LabReportsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    val horizontalPadding = if (isCompact) 14.dp else 16.dp
    val topGap = if (isCompact) 10.dp else 12.dp

    var selectedFilter by rememberSaveable { mutableStateOf(ReportFilter.ALL) }

    val allReports = remember {
        listOf(
            LabReportRecord(
                title = "Blood Glucose Test",
                dateText = "Oct 15, 2023",
                center = "City Lab HQ",
                icon = Icons.Filled.Science,
                iconContainer = Color(0xFFFCE8E8),
                iconTint = Color(0xFFC62828),
                status = ReportStatus.ATTENTION_NEEDED,
            ),
            LabReportRecord(
                title = "Lipid Profile",
                dateText = "Oct 12, 2023",
                center = "Wellness Center",
                icon = Icons.Filled.Air,
                iconContainer = Color(0xFFDDEEFF),
                iconTint = Color(0xFF35607D),
                status = ReportStatus.NORMAL,
            ),
            LabReportRecord(
                title = "X-Ray Chest PA",
                dateText = "Sep 28, 2023",
                center = "General Hospital",
                icon = Icons.Filled.Description,
                iconContainer = Color(0xFFDDE9F7),
                iconTint = Color(0xFF4B6A86),
                status = ReportStatus.NORMAL,
            ),
            LabReportRecord(
                title = "ECG Summary",
                dateText = "Sep 25, 2023",
                center = "Cardio Care",
                icon = Icons.Filled.Favorite,
                iconContainer = Color(0xFFF4E5CF),
                iconTint = Color(0xFF8C5D00),
                status = ReportStatus.REVIEW_REQUIRED,
            ),
        )
    }

    val visibleReports = remember(selectedFilter, allReports) {
        when (selectedFilter) {
            ReportFilter.ALL -> allReports
            ReportFilter.RECENT -> allReports.take(2)
            ReportFilter.CRITICAL -> allReports.filter {
                it.status == ReportStatus.ATTENTION_NEEDED || it.status == ReportStatus.REVIEW_REQUIRED
            }
        }
    }

    var screenMode by rememberSaveable { mutableStateOf(ReportsScreenMode.LIST.name) }
    var selectedReportDetails by remember {
        mutableStateOf(buildReportDetails(allReports.first()))
    }

    val currentMode = ReportsScreenMode.valueOf(screenMode)

    BackHandler(
        onBack = {
            if (currentMode == ReportsScreenMode.LIST) {
                onBack()
            } else {
                screenMode = ReportsScreenMode.LIST.name
            }
        },
    )

    when (currentMode) {
        ReportsScreenMode.DETAILS -> {
            ReportDetailsScreen(
                details = selectedReportDetails,
                onBack = { screenMode = ReportsScreenMode.LIST.name },
                onDownload = {},
                onShare = {},
            )
            return
        }

        ReportsScreenMode.UPLOAD -> {
            UploadReportScreen(
                onBack = { screenMode = ReportsScreenMode.LIST.name },
            )
            return
        }

        ReportsScreenMode.LIST -> Unit
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ReportsBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = topGap,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LabTopCircleButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Text(
                        text = "Lab Reports",
                        color = ReportsText,
                        fontSize = if (isCompact) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = "Health Records",
                        color = ReportsText,
                        fontSize = if (isCompact) 24.sp / 1.45f else 26.sp / 1.45f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Manage and view your clinical data",
                        color = ReportsMuted,
                        fontSize = if (isCompact) 13.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .background(ReportsTabContainer)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        label = "All",
                        selected = selectedFilter == ReportFilter.ALL,
                        onClick = { selectedFilter = ReportFilter.ALL },
                        modifier = Modifier.weight(1f),
                    )
                    FilterChip(
                        label = "Recent",
                        selected = selectedFilter == ReportFilter.RECENT,
                        onClick = { selectedFilter = ReportFilter.RECENT },
                        modifier = Modifier.weight(1f),
                    )
                    FilterChip(
                        label = "Critical",
                        selected = selectedFilter == ReportFilter.CRITICAL,
                        onClick = { selectedFilter = ReportFilter.CRITICAL },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (visibleReports.isEmpty()) {
                item {
                    Text(
                        text = "No lab reports found.",
                        color = ReportsMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                items(count = visibleReports.size) { index ->
                    LabReportCard(
                        report = visibleReports[index],
                        compact = isCompact,
                        onOpenDetails = {
                            selectedReportDetails = buildReportDetails(visibleReports[index])
                            screenMode = ReportsScreenMode.DETAILS.name
                        },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = horizontalPadding, bottom = 20.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Brush.horizontalGradient(listOf(ReportsFabBlue, ReportsFabBlueLight)))
                .clickable(onClick = { screenMode = ReportsScreenMode.UPLOAD.name })
                .padding(horizontal = if (isCompact) 20.dp else 22.dp, vertical = if (isCompact) 12.dp else 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Upload report",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = "Upload Report",
                color = Color.White,
                fontSize = if (isCompact) 15.sp else 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun LabTopCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, ReportsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ReportsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(if (selected) ReportsTabSelected else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) ReportsPrimary else ReportsMuted,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun LabReportCard(
    report: LabReportRecord,
    compact: Boolean,
    onOpenDetails: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ReportsSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 12.dp else 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                    .background(report.iconContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = report.icon,
                    contentDescription = report.title,
                    tint = report.iconTint,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = report.title,
                        color = ReportsText,
                        fontSize = if (compact) 18.sp else 19.sp,
                        lineHeight = if (compact) 22.sp else 23.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    ReportStatusChip(status = report.status)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Report date",
                            tint = ReportsMuted,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = report.dateText,
                            color = ReportsMuted,
                            fontSize = if (compact) 13.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = "•",
                            color = ReportsMuted,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = report.center,
                            color = ReportsMuted,
                            fontSize = if (compact) 13.sp else 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Row(
                        modifier = Modifier.clickable(onClick = onOpenDetails),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "Details",
                            color = ReportsPrimary,
                            fontSize = if (compact) 13.sp else 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open report",
                            tint = ReportsPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun buildReportDetails(report: LabReportRecord): ReportDetailsUi {
    return when (report.title) {
        "Blood Glucose Test" -> ReportDetailsUi(
            title = "Blood Glucose Test",
            reportDate = "Oct 15, 2023",
            referenceId = "#LAB-92834",
            statusLabel = "Attention Needed",
            isAttentionNeeded = true,
            primaryReading = "145",
            unit = "mg/dL",
            readingTier = 2,
            fastingNote = "Patient confirmed 12-hour fast prior to sample collection.",
            aiInsight = "Your levels are slightly elevated. We recommend consulting with a general physician for a detailed metabolic panel and potential lifestyle adjustment.",
            doctorNotes = "Follow the prescribed diet and monitor levels daily. Avoid processed sugars for the next 14 days and return for a follow-up A1C test in 3 months.",
            doctorName = "Dr. Sarah Jenkins, MD",
        )

        "Lipid Profile" -> ReportDetailsUi(
            title = "Lipid Profile",
            reportDate = "Oct 12, 2023",
            referenceId = "#LAB-92858",
            statusLabel = "Normal",
            isAttentionNeeded = false,
            primaryReading = "92",
            unit = "mg/dL",
            readingTier = 0,
            fastingNote = "Fasting sample collected in routine lab conditions.",
            aiInsight = "Lipid values are within expected limits. Continue current diet and annual screening.",
            doctorNotes = "Maintain heart-healthy food choices and continue regular activity.",
            doctorName = "Dr. Sarah Jenkins, MD",
        )

        "X-Ray Chest PA" -> ReportDetailsUi(
            title = "X-Ray Chest PA",
            reportDate = "Sep 28, 2023",
            referenceId = "#RAD-40127",
            statusLabel = "Normal",
            isAttentionNeeded = false,
            primaryReading = "100",
            unit = "%",
            readingTier = 1,
            fastingNote = "No fasting required for this imaging report.",
            aiInsight = "No acute cardiopulmonary abnormalities were identified in the current image.",
            doctorNotes = "Continue with follow-up only if new respiratory symptoms appear.",
            doctorName = "Dr. Sarah Jenkins, MD",
        )

        else -> ReportDetailsUi(
            title = report.title,
            reportDate = report.dateText,
            referenceId = "#LAB-91002",
            statusLabel = "Review Required",
            isAttentionNeeded = true,
            primaryReading = "76",
            unit = "bpm",
            readingTier = 2,
            fastingNote = "ECG completed under resting conditions.",
            aiInsight = "Mild rhythm variation observed. Clinical review is recommended to confirm significance.",
            doctorNotes = "Monitor symptoms and schedule follow-up if palpitations or chest discomfort persists.",
            doctorName = "Dr. Sarah Jenkins, MD",
        )
    }
}

@Composable
private fun ReportStatusChip(status: ReportStatus) {
    val background = when (status) {
        ReportStatus.ATTENTION_NEEDED -> Color(0xFFF9D8D4)
        ReportStatus.NORMAL -> Color(0xFFBFE1FE)
        ReportStatus.REVIEW_REQUIRED -> Color(0xFFEFD4AB)
    }

    val textColor = when (status) {
        ReportStatus.ATTENTION_NEEDED -> Color(0xFF9A1D1A)
        ReportStatus.NORMAL -> Color(0xFF375F7D)
        ReportStatus.REVIEW_REQUIRED -> Color(0xFF3D2A03)
    }

    val label = when (status) {
        ReportStatus.ATTENTION_NEEDED -> "ATTENTION NEEDED"
        ReportStatus.NORMAL -> "NORMAL"
        ReportStatus.REVIEW_REQUIRED -> "REVIEW REQUIRED"
    }

    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.25.sp,
        )
    }
}
