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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.viewmodel.AppUiState

private val ForgotBackground = Color(0xFFF7F9FC)
private val ForgotPrimary = Color(0xFF2D9CDB)
private val ForgotPrimaryDark = Color(0xFF1F8FE5)
private val ForgotText = Color(0xFF0F1930)
private val ForgotMuted = Color(0xFF5D6F8B)
private val ForgotFieldText = Color(0xFF344763)
private val ForgotPlaceholder = Color(0xFF90A1BB)
private val ForgotError = Color(0xFFC53A3A)
private val ForgotSuccess = Color(0xFF16BE88)
private val ForgotClose = Color(0xFF90A0B6)

@Composable
fun ForgotPasswordScreen(
    uiState: AppUiState,
    onIdentifierChanged: (String) -> Unit,
    onSendResetLink: () -> Unit,
    onBack: () -> Unit,
    onBackToLogin: () -> Unit,
    onDismissSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ForgotBackground)
    ) {
        ForgotBackgroundGlow()

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
                ForgotBackChevron(modifier = Modifier.clickable(onClick = onBack))
                Text(
                    text = "RuralCareAI",
                    color = ForgotText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(46.dp))

            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(ForgotPrimary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(106.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color(0x1E2D9CDB),
                            spotColor = Color(0x1E2D9CDB),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    LockResetIcon()
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Forgot Password",
                color = ForgotText,
                fontSize = 50.sp / 1.75f,
                lineHeight = 60.sp / 1.75f,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your email or phone number to reset your password.",
                color = ForgotMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            ForgotField(
                label = "ACCOUNT IDENTIFIER",
                value = uiState.forgotPasswordIdentifier,
                onValueChange = onIdentifierChanged,
                placeholder = "Email or Phone Number",
            )

            if (uiState.forgotPasswordError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uiState.forgotPasswordError,
                    color = ForgotError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(ForgotPrimary, ForgotPrimaryDark)
                        )
                    )
                    .clickable(enabled = !uiState.isSendingResetLink, onClick = onSendResetLink)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.isSendingResetLink) "Sending..." else "Send Reset Link",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Back to Login",
                color = ForgotMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onBackToLogin),
            )

            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "RuralCareAI © 2024. All rights reserved.",
                color = ForgotMuted.copy(alpha = 0.62f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (uiState.forgotPasswordSuccessMessage != null) {
            ForgotPasswordSuccessSnackbar(
                title = uiState.forgotPasswordSuccessMessage,
                subtitle = "Check your email for instructions...",
                onDismiss = onDismissSuccess,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            )
        }
    }
}

@Composable
private fun ForgotField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = ForgotPlaceholder,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color(0x162D9CDB),
                    spotColor = Color(0x162D9CDB),
                ),
            textStyle = TextStyle(
                color = ForgotFieldText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = ForgotPlaceholder,
                    fontSize = 16.sp,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = ForgotPlaceholder,
                unfocusedPlaceholderColor = ForgotPlaceholder,
                cursorColor = ForgotPrimary,
            ),
        )
    }
}

@Composable
private fun ForgotBackgroundGlow() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 4.dp)
                .size(190.dp)
                .clip(CircleShape)
                .background(ForgotPrimary.copy(alpha = 0.08f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-76).dp)
                .size(250.dp)
                .clip(CircleShape)
                .background(ForgotPrimary.copy(alpha = 0.06f)),
        )
    }
}

@Composable
private fun ForgotBackChevron(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            drawLine(
                color = ForgotMuted,
                start = Offset(size.width * 0.85f, size.height * 0.08f),
                end = Offset(size.width * 0.12f, size.height * 0.50f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = ForgotMuted,
                start = Offset(size.width * 0.12f, size.height * 0.50f),
                end = Offset(size.width * 0.85f, size.height * 0.92f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun LockResetIcon() {
    Canvas(modifier = Modifier.size(62.dp)) {
        // Circular reset arrow
        drawArc(
            color = ForgotPrimary,
            startAngle = 26f,
            sweepAngle = 320f,
            useCenter = false,
            topLeft = Offset(size.width * 0.10f, size.height * 0.10f),
            size = Size(size.width * 0.80f, size.height * 0.80f),
            style = Stroke(width = size.width * 0.10f, cap = StrokeCap.Round),
        )

        drawLine(
            color = ForgotPrimary,
            start = Offset(size.width * 0.28f, size.height * 0.12f),
            end = Offset(size.width * 0.10f, size.height * 0.28f),
            strokeWidth = size.width * 0.10f,
            cap = StrokeCap.Round,
        )

        drawLine(
            color = ForgotPrimary,
            start = Offset(size.width * 0.28f, size.height * 0.12f),
            end = Offset(size.width * 0.28f, size.height * 0.32f),
            strokeWidth = size.width * 0.10f,
            cap = StrokeCap.Round,
        )

        // Lock body
        drawRoundRect(
            color = ForgotPrimary,
            topLeft = Offset(size.width * 0.36f, size.height * 0.48f),
            size = Size(size.width * 0.30f, size.height * 0.26f),
            cornerRadius = CornerRadius(size.width * 0.05f),
        )

        // Lock shackle
        drawArc(
            color = ForgotPrimary,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.41f, size.height * 0.34f),
            size = Size(size.width * 0.20f, size.height * 0.22f),
            style = Stroke(width = size.width * 0.07f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun ForgotPasswordSuccessSnackbar(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x252D9CDB),
                spotColor = Color(0x252D9CDB),
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ForgotSuccess),
            contentAlignment = Alignment.Center,
        ) {
            ForgotCheckIcon(color = Color.White)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ForgotText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = ForgotMuted,
                fontSize = 12.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            ForgotCloseIcon(color = ForgotClose)
        }
    }
}

@Composable
private fun ForgotCheckIcon(color: Color) {
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
private fun ForgotCloseIcon(color: Color) {
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
