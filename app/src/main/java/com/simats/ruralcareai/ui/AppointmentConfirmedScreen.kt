package com.simats.ruralcareai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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

private val ConfirmedBackground = Color(0xFFF3F6FB)
private val ConfirmedPrimary = Color(0xFF2D9CDB)
private val ConfirmedText = Color(0xFF0F1730)
private val ConfirmedMuted = Color(0xFF61738C)

@Composable
fun AppointmentConfirmedScreen(
    booking: BookingConfirmationData,
    onBackToHome: () -> Unit,
    onAddToCalendar: () -> Unit = {},
    onShareDetails: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val methodTitle = if (booking.consultationType == ConsultationType.VIDEO_CALL) {
        "Video Call"
    } else {
        "In-Clinic Visit"
    }

    val methodSubtitle = if (booking.consultationType == ConsultationType.VIDEO_CALL) {
        "Link will be sent 5 mins before"
    } else {
        "Arrive 15 mins before consultation"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ConfirmedBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(102.dp)
                            .clip(CircleShape)
                            .background(ConfirmedPrimary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ConfirmedPrimary,
                            modifier = Modifier.size(54.dp),
                        )
                    }

                    Text(
                        text = "Appointment Confirmed",
                        color = ConfirmedText,
                        fontSize = 42.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Your consultation with ${booking.profile.name} has been successfully scheduled.",
                        color = ConfirmedMuted,
                        fontSize = 34.sp / 1.85f,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(20.dp))
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
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = booking.profile.name,
                                    color = ConfirmedText,
                                    fontSize = 40.sp / 1.8f,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = booking.profile.specialtyName,
                                    color = ConfirmedMuted,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF6B01E),
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = "${booking.profile.rating} (124 reviews)",
                                        color = ConfirmedText,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE6EDF6))

                        ConfirmedDetailRow(
                            icon = Icons.Filled.CalendarToday,
                            label = "DATE & TIME",
                            primaryValue = booking.dateLabel,
                            secondaryValue = booking.timeLabel,
                        )

                        ConfirmedDetailRow(
                            icon = if (booking.consultationType == ConsultationType.VIDEO_CALL) {
                                Icons.Filled.Videocam
                            } else {
                                Icons.Filled.LocalHospital
                            },
                            label = "METHOD",
                            primaryValue = methodTitle,
                            secondaryValue = methodSubtitle,
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onAddToCalendar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ConfirmedPrimary),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = "Add to Calendar",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onShareDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(1.dp, Color(0xFFB8DCF2)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ConfirmedPrimary),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = ConfirmedPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = "Share Details",
                            color = ConfirmedPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        onClick = onBackToHome,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8DFEA)),
                    ) {
                        Text(
                            text = "Back to Home",
                            color = Color(0xFF2E3B4E),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color(0xFF96A2B3),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "END-TO-END ENCRYPTED",
                        color = Color(0xFF96A2B3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmedDetailRow(
    icon: ImageVector,
    label: String,
    primaryValue: String,
    secondaryValue: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF2F6FB)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ConfirmedPrimary,
                modifier = Modifier.size(28.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = Color(0xFF8A99B0),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Text(
                text = primaryValue,
                color = ConfirmedText,
                fontSize = 36.sp / 1.8f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = secondaryValue,
                color = ConfirmedMuted,
                fontSize = 34.sp / 1.9f,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
