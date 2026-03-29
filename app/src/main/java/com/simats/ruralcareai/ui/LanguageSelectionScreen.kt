package com.simats.ruralcareai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.model.AppLanguage

private val ScreenBackground = Color(0xFFF7F9FC)
private val PrimaryBlue = Color(0xFF2D9CDB)
private val HeadlineColor = Color(0xFF18253E)
private val BodyMuted = Color(0xFF98A1B2)
private val CardWhite = Color(0xFFFFFFFF)
private val UnselectedIconColor = Color(0xFFA5ADBA)

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TopNavigation(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF5FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "文A",
                    color = PrimaryBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose Language",
                color = HeadlineColor,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select your preferred language to continue.",
                color = BodyMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LanguageOptionCard(
            language = AppLanguage.PUNJABI,
            selected = selectedLanguage == AppLanguage.PUNJABI,
            onClick = { onLanguageSelected(AppLanguage.PUNJABI) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LanguageOptionCard(
            language = AppLanguage.HINDI,
            selected = selectedLanguage == AppLanguage.HINDI,
            onClick = { onLanguageSelected(AppLanguage.HINDI) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LanguageOptionCard(
            language = AppLanguage.ENGLISH,
            selected = selectedLanguage == AppLanguage.ENGLISH,
            onClick = { onLanguageSelected(AppLanguage.ENGLISH) }
        )

        Spacer(modifier = Modifier.weight(1f))

        ContinueButton(onClick = onContinue)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "You can change this later in settings.",
            modifier = Modifier.fillMaxWidth(),
            color = BodyMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun TopNavigation(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            ChevronLeftIcon(color = PrimaryBlue)
        }

        Text(
            text = "Language",
            color = HeadlineColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.size(36.dp))
    }
}

@Composable
private fun LanguageOptionCard(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderWidth = if (selected) 2.dp else 0.dp
    val borderColor = if (selected) PrimaryBlue else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardWhite)
            .border(borderWidth, borderColor, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconContainerColor = if (selected) Color(0xFFEFF5FE) else Color(0xFFF3F4F6)
        val iconTextColor = if (selected) PrimaryBlue else UnselectedIconColor

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = language.iconText,
                color = iconTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (language.nativeLabel != null) {
                Text(
                    text = language.nativeLabel,
                    color = BodyMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Text(
                text = language.displayName,
                color = HeadlineColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                CheckIcon(color = Color.White)
            }
        }
    }
}

@Composable
private fun ContinueButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2D9CDB), Color(0xFF1E88E5))
                )
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            ArrowRightIcon(color = Color.White)
        }
    }
}

@Composable
private fun ChevronLeftIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.64f, size.height * 0.2f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.36f, size.height * 0.5f),
            strokeWidth = size.minDimension * 0.13f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.36f, size.height * 0.5f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.64f, size.height * 0.8f),
            strokeWidth = size.minDimension * 0.13f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CheckIcon(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.55f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.44f, size.height * 0.78f),
            strokeWidth = size.minDimension * 0.17f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.44f, size.height * 0.78f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.28f),
            strokeWidth = size.minDimension * 0.17f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ArrowRightIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.12f, size.height * 0.5f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.88f, size.height * 0.5f),
            strokeWidth = size.minDimension * 0.11f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.58f, size.height * 0.2f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.88f, size.height * 0.5f),
            strokeWidth = size.minDimension * 0.11f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.58f, size.height * 0.8f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.88f, size.height * 0.5f),
            strokeWidth = size.minDimension * 0.11f,
            cap = StrokeCap.Round
        )
    }
}
