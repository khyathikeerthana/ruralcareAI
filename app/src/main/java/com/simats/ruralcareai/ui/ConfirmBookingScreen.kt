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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BookingConfirmationData(
    val profile: DoctorProfileData,
    val dateLabel: String,
    val timeLabel: String,
    val consultationType: ConsultationType,
    val locationLabel: String,
    val patientName: String,
    val patientPhone: String,
    val fee: String,
)

private val ConfirmBackground = Color(0xFFF3F6FB)
private val ConfirmPrimary = Color(0xFF2D9CDB)
private val ConfirmText = Color(0xFF0F1730)
private val ConfirmMuted = Color(0xFF61738C)

@Composable
fun ConfirmBookingScreen(
    booking: BookingConfirmationData,
    onBack: () -> Unit,
    onViewProfile: () -> Unit,
    onProceedToPayment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ConfirmBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 180.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ConfirmText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "Confirm Booking",
                        color = ConfirmText,
                        fontSize = 30.sp / 1.5f,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            item {
                Text(
                    text = "APPOINTMENT SUMMARY",
                    color = Color(0xFF6A7D97),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            booking.profile.avatarTop,
                                            booking.profile.avatarBottom,
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = booking.profile.avatarInitials,
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = booking.profile.name,
                                color = ConfirmText,
                                fontSize = 22.sp / 1.3f,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = booking.profile.specialtyName,
                                color = ConfirmPrimary,
                                fontSize = 18.sp / 1.2f,
                                fontWeight = FontWeight.Medium,
                            )

                            Row(
                                modifier = Modifier.clickable(onClick = onViewProfile),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "View Profile",
                                    color = ConfirmMuted,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = ConfirmMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column {
                        SummaryDetailRow(
                            icon = Icons.Filled.CalendarToday,
                            label = "Date",
                            value = booking.dateLabel,
                            showDivider = true,
                        )
                        SummaryDetailRow(
                            icon = Icons.Filled.Schedule,
                            label = "Time",
                            value = booking.timeLabel,
                            showDivider = true,
                        )
                        SummaryDetailRow(
                            icon = if (booking.consultationType == ConsultationType.VIDEO_CALL) {
                                Icons.Filled.Videocam
                            } else {
                                Icons.Filled.LocalHospital
                            },
                            label = "Consultation Type",
                            value = if (booking.consultationType == ConsultationType.VIDEO_CALL) {
                                "Video Call"
                            } else {
                                "In-Clinic"
                            },
                            showDivider = true,
                        )
                        SummaryDetailRow(
                            icon = Icons.Filled.LocationOn,
                            label = "Location",
                            value = booking.locationLabel,
                            showDivider = false,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "PATIENT INFORMATION",
                        color = Color(0xFF6A7D97),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFDCEEFF))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Edit",
                            color = ConfirmPrimary,
                            fontSize = 16.sp / 1.15f,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = booking.patientName,
                            color = ConfirmText,
                            fontSize = 22.sp / 1.3f,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = booking.patientPhone,
                            color = ConfirmMuted,
                            fontSize = 17.sp / 1.15f,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFE5ECF4))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Consultation Fee",
                    color = ConfirmMuted,
                    fontSize = 24.sp / 1.4f,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = booking.fee,
                    color = ConfirmText,
                    fontSize = 48.sp / 1.7f,
                    fontWeight = FontWeight.Bold,
                )
            }

            Button(
                onClick = onProceedToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                shape = RoundedCornerShape(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ConfirmPrimary),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Proceed to Payment",
                        color = Color.White,
                        fontSize = 42.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ConfirmPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ConfirmPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Text(
                    text = label,
                    color = ConfirmMuted,
                    fontSize = 18.sp / 1.2f,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = value,
                color = ConfirmText,
                fontSize = 20.sp / 1.3f,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.width(170.dp),
            )
        }

        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE8EEF6))
            )
        }
    }
}
