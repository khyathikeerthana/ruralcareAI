package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DoctorProfileData(
    val name: String,
    val title: String,
    val specialtyName: String,
    val yearsExp: String,
    val rating: String,
    val patients: String,
    val about: String,
    val clinicSummary: String,
    val educationInstitute: String,
    val educationDegree: String,
    val clinicAddress: String,
    val reviewAuthor: String,
    val reviewText: String,
    val price: String,
    val avatarInitials: String,
    val avatarTop: Color,
    val avatarBottom: Color,
    val isOnline: Boolean,
)

enum class ConsultationType {
    VIDEO_CALL,
    IN_CLINIC,
}

private val ProfileBackground = Color(0xFFF3F6FB)
private val ProfilePrimary = Color(0xFF2D9CDB)
private val ProfileText = Color(0xFF0F1730)
private val ProfileMuted = Color(0xFF5E7088)
private val ProfileCard = Color.White

@Composable
fun DoctorProfileScreen(
    profile: DoctorProfileData,
    onBack: () -> Unit,
    onBookNow: (ConsultationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedConsultation by remember { mutableStateOf(ConsultationType.VIDEO_CALL) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ProfileBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ProfileTopBar(onBack = onBack)
            }

            item {
                ProfileHeader(profile = profile)
            }

            item {
                StatsRow(profile = profile)
            }

            item {
                SectionTitle(title = "About Doctor")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile.about,
                    color = ProfileMuted,
                    fontSize = 16.sp,
                    lineHeight = 30.sp / 1.35f,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                SectionTitle(title = "Education")
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEAF2FA)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = ProfilePrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = profile.educationInstitute,
                            color = ProfileText,
                            fontSize = 20.sp / 1.25f,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = profile.educationDegree,
                            color = ProfileMuted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                SectionTitle(title = "Consultation Type")
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ConsultationCard(
                        title = "Video Call",
                        modifier = Modifier.weight(1f),
                        selected = selectedConsultation == ConsultationType.VIDEO_CALL,
                        icon = Icons.Filled.Videocam,
                        onClick = { selectedConsultation = ConsultationType.VIDEO_CALL },
                    )
                    ConsultationCard(
                        title = "In-Clinic",
                        modifier = Modifier.weight(1f),
                        selected = selectedConsultation == ConsultationType.IN_CLINIC,
                        icon = Icons.Filled.LocalHospital,
                        onClick = { selectedConsultation = ConsultationType.IN_CLINIC },
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionTitle(title = "Clinic Location")
                    Text(
                        text = "View on Maps",
                        color = ProfilePrimary,
                        fontSize = 16.sp / 1.1f,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                MapCard()
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = profile.clinicAddress,
                    color = ProfileMuted,
                    fontSize = 16.sp / 1.1f,
                    fontWeight = FontWeight.Medium,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionTitle(title = "Patient Reviews")
                    Text(
                        text = "See All",
                        color = ProfilePrimary,
                        fontSize = 16.sp / 1.1f,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                ReviewCard(profile = profile)
            }
        }

        BookingFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            price = profile.price,
            onBookNow = { onBookNow(selectedConsultation) },
        )
    }
}

@Composable
private fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleTopButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack, tint = ProfileText)

        Text(
            text = "Doctor Details",
            color = ProfileText,
            fontSize = 26.sp / 1.5f,
            fontWeight = FontWeight.SemiBold,
        )

        CircleTopButton(icon = Icons.Filled.Favorite, onClick = {}, tint = Color(0xFFE83A66))
    }
}

@Composable
private fun CircleTopButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0xFFE5ECF4), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ProfileHeader(profile: DoctorProfileData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(126.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(profile.avatarTop, profile.avatarBottom)))
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.avatarInitials,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (profile.isOnline) Color(0xFF22C55E) else Color(0xFF94A3B8))
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Text(
            text = profile.name,
            color = ProfileText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = profile.title,
            color = ProfilePrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFDCEEFF))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = ProfilePrimary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "RURALCAREAI CERTIFIED",
                color = ProfilePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }
    }
}

@Composable
private fun StatsRow(profile: DoctorProfileData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProfileStatCard(label = "YEARS EXP", value = profile.yearsExp, modifier = Modifier.weight(1f))
        ProfileStatCard(
            label = "RATING",
            value = profile.rating,
            modifier = Modifier.weight(1f),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFF6B01E),
                    modifier = Modifier.size(15.dp),
                )
            },
        )
        ProfileStatCard(label = "PATIENTS", value = profile.patients, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCard),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    color = ProfileText,
                    fontSize = 32.sp / 1.5f,
                    fontWeight = FontWeight.Bold,
                )
                trailingIcon?.invoke()
            }
            Text(
                text = label,
                color = Color(0xFF8A99B0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = ProfileText,
        fontSize = 22.sp / 1.3f,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ConsultationCard(
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) ProfilePrimary else Color(0xFFE2EAF3),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ProfilePrimary,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = title,
                color = ProfileText,
                fontSize = 20.sp / 1.35f,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MapCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFFD9DDE3)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE6E9EC))
                .border(2.dp, Color.White, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ProfilePrimary)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(profile: DoctorProfileData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9EEF5))
                    )
                    Text(
                        text = profile.reviewAuthor,
                        color = ProfileText,
                        fontSize = 17.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFF6B01E),
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }
            Text(
                text = profile.reviewText,
                color = ProfileMuted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun BookingFooter(
    modifier: Modifier = Modifier,
    price: String,
    onBookNow: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .border(1.dp, Color(0xFFE5ECF4))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "TOTAL PRICE",
                color = Color(0xFF8A99B0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = price,
                color = ProfileText,
                fontSize = 36.sp / 1.5f,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(
            onClick = onBookNow,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ProfilePrimary),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Book Now",
                    color = Color.White,
                    fontSize = 17.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
