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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.model.UserRole

private val ProfileBackground = Color(0xFFF4F6FA)
private val ProfileText = Color(0xFF141721)
private val ProfileMuted = Color(0xFF646A74)
private val ProfileBlue = Color(0xFF1F9BE6)
private val ProfileBlueDark = Color(0xFF228BD9)
private val ProfileCard = Color(0xFFFFFFFF)
private val ProfileOutline = Color(0xFFE2E7EF)
private val ProfileError = Color(0xFFC53A3A)

@Composable
fun ProfileSelectionScreen(
    selectedRole: UserRole?,
    errorMessage: String?,
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ProfileBackground)
            .verticalScroll(rememberScrollState())
            .padding(start = 22.dp, end = 22.dp, top = 36.dp, bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackChevron(modifier = Modifier.clickable(onClick = onBack))
            Text(
                text = "Nabba Health",
                color = ProfileText,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Who are you?",
            color = ProfileText,
            fontSize = 56.sp / 2,
            lineHeight = 64.sp / 2,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select your role to continue your healthcare\njourney in Nabha.",
            color = ProfileMuted,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserRole.entries.forEach { role ->
            ProfileCard(
                role = role,
                selected = selectedRole == role,
                onClick = { onRoleSelected(role) },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (errorMessage != null && selectedRole == null) {
            Text(
                text = errorMessage,
                color = ProfileError,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x352D9CDB),
                    spotColor = Color(0x352D9CDB),
                )
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(listOf(ProfileBlue, ProfileBlueDark))
                )
                .clickable(onClick = onContinue)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Continue ->",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "By continuing, you agree to Nabha Health's Privacy\nPolicy and Terms of Service",
            color = ProfileMuted,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun ProfileCard(
    role: UserRole,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) ProfileBlue else ProfileOutline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .shadow(
                elevation = if (selected) 10.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x132D9CDB),
                spotColor = Color(0x132D9CDB),
            )
            .background(ProfileCard)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selected) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "SELECTED",
                        color = ProfileBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp,
                    )
                    SelectedBadge()
                }
            }

            Text(
                text = role.displayName,
                color = ProfileText,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = role.description,
                color = ProfileMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Box(
            modifier = Modifier
                .size(108.dp, 88.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(roleBackground(role)),
            contentAlignment = Alignment.Center,
        ) {
            RoleIllustration(role)
        }
    }
}

@Composable
private fun SelectedBadge() {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(ProfileBlue),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawLine(
                color = Color.White,
                start = Offset(size.width * 0.10f, size.height * 0.55f),
                end = Offset(size.width * 0.38f, size.height * 0.83f),
                strokeWidth = size.width * 0.20f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width * 0.38f, size.height * 0.83f),
                end = Offset(size.width * 0.90f, size.height * 0.18f),
                strokeWidth = size.width * 0.20f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun BackChevron(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(14.dp)) {
            drawLine(
                color = ProfileText,
                start = Offset(size.width * 0.85f, size.height * 0.08f),
                end = Offset(size.width * 0.12f, size.height * 0.50f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = ProfileText,
                start = Offset(size.width * 0.12f, size.height * 0.50f),
                end = Offset(size.width * 0.85f, size.height * 0.92f),
                strokeWidth = size.width * 0.16f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun RoleIllustration(role: UserRole) {
    Canvas(modifier = Modifier.size(74.dp)) {
        val skin = Color(0xFFF5C49A)
        val coat = when (role) {
            UserRole.ADMIN -> Color(0xFFE8EDF3)
            UserRole.PATIENT -> Color(0xFFF7F3EA)
            UserRole.COMMUNITY_HEALTH_WORKER -> Color(0xFFE97D3C)
            UserRole.MEDICAL_PROFESSIONAL -> Color(0xFFEAF3F7)
        }
        val hair = when (role) {
            UserRole.ADMIN -> Color(0xFF2D3A4F)
            UserRole.PATIENT -> Color(0xFF4F3A33)
            UserRole.COMMUNITY_HEALTH_WORKER -> Color(0xFF2D3A4F)
            UserRole.MEDICAL_PROFESSIONAL -> Color(0xFF5A413A)
        }

        drawCircle(color = skin, radius = size.width * 0.16f, center = Offset(size.width * 0.5f, size.height * 0.25f))
        drawRoundRect(
            color = coat,
            topLeft = Offset(size.width * 0.28f, size.height * 0.40f),
            size = Size(size.width * 0.44f, size.height * 0.44f),
            cornerRadius = CornerRadius(size.width * 0.08f),
        )
        drawRoundRect(
            color = hair,
            topLeft = Offset(size.width * 0.34f, size.height * 0.09f),
            size = Size(size.width * 0.32f, size.height * 0.16f),
            cornerRadius = CornerRadius(size.width * 0.10f),
        )

        if (role == UserRole.MEDICAL_PROFESSIONAL) {
            drawArc(
                color = Color(0xFF2EA4B8),
                startAngle = 210f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(size.width * 0.26f, size.height * 0.38f),
                size = Size(size.width * 0.48f, size.height * 0.48f),
                style = Stroke(width = size.width * 0.03f),
            )
        }
    }
}

private fun roleBackground(role: UserRole): Color {
    return when (role) {
        UserRole.ADMIN -> Color(0xFF7CB4D6)
        UserRole.PATIENT -> Color(0xFFA0D7CE)
        UserRole.COMMUNITY_HEALTH_WORKER -> Color(0xFFF0DFC6)
        UserRole.MEDICAL_PROFESSIONAL -> Color(0xFF61BDC0)
    }
}
