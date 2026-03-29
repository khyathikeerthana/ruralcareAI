package com.simats.ruralcareai.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.simats.ruralcareai.network.DoctorApiClient
import com.simats.ruralcareai.network.DoctorApiResult
import com.simats.ruralcareai.model.UserRole
import com.simats.ruralcareai.viewmodel.AppUiState
import com.simats.ruralcareai.viewmodel.AuthMode
import com.simats.ruralcareai.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RuralCareApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showSplash by rememberSaveable { mutableStateOf(true) }

    if (showSplash) {
        RuralCareSplashScreen(onFinished = { showSplash = false })
        return
    }

    if (!uiState.isLanguageConfirmed) {
        LanguageSelectionScreen(
            selectedLanguage = uiState.selectedLanguage,
            onLanguageSelected = viewModel::selectLanguage,
            onContinue = viewModel::confirmLanguageSelection,
            onBack = { showSplash = true }
        )
        return
    }

    if (!uiState.isOnboardingComplete) {
        OnboardingScreen(
            onComplete = viewModel::completeOnboarding
        )
        return
    }

    if (!uiState.isProfileSelectionComplete) {
        ProfileSelectionScreen(
            selectedRole = uiState.selectedRole,
            errorMessage = uiState.authError,
            onRoleSelected = viewModel::selectRole,
            onBack = viewModel::backToOnboardingFromProfileSelection,
            onContinue = viewModel::continueFromProfileSelection,
        )
        return
    }

    if (!uiState.isSignupFlowComplete) {
        if (uiState.showProfileSetupSuccessScreen) {
            ProfileSetupSuccessScreen(
                onClose = viewModel::goToDashboardFromProfileSetupSuccess,
                onGoToDashboard = viewModel::goToDashboardFromProfileSetupSuccess,
            )
        } else if (uiState.showProfileSetupScreen) {
            ProfileSetupScreen(
                uiState = uiState,
                onBack = viewModel::backToSignupFromProfileSetup,
                onFullNameChanged = viewModel::onProfileSetupFullNameChanged,
                onAgeChanged = viewModel::onProfileSetupAgeChanged,
                onGenderChanged = viewModel::onProfileSetupGenderChanged,
                onVillageChanged = viewModel::onProfileSetupVillageChanged,
                onBloodTypeChanged = viewModel::onProfileSetupBloodTypeChanged,
                onCompleteSetup = viewModel::submitProfileSetup,
            )
        } else if (uiState.showForgotPasswordScreen) {
            ForgotPasswordScreen(
                uiState = uiState,
                onIdentifierChanged = viewModel::onForgotPasswordIdentifierChanged,
                onSendResetLink = viewModel::submitForgotPassword,
                onBack = viewModel::backToLoginFromForgotPassword,
                onBackToLogin = viewModel::backToLoginFromForgotPassword,
                onDismissSuccess = viewModel::dismissForgotPasswordSuccess,
            )
        } else if (uiState.showLoginScreen) {
            LoginScreen(
                uiState = uiState,
                onIdentifierChanged = viewModel::onIdentifierChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onLogin = viewModel::submitLogin,
                onBackClick = viewModel::backToProfileSelectionFromLogin,
                onForgotPassword = viewModel::openForgotPasswordFromLogin,
                showCreateAccountAction = uiState.selectedRole?.supportsSelfSignup == true,
                onCreateAccountClick = viewModel::navigateToSignUpFromLogin,
                onDismissWarning = viewModel::dismissAuthWarning,
                onDismissSuccess = viewModel::dismissLoginSuccess,
            )
        } else {
            SignUpScreen(
                uiState = uiState,
                onFullNameChanged = viewModel::onRegistrationFullNameChanged,
                onPhoneChanged = viewModel::onRegistrationPhoneChanged,
                onEmailChanged = viewModel::onRegistrationEmailChanged,
                onPasswordChanged = viewModel::onRegistrationPasswordChanged,
                onConfirmPasswordChanged = viewModel::onRegistrationConfirmPasswordChanged,
                onCreateAccount = viewModel::submitRegistration,
                onLoginClick = viewModel::openLoginFromSignup,
                onBackClick = viewModel::backToProfileSelectionFromSignup,
                onDismissWarning = viewModel::dismissAuthWarning,
                onDismissSuccess = viewModel::completeSignupFlow,
            )
        }
        return
    }

    if (uiState.isAuthenticated && uiState.selectedRole == UserRole.PATIENT) {
        if (uiState.showChatScreen) {
            PatientChatHubScreen(
                patientId = uiState.currentPatientId,
                onBackToHome = viewModel::closeChatScreen,
            )
        } else if (uiState.showEditProfileScreen) {
            EditPatientProfileScreen(
                uiState = uiState,
                onBack = viewModel::closeEditProfileScreen,
                onSave = viewModel::saveEditedProfile,
            )
        } else if (uiState.showLabReportsScreen) {
            LabReportsScreen(
                onBack = viewModel::closeLabReportsScreen,
            )
        } else if (uiState.showPrescriptionsScreen) {
            PrescriptionsScreen(
                onBack = viewModel::closePrescriptionsScreen,
            )
        } else if (uiState.showMedicalHistoryScreen) {
            MedicalHistoryScreen(
                onBack = viewModel::closeMedicalHistoryScreen,
            )
        } else if (uiState.showProfileScreen) {
            PatientProfileScreen(
                uiState = uiState,
                onBack = viewModel::closeProfileScreen,
                onOpenEditProfile = viewModel::openEditProfileScreen,
                onOpenMedicalHistory = viewModel::openMedicalHistoryScreen,
                onOpenLabReports = viewModel::openLabReportsScreen,
                onOpenPrescriptions = viewModel::openPrescriptionsScreen,
                onSignOut = viewModel::signOut,
            )
        } else if (uiState.showDoctorListingScreen) {
            DoctorListingScreen(
                specialty = uiState.selectedDoctorSpecialty,
                onBack = viewModel::closeDoctorListingScreen,
                patientName = uiState.patientProfile?.fullName ?: uiState.currentUserName,
                patientPhone = uiState.identifier,
            )
        } else if (uiState.showAshaAiScreen) {
            AshaAIScreen(
                patientName = uiState.patientProfile?.fullName ?: uiState.currentUserName ?: "Sarah Jenkins",
                patientId = uiState.currentPatientId,
                onOpenHome = viewModel::closeAshaAIScreen,
                onOpenConsults = viewModel::openConsultsFromAshaAI,
                onOpenChat = viewModel::openChatScreen,
                onOpenProfile = viewModel::openProfileScreen,
            )
        } else if (uiState.showConsultsScreen) {
            ConsultsSpecialtiesScreen(
                onBack = viewModel::closeConsultsScreen,
                onHome = viewModel::closeConsultsScreen,
                onOpenAshaAI = viewModel::openAshaAIScreen,
                onOpenChat = viewModel::openChatScreen,
                onOpenProfile = viewModel::openProfileScreen,
                onSpecialtyClick = viewModel::openDoctorListingForSpecialty,
            )
        } else if (uiState.showMyAppointmentsScreen) {
            MyAppointmentsScreen(
                uiState = uiState,
                onBack = viewModel::closeMyAppointmentsScreen,
            )
        } else if (uiState.showMedicineRemindersScreen) {
            MedicineRemindersScreen(
                uiState = uiState,
                onBack = viewModel::closeMedicineRemindersScreen,
            )
        } else {
            PatientDashboardScreen(
                uiState = uiState,
                onSpecialtyClick = viewModel::openDoctorListingForSpecialty,
                onOpenConsults = viewModel::openConsultsScreen,
                onOpenAshaAI = viewModel::openAshaAIScreen,
                onOpenChat = viewModel::openChatScreen,
                onOpenProfile = viewModel::openProfileScreen,
                onOpenMyAppointments = viewModel::openMyAppointmentsScreen,
                onOpenMedicineReminders = viewModel::openMedicineRemindersScreen,
            )
        }
        return
    }

    if (uiState.isAuthenticated && uiState.selectedRole == UserRole.MEDICAL_PROFESSIONAL) {
        val coroutineScope = rememberCoroutineScope()
        var doctorRefreshTick by rememberSaveable { mutableStateOf(0) }
        var doctorPatientsSearchQuery by rememberSaveable { mutableStateOf("") }

        val doctorIdentityResult by produceState<DoctorApiResult<com.simats.ruralcareai.network.DoctorIdentityDto>?>(
            initialValue = null,
            key1 = uiState.currentUserId,
        ) {
            val userId = uiState.currentUserId
            value = if (userId == null) {
                DoctorApiResult.Error("Doctor session missing")
            } else {
                DoctorApiClient.getDoctorByUser(userId)
            }
        }

        val doctorIdentity = when (val result = doctorIdentityResult) {
            is DoctorApiResult.Success -> result.data
            else -> null
        }
        val doctorId = doctorIdentity?.doctorId

        val dashboardResult by produceState<DoctorApiResult<com.simats.ruralcareai.network.DoctorDashboardDto>?>(
            initialValue = null,
            key1 = doctorId,
            key2 = doctorRefreshTick,
        ) {
            value = if (doctorId == null) null else DoctorApiClient.getDoctorDashboard(doctorId)
        }

        val patientsResult by produceState<DoctorApiResult<List<com.simats.ruralcareai.network.DoctorPatientSummaryDto>>?>(
            initialValue = null,
            doctorId,
            uiState.showDoctorPatientsScreen || uiState.showDoctorScheduleScreen,
            doctorPatientsSearchQuery,
            doctorRefreshTick,
        ) {
            value = if (doctorId == null) null else DoctorApiClient.getDoctorPatients(doctorId, doctorPatientsSearchQuery)
        }

        val selectedPatientDetailResult by produceState<DoctorApiResult<com.simats.ruralcareai.network.DoctorPatientDetailDto>?>(
            initialValue = null,
            doctorId,
            uiState.selectedDoctorPatientId,
            uiState.showDoctorPatientProfileScreen,
            doctorRefreshTick,
        ) {
            val patientId = uiState.selectedDoctorPatientId
            value = if (doctorId == null || patientId == null || !uiState.showDoctorPatientProfileScreen) {
                null
            } else {
                DoctorApiClient.getPatientDetail(doctorId, patientId)
            }
        }

        val analyticsResult by produceState<DoctorApiResult<com.simats.ruralcareai.network.DoctorAnalyticsDto>?>(
            initialValue = null,
            key1 = doctorId,
            key2 = uiState.showDoctorAnalyticsScreen,
            key3 = doctorRefreshTick,
        ) {
            value = if (doctorId == null || !uiState.showDoctorAnalyticsScreen) {
                null
            } else {
                DoctorApiClient.getDoctorAnalytics(doctorId)
            }
        }

        val doctorAppointmentsResult by produceState<DoctorApiResult<List<com.simats.ruralcareai.network.DoctorAppointmentItemDto>>?>(
            initialValue = null,
            key1 = doctorId,
            key2 = doctorRefreshTick,
        ) {
            value = if (doctorId == null) null else DoctorApiClient.getDoctorAppointments(doctorId)
        }

        val doctorProfileResult by produceState<DoctorApiResult<com.simats.ruralcareai.network.DoctorProfileDto>?>(
            initialValue = null,
            key1 = doctorId,
            key2 = doctorRefreshTick,
        ) {
            value = if (doctorId == null) null else DoctorApiClient.getDoctorProfile(doctorId)
        }

        val dashboard = when (val result = dashboardResult) {
            is DoctorApiResult.Success -> result.data
            else -> null
        }
        val patients = when (val result = patientsResult) {
            is DoctorApiResult.Success -> result.data
            else -> emptyList()
        }
        val selectedPatientDetail = when (val result = selectedPatientDetailResult) {
            is DoctorApiResult.Success -> result.data
            else -> null
        }
        val analytics = when (val result = analyticsResult) {
            is DoctorApiResult.Success -> result.data
            else -> null
        }
        val allDoctorAppointments = when (val result = doctorAppointmentsResult) {
            is DoctorApiResult.Success -> result.data
            else -> emptyList()
        }
        val doctorProfile = when (val result = doctorProfileResult) {
            is DoctorApiResult.Success -> result.data
            else -> null
        }
        val waitingAppointments = allDoctorAppointments.filter {
            when (it.status.lowercase()) {
                "waiting", "scheduled", "in_progress", "in-progress" -> true
                else -> false
            }
        }
        val completedAppointments = allDoctorAppointments.filter {
            when (it.status.lowercase()) {
                "done", "completed" -> true
                else -> false
            }
        }
        val waitingDurations = waitingAppointments.mapNotNull { appointment ->
            runCatching {
                Duration.between(OffsetDateTime.parse(appointment.scheduledAt), OffsetDateTime.now()).toMinutes().coerceAtLeast(0)
            }.getOrNull()
        }
        val averageWaitMinutes = if (waitingDurations.isEmpty()) 0 else waitingDurations.average().toInt()
        val dayLabel = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        val openDoctorPatientById: (Int) -> Unit = { patientId ->
            val selected = patients.firstOrNull { it.patientId == patientId }
            val selectedAppointment = allDoctorAppointments.firstOrNull { it.patientId == patientId }
            viewModel.openDoctorPatientProfile(
                patientId = patientId,
                patientName = selected?.fullName ?: selectedAppointment?.patientName ?: "Patient",
                location = selected?.location ?: selectedAppointment?.patientLocation ?: "Unknown",
                status = selected?.status ?: selectedAppointment?.status?.uppercase() ?: "STABLE",
            )
        }

        if (uiState.showDoctorChatScreen) {
            DoctorChatHubScreen(
                doctorId = doctorId,
                initialPatientId = uiState.selectedPatientIdForChat,
                onBackToHome = viewModel::closeDoctorChatScreen,
            )
            return
        }

        if (uiState.showDoctorCompletedScreen) {
            DoctorCompletedSessionsScreen(
                completedCount = completedAppointments.size,
                completedDateLabel = dayLabel,
                completedPatients = completedAppointments.map {
                    CompletedPatient(
                        patientId = it.patientId,
                        name = it.patientName,
                        time = formatTimeLabel(it.scheduledAt),
                    )
                },
                onOpenPatient = openDoctorPatientById,
                onBack = viewModel::closeDoctorCompletedScreen,
            )
            return
        }

        if (uiState.showDoctorWaitingScreen) {
            DoctorWaitingQueueScreen(
                waitingCount = waitingAppointments.size,
                avgWaitMinutes = averageWaitMinutes,
                queueDateLabel = dayLabel,
                waitingPatients = waitingAppointments.map {
                    WaitingPatient(
                        patientId = it.patientId,
                        name = it.patientName,
                        time = formatTimeLabel(it.scheduledAt),
                        status = when (it.status.lowercase()) {
                            "waiting" -> "IN LOBBY"
                            "scheduled" -> "SCHEDULED"
                            else -> "IN PROGRESS"
                        },
                        action = if (it.consultationMode.lowercase() == "video") "Go to Chat" else "Open",
                    )
                },
                onOpenPatient = openDoctorPatientById,
                onBack = viewModel::closeDoctorWaitingScreen,
            )
            return
        }

        if (uiState.showDoctorScheduleScreen) {
            DoctorScheduleAppointmentScreen(
                onBack = viewModel::closeDoctorScheduleScreen,
                patientOptions = patients.map {
                    SchedulePatientOption(patientId = it.patientId, name = it.fullName)
                },
                onConfirmSchedule = { patientId, isoDateTime, mode ->
                    if (doctorId != null && patientId > 0) {
                        coroutineScope.launch {
                            val result = DoctorApiClient.createAppointment(
                                patientId = patientId,
                                doctorId = doctorId,
                                scheduledAt = isoDateTime,
                                consultationMode = mode,
                                reason = "Follow-up consultation",
                            )
                            if (result is DoctorApiResult.Success) {
                                doctorRefreshTick += 1
                                viewModel.closeDoctorScheduleScreen()
                            }
                        }
                    }
                },
            )
            return
        }

        if (uiState.showDoctorAnalyticsScreen) {
            DoctorAnalyticsScreen(
                consultationsDone = analytics?.consultationsDone ?: 0,
                avgConsultationMinutes = analytics?.avgConsultationMinutes ?: 0.0,
                waitingNow = analytics?.waitingNow ?: 0,
                doneToday = analytics?.doneToday ?: 0,
                commonConditions = analytics?.commonConditions ?: emptyList(),
                onOpenHome = viewModel::openDoctorHomeScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
            )
            return
        }

        if (uiState.showDoctorEditProfileScreen) {
            DoctorEditProfileScreen(
                profile = DoctorEditableProfile(
                    fullName = doctorProfile?.fullName ?: doctorIdentity?.fullName ?: uiState.currentUserName.orEmpty(),
                    specialization = doctorProfile?.specialization ?: doctorIdentity?.specialization.orEmpty(),
                    phone = doctorProfile?.phone.orEmpty(),
                    email = doctorProfile?.email.orEmpty(),
                    yearsExperience = doctorProfile?.yearsExperience ?: doctorIdentity?.yearsExperience ?: 0,
                    qualification = doctorProfile?.qualification.orEmpty(),
                    clinicName = doctorProfile?.hospitalName.orEmpty(),
                    clinicAddress = doctorProfile?.assignedLocation.orEmpty(),
                    clinicHours = doctorProfile?.clinicHours.orEmpty(),
                    profilePhotoPath = doctorProfile?.profilePhotoPath ?: uiState.currentUserPhotoPath.orEmpty(),
                ),
                onBack = viewModel::closeDoctorEditProfileScreen,
                onSave = { updatedProfile ->
                    if (doctorId != null) {
                        coroutineScope.launch {
                            val saveResult = DoctorApiClient.updateDoctorProfile(
                                doctorId = doctorId,
                                fullName = updatedProfile.fullName,
                                email = updatedProfile.email,
                                phone = updatedProfile.phone,
                                specialization = updatedProfile.specialization,
                                yearsExperience = updatedProfile.yearsExperience,
                                qualification = updatedProfile.qualification,
                                hospitalName = updatedProfile.clinicName,
                                assignedLocation = updatedProfile.clinicAddress,
                                clinicHours = updatedProfile.clinicHours,
                                languages = doctorProfile?.languages ?: emptyList(),
                                profilePhotoPath = updatedProfile.profilePhotoPath,
                            )
                            if (saveResult is DoctorApiResult.Success) {
                                doctorRefreshTick += 1
                                viewModel.closeDoctorEditProfileScreen()
                            }
                        }
                    }
                },
                onSignOut = viewModel::signOut,
            )
            return
        }

        if (uiState.showDoctorProfileScreen) {
            DoctorDashboardProfileScreen(
                doctorName = doctorProfile?.fullName ?: doctorIdentity?.fullName ?: uiState.currentUserName ?: "Dr. Harpreet Singh",
                doctorSpecialty = doctorProfile?.specialization ?: doctorIdentity?.specialization ?: "General Physician",
                experienceLabel = "${doctorProfile?.yearsExperience ?: doctorIdentity?.yearsExperience ?: 0}+\nYears",
                ratingLabel = if ((analytics?.consultationsDone ?: 0) > 0) "4.8" else "0.0",
                patientsLabel = patients.size.toString(),
                clinicName = doctorProfile?.hospitalName ?: "RuralCareAI Clinic",
                clinicAddress = doctorProfile?.assignedLocation ?: "Assigned via backend doctor profile",
                clinicHours = doctorProfile?.clinicHours ?: "Mon - Fri: 09:00 AM - 06:00 PM",
                onOpenHome = viewModel::openDoctorHomeScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenEditProfile = viewModel::openDoctorEditProfileScreen,
                onOpenSecurity = viewModel::openDoctorSecurityScreen,
                onOpenHelp = viewModel::openDoctorHelpScreen,
                onOpenLanguage = viewModel::openDoctorLanguageScreen,
                onSignOut = viewModel::signOut,
            )
            return
        }

        if (uiState.showDoctorSecurityScreen) {
            DoctorSecurityScreen(
                onBack = viewModel::closeDoctorSecurityScreen,
                onOpenHome = viewModel::openDoctorHomeScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
            )
            return
        }

        if (uiState.showDoctorHelpScreen) {
            DoctorHelpSupportScreen(
                onBack = viewModel::closeDoctorHelpScreen,
                onOpenHome = viewModel::openDoctorHomeScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
            )
            return
        }

        if (uiState.showDoctorLanguageScreen) {
            DoctorLanguageSelectionScreen(
                onBack = viewModel::closeDoctorLanguageScreen,
                onOpenHome = viewModel::openDoctorHomeScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
            )
            return
        }

        if (uiState.showDoctorPatientProfileScreen) {
            DoctorPatientProfileScreen(
                patientName = selectedPatientDetail?.fullName ?: uiState.selectedDoctorPatientName ?: "Patient",
                patientLocation = selectedPatientDetail?.location ?: uiState.selectedDoctorPatientLocation ?: "Nabha",
                patientStatus = uiState.selectedDoctorPatientStatus ?: "STABLE",
                patientIdLabel = "RC-${uiState.selectedDoctorPatientId ?: 0}",
                patientAgeGender = listOfNotNull(
                    selectedPatientDetail?.age?.toString(),
                    selectedPatientDetail?.gender?.firstOrNull()?.uppercaseChar()?.toString(),
                ).joinToString("")
                    .ifBlank { "N/A" },
                bloodType = selectedPatientDetail?.bloodType ?: "Unknown",
                allergyLabel = "Check records",
                primaryReason = selectedPatientDetail?.primaryReason ?: "No recent complaint",
                recordsCount = selectedPatientDetail?.recordsCount ?: 0,
                prescriptionsCount = selectedPatientDetail?.prescriptionsCount ?: 0,
                onBack = viewModel::closeDoctorPatientProfile,
                onOpenHome = viewModel::closeDoctorPatientsScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
                onSavePatientUpdate = {
                    val patientId = uiState.selectedDoctorPatientId
                    if (doctorId != null && patientId != null && selectedPatientDetail != null) {
                        coroutineScope.launch {
                            DoctorApiClient.updatePatient(
                                doctorId = doctorId,
                                patientId = patientId,
                                fullName = selectedPatientDetail.fullName,
                                location = selectedPatientDetail.location ?: "",
                                gender = selectedPatientDetail.gender ?: "Other",
                                bloodType = selectedPatientDetail.bloodType ?: "",
                                weightKg = selectedPatientDetail.weightKg,
                                emergencyContact = selectedPatientDetail.emergencyContact ?: "",
                            )
                            DoctorApiClient.createRecord(
                                patientId = patientId,
                                doctorId = doctorId,
                                recordType = "consultation_summary",
                                title = "Doctor update",
                                description = selectedPatientDetail.primaryReason ?: "Updated by doctor",
                            )
                            doctorRefreshTick += 1
                        }
                    }
                },
            )
            return
        }

        if (uiState.showDoctorPatientsScreen) {
            DoctorPatientsScreen(
                doctorName = uiState.currentUserName ?: "Dr. Jane Smith",
                doctorPhoto = uiState.currentUserPhotoPath,
                patients = patients.map {
                    DoctorPatientItem(
                        id = it.patientId,
                        name = it.fullName,
                        location = it.location ?: "Unknown",
                        status = it.status,
                    )
                },
                searchQuery = doctorPatientsSearchQuery,
                onSearchQueryChange = { doctorPatientsSearchQuery = it },
                onOpenHome = viewModel::closeDoctorPatientsScreen,
                onOpenQueue = viewModel::openDoctorQueueScreen,
                onOpenPatientProfile = { patient ->
                    viewModel.openDoctorPatientProfile(
                        patientId = patient.id,
                        patientName = patient.name,
                        location = patient.location,
                        status = patient.status,
                    )
                },
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
            )
            return
        }

        if (uiState.showDoctorQueueScreen) {
            DoctorDashboardScreen(
                doctorName = uiState.currentUserName ?: "Dr. Jane Smith",
                doctorPhoto = uiState.currentUserPhotoPath,
                stats = DoctorStats(
                    totalAppointments = dashboard?.totalAppointments ?: 0,
                    waitingAppointments = dashboard?.waitingAppointments ?: 0,
                    completedAppointments = dashboard?.doneAppointments ?: 0,
                ),
                queuePatients = (dashboard?.queue ?: emptyList()).map {
                    PatientInQueue(
                        id = it.patientId,
                        name = it.patientName,
                        age = 0,
                        gender = "",
                        location = it.patientLocation ?: "Unknown",
                        photo = null,
                        scheduledTime = formatTimeLabel(it.scheduledAt),
                        status = when (it.status.lowercase()) {
                            "waiting" -> "Waiting"
                            "done" -> "Confirmed"
                            else -> "In-Progress"
                        },
                        isVideoConsultation = it.consultationMode.lowercase() == "video",
                        primaryActionLabel = "View Profile",
                        secondaryActionLabel = "Cancel",
                    )
                },
                onOpenChat = viewModel::openDoctorChatScreen,
                onOpenPatients = viewModel::openDoctorPatientsScreen,
                onOpenQueue = viewModel::closeDoctorQueueScreen,
                onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
                onOpenProfile = viewModel::openDoctorProfileScreen,
                onOpenSchedule = viewModel::openDoctorScheduleScreen,
                onPrimaryQueueAction = { patientId ->
                    val selected = patients.firstOrNull { it.patientId == patientId }
                    if (selected != null) {
                        viewModel.openDoctorPatientProfile(
                            patientId = selected.patientId,
                            patientName = selected.fullName,
                            location = selected.location ?: "Unknown",
                            status = selected.status,
                        )
                    }
                },
                onSecondaryQueueAction = { patientId ->
                    val selected = dashboard?.queue?.firstOrNull { it.patientId == patientId }
                    if (selected != null) {
                        coroutineScope.launch {
                            DoctorApiClient.updateAppointmentStatus(selected.appointmentId, "cancelled")
                            doctorRefreshTick += 1
                        }
                    }
                },
            )
            return
        }

        DoctorDashboardHomeScreen(
            doctorName = dashboard?.doctorName ?: uiState.currentUserName ?: "Dr. Jane Smith",
            doctorPhoto = uiState.currentUserPhotoPath,
            totalAppointments = dashboard?.totalAppointments ?: 0,
            waitingAppointments = dashboard?.waitingAppointments ?: 0,
            nextPatientId = dashboard?.upNext?.patientId,
            nextPatientName = dashboard?.upNext?.patientName ?: "No upcoming patient",
            nextPatientTime = dashboard?.upNext?.scheduledAt?.let { formatTimeLabel(it) } ?: "--:--",
            nextPatientLocation = dashboard?.upNext?.patientLocation ?: "",
            queuePreview = (dashboard?.queue ?: emptyList()).take(3).map {
                HomeQueuePatient(
                    id = it.patientId,
                    name = it.patientName,
                    scheduledTime = formatTimeLabel(it.scheduledAt),
                    status = it.status.replaceFirstChar { ch -> ch.uppercaseChar() },
                )
            },
            onOpenQueue = viewModel::openDoctorQueueScreen,
            onOpenWaiting = viewModel::openDoctorWaitingScreen,
            onOpenCompleted = viewModel::openDoctorCompletedScreen,
            onOpenPatients = viewModel::openDoctorPatientsScreen,
            onOpenChats = viewModel::openDoctorChatsScreen,
            onOpenChat = viewModel::openDoctorChatScreen,
            onOpenQueuePatient = { patientId ->
                val selected = patients.firstOrNull { it.patientId == patientId }
                if (selected != null) {
                    viewModel.openDoctorPatientProfile(
                        patientId = selected.patientId,
                        patientName = selected.fullName,
                        location = selected.location ?: "Unknown",
                        status = selected.status,
                    )
                }
            },
            onOpenAnalytics = viewModel::openDoctorAnalyticsScreen,
            onOpenProfile = viewModel::openDoctorProfileScreen,
        )
        return
    }

    if (uiState.isAuthenticated && uiState.selectedRole == UserRole.COMMUNITY_HEALTH_WORKER) {
        CommunityHealthWorkerDashboardScreen(
            workerName = uiState.currentUserName ?: "Nabha Center Worker",
            currentUserId = uiState.currentUserId,
            onOpenProfile = {},
        )
        return
    }

    if (uiState.isAuthenticated && uiState.selectedRole == UserRole.ADMIN) {
        AdminDashboardScreen(
            onBack = viewModel::signOut,
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                uiState = uiState,
                onBackToRoles = viewModel::clearRoleSelection
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.selectedRole == null -> {
                    RoleSelectionScreen(onRoleSelected = viewModel::selectRole)
                }

                uiState.isAuthenticated -> {
                    RoleDashboardScreen(
                        uiState = uiState,
                        onSignOut = viewModel::signOut
                    )
                }

                else -> {
                    AuthGateScreen(
                        uiState = uiState,
                        onAuthModeChanged = viewModel::setAuthMode,
                        onIdentifierChanged = viewModel::onIdentifierChanged,
                        onPasswordChanged = viewModel::onPasswordChanged,
                        onRememberChanged = viewModel::onRememberMeChanged,
                        onSubmit = viewModel::submitAuth
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTopBar(
    uiState: AppUiState,
    onBackToRoles: () -> Unit
) {
    Surface(shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = "RuralCareAI",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                val subtitle = when {
                    uiState.selectedRole == null -> "Role-based telemedicine access"
                    uiState.isAuthenticated -> "${uiState.selectedRole.displayName} dashboard"
                    else -> "${uiState.selectedRole.displayName} authentication"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.selectedRole != null && !uiState.isAuthenticated) {
                TextButton(onClick = onBackToRoles) {
                    Text("Change role")
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Choose your access role",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Start with Patient, Doctor, or Admin. Each role opens a dedicated workflow.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(UserRole.entries) { role ->
            RoleCard(role = role, onClick = { onRoleSelected(role) })
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Medical disclaimer: this app supports care coordination and communication. It does not provide automated diagnosis.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = role.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = role.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            role.starterActions.take(3).forEach { item ->
                Text(
                    text = "- $item",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun AuthGateScreen(
    uiState: AppUiState,
    onAuthModeChanged: (AuthMode) -> Unit,
    onIdentifierChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRememberChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    val role = uiState.selectedRole ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (uiState.authMode == AuthMode.LOGIN) {
                    "Sign in as ${role.displayName}"
                } else {
                    "Create ${role.displayName} account"
                },
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Authentication is currently local demo mode. API integration will be connected in the next phase.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeButton(
                    label = "Login",
                    selected = uiState.authMode == AuthMode.LOGIN,
                    onClick = { onAuthModeChanged(AuthMode.LOGIN) }
                )
                ModeButton(
                    label = "Register",
                    selected = uiState.authMode == AuthMode.REGISTER,
                    onClick = { onAuthModeChanged(AuthMode.REGISTER) }
                )
            }
        }

        item {
            OutlinedTextField(
                value = uiState.identifier,
                onValueChange = onIdentifierChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Phone or email") }
            )
        }

        item {
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") }
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = onRememberChanged
                )
                Text(
                    text = "Keep me signed in on this device",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (uiState.authError != null) {
            item {
                Text(
                    text = uiState.authError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.authMode == AuthMode.LOGIN) "Continue" else "Create account"
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}

@Composable
private fun RoleDashboardScreen(
    uiState: AppUiState,
    onSignOut: () -> Unit
) {
    val role = uiState.selectedRole ?: return
    val patientProfile = uiState.patientProfile

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${role.displayName} Dashboard",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "First milestone complete: role-based app shell with focused workflows.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (role == UserRole.PATIENT && patientProfile != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Profile Snapshot",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()
                        Text("Name: ${patientProfile.fullName}")
                        Text("Age: ${patientProfile.age ?: "Not set"}")
                        Text("Gender: ${patientProfile.gender ?: "Not set"}")
                        Text("Village: ${patientProfile.village ?: "Not set"}")
                        Text("Blood Type: ${patientProfile.bloodType ?: "Not set"}")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Assigned capabilities",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider()
                    role.starterActions.forEach { action ->
                        Text(
                            text = "- $action",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Consultation module placeholder",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Video, audio, and chat sessions will be integrated with backend consultation APIs in the next phase.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        item {
            Text(
                text = "For rural connectivity, this release keeps data-light UI and text-first workflows.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign out")
            }
        }
    }
}

private fun formatTimeLabel(isoDateTime: String): String {
    return try {
        val parsed = OffsetDateTime.parse(isoDateTime)
        parsed.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (_: Exception) {
        isoDateTime
    }
}