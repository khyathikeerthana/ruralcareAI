package com.simats.ruralcareai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.viewmodel.AppUiState

private val SignUpBackground = Color(0xFFF7F9FC)
private val SignUpBlue = Color(0xFF2D9CDB)
private val SignUpBlueDark = Color(0xFF1F8FE5)
private val SignUpText = Color(0xFF18253E)
private val SignUpMuted = Color(0xFF6F809B)
private val SignUpFieldText = Color(0xFF31435E)
private val SignUpPlaceholder = Color(0xFF9AA8BF)
private val SignUpError = Color(0xFFC53A3A)
private val SnackbarBackground = Color(0xFFFFFFFF)
private val SnackbarText = Color(0xFF18253E)
private val SnackbarClose = Color(0xFF93A0B5)
private val SnackbarSuccess = Color(0xFF2AC98A)
private val SnackbarWarning = Color(0xFFF0A53B)

@Composable
fun SignUpScreen(
    uiState: AppUiState,
    onFullNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onCreateAccount: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    onDismissWarning: () -> Unit,
    onDismissSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }
    val selectedRoleName = uiState.selectedRole?.displayName ?: "Patient"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SignUpBackground)
    ) {
        BackgroundGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 22.dp, end = 22.dp, top = 36.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignUpBackChevron(modifier = Modifier.clickable(onClick = onBackClick))
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(98.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFDFF0FD)),
                contentAlignment = Alignment.Center,
            ) {
                MedicalServicesIcon()
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Create $selectedRoleName Account",
                color = SignUpText,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sign up to continue using RuralCareAI as $selectedRoleName.",
                color = SignUpMuted,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            SignUpField(
                label = "Full Name",
                value = uiState.registrationFullName,
                onValueChange = onFullNameChanged,
                placeholder = "Enter your full name",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpField(
                label = "Phone Number",
                value = uiState.registrationPhone,
                onValueChange = onPhoneChanged,
                placeholder = "+1 (555) 000-0000",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpField(
                label = "Email Address",
                value = uiState.registrationEmail,
                onValueChange = onEmailChanged,
                placeholder = "email@example.com",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpField(
                label = "Password",
                value = uiState.registrationPassword,
                onValueChange = onPasswordChanged,
                placeholder = "••••••••",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingContent = {
                    EyeButton(
                        visible = showPassword,
                        onClick = { showPassword = !showPassword },
                    )
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpField(
                label = "Confirm Password",
                value = uiState.registrationConfirmPassword,
                onValueChange = onConfirmPasswordChanged,
                placeholder = "••••••••",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingContent = {
                    EyeButton(
                        visible = showConfirmPassword,
                        onClick = { showConfirmPassword = !showConfirmPassword },
                    )
                },
            )

            if (uiState.authError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.authError,
                    color = SignUpError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(SignUpBlue, SignUpBlueDark)
                        )
                    )
                    .clickable(enabled = !uiState.isRegistering, onClick = onCreateAccount)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.isRegistering) "Creating..." else "Create $selectedRoleName Account",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Already have an account? ",
                    color = SignUpMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Login",
                    color = SignUpBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onLoginClick),
                )
            }

            Spacer(modifier = Modifier.height(88.dp))
        }

        if (uiState.authWarningMessage != null) {
            WarningSnackbar(
                message = uiState.authWarningMessage,
                onDismiss = onDismissWarning,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            )
        } else if (uiState.registrationSuccessMessage != null) {
            SuccessSnackbar(
                message = uiState.registrationSuccessMessage,
                onDismiss = onDismissSuccess,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            )
        }
    }
}

@Composable
private fun WarningSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x26F0A53B),
                spotColor = Color(0x26F0A53B),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(SnackbarBackground)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SnackbarWarning),
            contentAlignment = Alignment.Center,
        ) {
            WarningBadgeIcon(color = Color.White)
        }

        Text(
            text = message,
            color = SnackbarText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            CloseIcon(color = SnackbarClose)
        }
    }
}

@Composable
private fun BackgroundGlow() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 8.dp)
                .size(170.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D9CDB).copy(alpha = 0.08f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 6.dp, bottom = 46.dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D9CDB).copy(alpha = 0.06f)),
        )
    }
}

@Composable
private fun SignUpBackChevron(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            drawLine(
                color = SignUpText,
                start = Offset(size.width * 0.85f, size.height * 0.08f),
                end = Offset(size.width * 0.12f, size.height * 0.50f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = SignUpText,
                start = Offset(size.width * 0.12f, size.height * 0.50f),
                end = Offset(size.width * 0.85f, size.height * 0.92f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun SignUpField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = SignUpText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = Color(0x192D9CDB),
                    spotColor = Color(0x192D9CDB),
                ),
            textStyle = TextStyle(
                color = SignUpFieldText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = SignUpPlaceholder,
                    fontSize = 16.sp,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            trailingIcon = trailingContent,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = SignUpPlaceholder,
                unfocusedPlaceholderColor = SignUpPlaceholder,
                cursorColor = SignUpBlue,
            ),
        )
    }
}

@Composable
private fun MedicalServicesIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val bagColor = SignUpBlue
        val bagWidth = size.width * 0.72f
        val bagHeight = size.height * 0.60f
        val bagX = (size.width - bagWidth) / 2f
        val bagY = size.height * 0.26f

        drawRoundRect(
            color = bagColor,
            topLeft = Offset(bagX, bagY),
            size = Size(bagWidth, bagHeight),
            cornerRadius = CornerRadius(size.width * 0.08f),
        )

        drawRoundRect(
            color = bagColor,
            topLeft = Offset(size.width * 0.38f, size.height * 0.10f),
            size = Size(size.width * 0.24f, size.height * 0.18f),
            cornerRadius = CornerRadius(size.width * 0.05f),
            style = Stroke(width = size.width * 0.06f),
        )

        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.5f, size.height * 0.42f),
            end = Offset(size.width * 0.5f, size.height * 0.68f),
            strokeWidth = size.width * 0.09f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.36f, size.height * 0.55f),
            end = Offset(size.width * 0.64f, size.height * 0.55f),
            strokeWidth = size.width * 0.09f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun EyeButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(28.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val stroke = size.width * 0.10f
            drawArc(
                color = SignUpPlaceholder,
                startAngle = 25f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                size = Size(size.width * 0.84f, size.height * 0.60f),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = SignUpPlaceholder,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                size = Size(size.width * 0.84f, size.height * 0.60f),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawCircle(
                color = SignUpPlaceholder,
                radius = size.width * 0.12f,
                center = Offset(size.width * 0.5f, size.height * 0.5f),
            )
            if (!visible) {
                drawLine(
                    color = SignUpPlaceholder,
                    start = Offset(size.width * 0.18f, size.height * 0.82f),
                    end = Offset(size.width * 0.82f, size.height * 0.18f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun SuccessSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x262AC98A),
                spotColor = Color(0x262AC98A),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(SnackbarBackground)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SnackbarSuccess),
            contentAlignment = Alignment.Center,
        ) {
            CheckBadgeIcon(color = Color.White)
        }

        Text(
            text = message,
            color = SnackbarText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            CloseIcon(color = SnackbarClose)
        }
    }
}

@Composable
private fun CheckBadgeIcon(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.18f, size.height * 0.55f),
            end = Offset(size.width * 0.42f, size.height * 0.78f),
            strokeWidth = size.width * 0.16f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.78f),
            end = Offset(size.width * 0.82f, size.height * 0.22f),
            strokeWidth = size.width * 0.16f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun WarningBadgeIcon(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.18f),
            end = Offset(size.width * 0.5f, size.height * 0.64f),
            strokeWidth = size.width * 0.16f,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = color,
            radius = size.width * 0.08f,
            center = Offset(size.width * 0.5f, size.height * 0.84f),
        )
    }
}

@Composable
private fun CloseIcon(color: Color) {
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
