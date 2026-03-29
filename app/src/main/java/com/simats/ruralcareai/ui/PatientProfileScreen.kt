package com.simats.ruralcareai.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Summarize
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.viewmodel.AppUiState

private val ProfileBackground = Color(0xFFF2F4F8)
private val ProfilePrimary = Color(0xFF1F9BE6)
private val ProfileText = Color(0xFF0F1730)
private val ProfileMuted = Color(0xFF7A8598)
private val ProfileCard = Color(0xFFFFFFFF)
private val ProfileOutline = Color(0xFFE4E9F1)

private data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
)

private enum class ProfileScreenMode {
    MAIN,
    NOTIFICATIONS,
    SETTINGS,
    HELP_SUPPORT,
}

@Composable
fun PatientProfileScreen(
    uiState: AppUiState,
    onBack: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenMedicalHistory: () -> Unit,
    onOpenLabReports: () -> Unit,
    onOpenPrescriptions: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    var screenMode by rememberSaveable { mutableStateOf(ProfileScreenMode.MAIN.name) }
    val currentMode = ProfileScreenMode.valueOf(screenMode)

    BackHandler(
        onBack = {
            if (currentMode == ProfileScreenMode.MAIN) {
                onBack()
            } else {
                screenMode = ProfileScreenMode.MAIN.name
            }
        },
    )

    when (currentMode) {
        ProfileScreenMode.NOTIFICATIONS -> {
            NotificationsScreen(onBack = { screenMode = ProfileScreenMode.MAIN.name })
            return
        }
        ProfileScreenMode.SETTINGS -> {
            SettingsScreen(onBack = { screenMode = ProfileScreenMode.MAIN.name })
            return
        }
        ProfileScreenMode.HELP_SUPPORT -> {
            HelpSupportScreen(onBack = { screenMode = ProfileScreenMode.MAIN.name })
            return
        }
        ProfileScreenMode.MAIN -> Unit
    }

    val profile = uiState.patientProfile
    val fullName = profile?.fullName ?: uiState.currentUserName ?: "Patient"
    val profilePhoto = profile?.photoPath ?: uiState.currentUserPhotoPath
    val location = profile?.village?.takeIf { it.isNotBlank() } ?: "Village not set"
    val ageText = profile?.age?.toString() ?: "--"
    val bloodTypeText = profile?.bloodType?.takeIf { it.isNotBlank() } ?: "--"
    val weightText = profile?.weightKg?.let { formatWeight(it) } ?: "--"
    val languageText = uiState.selectedLanguage.displayName

    val healthRecordItems = listOf(
        ProfileMenuItem(icon = Icons.AutoMirrored.Filled.StickyNote2, title = "Medical History", subtitle = "Records and diagnoses"),
        ProfileMenuItem(icon = Icons.Filled.Science, title = "Lab Reports", subtitle = "Recent test reports"),
        ProfileMenuItem(icon = Icons.Filled.Summarize, title = "Prescriptions", subtitle = "Medication timeline"),
    )

    val appSettingItems = listOf(
        ProfileMenuItem(icon = Icons.Filled.Notifications, title = "Notifications", subtitle = "Manage health alerts"),
        ProfileMenuItem(icon = Icons.Filled.Language, title = "Language", subtitle = languageText),
        ProfileMenuItem(icon = Icons.Filled.Security, title = "Settings", subtitle = "Privacy and account safety"),
        ProfileMenuItem(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "Help & Support", subtitle = "Get assistance"),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ProfileBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircleTopButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Text(
                        text = "Profile",
                        color = ProfileText,
                        fontSize = if (isCompact) 23.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    CircleTopButton(
                        icon = Icons.Filled.Edit,
                        contentDescription = "Edit profile",
                        onClick = onOpenEditProfile,
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(modifier = Modifier.size(98.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFE7C9A9), Color(0xFFC39A78))))
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            AvatarContent(
                                photoData = profilePhoto,
                                fallbackName = fullName,
                                textSize = 30.sp,
                            )
                        }
                    }

                    Text(
                        text = fullName,
                        color = ProfileText,
                        fontSize = 32.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = ProfileMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = location,
                            color = ProfileMuted,
                            fontSize = 16.sp / 1.1f,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                SectionTitle("PERSONAL INFORMATION")
            }

            item {
                ProfileInfoCard(
                    rows = listOf(
                        "Age" to ageText,
                        "Blood Group" to bloodTypeText,
                        "Weight" to weightText,
                    )
                )
            }

            item {
                SectionTitle("HEALTH RECORDS")
            }

            item {
                ProfileMenuCard(
                    items = healthRecordItems,
                    onItemClick = { item ->
                        when (item.title) {
                            "Medical History" -> onOpenMedicalHistory()
                            "Lab Reports" -> onOpenLabReports()
                            "Prescriptions" -> onOpenPrescriptions()
                        }
                    },
                )
            }

            item {
                SectionTitle("APP SETTINGS")
            }

            item {
                ProfileMenuCard(
                    items = appSettingItems,
                    onItemClick = { item ->
                        when (item.title) {
                            "Notifications" -> screenMode = ProfileScreenMode.NOTIFICATIONS.name
                            "Settings", "Security" -> screenMode = ProfileScreenMode.SETTINGS.name
                            "Help & Support" -> screenMode = ProfileScreenMode.HELP_SUPPORT.name
                            else -> Unit
                        }
                    },
                )
            }

            item {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEDEE),
                        contentColor = Color(0xFFE43232),
                    ),
                ) {
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFE43232),
                        fontSize = 24.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                Text(
                    text = "RuralCareAI v2.6.4",
                    color = Color(0xFF8C95A5),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun EditPatientProfileScreen(
    uiState: AppUiState,
    onBack: () -> Unit,
    onSave: (
        fullName: String,
        email: String,
        phone: String,
        village: String,
        bloodType: String,
        age: Int?,
        gender: String?,
        weight: Double?,
        photo: String?,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val profile = uiState.patientProfile

    var fullName by rememberSaveable(uiState.currentUserId, profile?.fullName, uiState.currentUserName) {
        mutableStateOf(profile?.fullName ?: uiState.currentUserName.orEmpty())
    }
    var email by rememberSaveable(uiState.currentUserId, profile?.email, uiState.currentUserEmail) {
        mutableStateOf(profile?.email ?: uiState.currentUserEmail.orEmpty())
    }
    var phone by rememberSaveable(uiState.currentUserId, profile?.phone, uiState.currentUserPhone) {
        mutableStateOf(profile?.phone ?: uiState.currentUserPhone.orEmpty())
    }
    var village by rememberSaveable(uiState.currentUserId, profile?.village) {
        mutableStateOf(profile?.village.orEmpty())
    }
    var bloodType by rememberSaveable(uiState.currentUserId, profile?.bloodType) {
        mutableStateOf(profile?.bloodType.orEmpty())
    }
    var weightKg by rememberSaveable(uiState.currentUserId, profile?.weightKg) {
        mutableStateOf(profile?.weightKg?.toString() ?: "")
    }
    var photoPath by rememberSaveable(uiState.currentUserId, profile?.photoPath) {
        mutableStateOf(profile?.photoPath.orEmpty())
    }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val encoded = uri?.let { encodeUriToBase64(context, it) }
        if (!encoded.isNullOrBlank()) {
            photoPath = encoded
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ProfileBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircleTopButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Text(
                        text = "Edit Profile",
                        color = ProfileText,
                        fontSize = 28.sp / 1.6f,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = if (uiState.isSavingProfileEdit) "Saving" else "Save",
                        color = if (uiState.isSavingProfileEdit) Color(0xFF8AAFD0) else ProfilePrimary,
                        fontSize = 18.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(enabled = !uiState.isSavingProfileEdit) {
                            onSave(
                                fullName,
                                email,
                                phone,
                                village,
                                bloodType,
                                profile?.age,
                                profile?.gender,
                                weightKg.toDoubleOrNull(),
                                photoPath.takeIf { it.isNotBlank() },
                            )
                        },
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFE7C9A9), Color(0xFFC39A78))))
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            AvatarContent(
                                photoData = photoPath,
                                fallbackName = fullName,
                                textSize = 34.sp,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ProfilePrimary)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable(onClick = {
                                    photoPickerLauncher.launch("image/*")
                                }),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Text(
                        text = "Change Photo",
                        color = ProfilePrimary,
                        fontSize = 18.sp / 1.15f,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { photoPickerLauncher.launch("image/*") },
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ProfileCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ProfileOutline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SectionTitle("PERSONAL INFORMATION")

                        EditField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it },
                        )

                        EditField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { email = it },
                        )

                        EditField(
                            label = "Phone Number",
                            value = phone,
                            onValueChange = { phone = it },
                        )

                        EditField(
                            label = "Location",
                            value = village,
                            onValueChange = { village = it },
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ProfileCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ProfileOutline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SectionTitle("HEALTH RECORDS")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Blood Group",
                                    color = ProfileMuted,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                OutlinedTextField(
                                    value = bloodType,
                                    onValueChange = { bloodType = it.uppercase() },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF4F7FB),
                                        unfocusedContainerColor = Color(0xFFF4F7FB),
                                        focusedBorderColor = ProfileOutline,
                                        unfocusedBorderColor = ProfileOutline,
                                    ),
                                )
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Weight (kg)",
                                    color = ProfileMuted,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                OutlinedTextField(
                                    value = weightKg,
                                    onValueChange = { value -> weightKg = value.filter { it.isDigit() } },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFFF4F7FB),
                                        unfocusedContainerColor = Color(0xFFF4F7FB),
                                        focusedBorderColor = ProfileOutline,
                                        unfocusedBorderColor = ProfileOutline,
                                    ),
                                )
                            }
                        }

                    }
                }
            }

            if (!uiState.profileEditError.isNullOrBlank()) {
                item {
                    Text(
                        text = uiState.profileEditError.orEmpty(),
                        color = Color(0xFFB42318),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleTopButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, ProfileOutline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ProfilePrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF8A93A4),
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
    )
}

@Composable
private fun ProfileInfoCard(rows: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileOutline),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = row.first,
                        color = ProfileMuted,
                        fontSize = 18.sp / 1.2f,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = row.second,
                        color = if (index == 1) ProfilePrimary else ProfileText,
                        fontSize = 18.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                    )
                }

                if (index != rows.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ProfileOutline)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuCard(
    items: List<ProfileMenuItem>,
    onItemClick: ((ProfileMenuItem) -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileOutline),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onItemClick?.invoke(item) })
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEAF2FA)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = ProfilePrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = item.title,
                                color = ProfileText,
                                fontSize = 17.sp / 1.2f,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (!item.subtitle.isNullOrBlank()) {
                                Text(
                                    text = item.subtitle,
                                    color = ProfileMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    Text(
                        text = ">",
                        color = Color(0xFF96A3B7),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                if (index != items.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ProfileOutline)
                    )
                }
            }
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = ProfileMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F7FB),
                unfocusedContainerColor = Color(0xFFF4F7FB),
                focusedBorderColor = ProfileOutline,
                unfocusedBorderColor = ProfileOutline,
            ),
        )
    }
}

private fun profileInitials(name: String): String {
    val initials = name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")

    return initials.ifBlank { "P" }
}

private fun formatWeight(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "${value.toInt()} kg"
    } else {
        "${"%.1f".format(value)} kg"
    }
}

@Composable
private fun AvatarContent(
    photoData: String?,
    fallbackName: String,
    textSize: androidx.compose.ui.unit.TextUnit,
) {
    val image = remember(photoData) { decodePhotoData(photoData) }
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = "Profile photo",
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        Text(
            text = profileInitials(fallbackName),
            color = Color.White,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun decodePhotoData(value: String?): ImageBitmap? {
    if (value.isNullOrBlank()) return null
    val base64Payload = value.substringAfter("base64,", value)

    return try {
        val bytes = Base64.decode(base64Payload, Base64.DEFAULT)
        if (bytes.isEmpty()) {
            null
        } else {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    } catch (_: Exception) {
        null
    }
}

private fun encodeUriToBase64(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val bytes = input.readBytes()
            if (bytes.isEmpty()) {
                null
            } else {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        }
    } catch (_: Exception) {
        null
    }
}
