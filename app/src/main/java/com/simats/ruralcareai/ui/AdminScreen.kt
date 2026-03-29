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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AdminAnalyticsDto
import com.simats.ruralcareai.network.AdminAnalyticsFetchResult
import com.simats.ruralcareai.network.AdminApiClient
import com.simats.ruralcareai.network.AdminDashboardFetchResult
import com.simats.ruralcareai.network.AdminDashboardOverviewDto
import com.simats.ruralcareai.network.AdminDoctorDto
import com.simats.ruralcareai.network.AdminDoctorMutationResult
import com.simats.ruralcareai.network.AdminDoctorUpsertPayload
import com.simats.ruralcareai.network.AdminDoctorsFetchResult
import com.simats.ruralcareai.network.AdminOperationResult
import com.simats.ruralcareai.network.AdminPatientDto
import com.simats.ruralcareai.network.AdminPatientsFetchResult
import com.simats.ruralcareai.network.AdminWorkerDto
import com.simats.ruralcareai.network.AdminWorkerMutationResult
import com.simats.ruralcareai.network.AdminWorkerUpsertPayload
import com.simats.ruralcareai.network.AdminWorkersFetchResult
import com.simats.ruralcareai.network.AdminWorkersSummaryDto
import com.simats.ruralcareai.network.AdminWorkersSummaryResult
import kotlinx.coroutines.launch

private val AdminPrimary = Color(0xFF0B6FA2)
private val AdminPrimaryLight = Color(0xFF2EA3E6)
private val AdminBackground = Color(0xFFF2F4F8)
private val AdminSurface = Color(0xFFFFFFFF)
private val AdminText = Color(0xFF131A22)
private val AdminMuted = Color(0xFF6F7A89)
private val AdminOutline = Color(0xFFD6DEE9)
private val AdminInactive = Color(0xFF8B99AD)
private val AdminActiveNavBg = Color(0xFFDDEEFF)

enum class AdminBottomTab {
    DASHBOARD,
    DOCTORS,
    WORKERS,
    ANALYTICS,
}

private enum class AdminScreenMode {
    DASHBOARD,
    PATIENTS,
    DOCTORS,
    DOCTOR_PROFILE,
    ADD_DOCTOR,
    EDIT_DOCTOR,
    WORKERS,
    WORKER_PROFILE,
    ADD_WORKER,
    EDIT_WORKER,
    ANALYTICS,
}

private data class AdminStatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val change: String? = null,
)

private data class AdminActivityEntry(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
)

private data class AdminPatientUi(
    val backendId: Int,
    val fullName: String,
    val village: String,
    val age: Int?,
    val gender: String,
    val phone: String,
    val email: String,
    val joinDate: String,
    val photoPath: String? = null,
)

@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    var doctors by remember { mutableStateOf<List<AdminDoctorUi>>(emptyList()) }
    var patients by remember { mutableStateOf<List<AdminPatientUi>>(emptyList()) }
    var workers by remember { mutableStateOf<List<AdminWorkerUi>>(emptyList()) }
    var dashboardOverview by remember { mutableStateOf<AdminDashboardOverviewDto?>(null) }
    var workersSummary by remember { mutableStateOf<AdminWorkersSummaryDto?>(null) }
    var analytics by remember { mutableStateOf<AdminAnalyticsDto?>(null) }

    var selectedDoctorId by rememberSaveable { mutableStateOf("") }
    var selectedWorkerId by rememberSaveable { mutableStateOf("") }
    var selectedAnalyticsRange by rememberSaveable { mutableStateOf("All Time") }
    var screenMode by rememberSaveable { mutableStateOf(AdminScreenMode.DASHBOARD.name) }
    var isDashboardLoading by remember { mutableStateOf(false) }
    var isAnalyticsLoading by remember { mutableStateOf(false) }
    var adminError by remember { mutableStateOf<String?>(null) }

    suspend fun refreshDashboardData() {
        isDashboardLoading = true
        var firstError: String? = null

        when (val result = AdminApiClient.fetchDashboardOverview(limit = 8)) {
            is AdminDashboardFetchResult.Success -> {
                dashboardOverview = result.overview
            }

            is AdminDashboardFetchResult.Error -> {
                if (firstError == null) {
                    firstError = result.message
                }
            }
        }

        when (val result = AdminApiClient.listDoctors()) {
            is AdminDoctorsFetchResult.Success -> {
                doctors = result.doctors.map { it.toUiModel() }
                if (selectedDoctorId.isBlank()) {
                    selectedDoctorId = doctors.firstOrNull()?.id.orEmpty()
                }
            }

            is AdminDoctorsFetchResult.Error -> {
                if (firstError == null) {
                    firstError = result.message
                }
            }
        }

        when (val result = AdminApiClient.listPatients()) {
            is AdminPatientsFetchResult.Success -> {
                patients = result.patients.map { it.toUiModel() }
            }

            is AdminPatientsFetchResult.Error -> {
                if (firstError == null) {
                    firstError = result.message
                }
            }
        }

        when (val result = AdminApiClient.listWorkers()) {
            is AdminWorkersFetchResult.Success -> {
                workers = result.workers.map { it.toUiModel() }
                if (selectedWorkerId.isBlank()) {
                    selectedWorkerId = workers.firstOrNull()?.id.orEmpty()
                }
            }

            is AdminWorkersFetchResult.Error -> {
                if (firstError == null) {
                    firstError = result.message
                }
            }
        }

        when (val result = AdminApiClient.getWorkersSummary()) {
            is AdminWorkersSummaryResult.Success -> {
                workersSummary = result.summary
            }

            is AdminWorkersSummaryResult.Error -> {
                if (firstError == null) {
                    firstError = result.message
                }
            }
        }

        adminError = firstError
        isDashboardLoading = false
    }

    suspend fun refreshAnalyticsData(rangeLabel: String) {
        isAnalyticsLoading = true
        when (val result = AdminApiClient.fetchAnalytics(analyticsRangeToApiValue(rangeLabel))) {
            is AdminAnalyticsFetchResult.Success -> {
                analytics = result.analytics
            }

            is AdminAnalyticsFetchResult.Error -> {
                adminError = result.message
            }
        }
        isAnalyticsLoading = false
    }

    LaunchedEffect(Unit) {
        refreshDashboardData()
    }

    LaunchedEffect(selectedAnalyticsRange) {
        refreshAnalyticsData(selectedAnalyticsRange)
    }

    val currentMode = AdminScreenMode.valueOf(screenMode)

    BackHandler {
        when (currentMode) {
            AdminScreenMode.DASHBOARD -> onBack()
            AdminScreenMode.PATIENTS -> screenMode = AdminScreenMode.DASHBOARD.name
            AdminScreenMode.DOCTOR_PROFILE -> screenMode = AdminScreenMode.DOCTORS.name
            AdminScreenMode.ADD_DOCTOR -> screenMode = AdminScreenMode.DOCTORS.name
            AdminScreenMode.EDIT_DOCTOR -> screenMode = AdminScreenMode.DOCTOR_PROFILE.name
            AdminScreenMode.WORKER_PROFILE -> screenMode = AdminScreenMode.WORKERS.name
            AdminScreenMode.ADD_WORKER -> screenMode = AdminScreenMode.WORKERS.name
            AdminScreenMode.EDIT_WORKER -> screenMode = AdminScreenMode.WORKER_PROFILE.name

            else -> screenMode = AdminScreenMode.DASHBOARD.name
        }
    }

    when (currentMode) {
        AdminScreenMode.PATIENTS -> {
            AdminPatientsScreen(
                patients = patients,
                onOpenDashboard = { screenMode = AdminScreenMode.DASHBOARD.name },
                onOpenDoctors = { screenMode = AdminScreenMode.DOCTORS.name },
                onOpenWorkers = { screenMode = AdminScreenMode.WORKERS.name },
                onOpenAnalytics = { screenMode = AdminScreenMode.ANALYTICS.name },
            )
            return
        }

        AdminScreenMode.DOCTORS -> {
            AdminDoctorsScreen(
                doctors = doctors,
                onOpenDoctor = {
                    selectedDoctorId = it.id
                    screenMode = AdminScreenMode.DOCTOR_PROFILE.name
                },
                onAddDoctor = { screenMode = AdminScreenMode.ADD_DOCTOR.name },
                onOpenDashboard = { screenMode = AdminScreenMode.DASHBOARD.name },
                onOpenWorkers = { screenMode = AdminScreenMode.WORKERS.name },
                onOpenAnalytics = { screenMode = AdminScreenMode.ANALYTICS.name },
            )
            return
        }

        AdminScreenMode.DOCTOR_PROFILE -> {
            val selectedDoctor = doctors.firstOrNull { it.id == selectedDoctorId } ?: doctors.firstOrNull()
            if (selectedDoctor == null) {
                screenMode = AdminScreenMode.DOCTORS.name
                return
            }

            AdminDoctorProfileScreen(
                doctor = selectedDoctor,
                onBack = { screenMode = AdminScreenMode.DOCTORS.name },
                onEdit = { screenMode = AdminScreenMode.EDIT_DOCTOR.name },
                onDeactivate = {
                    coroutineScope.launch {
                        when (val result = AdminApiClient.deactivateDoctor(selectedDoctor.backendId)) {
                            is AdminOperationResult.Success -> {
                                adminError = null
                                screenMode = AdminScreenMode.DOCTORS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminOperationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                onDelete = {
                    coroutineScope.launch {
                        when (val result = AdminApiClient.deleteDoctor(selectedDoctor.backendId)) {
                            is AdminOperationResult.Success -> {
                                adminError = null
                                screenMode = AdminScreenMode.DOCTORS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminOperationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
            )
            return
        }

        AdminScreenMode.ADD_DOCTOR -> {
            AdminAddDoctorScreen(
                onBack = { screenMode = AdminScreenMode.DOCTORS.name },
                onCreateDoctor = { input ->
                    coroutineScope.launch {
                        when (val result = AdminApiClient.createDoctor(input.toCreateDoctorPayload())) {
                            is AdminDoctorMutationResult.Success -> {
                                adminError = null
                                selectedDoctorId = result.doctor.id.toDoctorUiId()
                                screenMode = AdminScreenMode.DOCTORS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminDoctorMutationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                errorMessage = adminError,
            )
            return
        }

        AdminScreenMode.EDIT_DOCTOR -> {
            val selectedDoctor = doctors.firstOrNull { it.id == selectedDoctorId } ?: doctors.firstOrNull()
            if (selectedDoctor == null) {
                screenMode = AdminScreenMode.DOCTORS.name
                return
            }

            AdminEditDoctorScreen(
                doctor = selectedDoctor,
                onBack = { screenMode = AdminScreenMode.DOCTOR_PROFILE.name },
                onSaveDoctor = { input ->
                    coroutineScope.launch {
                        when (
                            val result = AdminApiClient.updateDoctor(
                                doctorId = selectedDoctor.backendId,
                                payload = input.toUpdateDoctorPayload(selectedDoctor),
                            )
                        ) {
                            is AdminDoctorMutationResult.Success -> {
                                adminError = null
                                selectedDoctorId = result.doctor.id.toDoctorUiId()
                                screenMode = AdminScreenMode.DOCTOR_PROFILE.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminDoctorMutationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                errorMessage = adminError,
            )
            return
        }

        AdminScreenMode.WORKERS -> {
            AdminWorkersScreen(
                workers = workers,
                activeNowCount = workersSummary?.activeNow,
                onFieldCount = workersSummary?.onField,
                onOpenWorker = {
                    selectedWorkerId = it.id
                    screenMode = AdminScreenMode.WORKER_PROFILE.name
                },
                onAddWorker = { screenMode = AdminScreenMode.ADD_WORKER.name },
                onOpenDashboard = { screenMode = AdminScreenMode.DASHBOARD.name },
                onOpenDoctors = { screenMode = AdminScreenMode.DOCTORS.name },
                onOpenAnalytics = { screenMode = AdminScreenMode.ANALYTICS.name },
            )
            return
        }

        AdminScreenMode.WORKER_PROFILE -> {
            val selectedWorker = workers.firstOrNull { it.id == selectedWorkerId } ?: workers.firstOrNull()
            if (selectedWorker == null) {
                screenMode = AdminScreenMode.WORKERS.name
                return
            }

            AdminWorkerProfileScreen(
                worker = selectedWorker,
                onBack = { screenMode = AdminScreenMode.WORKERS.name },
                onEdit = { screenMode = AdminScreenMode.EDIT_WORKER.name },
                onDeactivate = {
                    coroutineScope.launch {
                        when (val result = AdminApiClient.deactivateWorker(selectedWorker.backendId)) {
                            is AdminOperationResult.Success -> {
                                adminError = null
                                screenMode = AdminScreenMode.WORKERS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminOperationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                onDelete = {
                    coroutineScope.launch {
                        when (val result = AdminApiClient.deleteWorker(selectedWorker.backendId)) {
                            is AdminOperationResult.Success -> {
                                adminError = null
                                screenMode = AdminScreenMode.WORKERS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminOperationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
            )
            return
        }

        AdminScreenMode.ADD_WORKER -> {
            AdminAddWorkerScreen(
                onBack = { screenMode = AdminScreenMode.WORKERS.name },
                onCreateWorker = { input ->
                    coroutineScope.launch {
                        when (val result = AdminApiClient.createWorker(input.toCreateWorkerPayload())) {
                            is AdminWorkerMutationResult.Success -> {
                                adminError = null
                                selectedWorkerId = result.worker.id.toWorkerUiId()
                                screenMode = AdminScreenMode.WORKERS.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminWorkerMutationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                errorMessage = adminError,
            )
            return
        }

        AdminScreenMode.EDIT_WORKER -> {
            val selectedWorker = workers.firstOrNull { it.id == selectedWorkerId } ?: workers.firstOrNull()
            if (selectedWorker == null) {
                screenMode = AdminScreenMode.WORKERS.name
                return
            }

            AdminEditWorkerScreen(
                worker = selectedWorker,
                onBack = { screenMode = AdminScreenMode.WORKER_PROFILE.name },
                onSaveWorker = { input ->
                    coroutineScope.launch {
                        when (
                            val result = AdminApiClient.updateWorker(
                                workerId = selectedWorker.backendId,
                                payload = input.toUpdateWorkerPayload(selectedWorker),
                            )
                        ) {
                            is AdminWorkerMutationResult.Success -> {
                                adminError = null
                                selectedWorkerId = result.worker.id.toWorkerUiId()
                                screenMode = AdminScreenMode.WORKER_PROFILE.name
                                refreshDashboardData()
                                refreshAnalyticsData(selectedAnalyticsRange)
                            }

                            is AdminWorkerMutationResult.Error -> {
                                adminError = result.message
                            }
                        }
                    }
                },
                errorMessage = adminError,
            )
            return
        }

        AdminScreenMode.ANALYTICS -> {
            AdminAnalyticsScreen(
                analytics = analytics,
                selectedRange = selectedAnalyticsRange,
                isLoading = isAnalyticsLoading,
                onSelectRange = { selectedAnalyticsRange = it },
                onOpenDashboard = { screenMode = AdminScreenMode.DASHBOARD.name },
                onOpenDoctors = { screenMode = AdminScreenMode.DOCTORS.name },
                onOpenWorkers = { screenMode = AdminScreenMode.WORKERS.name },
            )
            return
        }

        AdminScreenMode.DASHBOARD -> Unit
    }

    AdminOverviewScreen(
        modifier = modifier,
        onBack = onBack,
        dashboardOverview = dashboardOverview,
        recentActivity = dashboardOverview?.recentActivity.orEmpty(),
        isLoading = isDashboardLoading,
        adminError = adminError,
        onOpenDoctors = { screenMode = AdminScreenMode.DOCTORS.name },
        onOpenWorkers = { screenMode = AdminScreenMode.WORKERS.name },
        onOpenAnalytics = { screenMode = AdminScreenMode.ANALYTICS.name },
        onOpenPatients = { screenMode = AdminScreenMode.PATIENTS.name },
        onAddDoctor = { screenMode = AdminScreenMode.ADD_DOCTOR.name },
        onAddWorker = { screenMode = AdminScreenMode.ADD_WORKER.name },
    )
}

@Composable
private fun AdminOverviewScreen(
    modifier: Modifier,
    onBack: () -> Unit,
    dashboardOverview: AdminDashboardOverviewDto?,
    recentActivity: List<com.simats.ruralcareai.network.AdminActivityDto>,
    isLoading: Boolean,
    adminError: String?,
    onOpenDoctors: () -> Unit,
    onOpenWorkers: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPatients: () -> Unit,
    onAddDoctor: () -> Unit,
    onAddWorker: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    val horizontalPadding = if (isCompact) 12.dp else 14.dp
    val topPadding = if (isCompact) 10.dp else 12.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AdminBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = topPadding,
                bottom = if (isCompact) 120.dp else 128.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                AdminHeader(isCompact = isCompact)
            }

            item {
                AdminStatsGrid(
                    isCompact = isCompact,
                    dashboardOverview = dashboardOverview,
                    isLoading = isLoading,
                    onOpenPatients = onOpenPatients,
                )
            }

            if (!adminError.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE7E7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0B8B8)),
                    ) {
                        Text(
                            text = adminError,
                            color = Color(0xFF8D1F1F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        )
                    }
                }
            }

            item {
                AdminActionButtons(
                    isCompact = isCompact,
                    onAddDoctor = onAddDoctor,
                    onAddWorker = onAddWorker,
                )
            }

            item {
                OperationalInsightCard(isCompact = isCompact)
            }

            item {
                RecentActivitySection(
                    isCompact = isCompact,
                    activities = recentActivity,
                    isLoading = isLoading,
                )
            }

            item {
                RegionalCoverageSection(isCompact = isCompact)
            }
        }

        AdminBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = if (isCompact) 10.dp else 12.dp,
                ),
            selectedTab = AdminBottomTab.DASHBOARD,
            onDashboardClick = {},
            onDoctorsClick = onOpenDoctors,
            onWorkersClick = onOpenWorkers,
            onAnalyticsClick = onOpenAnalytics,
        )
    }
}

@Composable
private fun AdminHeader(
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "RuralHealth Admin",
                color = AdminText,
                fontSize = if (isCompact) 17.sp else 19.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "LIVE OPERATIONS DASHBOARD",
                color = AdminMuted,
                fontSize = if (isCompact) 10.sp else 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AdminStatsGrid(
    isCompact: Boolean,
    dashboardOverview: AdminDashboardOverviewDto?,
    isLoading: Boolean,
    onOpenPatients: () -> Unit,
) {
    val totalPatients = dashboardOverview?.totalPatients ?: 0
    val totalDoctors = dashboardOverview?.totalDoctors ?: 0
    val activeConsultations = dashboardOverview?.activeConsultations ?: 0
    val completedToday = dashboardOverview?.completedToday ?: 0
    val placeholder = if (isLoading && dashboardOverview == null) "..." else null

    val stats = listOf(
        AdminStatItem(
            title = "Total Patients",
            value = placeholder ?: totalPatients.toString(),
            icon = Icons.Filled.Person,
            iconBackground = Color(0xFFDDEEF8),
            iconTint = AdminPrimary,
        ),
        AdminStatItem(
            title = "Total Doctors",
            value = placeholder ?: totalDoctors.toString(),
            icon = Icons.Filled.MedicalServices,
            iconBackground = Color(0xFFF4E8D3),
            iconTint = Color(0xFF8E5E00),
        ),
        AdminStatItem(
            title = "Active Consultations",
            value = placeholder ?: activeConsultations.toString(),
            icon = Icons.Filled.Group,
            iconBackground = Color(0xFFDDEEF8),
            iconTint = AdminPrimary,
        ),
        AdminStatItem(
            title = "Completed Today",
            value = placeholder ?: completedToday.toString(),
            icon = Icons.Filled.CheckCircle,
            iconBackground = Color(0xFFE3E8EE),
            iconTint = Color(0xFF4F6B87),
        ),
    )

    Column(verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp),
        ) {
            AdminStatCard(
                item = stats[0],
                compact = isCompact,
                modifier = Modifier.weight(1f),
                onClick = onOpenPatients,
            )
            AdminStatCard(
                item = stats[1],
                compact = isCompact,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp),
        ) {
            AdminStatCard(
                item = stats[2],
                compact = isCompact,
                modifier = Modifier.weight(1f),
                featured = true,
            )
            AdminStatCard(
                item = stats[3],
                compact = isCompact,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AdminStatCard(
    item: AdminStatItem,
    compact: Boolean,
    modifier: Modifier,
    featured: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (featured) 2.dp else 1.dp,
            color = if (featured) AdminPrimary else AdminOutline,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBubble(
                    icon = item.icon,
                    containerColor = item.iconBackground,
                    iconTint = item.iconTint,
                    size = if (compact) 40.dp else 44.dp,
                    iconSize = if (compact) 20.dp else 22.dp,
                )

                if (item.change != null) {
                    Text(
                        text = item.change,
                        color = AdminPrimary,
                        fontSize = if (compact) 11.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(Color(0xFFDDF0FF))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Text(
                text = item.title,
                color = AdminText,
                fontSize = if (compact) 13.sp else 14.sp,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = item.value,
                color = AdminText,
                fontSize = if (compact) 36.sp / 1.75f else 38.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun AdminActionButtons(
    isCompact: Boolean,
    onAddDoctor: () -> Unit,
    onAddWorker: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 10.dp),
        ) {
            AdminPillButton(
                label = "Add Doctor",
                icon = Icons.Filled.PersonAdd,
                containerColor = Color(0xFF0C6F9F),
                textColor = Color.White,
                modifier = Modifier.weight(1f),
                compact = isCompact,
                onClick = onAddDoctor,
            )
            AdminPillButton(
                label = "Add Worker",
                icon = Icons.Filled.Group,
                containerColor = Color(0xFF43A5DC),
                textColor = Color(0xFF052B45),
                modifier = Modifier.weight(1f),
                compact = isCompact,
                onClick = onAddWorker,
            )
        }

        AdminPillButton(
            label = "Create Campaign",
            icon = Icons.Filled.Edit,
            containerColor = Color(0xFFE3E6EB),
            textColor = AdminText,
            modifier = Modifier.fillMaxWidth(),
            compact = isCompact,
            onClick = {},
        )
    }
}

@Composable
private fun AdminPillButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier,
    compact: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(if (compact) 46.dp else 50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(if (compact) 18.dp else 19.dp),
            )
            Text(
                text = label,
                color = textColor,
                fontSize = if (compact) 12.sp else 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun OperationalInsightCard(isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(AdminPrimary, AdminPrimaryLight),
                    ),
                )
                .padding(if (isCompact) 14.dp else 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "OPERATIONAL INSIGHT",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Patient care efficiency is up 12%",
                    color = Color.White,
                    fontSize = if (isCompact) 27.sp / 1.75f else 30.sp / 1.75f,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Faster triage in Bihar North sector.",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = if (isCompact) 12.sp else 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Box(
                modifier = Modifier
                    .size(if (isCompact) 82.dp else 92.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 28.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.28f)),
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 20.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 36.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.34f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(
    isCompact: Boolean,
    activities: List<com.simats.ruralcareai.network.AdminActivityDto>,
    isLoading: Boolean,
) {
    val entries = activities.map { item ->
        val (icon, iconBackground, iconTint) = activityVisual(item.category)
        AdminActivityEntry(
            title = item.title,
            subtitle = item.subtitle,
            icon = icon,
            iconBackground = iconBackground,
            iconTint = iconTint,
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AdminOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 14.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    color = AdminText,
                    fontSize = if (isCompact) 15.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "View All",
                    color = AdminPrimary,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (isLoading && entries.isEmpty()) {
                Text(
                    text = "Loading recent activity...",
                    color = AdminMuted,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            } else if (entries.isEmpty()) {
                Text(
                    text = "No recent activity yet.",
                    color = AdminMuted,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            } else {
                entries.forEach { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        IconBubble(
                            icon = entry.icon,
                            containerColor = entry.iconBackground,
                            iconTint = entry.iconTint,
                            size = if (isCompact) 40.dp else 44.dp,
                            iconSize = if (isCompact) 20.dp else 22.dp,
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = entry.title,
                                color = AdminText,
                                fontSize = if (isCompact) 14.sp else 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = entry.subtitle,
                                color = AdminMuted,
                                fontSize = if (isCompact) 12.sp else 13.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun AdminDoctorDto.toUiModel(): AdminDoctorUi {
    return AdminDoctorUi(
        backendId = id,
        id = id.toDoctorUiId(),
        fullName = fullName,
        specialty = specialty,
        joinDate = joinDate.toUiJoinDate(),
        phone = phone ?: "Not provided",
        email = email ?: "Not provided",
        experienceYears = experienceYears,
        qualification = qualification ?: "Not provided",
        languages = if (languages.isEmpty()) listOf("English") else languages,
        hospital = hospital ?: "Not assigned",
        location = location ?: "Not assigned",
        photoPath = photoPath,
        status = status.toDoctorUiStatus(),
        password = "Doctor@123",
    )
}

private fun AdminPatientDto.toUiModel(): AdminPatientUi {
    return AdminPatientUi(
        backendId = id,
        fullName = fullName,
        village = village ?: "Not assigned",
        age = age,
        gender = gender ?: "Not set",
        phone = phone ?: "Not provided",
        email = email ?: "Not provided",
        joinDate = joinDate.toUiJoinDate(),
        photoPath = photoPath,
    )
}

private fun AdminWorkerDto.toUiModel(): AdminWorkerUi {
    val villageName = assignedVillage ?: "Not assigned"
    return AdminWorkerUi(
        backendId = id,
        id = workerCode,
        fullName = fullName,
        village = villageName,
        joinDate = joinDate.toUiJoinDate(),
        phone = phone ?: "Not provided",
        email = email ?: "Not provided",
        role = roleTitle,
        assignedVillage = villageName,
        photoPath = photoPath,
        status = status.toWorkerUiStatus(),
        password = "Worker@123",
    )
}

private fun AdminDoctorFormInput.toCreateDoctorPayload(): AdminDoctorUpsertPayload {
    return AdminDoctorUpsertPayload(
        fullName = fullName,
        email = email,
        phone = phone,
        password = password.ifBlank { "Doctor@123" },
        specialty = specialization,
        experienceYears = experienceYears,
        qualification = qualification,
        hospital = hospital,
        location = location,
        languages = if (languages.isEmpty()) listOf("English") else languages,
        status = "active",
        isVerified = true,
        photoPath = photoPath,
    )
}

private fun AdminDoctorFormInput.toUpdateDoctorPayload(currentDoctor: AdminDoctorUi): AdminDoctorUpsertPayload {
    return AdminDoctorUpsertPayload(
        fullName = fullName,
        email = email,
        phone = phone,
        password = password.ifBlank { null },
        specialty = specialization,
        experienceYears = experienceYears,
        qualification = qualification,
        hospital = hospital,
        location = location,
        languages = if (languages.isEmpty()) currentDoctor.languages else languages,
        status = currentDoctor.status.toApiDoctorStatus(),
        isVerified = currentDoctor.status == AdminDoctorStatus.ACTIVE,
        photoPath = photoPath,
    )
}

private fun AdminWorkerFormInput.toCreateWorkerPayload(): AdminWorkerUpsertPayload {
    return AdminWorkerUpsertPayload(
        fullName = fullName,
        email = email,
        phone = phone,
        password = password.ifBlank { "Worker@123" },
        workerCode = workerCode,
        assignedVillage = assignedVillage,
        roleTitle = roleTitle,
        status = "active",
        photoPath = photoPath,
    )
}

private fun AdminWorkerFormInput.toUpdateWorkerPayload(currentWorker: AdminWorkerUi): AdminWorkerUpsertPayload {
    return AdminWorkerUpsertPayload(
        fullName = fullName,
        email = email,
        phone = phone,
        password = password.ifBlank { null },
        workerCode = workerCode,
        assignedVillage = assignedVillage,
        roleTitle = roleTitle,
        status = currentWorker.status.toApiWorkerStatus(),
        photoPath = photoPath,
    )
}

private fun String.toDoctorUiStatus(): AdminDoctorStatus {
    return when (lowercase()) {
        "active" -> AdminDoctorStatus.ACTIVE
        "pending" -> AdminDoctorStatus.PENDING
        else -> AdminDoctorStatus.DISABLED
    }
}

private fun String.toWorkerUiStatus(): AdminWorkerStatus {
    return when (lowercase()) {
        "active" -> AdminWorkerStatus.ACTIVE
        "on_field" -> AdminWorkerStatus.ON_FIELD
        else -> AdminWorkerStatus.OFFLINE
    }
}

private fun AdminDoctorStatus.toApiDoctorStatus(): String {
    return when (this) {
        AdminDoctorStatus.ACTIVE -> "active"
        AdminDoctorStatus.PENDING -> "pending"
        AdminDoctorStatus.DISABLED -> "disabled"
    }
}

private fun AdminWorkerStatus.toApiWorkerStatus(): String {
    return when (this) {
        AdminWorkerStatus.ACTIVE -> "active"
        AdminWorkerStatus.ON_FIELD -> "on_field"
        AdminWorkerStatus.OFFLINE -> "offline"
    }
}

private fun Int.toDoctorUiId(): String = "#DR-${toString().padStart(4, '0')}"

private fun Int.toWorkerUiId(): String = "#WK-${toString().padStart(4, '0')}"

private fun String.toUiJoinDate(): String {
    return if (length >= 10) substring(0, 10) else this
}

private fun analyticsRangeToApiValue(rangeLabel: String): String {
    return when (rangeLabel.lowercase()) {
        "week" -> "week"
        "month" -> "month"
        "year" -> "year"
        "all time" -> "all_time"
        else -> "all_time"
    }
}

private fun activityVisual(category: String): Triple<ImageVector, Color, Color> {
    return when (category.lowercase()) {
        "doctor" -> Triple(Icons.Filled.MedicalServices, Color(0xFFF4E8D3), Color(0xFF8E5E00))
        "worker" -> Triple(Icons.Filled.Group, Color(0xFFDDEEF8), AdminPrimary)
        "appointment" -> Triple(Icons.Filled.CheckCircle, Color(0xFFE3E8EE), Color(0xFF4F6B87))
        "consultation" -> Triple(Icons.Filled.History, Color(0xFFE6F0F8), AdminPrimary)
        "prescription" -> Triple(Icons.Filled.Edit, Color(0xFFEAF3FF), AdminPrimary)
        else -> Triple(Icons.Filled.Info, Color(0xFFE6ECF3), AdminMuted)
    }
}

@Composable
private fun AdminPatientsScreen(
    patients: List<AdminPatientUi>,
    onOpenDashboard: () -> Unit,
    onOpenDoctors: () -> Unit,
    onOpenWorkers: () -> Unit,
    onOpenAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AdminBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Patients",
                        color = AdminText,
                        fontSize = if (isCompact) 28.sp / 1.45f else 30.sp / 1.45f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Live patient records from database.",
                        color = AdminMuted,
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AdminSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AdminOutline),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (patients.isEmpty()) {
                            Text(
                                text = "No patients yet.",
                                color = AdminMuted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
                            )
                        } else {
                            patients.forEachIndexed { index, patient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = if (isCompact) 12.dp else 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    IconBubble(
                                        icon = Icons.Filled.Person,
                                        containerColor = Color(0xFFDDEEF8),
                                        iconTint = AdminPrimary,
                                        size = if (isCompact) 44.dp else 48.dp,
                                        iconSize = if (isCompact) 20.dp else 22.dp,
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        Text(
                                            text = patient.fullName,
                                            color = AdminText,
                                            fontSize = if (isCompact) 16.sp / 1.2f else 17.sp / 1.2f,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "${patient.village} | ${patient.gender}",
                                            color = AdminMuted,
                                            fontSize = if (isCompact) 13.sp else 14.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }

                                    Text(
                                        text = patient.age?.let { "$it y" } ?: "-",
                                        color = AdminPrimary,
                                        fontSize = if (isCompact) 13.sp else 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                if (index < patients.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(AdminOutline.copy(alpha = 0.9f)),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AdminBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = if (isCompact) 10.dp else 12.dp),
            selectedTab = AdminBottomTab.DASHBOARD,
            onDashboardClick = onOpenDashboard,
            onDoctorsClick = onOpenDoctors,
            onWorkersClick = onOpenWorkers,
            onAnalyticsClick = onOpenAnalytics,
        )
    }
}

@Composable
private fun RegionalCoverageSection(isCompact: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AdminOutline),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 140.dp else 160.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE8EBF0), Color(0xFFCDD3DC)),
                        ),
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isCompact) 14.dp else 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Regional coverage",
                        tint = AdminPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Regional Coverage: 84%",
                        color = AdminText,
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = "Live Satellite Sync",
                    color = AdminMuted,
                    fontSize = if (isCompact) 12.sp else 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun AdminBottomNavigation(
    modifier: Modifier,
    selectedTab: AdminBottomTab,
    onDashboardClick: () -> Unit,
    onDoctorsClick: () -> Unit,
    onWorkersClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AdminOutline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdminBottomNavItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Home,
                label = "Dashboard",
                selected = selectedTab == AdminBottomTab.DASHBOARD,
                onClick = onDashboardClick,
            )
            AdminBottomNavItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.MedicalServices,
                label = "Doctors",
                selected = selectedTab == AdminBottomTab.DOCTORS,
                onClick = onDoctorsClick,
            )
            AdminBottomNavItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Group,
                label = "Workers",
                selected = selectedTab == AdminBottomTab.WORKERS,
                onClick = onWorkersClick,
            )
            AdminBottomNavItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.History,
                label = "Analytics",
                selected = selectedTab == AdminBottomTab.ANALYTICS,
                onClick = onAnalyticsClick,
            )
        }
    }
}

@Composable
private fun AdminBottomNavItem(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            .background(if (selected) AdminActiveNavBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) AdminPrimary else AdminInactive,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            color = if (selected) AdminPrimary else AdminInactive,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

@Composable
private fun HeaderAction(
    icon: ImageVector,
    showBadge: Boolean,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(AdminSurface)
            .border(1.dp, AdminOutline, androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AdminText,
            modifier = Modifier.size(18.dp),
        )

        if (showBadge) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFD32F2F))
                    .align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    size: Dp,
    iconSize: Dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(iconSize),
        )
    }
}
