package com.simats.ruralcareai.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBlueTop = Color(0xFF1F8FE5)
private val SplashBlueBottom = Color(0xFF7FB3E6)
private val SplashWhite = Color(0xFFF4F8FF)

@Composable
fun RuralCareSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    durationMillis: Long = 2400L,
) {
    var revealContent by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (revealContent) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "splash-content-alpha"
    )

    val contentScale by animateFloatAsState(
        targetValue = if (revealContent) 1f else 0.92f,
        animationSpec = tween(durationMillis = 900),
        label = "splash-content-scale"
    )

    val glowPulse by rememberInfiniteTransition(label = "glow-pulse").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-pulse-value"
    )

    LaunchedEffect(Unit) {
        revealContent = true
        delay(durationMillis)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SplashBlueTop, SplashBlueBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    scaleX = contentScale
                    scaleY = contentScale
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            BrandIcon(glowPulse = glowPulse)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "RuralCareAI",
                color = SplashWhite,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Nabha, Punjab",
                color = SplashWhite.copy(alpha = 0.95f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "AI-POWERED RURAL HEALTHCARE",
                color = SplashWhite.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SplashDot(active = true)
                SplashDot(active = false)
                SplashDot(active = false)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BrandIcon(glowPulse: Float) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(glowPulse),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
                .alpha(0.9f)
        )

        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .border(width = 1.2.dp, color = Color.White.copy(alpha = 0.30f), shape = RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            ShieldCrossIcon(modifier = Modifier.width(48.dp).height(56.dp))
        }
    }
}

@Composable
private fun SplashDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                if (active) Color.White else Color.White.copy(alpha = 0.32f)
            )
    )
}

@Composable
private fun ShieldCrossIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawShieldPath()
        drawMedicalCross()
    }
}

private fun DrawScope.drawShieldPath() {
    val shield = Path().apply {
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.74f)
        lineTo(size.width * 0.5f, size.height)
        lineTo(0f, size.height * 0.74f)
        close()
    }

    drawPath(
        path = shield,
        color = Color.White,
        style = Fill
    )

    drawPath(
        path = shield,
        color = Color.White.copy(alpha = 0.9f),
        style = Stroke(width = size.minDimension * 0.014f)
    )
}

private fun DrawScope.drawMedicalCross() {
    val crossColor = SplashBlueTop
    val verticalWidth = size.width * 0.18f
    val verticalHeight = size.height * 0.36f
    val horizontalWidth = size.width * 0.44f
    val horizontalHeight = size.width * 0.18f

    val verticalX = (size.width - verticalWidth) / 2f
    val verticalY = size.height * 0.33f
    val horizontalX = (size.width - horizontalWidth) / 2f
    val horizontalY = size.height * 0.44f

    drawRoundRect(
        color = crossColor,
        topLeft = androidx.compose.ui.geometry.Offset(verticalX, verticalY),
        size = androidx.compose.ui.geometry.Size(verticalWidth, verticalHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(verticalWidth / 2f)
    )

    drawRoundRect(
        color = crossColor,
        topLeft = androidx.compose.ui.geometry.Offset(horizontalX, horizontalY),
        size = androidx.compose.ui.geometry.Size(horizontalWidth, horizontalHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(horizontalHeight / 2f)
    )
}
