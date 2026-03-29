package com.simats.ruralcareai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.ruralcareai.model.AppLanguage
import com.simats.ruralcareai.model.UserRole
import com.simats.ruralcareai.network.AuthApiClient
import com.simats.ruralcareai.network.LoginPayload
import com.simats.ruralcareai.network.PatientProfile
import com.simats.ruralcareai.network.ProfileFetchResult
import com.simats.ruralcareai.network.ProfileSetupPayload
import com.simats.ruralcareai.network.ProfileSetupResult
import com.simats.ruralcareai.network.ProfileUpdatePayload
import com.simats.ruralcareai.network.ProfileUpdateResult
import com.simats.ruralcareai.network.RegisterPayload
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    REGISTER
}

private const val LocalAdminEmail = "admin@gmail.com"
private const val LocalAdminPassword = "Admin"

data class PatientProfileUi(
    val fullName: String,
    val email: String?,
    val phone: String?,
    val age: Int?,
    val gender: String?,
    val village: String?,
    val bloodType: String?,
    val weightKg: Double?,
    val photoPath: String?,
)

data class AppUiState(
    val selectedLanguage: AppLanguage = AppLanguage.PUNJABI,
    val isLanguageConfirmed: Boolean = false,
    val isOnboardingComplete: Boolean = false,
    val isProfileSelectionComplete: Boolean = false,
    val isSignupFlowComplete: Boolean = false,
    val selectedRole: UserRole? = null,
    val authMode: AuthMode = AuthMode.LOGIN,
    val identifier: String = "",
    val password: String = "",
    val registrationFullName: String = "",
    val registrationPhone: String = "",
    val registrationEmail: String = "",
    val registrationPassword: String = "",
    val registrationConfirmPassword: String = "",
    val rememberMe: Boolean = true,
    val showLoginScreen: Boolean = false,
    val isRegistering: Boolean = false,
    val registrationSuccessMessage: String? = null,
    val showProfileSetupScreen: Boolean = false,
    val showProfileSetupSuccessScreen: Boolean = false,
    val isSavingProfileSetup: Boolean = false,
    val profileSetupFullName: String = "",
    val profileSetupAge: String = "",
    val profileSetupGender: String = "Male",
    val profileSetupVillage: String = "",
    val profileSetupBloodType: String = "",
    val profileSetupError: String? = null,
    val isLoggingIn: Boolean = false,
    val loginSuccessMessage: String? = null,
    val showForgotPasswordScreen: Boolean = false,
    val forgotPasswordIdentifier: String = "",
    val isSendingResetLink: Boolean = false,
    val forgotPasswordSuccessMessage: String? = null,
    val forgotPasswordError: String? = null,
    val accessToken: String? = null,
    val currentUserId: Int? = null,
    val currentPatientId: Int? = null,
    val currentUserName: String? = null,
    val currentUserEmail: String? = null,
    val currentUserPhone: String? = null,
    val currentUserPhotoPath: String? = null,
    val patientProfile: PatientProfileUi? = null,
    val authError: String? = null,
    val authWarningMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val showConsultsScreen: Boolean = false,
    val showDoctorListingScreen: Boolean = false,
    val showAshaAiScreen: Boolean = false,
    val showChatScreen: Boolean = false,
    val showProfileScreen: Boolean = false,
    val showEditProfileScreen: Boolean = false,
    val showLabReportsScreen: Boolean = false,
    val showMedicalHistoryScreen: Boolean = false,
    val showPrescriptionsScreen: Boolean = false,
    val isSavingProfileEdit: Boolean = false,
    val profileEditError: String? = null,
    val showMyAppointmentsScreen: Boolean = false,
    val showMedicineRemindersScreen: Boolean = false,
    val selectedDoctorSpecialty: String = "General Physician",
    val showDoctorDashboard: Boolean = false,
    val showDoctorChatScreen: Boolean = false,
    val showDoctorQueueScreen: Boolean = false,
    val showDoctorScheduleScreen: Boolean = false,
    val showDoctorWaitingScreen: Boolean = false,
    val showDoctorCompletedScreen: Boolean = false,
    val showDoctorPatientsScreen: Boolean = false,
    val showDoctorPatientProfileScreen: Boolean = false,
    val showDoctorAnalyticsScreen: Boolean = false,
    val showDoctorProfileScreen: Boolean = false,
    val showDoctorEditProfileScreen: Boolean = false,
    val showDoctorSecurityScreen: Boolean = false,
    val showDoctorHelpScreen: Boolean = false,
    val showDoctorLanguageScreen: Boolean = false,
    val selectedPatientIdForChat: Int? = null,
    val selectedDoctorPatientId: Int? = null,
    val selectedDoctorPatientName: String? = null,
    val selectedDoctorPatientLocation: String? = null,
    val selectedDoctorPatientStatus: String? = null,
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun selectLanguage(language: AppLanguage) {
        _uiState.update {
            it.copy(selectedLanguage = language)
        }
    }

    fun confirmLanguageSelection() {
        _uiState.update {
            it.copy(isLanguageConfirmed = true)
        }
    }

    fun completeOnboarding() {
        _uiState.update {
            it.copy(isOnboardingComplete = true)
        }
    }

    fun backToOnboardingFromProfileSelection() {
        _uiState.update {
            it.copy(
                isOnboardingComplete = false,
                isProfileSelectionComplete = false,
                selectedRole = null,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                patientProfile = null,
                authError = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun backToProfileSelectionFromSignup() {
        _uiState.update {
            it.copy(
                isProfileSelectionComplete = false,
                showLoginScreen = false,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                authError = null,
                authWarningMessage = null,
                registrationFullName = "",
                registrationPhone = "",
                registrationEmail = "",
                registrationPassword = "",
                registrationConfirmPassword = "",
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                patientProfile = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun backToProfileSelectionFromLogin() {
        _uiState.update {
            it.copy(
                isProfileSelectionComplete = false,
                showLoginScreen = false,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                identifier = "",
                password = "",
                authError = null,
                authWarningMessage = null,
                loginSuccessMessage = null,
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                patientProfile = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun continueFromProfileSelection() {
        val role = _uiState.value.selectedRole
        if (role == null) {
            _uiState.update { it.copy(authError = "Select a profile to continue.") }
            return
        }

        _uiState.update {
            it.copy(
                isProfileSelectionComplete = true,
                isSignupFlowComplete = false,
                showLoginScreen = !role.supportsSelfSignup,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                isAuthenticated = false,
                authError = null,
                authWarningMessage = null,
                identifier = "",
                password = "",
                registrationFullName = "",
                registrationPhone = "",
                registrationEmail = "",
                registrationPassword = "",
                registrationConfirmPassword = "",
                registrationSuccessMessage = null,
                loginSuccessMessage = null,
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                patientProfile = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun onRegistrationFullNameChanged(value: String) {
        _uiState.update {
            it.copy(registrationFullName = value, authError = null, authWarningMessage = null)
        }
    }

    fun onRegistrationPhoneChanged(value: String) {
        _uiState.update {
            it.copy(registrationPhone = value, authError = null, authWarningMessage = null)
        }
    }

    fun onRegistrationEmailChanged(value: String) {
        _uiState.update {
            it.copy(registrationEmail = value, authError = null, authWarningMessage = null)
        }
    }

    fun onRegistrationPasswordChanged(value: String) {
        _uiState.update {
            it.copy(registrationPassword = value, authError = null, authWarningMessage = null)
        }
    }

    fun onRegistrationConfirmPasswordChanged(value: String) {
        _uiState.update {
            it.copy(registrationConfirmPassword = value, authError = null, authWarningMessage = null)
        }
    }

    fun openLoginFromSignup() {
        _uiState.update {
            it.copy(
                showLoginScreen = true,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupError = null,
                showForgotPasswordScreen = false,
                identifier = "",
                password = "",
                authError = null,
                authWarningMessage = null,
                registrationSuccessMessage = null,
                loginSuccessMessage = null,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
            )
        }
    }

    fun navigateToSignUpFromLogin() {
        _uiState.update {
            it.copy(
                showLoginScreen = false,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupError = null,
                showForgotPasswordScreen = false,
                identifier = "",
                password = "",
                authError = null,
                authWarningMessage = null,
                loginSuccessMessage = null,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
            )
        }
    }

    fun openForgotPasswordFromLogin() {
        val currentIdentifier = _uiState.value.identifier.trim()
        _uiState.update {
            it.copy(
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                showForgotPasswordScreen = true,
                forgotPasswordIdentifier = if (currentIdentifier.isNotBlank()) currentIdentifier else it.forgotPasswordIdentifier,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                authError = null,
                authWarningMessage = null,
                loginSuccessMessage = null,
            )
        }
    }

    fun backToLoginFromForgotPassword() {
        _uiState.update {
            it.copy(
                showForgotPasswordScreen = false,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
            )
        }
    }

    fun onForgotPasswordIdentifierChanged(value: String) {
        _uiState.update {
            it.copy(
                forgotPasswordIdentifier = value,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
            )
        }
    }

    fun submitForgotPassword() {
        val state = _uiState.value
        val identifier = state.forgotPasswordIdentifier.trim()

        if (identifier.isBlank()) {
            _uiState.update { it.copy(forgotPasswordError = "Enter your email or phone number.") }
            return
        }

        _uiState.update {
            it.copy(
                isSendingResetLink = true,
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
            )
        }

        viewModelScope.launch {
            delay(650)
            _uiState.update {
                it.copy(
                    isSendingResetLink = false,
                    forgotPasswordSuccessMessage = "Reset link sent",
                    forgotPasswordError = null,
                )
            }
        }
    }

    fun dismissForgotPasswordSuccess() {
        _uiState.update {
            it.copy(forgotPasswordSuccessMessage = null)
        }
    }

    fun backToSignupFromProfileSetup() {
        _uiState.update {
            it.copy(
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupError = null,
            )
        }
    }

    fun onProfileSetupFullNameChanged(value: String) {
        _uiState.update {
            it.copy(profileSetupFullName = value, profileSetupError = null)
        }
    }

    fun onProfileSetupAgeChanged(value: String) {
        val filtered = value.filter { char -> char.isDigit() }
        _uiState.update {
            it.copy(profileSetupAge = filtered, profileSetupError = null)
        }
    }

    fun onProfileSetupGenderChanged(value: String) {
        _uiState.update {
            it.copy(profileSetupGender = value, profileSetupError = null)
        }
    }

    fun onProfileSetupVillageChanged(value: String) {
        _uiState.update {
            it.copy(profileSetupVillage = value, profileSetupError = null)
        }
    }

    fun onProfileSetupBloodTypeChanged(value: String) {
        _uiState.update {
            it.copy(profileSetupBloodType = value, profileSetupError = null)
        }
    }

    fun submitProfileSetup() {
        val state = _uiState.value
        val userId = state.currentUserId
        val fullName = state.profileSetupFullName.trim()
        val age = state.profileSetupAge.toIntOrNull()
        val village = state.profileSetupVillage.trim()
        val bloodType = state.profileSetupBloodType.trim().uppercase()
        val allowedGenders = setOf("Male", "Female", "Other")

        when {
            userId == null -> {
                _uiState.update { it.copy(profileSetupError = "Unable to identify the user session.") }
                return
            }
            fullName.length < 2 -> {
                _uiState.update { it.copy(profileSetupError = "Enter your full name.") }
                return
            }
            age == null || age !in 1..120 -> {
                _uiState.update { it.copy(profileSetupError = "Enter a valid age.") }
                return
            }
            state.profileSetupGender !in allowedGenders -> {
                _uiState.update { it.copy(profileSetupError = "Select a valid gender.") }
                return
            }
            village.length < 2 -> {
                _uiState.update { it.copy(profileSetupError = "Enter your village.") }
                return
            }
            bloodType.isBlank() -> {
                _uiState.update { it.copy(profileSetupError = "Select blood type.") }
                return
            }
        }

        _uiState.update {
            it.copy(
                isSavingProfileSetup = true,
                profileSetupError = null,
            )
        }

        viewModelScope.launch {
            when (
                val result = AuthApiClient.setupPatientProfile(
                    ProfileSetupPayload(
                        userId = userId,
                        fullName = fullName,
                        age = age,
                        gender = state.profileSetupGender,
                        village = village,
                        bloodType = bloodType,
                    )
                )
            ) {
                is ProfileSetupResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSavingProfileSetup = false,
                            showProfileSetupScreen = false,
                            showProfileSetupSuccessScreen = true,
                            profileSetupError = null,
                            profileSetupFullName = result.profile.fullName,
                            profileSetupAge = result.profile.age?.toString().orEmpty(),
                            profileSetupGender = result.profile.gender ?: "Male",
                            profileSetupVillage = result.profile.village.orEmpty(),
                            profileSetupBloodType = result.profile.bloodType.orEmpty(),
                            currentUserName = result.profile.fullName,
                            currentUserEmail = result.profile.email,
                            currentUserPhone = result.profile.phone,
                            patientProfile = result.profile.toUiProfile(),
                        )
                    }
                }
                is ProfileSetupResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSavingProfileSetup = false,
                            profileSetupError = result.message,
                        )
                    }
                }
            }
        }
    }

    fun goToDashboardFromProfileSetupSuccess() {
        _uiState.update {
            it.copy(
                showProfileSetupSuccessScreen = false,
                isSignupFlowComplete = true,
                isAuthenticated = true,
                profileSetupError = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openDoctorListingForSpecialty(specialty: String) {
        _uiState.update {
            it.copy(
                showDoctorListingScreen = true,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                selectedDoctorSpecialty = specialty,
            )
        }
    }

    fun openConsultsScreen() {
        _uiState.update {
            it.copy(
                showConsultsScreen = true,
                showAshaAiScreen = false,
                showDoctorListingScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openAshaAIScreen() {
        _uiState.update {
            it.copy(
                showAshaAiScreen = true,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openChatScreen() {
        _uiState.update {
            it.copy(
                showChatScreen = true,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openProfileScreen() {
        _uiState.update {
            it.copy(
                showProfileScreen = true,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun closeProfileScreen() {
        _uiState.update {
            it.copy(
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun openEditProfileScreen() {
        _uiState.update {
            it.copy(
                showEditProfileScreen = true,
                showProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun closeEditProfileScreen() {
        _uiState.update {
            it.copy(
                showEditProfileScreen = false,
                showProfileScreen = true,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
            )
        }
    }

    fun openLabReportsScreen() {
        _uiState.update {
            it.copy(
                showLabReportsScreen = true,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun closeLabReportsScreen() {
        _uiState.update {
            it.copy(
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                showProfileScreen = true,
                showEditProfileScreen = false,
            )
        }
    }

    fun openMedicalHistoryScreen() {
        _uiState.update {
            it.copy(
                showMedicalHistoryScreen = true,
                showLabReportsScreen = false,
                showPrescriptionsScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun closeMedicalHistoryScreen() {
        _uiState.update {
            it.copy(
                showMedicalHistoryScreen = false,
                showLabReportsScreen = false,
                showPrescriptionsScreen = false,
                showProfileScreen = true,
                showEditProfileScreen = false,
            )
        }
    }

    fun openPrescriptionsScreen() {
        _uiState.update {
            it.copy(
                showPrescriptionsScreen = true,
                showMedicalHistoryScreen = false,
                showLabReportsScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showMyAppointmentsScreen = false,
                showMedicineRemindersScreen = false,
            )
        }
    }

    fun closePrescriptionsScreen() {
        _uiState.update {
            it.copy(
                showPrescriptionsScreen = false,
                showMedicalHistoryScreen = false,
                showLabReportsScreen = false,
                showProfileScreen = true,
                showEditProfileScreen = false,
            )
        }
    }

    fun saveEditedProfile(
        fullName: String,
        email: String,
        phone: String,
        village: String,
        bloodType: String,
        age: Int?,
        gender: String?,
        weightKg: Double? = null,
        photoPath: String? = null,
    ) {
        val state = _uiState.value
        val userId = state.currentUserId

        val normalizedFullName = fullName.trim()
        val normalizedEmail = email.trim().lowercase()
        val normalizedPhone = phone.trim()
        val normalizedVillage = village.trim()
        val normalizedBloodType = bloodType.trim().uppercase()

        when {
            userId == null -> {
                _uiState.update { it.copy(profileEditError = "Unable to identify the user session.") }
                return
            }
            normalizedFullName.length < 2 -> {
                _uiState.update { it.copy(profileEditError = "Enter a valid full name.") }
                return
            }
            !normalizedEmail.contains("@") -> {
                _uiState.update { it.copy(profileEditError = "Enter a valid email address.") }
                return
            }
            normalizedPhone.length < 7 -> {
                _uiState.update { it.copy(profileEditError = "Enter a valid phone number.") }
                return
            }
            normalizedVillage.length < 2 -> {
                _uiState.update { it.copy(profileEditError = "Enter a valid location.") }
                return
            }
            normalizedBloodType.isBlank() -> {
                _uiState.update { it.copy(profileEditError = "Select a valid blood group.") }
                return
            }
        }

        _uiState.update {
            it.copy(
                isSavingProfileEdit = true,
                profileEditError = null,
            )
        }

        viewModelScope.launch {
            when (
                val result = AuthApiClient.updatePatientProfile(
                    userId = userId,
                    payload = ProfileUpdatePayload(
                        fullName = normalizedFullName,
                        email = normalizedEmail,
                        phone = normalizedPhone,
                        village = normalizedVillage,
                        bloodType = normalizedBloodType,
                        age = age,
                        gender = gender,
                        weightKg = weightKg,
                        photoPath = photoPath,
                    )
                )
            ) {
                is ProfileUpdateResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isSavingProfileEdit = false,
                            showEditProfileScreen = false,
                            showProfileScreen = true,
                            profileEditError = null,
                            currentUserName = result.profile.fullName,
                            currentUserEmail = result.profile.email,
                            currentUserPhone = result.profile.phone,
                            currentUserPhotoPath = result.profile.photoPath,
                            patientProfile = result.profile.toUiProfile(),
                        )
                    }
                }
                is ProfileUpdateResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSavingProfileEdit = false,
                            profileEditError = result.message,
                        )
                    }
                }
            }
        }
    }

    fun closeChatScreen() {
        _uiState.update {
            it.copy(
                showChatScreen = false,
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun closeAshaAIScreen() {
        _uiState.update {
            it.copy(
                showAshaAiScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openConsultsFromAshaAI() {
        _uiState.update {
            it.copy(
                showAshaAiScreen = false,
                showConsultsScreen = true,
                showDoctorListingScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun closeConsultsScreen() {
        _uiState.update {
            it.copy(
                showAshaAiScreen = false,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun closeDoctorListingScreen() {
        _uiState.update {
            it.copy(
                showDoctorListingScreen = false,
                showChatScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openMyAppointmentsScreen() {
        _uiState.update {
            it.copy(
                showMyAppointmentsScreen = true,
                showMedicineRemindersScreen = false,
                showConsultsScreen = false,
                showAshaAiScreen = false,
                showChatScreen = false,
                showDoctorListingScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun closeMyAppointmentsScreen() {
        _uiState.update {
            it.copy(
                showMyAppointmentsScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun openMedicineRemindersScreen() {
        _uiState.update {
            it.copy(
                showMedicineRemindersScreen = true,
                showMyAppointmentsScreen = false,
                showConsultsScreen = false,
                showAshaAiScreen = false,
                showChatScreen = false,
                showDoctorListingScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun closeMedicineRemindersScreen() {
        _uiState.update {
            it.copy(
                showMedicineRemindersScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
            )
        }
    }

    fun dismissProfileSetupSuccess() {
        _uiState.update {
            it.copy(showProfileSetupSuccessScreen = false)
        }
    }

    fun submitLogin() {
        val state = _uiState.value
        val identifier = state.identifier.trim()
        val password = state.password

        when {
            identifier.isBlank() -> {
                _uiState.update { it.copy(authError = "Enter your email or phone number.") }
                return
            }
            password.length < 4 -> {
                _uiState.update { it.copy(authError = "Enter your password.") }
                return
            }
        }

        if (
            state.selectedRole == UserRole.ADMIN &&
            identifier.equals(LocalAdminEmail, ignoreCase = true) &&
            password == LocalAdminPassword
        ) {
            _uiState.update {
                it.copy(
                    isLoggingIn = false,
                    loginSuccessMessage = "Login Successful",
                    accessToken = "local-admin-token",
                    currentUserId = -1,
                    currentPatientId = null,
                    currentUserName = "RuralHealth Admin",
                    currentUserEmail = LocalAdminEmail,
                    currentUserPhone = null,
                    currentUserPhotoPath = null,
                    patientProfile = null,
                    selectedRole = UserRole.ADMIN,
                    authError = null,
                    authWarningMessage = null,
                    showProfileSetupScreen = false,
                    showProfileSetupSuccessScreen = false,
                    profileSetupError = null,
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoggingIn = true,
                authError = null,
                authWarningMessage = null,
                loginSuccessMessage = null,
            )
        }

        viewModelScope.launch {
            when (
                val result = AuthApiClient.login(
                    LoginPayload(
                        identifier = identifier,
                        password = password,
                        expectedRole = state.selectedRole?.backendRole,
                    )
                )
            ) {
                is com.simats.ruralcareai.network.LoginResult.Success -> {
                    val resolvedRole = state.selectedRole ?: UserRole.PATIENT
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            loginSuccessMessage = "Login Successful",
                            accessToken = result.session.accessToken,
                            currentUserId = result.session.userId,
                            currentPatientId = result.session.patientId,
                            currentUserName = result.session.fullName,
                            currentUserEmail = result.session.email,
                            currentUserPhone = result.session.phone,
                            selectedRole = resolvedRole,
                            authError = null,
                            authWarningMessage = null,
                            showProfileSetupScreen = false,
                            showProfileSetupSuccessScreen = false,
                            profileSetupError = null,
                        )
                    }
                    loadPatientProfileIfAvailable(result.session.userId, resolvedRole)
                }
                is com.simats.ruralcareai.network.LoginResult.Error -> {
                    _uiState.update {
                        val mismatch = isProfileMismatchMessage(result.message)
                        it.copy(
                            isLoggingIn = false,
                            authError = if (mismatch) null else result.message,
                            authWarningMessage = if (mismatch) result.message else null,
                        )
                    }
                }
            }
        }
    }

    fun dismissLoginSuccess() {
        val state = _uiState.value
        val needsProfileSetup = state.selectedRole == UserRole.PATIENT && state.patientProfile == null

        _uiState.update {
            it.copy(
                loginSuccessMessage = null,
                isSignupFlowComplete = !needsProfileSetup,
                isAuthenticated = true,
                showLoginScreen = false,
                showProfileSetupScreen = needsProfileSetup,
                showProfileSetupSuccessScreen = false,
                showForgotPasswordScreen = false,
                authWarningMessage = null,
                profileSetupFullName = if (needsProfileSetup) it.currentUserName.orEmpty() else it.profileSetupFullName,
                profileSetupAge = if (needsProfileSetup) "" else it.profileSetupAge,
                profileSetupGender = if (needsProfileSetup) "Male" else it.profileSetupGender,
                profileSetupVillage = if (needsProfileSetup) "" else it.profileSetupVillage,
                profileSetupBloodType = if (needsProfileSetup) "" else it.profileSetupBloodType,
                profileSetupError = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
            )
        }
    }

    fun dismissAuthWarning() {
        _uiState.update {
            it.copy(authWarningMessage = null)
        }
    }

    fun completeSignupFlow() {
        _uiState.update {
            it.copy(
                isSignupFlowComplete = true,
                registrationSuccessMessage = null,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupError = null,
                authError = null,
                authWarningMessage = null,
            )
        }
    }

    fun submitRegistration() {
        val state = _uiState.value
        val fullName = state.registrationFullName.trim()
        val phone = state.registrationPhone.trim()
        val email = state.registrationEmail.trim()
        val password = state.registrationPassword
        val confirmPassword = state.registrationConfirmPassword

        when {
            fullName.length < 2 -> {
                _uiState.update { it.copy(authError = "Enter your full name.") }
                return
            }

            phone.length < 7 -> {
                _uiState.update { it.copy(authError = "Enter a valid phone number.") }
                return
            }

            !email.contains("@") -> {
                _uiState.update { it.copy(authError = "Enter a valid email address.") }
                return
            }

            password.length < 8 -> {
                _uiState.update { it.copy(authError = "Password must be at least 8 characters.") }
                return
            }

            password != confirmPassword -> {
                _uiState.update { it.copy(authError = "Passwords do not match.") }
                return
            }
        }

        _uiState.update {
            it.copy(
                isRegistering = true,
                authError = null,
                authWarningMessage = null,
                registrationSuccessMessage = null,
            )
        }

        viewModelScope.launch {
            when (
                val result = AuthApiClient.register(
                    RegisterPayload(
                        fullName = fullName,
                        phone = phone,
                        email = email,
                        password = password,
                        role = (state.selectedRole ?: UserRole.PATIENT).backendRole,
                    )
                )
            ) {
                is com.simats.ruralcareai.network.RegistrationResult.Success -> {
                    val resolvedRole = state.selectedRole ?: UserRole.PATIENT
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            selectedRole = resolvedRole,
                            identifier = result.session.email ?: result.session.phone.orEmpty(),
                            password = "",
                            registrationPassword = "",
                            registrationConfirmPassword = "",
                            registrationSuccessMessage = null,
                            showProfileSetupScreen = resolvedRole == UserRole.PATIENT,
                            showProfileSetupSuccessScreen = false,
                            isSavingProfileSetup = false,
                            profileSetupFullName = fullName,
                            profileSetupAge = "",
                            profileSetupGender = "Male",
                            profileSetupVillage = "",
                            profileSetupBloodType = "",
                            profileSetupError = null,
                            showLoginScreen = false,
                            showForgotPasswordScreen = false,
                            accessToken = result.session.accessToken,
                            currentUserId = result.session.userId,
                            currentPatientId = result.session.patientId,
                            currentUserName = result.session.fullName,
                            currentUserEmail = result.session.email,
                            currentUserPhone = result.session.phone,
                            authError = null,
                            authWarningMessage = null,
                            isAuthenticated = true,
                            isSignupFlowComplete = resolvedRole != UserRole.PATIENT,
                            patientProfile = null,
                            showConsultsScreen = false,
                            showDoctorListingScreen = false,
                            showAshaAiScreen = false,
                            showProfileScreen = false,
                            showEditProfileScreen = false,
                            showLabReportsScreen = false,
                            showMedicalHistoryScreen = false,
                            showPrescriptionsScreen = false,
                            selectedDoctorSpecialty = "General Physician",
                        )
                    }
                }

                is com.simats.ruralcareai.network.RegistrationResult.Error -> {
                    _uiState.update {
                        val mismatch = isProfileMismatchMessage(result.message)
                        it.copy(
                            isRegistering = false,
                            authError = if (mismatch) null else result.message,
                            authWarningMessage = if (mismatch) result.message else null,
                        )
                    }
                }
            }
        }
    }

    fun selectRole(role: UserRole) {
        _uiState.update {
            it.copy(
                selectedRole = role,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                authError = null,
                authWarningMessage = null,
                isAuthenticated = false,
                identifier = "",
                password = "",
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                currentUserId = null,
                currentPatientId = null,
                currentUserName = null,
                currentUserEmail = null,
                currentUserPhone = null,
                patientProfile = null,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun clearRoleSelection() {
        _uiState.update {
            it.copy(
                selectedRole = null,
                isProfileSelectionComplete = false,
                isSignupFlowComplete = false,
                showLoginScreen = false,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                authError = null,
                authWarningMessage = null,
                isAuthenticated = false,
                identifier = "",
                password = "",
                registrationFullName = "",
                registrationPhone = "",
                registrationEmail = "",
                registrationPassword = "",
                registrationConfirmPassword = "",
                registrationSuccessMessage = null,
                loginSuccessMessage = null,
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                accessToken = null,
                currentUserId = null,
                currentPatientId = null,
                currentUserName = null,
                currentUserEmail = null,
                currentUserPhone = null,
                currentUserPhotoPath = null,
                patientProfile = null,
                authMode = AuthMode.LOGIN,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                showPrescriptionsScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    fun setAuthMode(mode: AuthMode) {
        _uiState.update {
            it.copy(authMode = mode, authError = null)
        }
    }

    fun onIdentifierChanged(value: String) {
        _uiState.update {
            it.copy(identifier = value, authError = null, authWarningMessage = null)
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(password = value, authError = null, authWarningMessage = null)
        }
    }

    fun onRememberMeChanged(value: Boolean) {
        _uiState.update {
            it.copy(rememberMe = value)
        }
    }

    fun submitAuth() {
        val state = _uiState.value
        if (state.identifier.isBlank()) {
            _uiState.update {
                it.copy(authError = "Enter phone number or email to continue.")
            }
            return
        }

        if (state.password.length < 4) {
            _uiState.update {
                it.copy(authError = "Password must be at least 4 characters.")
            }
            return
        }

        _uiState.update {
            it.copy(isAuthenticated = true, authError = null, authWarningMessage = null)
        }
    }

    fun signOut() {
        _uiState.update {
            it.copy(
                selectedRole = null,
                isProfileSelectionComplete = false,
                isSignupFlowComplete = false,
                showLoginScreen = false,
                showProfileSetupScreen = false,
                showProfileSetupSuccessScreen = false,
                isSavingProfileSetup = false,
                profileSetupFullName = "",
                profileSetupAge = "",
                profileSetupGender = "Male",
                profileSetupVillage = "",
                profileSetupBloodType = "",
                profileSetupError = null,
                showForgotPasswordScreen = false,
                isAuthenticated = false,
                authError = null,
                authWarningMessage = null,
                identifier = "",
                password = "",
                registrationFullName = "",
                registrationPhone = "",
                registrationEmail = "",
                registrationPassword = "",
                registrationConfirmPassword = "",
                registrationSuccessMessage = null,
                loginSuccessMessage = null,
                forgotPasswordIdentifier = "",
                forgotPasswordError = null,
                forgotPasswordSuccessMessage = null,
                isSendingResetLink = false,
                accessToken = null,
                currentUserId = null,
                currentPatientId = null,
                currentUserName = null,
                currentUserEmail = null,
                currentUserPhone = null,
                patientProfile = null,
                authMode = AuthMode.LOGIN,
                showConsultsScreen = false,
                showDoctorListingScreen = false,
                showAshaAiScreen = false,
                showProfileScreen = false,
                showEditProfileScreen = false,
                showLabReportsScreen = false,
                showMedicalHistoryScreen = false,
                isSavingProfileEdit = false,
                profileEditError = null,
                selectedDoctorSpecialty = "General Physician",
            )
        }
    }

    private suspend fun loadPatientProfileIfAvailable(userId: Int, role: UserRole) {
        if (role != UserRole.PATIENT) {
            _uiState.update {
                it.copy(
                    patientProfile = null,
                    currentUserEmail = null,
                    currentUserPhone = null,
                    currentUserPhotoPath = null,
                )
            }
            return
        }

        when (val profileResult = AuthApiClient.fetchPatientProfile(userId)) {
            is ProfileFetchResult.Success -> {
                _uiState.update {
                    it.copy(
                        patientProfile = profileResult.profile.toUiProfile(),
                        currentUserName = profileResult.profile.fullName,
                        currentUserEmail = profileResult.profile.email,
                        currentUserPhone = profileResult.profile.phone,
                        currentUserPhotoPath = profileResult.profile.photoPath,
                    )
                }
            }
            ProfileFetchResult.NotFound -> {
                _uiState.update { it.copy(patientProfile = null) }
            }
            is ProfileFetchResult.Error -> {
                _uiState.update { it.copy(patientProfile = null) }
            }
        }
    }

    private fun PatientProfile.toUiProfile(): PatientProfileUi {
        return PatientProfileUi(
            fullName = fullName,
            email = email,
            phone = phone,
            age = age,
            gender = gender,
            village = village,
            bloodType = bloodType,
            weightKg = weightKg,
            photoPath = photoPath,
        )
    }

    private fun isProfileMismatchMessage(message: String): Boolean {
        val normalized = message.lowercase()
        return normalized.contains("please login as") && normalized.contains("profile")
    }

    // Doctor Dashboard Navigation Methods
    fun openDoctorChatScreen(patientId: Int) {
        _uiState.update {
            it.copy(
                showDoctorChatScreen = true,
                showDoctorDashboard = false,
                showDoctorQueueScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorEditProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
                selectedPatientIdForChat = patientId,
            )
        }
    }

    fun openDoctorChatsScreen() {
        _uiState.update {
            it.copy(
                showDoctorChatScreen = true,
                showDoctorDashboard = false,
                showDoctorQueueScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorEditProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
                selectedPatientIdForChat = null,
            )
        }
    }

    fun closeDoctorChatScreen() {
        _uiState.update {
            it.copy(
                showDoctorChatScreen = false,
                showDoctorDashboard = true,
                selectedPatientIdForChat = null,
            )
        }
    }

    fun openDoctorQueueScreen() {
        _uiState.update {
            it.copy(
                showDoctorQueueScreen = true,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorQueueScreen() {
        _uiState.update {
            it.copy(
                showDoctorQueueScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorEditProfileScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorHomeScreen() {
        _uiState.update {
            it.copy(
                showDoctorDashboard = true,
                showDoctorQueueScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorEditProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorScheduleScreen() {
        _uiState.update {
            it.copy(
                showDoctorScheduleScreen = true,
                showDoctorQueueScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorScheduleScreen() {
        _uiState.update {
            it.copy(
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorWaitingScreen() {
        _uiState.update {
            it.copy(
                showDoctorWaitingScreen = true,
                showDoctorScheduleScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorQueueScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorWaitingScreen() {
        _uiState.update {
            it.copy(
                showDoctorWaitingScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorCompletedScreen() {
        _uiState.update {
            it.copy(
                showDoctorCompletedScreen = true,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorQueueScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorCompletedScreen() {
        _uiState.update {
            it.copy(
                showDoctorCompletedScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorPatientsScreen() {
        _uiState.update {
            it.copy(
                showDoctorPatientsScreen = true,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorQueueScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorPatientsScreen() {
        _uiState.update {
            it.copy(
                showDoctorPatientsScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorPatientProfile(patientId: Int, patientName: String, location: String, status: String) {
        _uiState.update {
            it.copy(
                showDoctorPatientProfileScreen = true,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorQueueScreen = false,
                showDoctorDashboard = false,
                showDoctorAnalyticsScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
                selectedDoctorPatientId = patientId,
                selectedDoctorPatientName = patientName,
                selectedDoctorPatientLocation = location,
                selectedDoctorPatientStatus = status,
            )
        }
    }

    fun closeDoctorPatientProfile() {
        _uiState.update {
            it.copy(
                showDoctorPatientProfileScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorAnalyticsScreen() {
        _uiState.update {
            it.copy(
                showDoctorAnalyticsScreen = true,
                showDoctorDashboard = false,
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorProfileScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorAnalyticsScreen() {
        _uiState.update {
            it.copy(
                showDoctorAnalyticsScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorProfileScreen() {
        _uiState.update {
            it.copy(
                showDoctorProfileScreen = true,
                showDoctorEditProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorProfileScreen() {
        _uiState.update {
            it.copy(
                showDoctorProfileScreen = false,
                showDoctorEditProfileScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorDashboard = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorEditProfileScreen() {
        _uiState.update {
            it.copy(
                showDoctorEditProfileScreen = true,
                showDoctorProfileScreen = false,
                showDoctorDashboard = false,
                showDoctorQueueScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun closeDoctorEditProfileScreen() {
        _uiState.update {
            it.copy(
                showDoctorEditProfileScreen = false,
                showDoctorProfileScreen = true,
            )
        }
    }

    fun openDoctorSecurityScreen() {
        _uiState.update {
            it.copy(
                showDoctorSecurityScreen = true,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
                showDoctorProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = false,
                showDoctorDashboard = false,
            )
        }
    }

    fun closeDoctorSecurityScreen() {
        _uiState.update {
            it.copy(
                showDoctorSecurityScreen = false,
                showDoctorProfileScreen = true,
                showDoctorHelpScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorHelpScreen() {
        _uiState.update {
            it.copy(
                showDoctorHelpScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorLanguageScreen = false,
                showDoctorProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = false,
                showDoctorDashboard = false,
            )
        }
    }

    fun closeDoctorHelpScreen() {
        _uiState.update {
            it.copy(
                showDoctorHelpScreen = false,
                showDoctorProfileScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorLanguageScreen = false,
            )
        }
    }

    fun openDoctorLanguageScreen() {
        _uiState.update {
            it.copy(
                showDoctorLanguageScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
                showDoctorProfileScreen = false,
                showDoctorAnalyticsScreen = false,
                showDoctorPatientProfileScreen = false,
                showDoctorPatientsScreen = false,
                showDoctorCompletedScreen = false,
                showDoctorWaitingScreen = false,
                showDoctorScheduleScreen = false,
                showDoctorQueueScreen = false,
                showDoctorDashboard = false,
            )
        }
    }

    fun closeDoctorLanguageScreen() {
        _uiState.update {
            it.copy(
                showDoctorLanguageScreen = false,
                showDoctorProfileScreen = true,
                showDoctorSecurityScreen = false,
                showDoctorHelpScreen = false,
            )
        }
    }
}