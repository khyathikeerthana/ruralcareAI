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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

private val HistoryBackground = Color(0xFFF2F4F8)
private val HistorySurface = Color(0xFFFDFEFF)
private val HistorySearch = Color(0xFFE4E8EE)
private val HistoryText = Color(0xFF131A22)
private val HistoryMuted = Color(0xFF6F7A89)
private val HistoryPrimary = Color(0xFF0B6FA2)
private val HistoryOutline = Color(0xFFD6DEE9)
private val HistoryBlue = Color(0xFF2D9CDB)
private val HistoryFabBlue = Color(0xFF0E74A8)
private val HistoryFabBlueLight = Color(0xFF2EA2E3)

private enum class HistoryStatus {
    URGENT,
    ONGOING,
    RECOVERED,
}

private enum class HistoryScreenMode {
    LIST,
    DETAILS,
    ADD_RECORD,
    EDIT_RECORD,
}

private data class MedicalHistoryRecord(
    val id: Int,
    val year: Int,
    val title: String,
    val note: String,
    val diagnosedText: String,
    val icon: ImageVector,
    val iconContainer: Color,
    val iconTint: Color,
    val status: HistoryStatus,
)

@Composable
fun MedicalHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    val horizontalPadding = if (isCompact) 14.dp else 16.dp
    val topGap = if (isCompact) 10.dp else 12.dp
    val sectionGap = if (isCompact) 12.dp else 14.dp

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val allRecords = remember {
        mutableStateListOf(
            MedicalHistoryRecord(
                id = 1,
                year = 2024,
                title = "Hypertension",
                note = "Routine monitoring for elevated blood pressure.",
                diagnosedText = "Diagnosed Feb 14, 2024",
                icon = Icons.Filled.Favorite,
                iconContainer = Color(0xFFFCE8E8),
                iconTint = Color(0xFFC62828),
                status = HistoryStatus.URGENT,
            ),
            MedicalHistoryRecord(
                id = 2,
                year = 2024,
                title = "Seasonal Allergies",
                note = "Moderate symptoms during pollination peak.",
                diagnosedText = "Diagnosed Mar 20, 2024",
                icon = Icons.Filled.Air,
                iconContainer = Color(0xFFDDEEFF),
                iconTint = Color(0xFF0A6EA0),
                status = HistoryStatus.ONGOING,
            ),
            MedicalHistoryRecord(
                id = 3,
                year = 2023,
                title = "Acute Bronchitis",
                note = "Viral infection resolved with medication.",
                diagnosedText = "Diagnosed Nov 02, 2023",
                icon = Icons.Filled.Healing,
                iconContainer = Color(0xFFE3ECF8),
                iconTint = Color(0xFF4D6C8A),
                status = HistoryStatus.RECOVERED,
            ),
        )
    }

    var screenMode by rememberSaveable { mutableStateOf(HistoryScreenMode.LIST.name) }
    var selectedRecordId by rememberSaveable { mutableStateOf(allRecords.firstOrNull()?.id ?: -1) }
    var selectedDetails by remember {
        mutableStateOf(allRecords.firstOrNull()?.let(::buildConditionDetails) ?: emptyConditionDetails())
    }

    val currentMode = HistoryScreenMode.valueOf(screenMode)

    BackHandler(
        onBack = {
            if (currentMode == HistoryScreenMode.LIST) {
                onBack()
            } else {
                screenMode = HistoryScreenMode.LIST.name
            }
        },
    )

    when (currentMode) {
        HistoryScreenMode.DETAILS -> {
            ConditionDetailsScreen(
                details = selectedDetails,
                onBack = { screenMode = HistoryScreenMode.LIST.name },
                onEdit = { screenMode = HistoryScreenMode.EDIT_RECORD.name },
                onDelete = {
                    val index = allRecords.indexOfFirst { it.id == selectedRecordId }
                    if (index >= 0) {
                        allRecords.removeAt(index)
                    }

                    if (allRecords.isNotEmpty()) {
                        selectedRecordId = allRecords.first().id
                        selectedDetails = buildConditionDetails(allRecords.first())
                    } else {
                        selectedRecordId = -1
                    }

                    screenMode = HistoryScreenMode.LIST.name
                },
            )
            return
        }

        HistoryScreenMode.ADD_RECORD -> {
            AddMedicalRecordScreen(
                onBack = { screenMode = HistoryScreenMode.LIST.name },
                onSave = { screenMode = HistoryScreenMode.LIST.name },
            )
            return
        }

        HistoryScreenMode.EDIT_RECORD -> {
            EditMedicalRecordScreen(
                initialDetails = selectedDetails,
                onBack = { screenMode = HistoryScreenMode.DETAILS.name },
                onSave = { updatedDetails ->
                    selectedDetails = updatedDetails

                    val selectedIndex = allRecords.indexOfFirst { it.id == selectedRecordId }
                    if (selectedIndex >= 0) {
                        val diagnosisValue = updatedDetails.diagnosedDate.trim()
                        val formattedDiagnosedText = if (diagnosisValue.startsWith("Diagnosed", ignoreCase = true)) {
                            diagnosisValue
                        } else {
                            "Diagnosed $diagnosisValue"
                        }

                        allRecords[selectedIndex] = allRecords[selectedIndex].copy(
                            title = updatedDetails.conditionName,
                            note = condensedHistoryNote(updatedDetails.notes),
                            diagnosedText = formattedDiagnosedText,
                            status = historyStatusFromLabel(updatedDetails.statusLabel),
                        )
                    }

                    screenMode = HistoryScreenMode.DETAILS.name
                },
            )
            return
        }

        HistoryScreenMode.LIST -> Unit
    }

    val query = searchQuery.trim().lowercase()
    val filteredRecords = if (query.isBlank()) {
        allRecords
    } else {
        allRecords.filter { record ->
            record.title.lowercase().contains(query) ||
                record.note.lowercase().contains(query) ||
                record.diagnosedText.lowercase().contains(query) ||
                record.year.toString().contains(query)
        }
    }

    val recordsByYear = filteredRecords
        .groupBy { it.year }
        .toSortedMap(compareByDescending { it })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HistoryBackground)
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
            verticalArrangement = Arrangement.spacedBy(sectionGap),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TopCircleAction(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 44.dp else 46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF4D8CC))
                            .border(2.dp, Color(0xFFDCEAF6), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.StickyNote2,
                            contentDescription = "Medical history",
                            tint = Color(0xFF9A8F8A),
                            modifier = Modifier.size(if (isCompact) 22.dp else 23.dp),
                        )
                    }

                    Text(
                        text = "Medical History",
                        color = HistoryText,
                        fontSize = if (isCompact) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(if (isCompact) 20.dp else 22.dp)),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = HistoryMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search conditions, dates, or notes...",
                            color = HistoryMuted.copy(alpha = 0.9f),
                            fontSize = if (isCompact) 14.sp else 15.sp,
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = HistorySearch,
                        unfocusedContainerColor = HistorySearch,
                        disabledContainerColor = HistorySearch,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = HistoryPrimary,
                    ),
                )
            }

            if (recordsByYear.isEmpty()) {
                item {
                    Text(
                        text = "No records found.",
                        color = HistoryMuted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                recordsByYear.forEach { (year, records) ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = year.toString(),
                                color = HistoryText,
                                fontSize = if (isCompact) 20.sp else 22.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = if (records.size == 1) "1 Record" else "${records.size} Records",
                                color = HistoryMuted,
                                fontSize = if (isCompact) 15.sp else 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    records.forEach { record ->
                        item {
                            MedicalHistoryCard(
                                record = record,
                                compact = isCompact,
                                onOpenDetails = {
                                    selectedRecordId = record.id
                                    selectedDetails = buildConditionDetails(record)
                                    screenMode = HistoryScreenMode.DETAILS.name
                                },
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = horizontalPadding, bottom = 20.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(HistoryFabBlue, HistoryFabBlueLight)))
                .clickable(onClick = { screenMode = HistoryScreenMode.ADD_RECORD.name })
                .padding(horizontal = if (isCompact) 20.dp else 22.dp, vertical = if (isCompact) 12.dp else 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add record",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = "Add Record",
                color = Color.White,
                fontSize = if (isCompact) 15.sp else 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun TopCircleAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, HistoryOutline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = HistoryPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun MedicalHistoryCard(
    record: MedicalHistoryRecord,
    compact: Boolean,
    onOpenDetails: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HistorySurface),
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
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(record.iconContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = record.icon,
                            contentDescription = record.title,
                            tint = record.iconTint,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = record.title,
                            color = HistoryText,
                            fontSize = if (compact) 18.sp else 19.sp,
                            lineHeight = if (compact) 22.sp else 23.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = record.note,
                            color = Color(0xFF303A47),
                            fontSize = if (compact) 13.sp else 14.sp,
                            lineHeight = if (compact) 18.sp else 19.sp,
                            maxLines = 1,
                        )
                    }
                }

                StatusChip(status = record.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Diagnosed date",
                        tint = HistoryMuted,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = record.diagnosedText,
                        color = HistoryMuted,
                        fontSize = if (compact) 13.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onOpenDetails),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Details",
                        color = HistoryPrimary,
                        fontSize = if (compact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View details",
                        tint = HistoryPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: HistoryStatus) {
    val background = when (status) {
        HistoryStatus.URGENT -> Color(0xFFF9D8D4)
        HistoryStatus.ONGOING -> Color(0xFFF4D6AC)
        HistoryStatus.RECOVERED -> Color(0xFFB6D7F3)
    }

    val textColor = when (status) {
        HistoryStatus.URGENT -> Color(0xFF9A1D1A)
        HistoryStatus.ONGOING -> Color(0xFF3D2A03)
        HistoryStatus.RECOVERED -> Color(0xFF385A7A)
    }

    val label = when (status) {
        HistoryStatus.URGENT -> "URGENT"
        HistoryStatus.ONGOING -> "ONGOING"
        HistoryStatus.RECOVERED -> "RECOVERED"
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
        )
    }
}

private fun historyStatusLabel(status: HistoryStatus): String {
    return when (status) {
        HistoryStatus.URGENT -> "Urgent"
        HistoryStatus.ONGOING -> "Ongoing"
        HistoryStatus.RECOVERED -> "Recovered"
    }
}

private fun historyStatusFromLabel(label: String): HistoryStatus {
    return when (label.trim().lowercase()) {
        "urgent" -> HistoryStatus.URGENT
        "recovered" -> HistoryStatus.RECOVERED
        else -> HistoryStatus.ONGOING
    }
}

private fun condensedHistoryNote(note: String): String {
    val compact = note
        .replace("\n", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    if (compact.isEmpty()) {
        return "No additional notes provided."
    }

    return if (compact.length > 72) {
        compact.take(69).trimEnd() + "..."
    } else {
        compact
    }
}

private fun emptyConditionDetails(): ConditionDetailsUi {
    return ConditionDetailsUi(
        conditionName = "-",
        statusLabel = "Ongoing",
        diagnosedDate = "-",
        symptoms = emptyList(),
        treatmentName = "-",
        treatmentDose = "-",
        notes = "No details available.",
        nextCheckupDate = "-",
    )
}

private fun buildConditionDetails(record: MedicalHistoryRecord): ConditionDetailsUi {
    val diagnosedDate = record.diagnosedText.removePrefix("Diagnosed ").trim()

    val symptoms = when (record.title) {
        "Hypertension" -> listOf("Headache", "Dizziness")
        "Seasonal Allergies" -> listOf("Sneezing", "Nasal congestion")
        "Acute Bronchitis" -> listOf("Cough", "Chest discomfort")
        else -> listOf("General symptoms")
    }

    val treatmentName = when (record.title) {
        "Hypertension" -> "Lisinopril 10mg"
        "Seasonal Allergies" -> "Cetirizine 10mg"
        "Acute Bronchitis" -> "Supportive medication"
        else -> "General treatment"
    }

    val treatmentDose = when (record.title) {
        "Hypertension" -> "1 tablet daily"
        "Seasonal Allergies" -> "1 tablet at night"
        "Acute Bronchitis" -> "As advised by doctor"
        else -> "Follow physician advice"
    }

    val nextCheckup = when (record.title) {
        "Hypertension" -> "May 15, 2024"
        "Seasonal Allergies" -> "Jun 02, 2024"
        "Acute Bronchitis" -> "Apr 18, 2024"
        else -> "May 30, 2024"
    }

    return ConditionDetailsUi(
        conditionName = record.title,
        statusLabel = historyStatusLabel(record.status),
        diagnosedDate = diagnosedDate,
        symptoms = symptoms,
        treatmentName = treatmentName,
        treatmentDose = treatmentDose,
        notes = "${record.note} Patient advised to continue follow-up and keep daily symptom logs for review.",
        nextCheckupDate = nextCheckup,
    )
}
