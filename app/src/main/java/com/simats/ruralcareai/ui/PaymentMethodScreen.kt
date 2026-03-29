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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

enum class PaymentOption {
    PAY_ONLINE,
    PAY_CASH_AT_CENTER,
}

private val PaymentBackground = Color(0xFFF3F6FB)
private val PaymentPrimary = Color(0xFF2D9CDB)
private val PaymentText = Color(0xFF0F1730)
private val PaymentMuted = Color(0xFF61738C)

@Composable
fun PaymentMethodScreen(
    booking: BookingConfirmationData,
    onBack: () -> Unit,
    onPayNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedOption by remember(booking.consultationType) {
        mutableStateOf(
            if (booking.consultationType == ConsultationType.IN_CLINIC) {
                PaymentOption.PAY_CASH_AT_CENTER
            } else {
                PaymentOption.PAY_ONLINE
            }
        )
    }

    val serviceFee = 2.50
    val consultationFee = parseMoney(booking.fee)
    val totalPayable = consultationFee + serviceFee

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(PaymentBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 190.dp),
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE4EBF5), CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PaymentText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "Payment Method",
                        color = PaymentText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(42.dp))
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Select Payment Method",
                        color = PaymentText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Choose your preferred way to pay for healthcare services.",
                        color = PaymentMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                PaymentOptionCard(
                    title = "Pay Online",
                    subtitle = "Fast and secure digital payment",
                    icon = Icons.Filled.AccountBalanceWallet,
                    selected = selectedOption == PaymentOption.PAY_ONLINE,
                    onClick = { selectedOption = PaymentOption.PAY_ONLINE },
                )
            }

            item {
                PaymentOptionCard(
                    title = "Pay Cash at Health Center",
                    subtitle = "Visit your nearest RuralCare center",
                    icon = Icons.Filled.Payments,
                    selected = selectedOption == PaymentOption.PAY_CASH_AT_CENTER,
                    onClick = { selectedOption = PaymentOption.PAY_CASH_AT_CENTER },
                )
            }

            item {
                Text(
                    text = "SUPPORTED UPI APPS",
                    color = Color(0xFF8A99B0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    UpiAppBadge(shortLabel = "GPay", appName = "GOOGLE PAY", color = Color(0xFF2563EB))
                    UpiAppBadge(shortLabel = "PhPe", appName = "PHONEPE", color = Color(0xFF9333EA))
                    UpiAppBadge(shortLabel = "Paytm", appName = "PAYTM", color = Color(0xFF0EA5E9))
                    UpiAppBadge(shortLabel = "BHIM", appName = "BHIM UPI", color = Color(0xFFF97316))
                }
            }

            item {
                PayCashDetailsCard(appointmentId = generateAppointmentId(booking))
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "TOTAL PAYABLE",
                        color = Color(0xFF8A99B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = formatMoney(totalPayable),
                        color = PaymentText,
                        fontSize = 38.sp / 1.5f,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "SERVICE FEE",
                        color = Color(0xFF8A99B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "+${formatMoney(serviceFee)}",
                        color = PaymentMuted,
                        fontSize = 24.sp / 1.5f,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Button(
                onClick = onPayNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaymentPrimary),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Pay Now",
                        color = Color.White,
                        fontSize = 20.sp,
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
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.84f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE6F2FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PaymentPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        color = PaymentText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = subtitle,
                        color = PaymentMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Icon(
                imageVector = if (selected) {
                    Icons.Filled.RadioButtonChecked
                } else {
                    Icons.Outlined.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = if (selected) PaymentPrimary else Color(0xFFB8C5D6),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun UpiAppBadge(
    shortLabel: String,
    appName: String,
    color: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5ECF4), RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = shortLabel,
                    color = Color.White,
                    fontSize = 16.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = appName,
            color = PaymentMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PayCashDetailsCard(appointmentId: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2F8)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(PaymentPrimary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Pay Cash Details",
                    color = PaymentPrimary,
                    fontSize = 18.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "When choosing to pay at the center, please present your Appointment ID ($appointmentId) at the front desk. Payments must be cleared 15 minutes prior to the consultation.",
                    color = PaymentMuted,
                    fontSize = 15.sp / 1.12f,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun parseMoney(raw: String): Double {
    val value = raw.filter { it.isDigit() || it == '.' }
    return value.toDoubleOrNull() ?: 0.0
}

private fun formatMoney(amount: Double): String {
    return "$" + String.format(Locale.US, "%.2f", amount)
}

private fun generateAppointmentId(booking: BookingConfirmationData): String {
    val doctorSeed = booking.profile.name
        .filter(Char::isLetter)
        .uppercase()
        .take(2)
        .ifBlank { "RC" }
    val dateSeed = booking.dateLabel.filter(Char::isDigit).takeLast(4).padStart(4, '0')
    val timeSeed = booking.timeLabel.filter(Char::isDigit).takeLast(4).padStart(4, '0')
    return "RC-$doctorSeed-$dateSeed-$timeSeed"
}
