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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AdminApiClient
import com.simats.ruralcareai.network.AdminPatientDto
import com.simats.ruralcareai.network.AdminPatientsFetchResult
import com.simats.ruralcareai.network.ChwApiClient
import com.simats.ruralcareai.network.ChwDashboardStatsDto
import com.simats.ruralcareai.network.ChwDashboardStatsResult
import com.simats.ruralcareai.network.ChwUpcomingVisitsResult
import com.simats.ruralcareai.network.ChwWorkerProfileDto
import com.simats.ruralcareai.network.ChwWorkerProfileResult

private val ChwBackground = Color(0xFFF2F4F8)
private val ChwSurface = Color(0xFFFDFEFF)
private val ChwSurfaceAlt = Color(0xFFE9ECF1)
private val ChwText = Color(0xFF121820)
private val ChwMuted = Color(0xFF6D7886)
private val ChwPrimary = Color(0xFF0D6796)
private val ChwPrimaryBright = Color(0xFF2D9CDB)
private val ChwSecondary = Color(0xFF3E627C)
private val ChwAccent = Color(0xFFCA850C)
private val ChwBorder = Color(0xFFD8DEE7)

private data class ChwStatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
)

private data class ChwQuickAction(
    val titleLine1: String,
    val titleLine2: String,
    val icon: ImageVector,
    val iconBg: Color,
)

private data class ChwVisit(
    val time: String,
    val distanceKm: String,
    val patientName: String,
    val reason: String,
    val reasonIcon: ImageVector,
    val reasonColor: Color,
    val address: String,
    val highlighted: Boolean,
    val visitDate: java.time.LocalDate? = null,
)

private enum class ChwScreenMode {
    HOME,
    PATIENTS,
    PATIENT_PROFILE,
    ADD_PATIENT,
    RECORD_VITALS,
    SCHEDULE_CAMP,
}

@Composable
fun CommunityHealthWorkerDashboardScreen(
    workerName: String,
    currentUserId: Int? = null,
    onAddPatient: () -> Unit = {},
    onRecordVitals: () -> Unit = {},
    onScheduleCamp: () -> Unit = {},
    onStartVisit: () -> Unit = {},
    onCallPatient: () -> Unit = {},
    onOpenPatients: () -> Unit = {},
    onOpenVisits: () -> Unit = {},
    onOpenAiHelp: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var screenMode by rememberSaveable { mutableStateOf(ChwScreenMode.HOME.name) }
    var statsRefreshKey by rememberSaveable { mutableIntStateOf(0) }
    var workerProfile by remember { mutableStateOf<ChwWorkerProfileDto?>(null) }
    var dashboardStats by remember { mutableStateOf<ChwDashboardStatsDto?>(null) }
    var upcomingVisits by remember { mutableStateOf<List<ChwVisit>>(emptyList()) }
    var upcomingVisitsError by remember { mutableStateOf<String?>(null) }
    var selectedPatient by remember { mutableStateOf<AdminPatientDto?>(null) }

    val currentMode = ChwScreenMode.valueOf(screenMode)

    LaunchedEffect(currentUserId, statsRefreshKey) {
        if (currentUserId == null) {
            workerProfile = null
            dashboardStats = null
            return@LaunchedEffect
        }

        when (val profileResult = ChwApiClient.getWorkerByUser(currentUserId)) {
            is ChwWorkerProfileResult.Success -> {
                workerProfile = profileResult.profile
                when (val dashboardResult = ChwApiClient.getDashboardStats(profileResult.profile.id)) {
                    is ChwDashboardStatsResult.Success -> {
                        dashboardStats = dashboardResult.stats
                    }

                    is ChwDashboardStatsResult.Error -> {
                        dashboardStats = null
                    }
                }

                when (val visitsResult = ChwApiClient.getUpcomingAppointments()) {
                    is com.simats.ruralcareai.network.ChwUpcomingVisitsResult.Success -> {
                        upcomingVisits = visitsResult.appointments.map { appt ->
                            val scheduledDate = runCatching {
                                java.time.OffsetDateTime.parse(appt.scheduledAt).toLocalDate()
                            }.getOrNull() ?: runCatching {
                                java.time.LocalDateTime.parse(appt.scheduledAt).toLocalDate()
                            }.getOrNull()

                            val visitTime = runCatching {
                                java.time.OffsetDateTime.parse(appt.scheduledAt)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
                            }.getOrNull() ?: runCatching {
                                java.time.LocalDateTime.parse(appt.scheduledAt)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
                            }.getOrNull() ?: "--:--"

                            val reasonText = appt.reason?.takeIf { it.isNotBlank() } ?: "General Checkup"
                            val (icon, iconColor) = if (reasonText.contains("antenatal", true) || reasonText.contains("maternal", true)) {
                                Icons.Filled.PregnantWoman to ChwSecondary
                            } else {
                                Icons.Filled.MedicalServices to ChwAccent
                            }

                            ChwVisit(
                                time = visitTime,
                                distanceKm = appt.patientLocation?.let { "${it}" } ?: "N/A",
                                patientName = appt.patientName ?: "Unknown Patient",
                                reason = reasonText,
                                reasonIcon = icon,
                                reasonColor = iconColor,
                                address = appt.patientLocation ?: "",
                                highlighted = true,
                                visitDate = scheduledDate,
                            )
                        }
                        upcomingVisitsError = null
                    }
                    is com.simats.ruralcareai.network.ChwUpcomingVisitsResult.Error -> {
                        upcomingVisits = emptyList()
                        upcomingVisitsError = visitsResult.message
                    }
                }
            }

            is ChwWorkerProfileResult.Error -> {
                workerProfile = null
                dashboardStats = null
            }
        }
    }

    if (currentMode == ChwScreenMode.PATIENTS) {
        ChwPatientsScreen(
            onBack = { screenMode = ChwScreenMode.HOME.name },
            onPatientSelected = { patient ->
                selectedPatient = patient
                screenMode = ChwScreenMode.PATIENT_PROFILE.name
            },
        )
        return
    }

    if (currentMode == ChwScreenMode.PATIENT_PROFILE) {
        selectedPatient?.let { patient ->
            ChwPatientProfileScreen(
                patient = patient,
                onBack = { screenMode = ChwScreenMode.PATIENTS.name },
            )
        }
        return
    }

    if (currentMode == ChwScreenMode.ADD_PATIENT) {
        ChwAddPatientRecordScreen(
            onBack = { screenMode = ChwScreenMode.HOME.name },
            onSave = {
                statsRefreshKey += 1
                screenMode = ChwScreenMode.HOME.name
            },
            workerId = workerProfile?.id,
            workerVillage = workerProfile?.assignedVillage.orEmpty(),
        )
        return
    }

    if (currentMode == ChwScreenMode.RECORD_VITALS) {
        ChwRecordVitalsScreen(
            onBack = { screenMode = ChwScreenMode.HOME.name },
            onSave = {
                statsRefreshKey += 1
                screenMode = ChwScreenMode.HOME.name
            },
            workerId = workerProfile?.id,
            workerVillage = workerProfile?.assignedVillage.orEmpty(),
        )
        return
    }

    if (currentMode == ChwScreenMode.SCHEDULE_CAMP) {
        ChwScheduleCampScreen(
            onBack = { screenMode = ChwScreenMode.HOME.name },
            onSave = {
                statsRefreshKey += 1
                screenMode = ChwScreenMode.HOME.name
            },
            workerId = workerProfile?.id,
            workerVillage = workerProfile?.assignedVillage.orEmpty(),
        )
        return
    }

    val config = LocalConfiguration.current
    val isCompactHeight = config.screenHeightDp <= 830

    val horizontalPadding = if (isCompactHeight) 14.dp else 16.dp
    val sectionGap = if (isCompactHeight) 14.dp else 16.dp
    val navBottomPadding = if (isCompactHeight) 18.dp else 22.dp

    val stats = listOf(
        ChwStatItem(
            title = "TOTAL\nPATIENTS",
            value = (dashboardStats?.totalPatients ?: 0).toString(),
            icon = Icons.Filled.Group,
            iconBg = Color(0xFFE5F0F9),
            iconTint = ChwSecondary,
        ),
        ChwStatItem(
            title = "VISITS\nTODAY",
            value = (dashboardStats?.visitsToday ?: 0).toString(),
            icon = Icons.Filled.CalendarToday,
            iconBg = Color(0xFFDDEFFF),
            iconTint = ChwPrimary,
        ),
        ChwStatItem(
            title = "VITALS\nCOLLECTED",
            value = (dashboardStats?.vitalsChecked ?: 0).toString(),
            icon = Icons.AutoMirrored.Filled.ShowChart,
            iconBg = Color(0xFFFFF0DB),
            iconTint = ChwAccent,
        ),
    )

    val quickActions = listOf(
        ChwQuickAction(
            titleLine1 = "Add New",
            titleLine2 = "Patient",
            icon = Icons.Filled.PersonAddAlt1,
            iconBg = ChwPrimaryBright,
        ),
        ChwQuickAction(
            titleLine1 = "Record",
            titleLine2 = "Vitals",
            icon = Icons.Filled.MedicalServices,
            iconBg = ChwAccent,
        ),
        ChwQuickAction(
            titleLine1 = "Schedule",
            titleLine2 = "Camp",
            icon = Icons.AutoMirrored.Filled.EventNote,
            iconBg = ChwSecondary,
        ),
    )

    val today = java.time.LocalDate.now()
    val tomorrow = today.plusDays(1)

    val todayVisits = upcomingVisits.filter { it.visitDate == today }
    val tomorrowVisits = upcomingVisits.filter { it.visitDate == tomorrow }


    var selectedDayIndex by rememberSaveable { mutableIntStateOf(0) }
    val visitsToDisplay = if (selectedDayIndex == 0) todayVisits else tomorrowVisits

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChwBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isCompactHeight) 32.dp else 36.dp,
                bottom = 108.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(sectionGap),
        ) {
            item {
                ChwTopHeader(workerName = workerName)
            }

            item {
                ChwCoverageCard(coverageArea = dashboardStats?.assignedVillage ?: workerProfile?.assignedVillage)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    stats.forEach { item ->
                        ChwStatCard(item = item, modifier = Modifier.weight(1f), compact = isCompactHeight)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        color = Color(0xFF2F3946),
                        fontSize = 12.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        quickActions.forEachIndexed { index, action ->
                            val onClick = when (index) {
                                0 -> {
                                    {
                                        onAddPatient()
                                        screenMode = ChwScreenMode.ADD_PATIENT.name
                                    }
                                }

                                1 -> {
                                    {
                                        onRecordVitals()
                                        screenMode = ChwScreenMode.RECORD_VITALS.name
                                    }
                                }

                                else -> {
                                    {
                                        onScheduleCamp()
                                        screenMode = ChwScreenMode.SCHEDULE_CAMP.name
                                    }
                                }
                            }
                            ChwQuickActionCard(
                                action = action,
                                modifier = Modifier.weight(1f),
                                compact = isCompactHeight,
                                onClick = onClick,
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "UPCOMING VISITS",
                            color = Color(0xFF2F3946),
                            fontSize = 13.sp,
                            letterSpacing = 1.6.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        ChwSegmentedSwitch(
                            selectedIndex = selectedDayIndex,
                            options = listOf("Today", "Tomorrow"),
                            onSelected = { selectedDayIndex = it },
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (!upcomingVisitsError.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBE6)),
                            ) {
                                Text(
                                    text = upcomingVisitsError ?: "Unable to load upcoming visits.",
                                    color = Color(0xFF8A1E1E),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        } else if (visitsToDisplay.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                            ) {
                                Text(
                                    text = "No upcoming visits scheduled.",
                                    color = ChwMuted,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        } else {
                            visitsToDisplay.forEach { visit ->
                                ChwVisitCard(
                                    visit = visit,
                                    compact = isCompactHeight,
                                    onStartVisit = onStartVisit,
                                    onCallPatient = onCallPatient,
                                )
                            }
                        }
                    }
                }
            }
        }

        ChwBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            bottomPadding = navBottomPadding,
            onOpenPatients = {
                screenMode = ChwScreenMode.PATIENTS.name
                onOpenPatients()
            },
            onOpenVisits = onOpenVisits,
            onOpenAiHelp = onOpenAiHelp,
            onOpenProfile = onOpenProfile,
        )
    }
}

@Composable
private fun ChwTopHeader(workerName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC1E7E6))
                    .border(2.dp, Color(0xFF9ED3D1), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Worker profile",
                    tint = ChwSecondary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "CENTER HUB",
                    color = Color(0xFF708299),
                    fontSize = 12.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Hello, ${workerName.ifBlank { "Nabha Center Worker" }}",
                    color = ChwText,
                    fontSize = 22.sp / 1.2f,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun ChwCoverageCard(coverageArea: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ChwSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE6F3FC)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Coverage location",
                        tint = ChwPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Active Coverage Area",
                        color = ChwMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = coverageArea?.ifBlank { "Area not assigned" } ?: "Area not assigned",
                        color = ChwText,
                        fontSize = 18.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChwStatCard(item: ChwStatItem, modifier: Modifier = Modifier, compact: Boolean) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ChwSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (compact) 12.dp else 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 42.dp else 44.dp)
                    .clip(CircleShape)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = item.title,
                color = ChwMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.7.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = item.value,
                color = ChwText,
                fontSize = if (compact) 20.sp else 22.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun ChwQuickActionCard(
    action: ChwQuickAction,
    modifier: Modifier = Modifier,
    compact: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ChwSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (compact) 10.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 50.dp else 54.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .background(action.iconBg.copy(alpha = 0.22f)),
                )
                Box(
                    modifier = Modifier
                        .size(if (compact) 42.dp else 46.dp)
                        .clip(CircleShape)
                        .background(action.iconBg),
                )
                Icon(
                    imageVector = action.icon,
                    contentDescription = "${action.titleLine1} ${action.titleLine2}",
                    tint = Color.White,
                    modifier = Modifier.size(if (compact) 20.dp else 22.dp),
                )
            }
            Text(
                text = "${action.titleLine1}\n${action.titleLine2}",
                color = ChwText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun ChwSegmentedSwitch(
    selectedIndex: Int,
    options: List<String>,
    onSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(ChwSurfaceAlt)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) Color.White else Color.Transparent)
                    .clickable { onSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option,
                    color = if (selected) ChwText else ChwMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ChwVisitCard(
    visit: ChwVisit,
    compact: Boolean,
    onStartVisit: () -> Unit,
    onCallPatient: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (visit.highlighted) 1f else 0.82f),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = ChwSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (visit.highlighted) Color(0xFFDCEFFF) else ChwSurfaceAlt)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = visit.time,
                            color = if (visit.highlighted) ChwPrimary else ChwMuted,
                            fontSize = 16.sp / 1.1f,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "• ${visit.distanceKm}",
                        color = ChwMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                if (visit.highlighted) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "More options",
                        tint = ChwMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFD6E5EE)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Patient avatar",
                        tint = ChwSecondary,
                        modifier = Modifier.size(30.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = visit.patientName,
                        color = ChwText,
                        fontSize = if (compact) 24.sp / 1.4f else 26.sp / 1.4f,
                        fontWeight = FontWeight.Bold,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = visit.reasonIcon,
                            contentDescription = "Visit reason",
                            tint = visit.reasonColor,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = visit.reason,
                            color = visit.reasonColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFFE9EDF3))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Address",
                        tint = ChwPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = visit.address,
                        color = Color(0xFF27313E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            if (visit.highlighted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(ChwPrimary)
                            .clickable(onClick = onStartVisit)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Directions,
                                contentDescription = "Start visit",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Start Visit",
                                color = Color.White,
                                fontSize = 16.sp / 1.1f,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ChwSurfaceAlt)
                            .clickable(onClick = onCallPatient),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Call patient",
                            tint = ChwMuted,
                            modifier = Modifier.size(23.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChwBottomNavigation(
    modifier: Modifier = Modifier,
    bottomPadding: androidx.compose.ui.unit.Dp,
    onOpenPatients: () -> Unit,
    onOpenVisits: () -> Unit,
    onOpenAiHelp: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
            .background(Color.White.copy(alpha = 0.97f))
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = bottomPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChwBottomItem(
                label = "HOME",
                icon = Icons.Filled.Home,
                selected = true,
                onClick = {},
            )
            ChwBottomItem(
                label = "PATIENTS",
                icon = Icons.Filled.Group,
                selected = false,
                onClick = onOpenPatients,
            )
            ChwBottomItem(
                label = "VISITS",
                icon = Icons.Filled.CalendarToday,
                selected = false,
                onClick = onOpenVisits,
            )
            ChwBottomItem(
                label = "AI HELP",
                icon = Icons.Filled.AutoAwesome,
                selected = false,
                onClick = onOpenAiHelp,
            )
            ChwBottomItem(
                label = "PROFILE",
                icon = Icons.Filled.Person,
                selected = false,
                onClick = onOpenProfile,
            )
        }
    }
}

@Composable
private fun ChwBottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val activeBackground = Color(0xFFE0F1FF)
    val activeTint = ChwPrimary
    val inactiveTint = Color(0xFF94A1B4)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) activeBackground else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeTint else inactiveTint,
            modifier = Modifier.size(23.dp),
        )
        Text(
            text = label,
            color = if (selected) activeTint else inactiveTint,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}