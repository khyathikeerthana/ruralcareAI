package com.simats.ruralcareai.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OnboardingBackground = Color(0xFFF7F9FC)
private val OnboardingBlue = Color(0xFF2D9CDB)
private val OnboardingBlueDark = Color(0xFF1F8FE5)
private val OnboardingHeadline = Color(0xFF1F2937)
private val OnboardingMuted = Color(0xFF6B7280)
private val DotInactive = Color(0xFFD1D5DB)

private data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val pageIndex: Int,
)

private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Consult Doctors From\nYour Village",
        subtitle = "Get AI-powered telemedicine and expert support from the comfort of your home in Nabha.",
        pageIndex = 0,
    ),
    OnboardingPageData(
        title = "Book Appointments\nEasily",
        subtitle = "Schedule consultations with qualified doctors anytime, anywhere, without long queues.",
        pageIndex = 1,
    ),
    OnboardingPageData(
        title = "Get Digital\nPrescriptions",
        subtitle = "Receive prescriptions and medical records securely on your phone, always accessible.",
        pageIndex = 2,
    ),
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val totalPages = onboardingPages.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnboardingBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            // Skip button — top right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Skip",
                    color = OnboardingMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable(onClick = onComplete)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }

            // Main content — stretchy middle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Illustration card with swipe gesture
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(24.dp))
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (dragAccumulator < -80f && currentPage < totalPages - 1) {
                                        currentPage++
                                    } else if (dragAccumulator > 80f && currentPage > 0) {
                                        currentPage--
                                    }
                                    dragAccumulator = 0f
                                },
                                onHorizontalDrag = { change, amount ->
                                    change.consume()
                                    dragAccumulator += amount
                                },
                            )
                        },
                ) {
                    AnimatedContent(
                        targetState = currentPage,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))) togetherWith
                                        (slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)))
                            } else {
                                (slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300))) togetherWith
                                        (slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)))
                            }
                        },
                        label = "onboarding-illustration",
                    ) { page ->
                        OnboardingIllustration(
                            pageIndex = page,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Animated title + subtitle
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        (fadeIn(tween(250))) togetherWith (fadeOut(tween(200)))
                    },
                    label = "onboarding-text",
                ) { page ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text(
                            text = onboardingPages[page].title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnboardingHeadline,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = onboardingPages[page].subtitle,
                            fontSize = 14.sp,
                            color = OnboardingMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }

            // Bottom section — dots, Next button, swipe hint
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Pagination dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(totalPages) { index ->
                        if (index == currentPage) {
                            // Active pill
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(OnboardingBlue),
                            )
                        } else {
                            // Inactive circle
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(DotInactive),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Next / Get Started button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(OnboardingBlue, OnboardingBlueDark)
                            )
                        )
                        .clickable {
                            if (currentPage < totalPages - 1) currentPage++
                            else onComplete()
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (currentPage < totalPages - 1) "Next" else "Get Started",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

            }
        }
    }
}

// ─── Illustrations ────────────────────────────────────────────────────────────

@Composable
private fun OnboardingIllustration(pageIndex: Int, modifier: Modifier = Modifier) {
    when (pageIndex) {
        0 -> ConsultationIllustration(modifier)
        1 -> AppointmentIllustration(modifier)
        else -> PrescriptionIllustration(modifier)
    }
}

@Composable
private fun ConsultationIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Sky/green outdoor background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFCBE8F5), Color(0xFFE8F5E9)),
                startY = 0f,
                endY = size.height,
            )
        )
        // Window panels (green trees outside)
        val winW = size.width * 0.44f
        val winH = size.height * 0.55f
        val winY = size.height * 0.03f
        drawRoundRect(
            color = Color(0xFF81C784),
            topLeft = Offset(size.width * 0.04f, winY),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(16f),
        )
        drawRoundRect(
            color = Color(0xFF66BB6A),
            topLeft = Offset(size.width * 0.52f, winY),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(16f),
        )
        // Window frame lines
        drawRoundRect(
            color = Color(0xFFBDBDBD),
            topLeft = Offset(size.width * 0.04f, winY),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(16f),
            style = Stroke(width = size.width * 0.012f),
        )
        drawRoundRect(
            color = Color(0xFFBDBDBD),
            topLeft = Offset(size.width * 0.52f, winY),
            size = Size(winW, winH),
            cornerRadius = CornerRadius(16f),
            style = Stroke(width = size.width * 0.012f),
        )
        // Table
        val tableY = size.height * 0.60f
        drawRoundRect(
            color = Color(0xFF8D6E63),
            topLeft = Offset(size.width * 0.05f, tableY),
            size = Size(size.width * 0.90f, size.height * 0.08f),
            cornerRadius = CornerRadius(8f),
        )
        // Laptop on table
        val laptopX = size.width * 0.36f
        val laptopY = tableY - size.height * 0.16f
        drawRoundRect(
            color = Color(0xFF455A64),
            topLeft = Offset(laptopX, laptopY),
            size = Size(size.width * 0.28f, size.height * 0.15f),
            cornerRadius = CornerRadius(6f),
        )
        drawRoundRect(
            color = Color(0xFF37474F),
            topLeft = Offset(laptopX + size.width * 0.01f, laptopY + size.height * 0.01f),
            size = Size(size.width * 0.26f, size.height * 0.12f),
            cornerRadius = CornerRadius(4f),
            style = Fill,
        )
        // Screen glow
        drawRoundRect(
            color = Color(0xFF4FC3F7).copy(alpha = 0.5f),
            topLeft = Offset(laptopX + size.width * 0.015f, laptopY + size.height * 0.015f),
            size = Size(size.width * 0.25f, size.height * 0.115f),
            cornerRadius = CornerRadius(3f),
        )
        // Doctor figure (left)
        drawPersonFigure(
            cx = size.width * 0.22f,
            tableTopY = tableY,
            bodyColor = Color(0xFFEFEFEF),
            headColor = Color(0xFFFDDCAA),
            isDoctor = true,
        )
        // Patient figure (right)
        drawPersonFigure(
            cx = size.width * 0.78f,
            tableTopY = tableY,
            bodyColor = Color(0xFF78909C),
            headColor = Color(0xFFD4956A),
            isDoctor = false,
        )
    }
}

private fun DrawScope.drawPersonFigure(
    cx: Float,
    tableTopY: Float,
    bodyColor: Color,
    headColor: Color,
    isDoctor: Boolean,
) {
    val headR = size.width * 0.055f
    val bodyW = size.width * 0.10f
    val bodyH = size.height * 0.22f
    val headY = tableTopY - bodyH - headR * 2.2f

    // Body (torso)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - bodyW / 2f, tableTopY - bodyH),
        size = Size(bodyW, bodyH),
        cornerRadius = CornerRadius(bodyW / 3f),
    )
    // Head
    drawCircle(color = headColor, radius = headR, center = Offset(cx, headY))
    // Hair
    drawArc(
        color = if (isDoctor) Color(0xFFF4D03F) else Color(0xFF2C1810),
        startAngle = 190f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(cx - headR, headY - headR),
        size = Size(headR * 2, headR * 2),
        style = Stroke(width = headR * 0.6f, cap = StrokeCap.Round),
    )
    // White coat stripe for doctor
    if (isDoctor) {
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cx - bodyW * 0.15f, tableTopY - bodyH),
            size = Size(bodyW * 0.30f, bodyH * 0.6f),
            cornerRadius = CornerRadius(4f),
        )
    }
    // Stethoscope for doctor
    if (isDoctor) {
        drawCircle(
            color = Color(0xFF1F8FE5),
            radius = size.width * 0.015f,
            center = Offset(cx - bodyW * 0.08f, tableTopY - bodyH * 0.65f),
        )
    }
}

@Composable
private fun AppointmentIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Soft teal background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2)),
            )
        )
        // Calendar base
        val calW = size.width * 0.55f
        val calH = size.height * 0.58f
        val calX = (size.width - calW) / 2f
        val calY = size.height * 0.12f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(calX, calY),
            size = Size(calW, calH),
            cornerRadius = CornerRadius(20f),
        )
        // Calendar header
        drawRoundRect(
            color = Color(0xFF2D9CDB),
            topLeft = Offset(calX, calY),
            size = Size(calW, calH * 0.22f),
            cornerRadius = CornerRadius(20f),
        )
        // Calendar grid dots
        val dotR = size.width * 0.018f
        val colStep = calW / 4f
        val rowStep = calH * 0.18f
        for (row in 0..2) {
            for (col in 0..2) {
                val dotX = calX + colStep * (col + 0.75f)
                val dotY = calY + calH * 0.35f + rowStep * row
                val isDone = (row == 0 && col == 0) || (row == 0 && col == 1)
                drawCircle(
                    color = if (isDone) Color(0xFF2D9CDB) else Color(0xFFE0E0E0),
                    radius = dotR,
                    center = Offset(dotX, dotY),
                )
            }
        }
        // Highlighted appointment box
        drawRoundRect(
            color = Color(0xFF2D9CDB).copy(alpha = 0.15f),
            topLeft = Offset(calX + calW * 0.08f, calY + calH * 0.63f),
            size = Size(calW * 0.84f, calH * 0.20f),
            cornerRadius = CornerRadius(10f),
        )
        drawRoundRect(
            color = Color(0xFF2D9CDB),
            topLeft = Offset(calX + calW * 0.08f, calY + calH * 0.63f),
            size = Size(calW * 0.06f, calH * 0.20f),
            cornerRadius = CornerRadius(3f),
        )
        // Checkmark badge
        val badgeCx = calX + calW * 0.82f
        val badgeCy = calY + calH * 0.05f
        drawCircle(color = Color(0xFF4CAF50), radius = size.width * 0.055f, center = Offset(badgeCx, badgeCy))
        // Check line
        drawLine(
            color = Color.White,
            start = Offset(badgeCx - size.width * 0.025f, badgeCy),
            end = Offset(badgeCx - size.width * 0.005f, badgeCy + size.width * 0.022f),
            strokeWidth = size.width * 0.016f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(badgeCx - size.width * 0.005f, badgeCy + size.width * 0.022f),
            end = Offset(badgeCx + size.width * 0.028f, badgeCy - size.width * 0.02f),
            strokeWidth = size.width * 0.016f,
            cap = StrokeCap.Round,
        )
        // Calendar top rings
        val ringY = calY + calH * 0.01f
        drawCircle(
            color = Color(0xFF1A6FA5),
            radius = size.width * 0.02f,
            center = Offset(calX + calW * 0.3f, ringY),
        )
        drawCircle(
            color = Color(0xFF1A6FA5),
            radius = size.width * 0.02f,
            center = Offset(calX + calW * 0.7f, ringY),
        )
        // Bottom text hint lines
        drawRoundRect(
            color = Color(0xFFBBDEFB),
            topLeft = Offset(calX - calW * 0.12f, calY + calH + size.height * 0.06f),
            size = Size(calW * 1.24f, size.height * 0.025f),
            cornerRadius = CornerRadius(4f),
        )
        drawRoundRect(
            color = Color(0xFFBBDEFB),
            topLeft = Offset(calX + calW * 0.1f, calY + calH + size.height * 0.10f),
            size = Size(calW * 0.8f, size.height * 0.020f),
            cornerRadius = CornerRadius(4f),
        )
    }
}

@Composable
private fun PrescriptionIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        // Soft purple/lavender background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5)),
            )
        )
        // Document base
        val docW = size.width * 0.52f
        val docH = size.height * 0.60f
        val docX = (size.width - docW) / 2f
        val docY = size.height * 0.10f
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(docX, docY),
            size = Size(docW, docH),
            cornerRadius = CornerRadius(16f),
        )
        // Document header stripe
        drawRoundRect(
            color = Color(0xFF7B1FA2).copy(alpha = 0.85f),
            topLeft = Offset(docX, docY),
            size = Size(docW, docH * 0.18f),
            cornerRadius = CornerRadius(16f),
        )
        // Rx symbol
        drawCircle(
            color = Color.White,
            radius = docW * 0.12f,
            center = Offset(docX + docW * 0.22f, docY + docH * 0.09f),
        )
        // Rx text lines (simulated)
        drawRoundRect(
            color = Color(0xFF7B1FA2),
            topLeft = Offset(docX + docW * 0.12f, docY + docH * 0.05f),
            size = Size(docW * 0.20f, docH * 0.035f),
            cornerRadius = CornerRadius(3f),
        )
        drawRoundRect(
            color = Color(0xFF7B1FA2),
            topLeft = Offset(docX + docW * 0.12f, docY + docH * 0.095f),
            size = Size(docW * 0.12f, docH * 0.03f),
            cornerRadius = CornerRadius(3f),
        )
        // Prescription lines
        val lineStartX = docX + docW * 0.12f
        val lineW = docW * 0.76f
        val lineShortW = docW * 0.50f
        val lineYStart = docY + docH * 0.26f
        val lineGap = docH * 0.095f
        for (i in 0..4) {
            val lineColor = if (i % 2 == 0) Color(0xFFEEEEEE) else Color(0xFFF5F5F5)
            val currentW = if (i % 3 == 2) lineShortW else lineW
            drawRoundRect(
                color = lineColor,
                topLeft = Offset(lineStartX, lineYStart + lineGap * i),
                size = Size(currentW, docH * 0.025f),
                cornerRadius = CornerRadius(4f),
            )
        }
        // Pill icon bottom right of doc
        val pillCx = docX + docW * 0.78f
        val pillCy = docY + docH * 0.78f
        val pillR = docW * 0.10f
        drawRoundRect(
            color = Color(0xFF9C27B0).copy(alpha = 0.2f),
            topLeft = Offset(pillCx - pillR, pillCy - pillR * 0.55f),
            size = Size(pillR * 2f, pillR * 1.1f),
            cornerRadius = CornerRadius(pillR * 0.55f),
        )
        drawRoundRect(
            color = Color(0xFF9C27B0),
            topLeft = Offset(pillCx - pillR, pillCy - pillR * 0.55f),
            size = Size(pillR * 2f, pillR * 1.1f),
            cornerRadius = CornerRadius(pillR * 0.55f),
            style = Stroke(width = size.width * 0.015f),
        )
        drawLine(
            color = Color(0xFF9C27B0),
            start = Offset(pillCx, pillCy - pillR * 0.55f),
            end = Offset(pillCx, pillCy + pillR * 0.55f),
            strokeWidth = size.width * 0.010f,
        )
        // Checkmark badge
        val badgeCx = docX + docW + size.width * 0.04f
        val badgeCy = docY + docH * 0.15f
        drawCircle(color = Color(0xFF4CAF50), radius = size.width * 0.055f, center = Offset(badgeCx, badgeCy))
        drawLine(
            color = Color.White,
            start = Offset(badgeCx - size.width * 0.025f, badgeCy),
            end = Offset(badgeCx - size.width * 0.005f, badgeCy + size.width * 0.022f),
            strokeWidth = size.width * 0.016f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.White,
            start = Offset(badgeCx - size.width * 0.005f, badgeCy + size.width * 0.022f),
            end = Offset(badgeCx + size.width * 0.028f, badgeCy - size.width * 0.02f),
            strokeWidth = size.width * 0.016f,
            cap = StrokeCap.Round,
        )
        // Floating pills
        drawRoundRect(
            color = Color(0xFFCE93D8),
            topLeft = Offset(docX - size.width * 0.10f, docY + docH * 0.35f),
            size = Size(size.width * 0.14f, size.width * 0.06f),
            cornerRadius = CornerRadius(size.width * 0.03f),
        )
        drawRoundRect(
            color = Color(0xFF80DEEA),
            topLeft = Offset(docX + docW + size.width * 0.03f, docY + docH * 0.55f),
            size = Size(size.width * 0.12f, size.width * 0.055f),
            cornerRadius = CornerRadius(size.width * 0.028f),
        )
    }
}

// ─── Canvas Icons ─────────────────────────────────────────────────────────────

@Composable
private fun OBChevronRightIcon(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.35f, size.height * 0.22f),
            end = Offset(size.width * 0.65f, size.height * 0.5f),
            strokeWidth = size.minDimension * 0.14f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.65f, size.height * 0.5f),
            end = Offset(size.width * 0.35f, size.height * 0.78f),
            strokeWidth = size.minDimension * 0.14f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun OBSwipeIcon(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        val sw = size.minDimension * 0.12f
        // Palm base
        drawRoundRect(
            color = color,
            topLeft = Offset(cx - size.width * 0.28f, cy - size.height * 0.18f),
            size = Size(size.width * 0.56f, size.height * 0.50f),
            cornerRadius = CornerRadius(size.width * 0.12f),
            style = Stroke(width = sw),
        )
        // Arrows left and right
        drawLine(
            color = color,
            start = Offset(cx + size.width * 0.36f, cy),
            end = Offset(cx + size.width * 0.50f, cy),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(cx + size.width * 0.44f, cy - size.height * 0.12f),
            end = Offset(cx + size.width * 0.50f, cy),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(cx + size.width * 0.44f, cy + size.height * 0.12f),
            end = Offset(cx + size.width * 0.50f, cy),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
    }
}
