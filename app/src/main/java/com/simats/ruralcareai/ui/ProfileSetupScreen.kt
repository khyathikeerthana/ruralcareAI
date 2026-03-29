package com.simats.ruralcareai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.viewmodel.AppUiState

private val SetupBackground = Color(0xFFF4F6FA)
private val SetupCard = Color(0xFFFFFFFF)
private val SetupText = Color(0xFF1B2A44)
private val SetupMuted = Color(0xFF8C9BB1)
private val SetupBorder = Color(0xFFE5ECF4)
private val SetupPrimary = Color(0xFF2D9CDB)
private val SetupPrimaryDark = Color(0xFF1F8FE5)
private val SetupError = Color(0xFFC53A3A)
private val SetupSuccess = Color(0xFF25C460)

private val BloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun ProfileSetupScreen(
    uiState: AppUiState,
    onBack: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onVillageChanged: (String) -> Unit,
    onBloodTypeChanged: (String) -> Unit,
    onCompleteSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompactDevice = LocalConfiguration.current.screenHeightDp <= 760

    val horizontalPadding = if (isCompactDevice) 16.dp else 20.dp
    val topPadding = if (isCompactDevice) 20.dp else 34.dp
    val avatarSize = if (isCompactDevice) 108.dp else 130.dp
    val cameraBadgeSize = if (isCompactDevice) 38.dp else 44.dp
    val sectionGap = if (isCompactDevice) 20.dp else 26.dp
    val actionButtonVerticalPadding = if (isCompactDevice) 16.dp else 18.dp
    val actionButtonTextSize = if (isCompactDevice) 17.sp else 18.sp

    var bloodTypeExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SetupBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = horizontalPadding, end = horizontalPadding, top = topPadding, bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onBack),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SetupBackChevron(color = SetupPrimary)
                    Text(
                        text = "Back",
                        color = SetupPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Text(
                    text = "Profile Setup",
                    color = SetupText,
                    fontSize = if (isCompactDevice) 19.sp else 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(if (isCompactDevice) 18.dp else 24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color(0xFFB5CBC4)),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    SetupAvatarPlaceholder()

                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp, bottom = 4.dp)
                            .size(cameraBadgeSize)
                            .clip(CircleShape)
                            .background(SetupPrimary)
                            .border(width = 3.dp, color = SetupBackground, shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        CameraGlyph()
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Change Photo",
                    color = SetupPrimary,
                    fontSize = if (isCompactDevice) 15.sp else 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(if (isCompactDevice) 20.dp else 28.dp))

            SectionLabel(text = "PERSONAL DETAILS")
            Spacer(modifier = Modifier.height(12.dp))

            CardField {
                SetupTextField(
                    label = "Full Name",
                    value = uiState.profileSetupFullName,
                    placeholder = "Required",
                    onValueChange = onFullNameChanged,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                DividerLine()

                SetupTextField(
                    label = "Age",
                    value = uiState.profileSetupAge,
                    placeholder = "Years",
                    onValueChange = onAgeChanged,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                )

                DividerLine()

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text(
                        text = "Gender",
                        color = SetupText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFFE4EAF1))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        GenderSegment(
                            text = "Male",
                            selected = uiState.profileSetupGender == "Male",
                            onClick = { onGenderChanged("Male") },
                        )
                        GenderSegment(
                            text = "Female",
                            selected = uiState.profileSetupGender == "Female",
                            onClick = { onGenderChanged("Female") },
                        )
                        GenderSegment(
                            text = "Other",
                            selected = uiState.profileSetupGender == "Other",
                            onClick = { onGenderChanged("Other") },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))

            SectionLabel(text = "MEDICAL & LOCATION")
            Spacer(modifier = Modifier.height(12.dp))

            CardField {
                SetupTextField(
                    label = "Village",
                    value = uiState.profileSetupVillage,
                    placeholder = "Search location...",
                    onValueChange = onVillageChanged,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                DividerLine()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { bloodTypeExpanded = true }
                        .padding(horizontal = 14.dp, vertical = if (isCompactDevice) 14.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Blood Type",
                        color = SetupText,
                        fontSize = if (isCompactDevice) 15.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (uiState.profileSetupBloodType.isBlank()) "Select Group" else uiState.profileSetupBloodType,
                        color = if (uiState.profileSetupBloodType.isBlank()) SetupMuted else SetupText,
                        fontSize = if (isCompactDevice) 15.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    UpDownArrows()
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    DropdownMenu(
                        expanded = bloodTypeExpanded,
                        onDismissRequest = { bloodTypeExpanded = false },
                    ) {
                        BloodTypes.forEach { bloodType ->
                            DropdownMenuItem(
                                text = { Text(bloodType) },
                                onClick = {
                                    onBloodTypeChanged(bloodType)
                                    bloodTypeExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            if (uiState.profileSetupError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.profileSetupError,
                    color = SetupError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(if (isCompactDevice) 20.dp else 24.dp))

            Text(
                text = "By completing setup, you agree to our Terms of Service and Privacy Policy. Your medical data is encrypted and secure.",
                color = SetupMuted,
                fontSize = if (isCompactDevice) 12.sp else 13.sp,
                lineHeight = if (isCompactDevice) 18.sp else 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            )

            Spacer(modifier = Modifier.height(if (isCompactDevice) 16.dp else 20.dp))

            Box(
                modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(SetupPrimary, SetupPrimaryDark)))
                .clickable(enabled = !uiState.isSavingProfileSetup, onClick = onCompleteSetup)
                .padding(vertical = actionButtonVerticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.isSavingProfileSetup) "Saving..." else "Complete Setup ->",
                    color = Color.White,
                    fontSize = actionButtonTextSize,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun ProfileSetupSuccessScreen(
    onClose: () -> Unit,
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompactDevice = LocalConfiguration.current.screenHeightDp <= 760
    val cardCornerRadius = if (isCompactDevice) 22.dp else 28.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SetupBackground),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isCompactDevice) 16.dp else 22.dp)
                .clip(RoundedCornerShape(cardCornerRadius))
                .background(SetupCard)
                .shadow(
                    elevation = if (isCompactDevice) 12.dp else 20.dp,
                    shape = RoundedCornerShape(cardCornerRadius),
                    ambientColor = Color(0x172D9CDB),
                    spotColor = Color(0x172D9CDB),
                )
                .padding(
                    horizontal = if (isCompactDevice) 18.dp else 24.dp,
                    vertical = if (isCompactDevice) 18.dp else 24.dp,
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(onClick = onClose),
                        contentAlignment = Alignment.Center,
                    ) {
                        CloseGlyph(color = SetupMuted)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .size(if (isCompactDevice) 100.dp else 120.dp)
                        .clip(CircleShape)
                        .background(SetupSuccess.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompactDevice) 84.dp else 102.dp)
                            .clip(CircleShape)
                            .background(SetupSuccess.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompactDevice) 64.dp else 78.dp)
                                .clip(CircleShape)
                                .background(SetupSuccess),
                            contentAlignment = Alignment.Center,
                        ) {
                            SetupCheckGlyph(color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactDevice) 20.dp else 28.dp))

                Text(
                    text = "Profile Created\nSuccessfully!",
                    color = SetupText,
                    fontSize = if (isCompactDevice) 24.sp else 28.sp,
                    lineHeight = if (isCompactDevice) 30.sp else 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(if (isCompactDevice) 14.dp else 18.dp))

                Text(
                    text = "Your account is ready. You can now start consulting with doctors and managing your health.",
                    color = SetupMuted,
                    fontSize = if (isCompactDevice) 14.sp else 15.sp,
                    lineHeight = if (isCompactDevice) 20.sp else 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp),
                )

                Spacer(modifier = Modifier.height(if (isCompactDevice) 22.dp else 30.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(SetupPrimary, SetupPrimaryDark)))
                        .clickable(onClick = onGoToDashboard)
                        .padding(vertical = if (isCompactDevice) 15.dp else 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Go to Dashboard",
                        color = Color.White,
                        fontSize = if (isCompactDevice) 17.sp else 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CardField(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SetupCard)
            .border(width = 1.dp, color = SetupBorder, shape = RoundedCornerShape(18.dp))
    ) {
        content()
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFF5F718A),
        fontSize = 16.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SetupTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = SetupText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            placeholder = {
                Text(
                    text = placeholder,
                    color = SetupMuted,
                    fontSize = 15.sp,
                )
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                color = SetupText,
                fontSize = 15.sp,
                textAlign = TextAlign.End,
            ),
            modifier = Modifier.fillMaxWidth(0.58f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = SetupPrimary,
            ),
        )
    }
}

@Composable
private fun RowScope.GenderSegment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SetupText,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SetupBorder)
    )
}

@Composable
private fun SetupBackChevron(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.85f, size.height * 0.15f),
            end = Offset(size.width * 0.20f, size.height * 0.50f),
            strokeWidth = size.width * 0.14f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.20f, size.height * 0.50f),
            end = Offset(size.width * 0.85f, size.height * 0.85f),
            strokeWidth = size.width * 0.14f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SetupAvatarPlaceholder() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color(0xFFDCE7E2),
            radius = size.minDimension * 0.23f,
            center = Offset(size.width * 0.50f, size.height * 0.38f),
        )
        drawCircle(
            color = Color(0xFFDCE7E2),
            radius = size.minDimension * 0.22f,
            center = Offset(size.width * 0.50f, size.height * 0.73f),
        )
    }
}

@Composable
private fun CameraGlyph() {
    Canvas(modifier = Modifier.size(20.dp)) {
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(size.width * 0.14f, size.height * 0.26f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.72f, size.height * 0.52f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.10f),
        )
        drawCircle(
            color = SetupPrimary,
            radius = size.width * 0.16f,
            center = Offset(size.width * 0.50f, size.height * 0.52f),
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(size.width * 0.28f, size.height * 0.14f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.20f, size.height * 0.14f),
        )
    }
}

@Composable
private fun UpDownArrows() {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = SetupMuted,
            start = Offset(size.width * 0.2f, size.height * 0.38f),
            end = Offset(size.width * 0.5f, size.height * 0.16f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = SetupMuted,
            start = Offset(size.width * 0.5f, size.height * 0.16f),
            end = Offset(size.width * 0.8f, size.height * 0.38f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )

        drawLine(
            color = SetupMuted,
            start = Offset(size.width * 0.2f, size.height * 0.62f),
            end = Offset(size.width * 0.5f, size.height * 0.84f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = SetupMuted,
            start = Offset(size.width * 0.5f, size.height * 0.84f),
            end = Offset(size.width * 0.8f, size.height * 0.62f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SetupCheckGlyph(color: Color) {
    Canvas(modifier = Modifier.size(34.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.20f, size.height * 0.56f),
            end = Offset(size.width * 0.44f, size.height * 0.78f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.44f, size.height * 0.78f),
            end = Offset(size.width * 0.84f, size.height * 0.24f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun CloseGlyph(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.2f, size.height * 0.2f),
            end = Offset(size.width * 0.8f, size.height * 0.8f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.8f, size.height * 0.2f),
            end = Offset(size.width * 0.2f, size.height * 0.8f),
            strokeWidth = size.width * 0.12f,
            cap = StrokeCap.Round,
        )
    }
}
