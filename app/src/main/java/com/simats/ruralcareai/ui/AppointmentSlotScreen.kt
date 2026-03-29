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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WbSunny
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SlotBackground = Color(0xFFF3F6FB)
private val SlotPrimary = Color(0xFF2D9CDB)
private val SlotText = Color(0xFF0F1730)
private val SlotMuted = Color(0xFF60738F)

private data class DateOption(
    val day: String,
    val date: String,
    val summaryLabel: String,
    val detailLabel: String,
)

private data class SlotOption(
    val time: String,
    val enabled: Boolean = true,
)

@Composable
fun AppointmentSlotScreen(
    profile: DoctorProfileData,
    consultationType: ConsultationType,
    onBack: () -> Unit,
    onConfirmSlot: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dates = listOf(
        DateOption("MON", "12", "Mon, 12 Oct", "Oct 12, 2023"),
        DateOption("TUE", "13", "Tue, 13 Oct", "Oct 13, 2023"),
        DateOption("WED", "14", "Wed, 14 Oct", "Oct 14, 2023"),
        DateOption("THU", "15", "Thu, 15 Oct", "Oct 15, 2023"),
        DateOption("FRI", "16", "Fri, 16 Oct", "Oct 16, 2023"),
    )

    val morningSlots = listOf(
        SlotOption("08:30 AM"),
        SlotOption("09:15 AM"),
        SlotOption("10:00 AM"),
        SlotOption("11:30 AM"),
    )

    val afternoonSlots = listOf(
        SlotOption("01:00 PM"),
        SlotOption("01:45 PM"),
        SlotOption("02:30 PM"),
        SlotOption("03:15 PM", enabled = false),
        SlotOption("04:00 PM"),
    )

    val eveningSlots = listOf(
        SlotOption("06:00 PM"),
        SlotOption("06:45 PM"),
        SlotOption("07:30 PM"),
    )

    var selectedDate by remember { mutableStateOf(dates.first()) }
    var selectedTime by remember { mutableStateOf("09:15 AM") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(SlotBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 190.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
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
                            tint = SlotText,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Text(
                        text = "Select Time Slot",
                        color = SlotText,
                        fontSize = 30.sp / 1.5f,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }
            }

            item {
                if (consultationType == ConsultationType.VIDEO_CALL) {
                    VideoDoctorCard(profile = profile)
                } else {
                    ClinicLocationCard(profile = profile)
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(dates) { dateOption ->
                        DateChip(
                            option = dateOption,
                            selected = selectedDate == dateOption,
                            onClick = { selectedDate = dateOption },
                        )
                    }
                }
            }

            item {
                SlotSectionTitle(
                    title = "MORNING SLOTS",
                    icon = Icons.Outlined.LightMode,
                )
                Spacer(modifier = Modifier.height(10.dp))
                SlotsGrid(
                    slots = morningSlots,
                    selectedTime = selectedTime,
                    onSelect = { selectedTime = it },
                )
            }

            item {
                SlotSectionTitle(
                    title = "AFTERNOON SLOTS",
                    icon = Icons.Outlined.WbSunny,
                )
                Spacer(modifier = Modifier.height(10.dp))
                SlotsGrid(
                    slots = afternoonSlots,
                    selectedTime = selectedTime,
                    onSelect = { selectedTime = it },
                )
            }

            item {
                SlotSectionTitle(
                    title = "EVENING SLOTS",
                    icon = Icons.Outlined.Bedtime,
                )
                Spacer(modifier = Modifier.height(10.dp))
                SlotsGrid(
                    slots = eveningSlots,
                    selectedTime = selectedTime,
                    onSelect = { selectedTime = it },
                )
            }
        }

        SlotFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            selectedLabel = "${selectedDate.summaryLabel} • $selectedTime",
            fee = profile.price,
            onConfirm = {
                onConfirmSlot(selectedDate.summaryLabel, selectedDate.detailLabel, selectedTime)
            },
        )
    }
}

@Composable
private fun VideoDoctorCard(profile: DoctorProfileData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(profile.avatarTop, profile.avatarBottom))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.avatarInitials,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = profile.name,
                    color = SlotText,
                    fontSize = 20.sp / 1.3f,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = profile.title,
                    color = SlotPrimary,
                    fontSize = 18.sp / 1.2f,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${profile.yearsExp.replace("+", "")} years exp. • ${profile.clinicSummary}",
                    color = SlotMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFF6B01E),
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = profile.rating,
                        color = SlotText,
                        fontSize = 18.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "(120+ reviews)",
                        color = Color(0xFF8A99B0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ClinicLocationCard(profile: DoctorProfileData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F3FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = SlotPrimary,
                    modifier = Modifier.size(34.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Clinic Location",
                    color = SlotText,
                    fontSize = 20.sp / 1.3f,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = profile.clinicSummary,
                    color = SlotPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = profile.clinicAddress,
                    color = SlotMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "In-clinic booking",
                        color = Color(0xFF16A34A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun DateChip(
    option: DateOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) SlotPrimary else Color.White
    val dayColor = if (selected) Color.White else Color(0xFF8A99B0)
    val dateColor = if (selected) Color.White else SlotText

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, if (selected) SlotPrimary else Color(0xFFE2EAF3), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = option.day,
            color = dayColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = option.date,
            color = dateColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SlotSectionTitle(
    title: String,
    icon: ImageVector,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SlotPrimary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = title,
            color = SlotText,
            fontSize = 34.sp / 1.6f,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp,
        )
    }
}

@Composable
private fun SlotsGrid(
    slots: List<SlotOption>,
    selectedTime: String,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        slots.chunked(3).forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowSlots.forEach { slot ->
                    val isSelected = slot.time == selectedTime
                    val borderColor = when {
                        !slot.enabled -> Color(0xFFEAEFF6)
                        isSelected -> SlotPrimary
                        else -> Color(0xFFDCE5EF)
                    }
                    val bgColor = when {
                        !slot.enabled -> Color(0xFFF2F4F8)
                        isSelected -> Color(0xFFE4F2FC)
                        else -> Color.White
                    }
                    val textColor = when {
                        !slot.enabled -> Color(0xFFBCC6D4)
                        isSelected -> SlotPrimary
                        else -> Color(0xFF24344E)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(bgColor)
                            .border(if (isSelected) 3.dp else 2.dp, borderColor, RoundedCornerShape(28.dp))
                            .clickable(enabled = slot.enabled) { onSelect(slot.time) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = slot.time,
                            color = textColor,
                            fontSize = 18.sp / 1.3f,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textDecoration = if (slot.enabled) TextDecoration.None else TextDecoration.LineThrough,
                        )
                    }
                }
                repeat(3 - rowSlots.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SlotFooter(
    modifier: Modifier = Modifier,
    selectedLabel: String,
    fee: String,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = modifier
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
                    text = "Selected Time",
                    color = SlotMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = selectedLabel,
                    color = SlotText,
                    fontSize = 32.sp / 1.7f,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Consultation Fee",
                    color = SlotMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = fee,
                    color = SlotPrimary,
                    fontSize = 42.sp / 1.7f,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SlotPrimary),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Confirm Slot",
                    color = Color.White,
                    fontSize = 42.sp / 1.7f,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
