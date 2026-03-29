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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Path
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
import kotlinx.coroutines.delay

private val LoginBackground = Color(0xFFF7F9FC)
private val LoginBlue = Color(0xFF2D9CDB)
private val LoginBlueDark = Color(0xFF1F8FE5)
private val LoginText = Color(0xFF18253E)
private val LoginMuted = Color(0xFF6F809B)
private val LoginFieldText = Color(0xFF31435E)
private val LoginPlaceholder = Color(0xFF9AA8BF)
private val LoginError = Color(0xFFC53A3A)
private val LoginSnackbarSuccess = Color(0xFF2AC98A)
private val LoginSnackbarWarning = Color(0xFFF0A53B)
private val LoginSnackbarClose = Color(0xFF93A0B5)
private const val LoginSuccessAutoDismissMs = 1200L

@Composable
fun LoginScreen(
    uiState: AppUiState,
    onIdentifierChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onBackClick: () -> Unit,
    onForgotPassword: () -> Unit = {},
    showCreateAccountAction: Boolean = true,
    onCreateAccountClick: () -> Unit,
    onDismissWarning: () -> Unit,
    onDismissSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val selectedRoleName = uiState.selectedRole?.displayName ?: "Patient"

    LaunchedEffect(uiState.loginSuccessMessage) {
        if (uiState.loginSuccessMessage != null) {
            delay(LoginSuccessAutoDismissMs)
            onDismissSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LoginBackground)
    ) {
        LoginBackgroundGlow()

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
                LoginBackChevron(modifier = Modifier.clickable(onClick = onBackClick))
                Text(
                    text = "RuralCareAI",
                    color = LoginText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Shield + heart icon card
            Box(
                modifier = Modifier
                    .size(98.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0x1A2D9CDB),
                        spotColor = Color(0x1A2D9CDB),
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                ShieldHeartIcon()
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "$selectedRoleName Login",
                color = LoginText,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Login to continue using RuralCareAI as $selectedRoleName.",
                color = LoginMuted,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(36.dp))

            LoginField(
                label = "EMAIL OR PHONE NUMBER",
                value = uiState.identifier,
                onValueChange = onIdentifierChanged,
                placeholder = "name@example.com",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                leadingContent = { AtIcon() },
            )

            Spacer(modifier = Modifier.height(20.dp))

            LoginField(
                label = "PASSWORD",
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = "••••••••",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingContent = { LockIcon() },
                trailingContent = {
                    LoginEyeButton(
                        visible = showPassword,
                        onClick = { showPassword = !showPassword },
                    )
                },
            )

            if (uiState.authError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uiState.authError,
                    color = LoginError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Forgot Password?",
                color = LoginBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onForgotPassword),
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(LoginBlue, LoginBlueDark)
                        )
                    )
                    .clickable(enabled = !uiState.isLoggingIn, onClick = onLogin)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.isLoggingIn) "Logging in..." else "Login as $selectedRoleName",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (showCreateAccountAction) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Don't have a $selectedRoleName account? ",
                        color = LoginMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Create Account",
                        color = LoginBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onCreateAccountClick),
                    )
                }
            } else {
                Text(
                    text = "Use the credentials shared by the administrator.",
                    color = LoginMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(88.dp))
        }

        if (uiState.authWarningMessage != null) {
            LoginWarningSnackbar(
                message = uiState.authWarningMessage,
                subtitle = "Please switch profile and continue.",
                onDismiss = onDismissWarning,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            )
        } else if (uiState.loginSuccessMessage != null) {
            LoginSuccessSnackbar(
                message = uiState.loginSuccessMessage,
                subtitle = "Redirecting to your dashboard...",
                onDismiss = onDismissSuccess,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            )
        }
    }
}

@Composable
private fun LoginWarningSnackbar(
    message: String,
    subtitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x30F0A53B),
                spotColor = Color(0x30F0A53B),
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(LoginSnackbarWarning),
            contentAlignment = Alignment.Center,
        ) {
            LoginWarningIcon(color = Color.White)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message,
                color = LoginText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = LoginMuted,
                fontSize = 13.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            LoginCloseIcon(color = LoginSnackbarClose)
        }
    }
}

@Composable
private fun LoginBackgroundGlow() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 10.dp)
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D9CDB).copy(alpha = 0.07f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 60.dp)
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF2D9CDB).copy(alpha = 0.06f)),
        )
    }
}

@Composable
private fun LoginBackChevron(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            drawLine(
                color = LoginText,
                start = Offset(size.width * 0.85f, size.height * 0.08f),
                end = Offset(size.width * 0.12f, size.height * 0.50f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = LoginText,
                start = Offset(size.width * 0.12f, size.height * 0.50f),
                end = Offset(size.width * 0.85f, size.height * 0.92f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = LoginMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
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
                color = LoginFieldText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = LoginPlaceholder,
                    fontSize = 16.sp,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingContent,
            trailingIcon = trailingContent,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = LoginPlaceholder,
                unfocusedPlaceholderColor = LoginPlaceholder,
                cursorColor = LoginBlue,
            ),
        )
    }
}

@Composable
private fun ShieldHeartIcon() {
    Canvas(modifier = Modifier.size(56.dp)) {
        // Shield shape
        val shieldPath = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.50f, h * 0.92f)
            cubicTo(w * 0.10f, h * 0.70f, w * 0.07f, h * 0.50f, w * 0.07f, h * 0.26f)
            cubicTo(w * 0.07f, h * 0.10f, w * 0.20f, h * 0.07f, w * 0.50f, h * 0.07f)
            cubicTo(w * 0.80f, h * 0.07f, w * 0.93f, h * 0.10f, w * 0.93f, h * 0.26f)
            cubicTo(w * 0.93f, h * 0.50f, w * 0.90f, h * 0.70f, w * 0.50f, h * 0.92f)
            close()
        }
        drawPath(shieldPath, color = LoginBlue)

        // Heart inside shield
        val cx = size.width * 0.50f
        val cy = size.height * 0.52f
        val s = size.width * 0.20f
        val heartPath = Path().apply {
            moveTo(cx, cy + s * 1.0f)
            cubicTo(cx - s * 0.3f, cy + s * 0.6f, cx - s * 1.2f, cy + s * 0.35f, cx - s * 1.2f, cy - s * 0.05f)
            cubicTo(cx - s * 1.2f, cy - s * 0.85f, cx - s * 0.45f, cy - s * 1.05f, cx, cy - s * 0.40f)
            cubicTo(cx + s * 0.45f, cy - s * 1.05f, cx + s * 1.2f, cy - s * 0.85f, cx + s * 1.2f, cy - s * 0.05f)
            cubicTo(cx + s * 1.2f, cy + s * 0.35f, cx + s * 0.3f, cy + s * 0.6f, cx, cy + s * 1.0f)
            close()
        }
        drawPath(heartPath, color = Color.White)
    }
}

@Composable
private fun AtIcon() {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .size(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val sw = size.width * 0.10f
            // Outer ring
            drawCircle(
                color = LoginPlaceholder,
                radius = size.width * 0.36f,
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                style = Stroke(width = sw),
            )
            // Inner dot
            drawCircle(
                color = LoginPlaceholder,
                radius = size.width * 0.14f,
                center = Offset(size.width * 0.5f, size.height * 0.5f),
            )
            // Right vertical tail
            drawLine(
                color = LoginPlaceholder,
                start = Offset(size.width * 0.86f, size.height * 0.30f),
                end = Offset(size.width * 0.86f, size.height * 0.72f),
                strokeWidth = sw,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun LockIcon() {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .size(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val sw = size.width * 0.11f
            val bodyL = size.width * 0.16f
            val bodyT = size.height * 0.47f
            val bodyW = size.width * 0.68f
            val bodyH = size.height * 0.45f

            drawRoundRect(
                color = LoginPlaceholder,
                topLeft = Offset(bodyL, bodyT),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(size.width * 0.10f),
            )

            drawArc(
                color = LoginPlaceholder,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(size.width * 0.29f, size.height * 0.06f),
                size = Size(size.width * 0.42f, size.height * 0.46f),
                style = Stroke(width = sw, cap = StrokeCap.Round),
            )

            drawCircle(
                color = Color.White,
                radius = size.width * 0.09f,
                center = Offset(size.width * 0.5f, bodyT + bodyH * 0.42f),
            )
        }
    }
}

@Composable
private fun LoginEyeButton(
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
                color = LoginPlaceholder,
                startAngle = 25f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                size = Size(size.width * 0.84f, size.height * 0.60f),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = LoginPlaceholder,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                size = Size(size.width * 0.84f, size.height * 0.60f),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawCircle(
                color = LoginPlaceholder,
                radius = size.width * 0.12f,
                center = Offset(size.width * 0.5f, size.height * 0.5f),
            )
            if (!visible) {
                drawLine(
                    color = LoginPlaceholder,
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
private fun LoginSuccessSnackbar(
    message: String,
    subtitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x302AC98A),
                spotColor = Color(0x302AC98A),
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(LoginSnackbarSuccess),
            contentAlignment = Alignment.Center,
        ) {
            LoginCheckIcon(color = Color.White)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message,
                color = LoginText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = LoginMuted,
                fontSize = 13.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            LoginCloseIcon(color = LoginSnackbarClose)
        }
    }
}

@Composable
private fun LoginCheckIcon(color: Color) {
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
private fun LoginWarningIcon(color: Color) {
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
private fun LoginCloseIcon(color: Color) {
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
