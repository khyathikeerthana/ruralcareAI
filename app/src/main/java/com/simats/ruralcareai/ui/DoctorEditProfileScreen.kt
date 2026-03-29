package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DoctorEditableProfile(
    val fullName: String,
    val specialization: String,
    val phone: String,
    val email: String,
    val yearsExperience: Int,
    val qualification: String,
    val clinicName: String,
    val clinicAddress: String,
    val clinicHours: String,
    val profilePhotoPath: String,
)

@Composable
fun DoctorEditProfileScreen(
    profile: DoctorEditableProfile = DoctorEditableProfile(
        fullName = "",
        specialization = "",
        phone = "",
        email = "",
        yearsExperience = 0,
        qualification = "",
        clinicName = "",
        clinicAddress = "",
        clinicHours = "",
        profilePhotoPath = "",
    ),
    onBack: () -> Unit = {},
    onSave: (DoctorEditableProfile) -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var fullName by remember(profile.fullName) { mutableStateOf(profile.fullName) }
    var specialization by remember(profile.specialization) { mutableStateOf(profile.specialization) }
    var phone by remember(profile.phone) { mutableStateOf(profile.phone) }
    var email by remember(profile.email) { mutableStateOf(profile.email) }
    var yearsExperience by remember(profile.yearsExperience) { mutableStateOf(profile.yearsExperience.toString()) }
    var qualification by remember(profile.qualification) { mutableStateOf(profile.qualification) }
    var clinicName by remember(profile.clinicName) { mutableStateOf(profile.clinicName) }
    var clinicAddress by remember(profile.clinicAddress) { mutableStateOf(profile.clinicAddress) }
    var clinicHours by remember(profile.clinicHours) { mutableStateOf(profile.clinicHours) }
    var profilePhotoPath by remember(profile.profilePhotoPath) { mutableStateOf(profile.profilePhotoPath) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            profilePhotoPath = uri.toString()
        }
    }

    LaunchedEffect(profile.profilePhotoPath) {
        profilePhotoPath = profile.profilePhotoPath
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DoctorDashboardPrimary,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onBack() },
                    )

                    Text(
                        text = "Edit Profile",
                        color = DoctorDashboardText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp),
                    )

                    Text(
                        text = "Save",
                        color = DoctorDashboardPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onSave(
                                DoctorEditableProfile(
                                    fullName = fullName,
                                    specialization = specialization,
                                    phone = phone,
                                    email = email,
                                    yearsExperience = yearsExperience.toIntOrNull() ?: 0,
                                    qualification = qualification,
                                    clinicName = clinicName,
                                    clinicAddress = clinicAddress,
                                    clinicHours = clinicHours,
                                    profilePhotoPath = profilePhotoPath,
                                )
                            )
                        },
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF1C6F98), Color(0xFF6DB2D5)))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2F9AD6)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                            )
                        }
                    }

                    Text(
                        text = if (profilePhotoPath.isBlank()) "Tap + to upload photo from device" else "Photo selected from device",
                        color = DoctorDashboardMuted,
                        fontSize = 15.sp,
                    )
                }
            }

            item {
                SectionLabel(icon = Icons.Filled.Person, title = "Personal Information")
            }

            item {
                FormSectionCard {
                    EditableField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
                    EditableField(label = "Specialty", value = specialization, onValueChange = { specialization = it })
                    EditableField(label = "Phone Number", value = phone, onValueChange = { phone = it })
                    EditableField(label = "Email", value = email, onValueChange = { email = it })
                    EditableField(label = "Years Experience", value = yearsExperience, onValueChange = { yearsExperience = it })
                    EditableField(label = "Qualification", value = qualification, onValueChange = { qualification = it })
                }
            }

            item {
                SectionLabel(icon = Icons.Filled.Settings, title = "Clinic Details")
            }

            item {
                FormSectionCard {
                    EditableField(label = "Clinic Name", value = clinicName, onValueChange = { clinicName = it })
                    EditableField(label = "Address", value = clinicAddress, onValueChange = { clinicAddress = it }, multiline = true)
                    EditableField(label = "Clinic Hours", value = clinicHours, onValueChange = { clinicHours = it }, multiline = true)
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { onSignOut() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign Out",
                        tint = Color(0xFFD81E1E),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFD81E1E),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = DoctorDashboardPrimary, modifier = Modifier.size(18.dp))
        Text(text = title, color = DoctorDashboardText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FormSectionCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { content() }
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    multiline: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = DoctorDashboardMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = if (multiline) 3 else 1,
            maxLines = if (multiline) 5 else 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE4E8EE),
                unfocusedContainerColor = Color(0xFFE4E8EE),
                focusedBorderColor = Color(0xFFD3D9E1),
                unfocusedBorderColor = Color(0xFFD3D9E1),
            ),
            shape = RoundedCornerShape(8.dp),
        )
    }
}
