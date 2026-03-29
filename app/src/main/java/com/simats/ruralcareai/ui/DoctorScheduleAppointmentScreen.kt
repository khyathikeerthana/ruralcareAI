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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class ScheduleDate(val day: String, val date: String)
private data class ScheduleSlot(val label: String, val enabled: Boolean = true)
data class SchedulePatientOption(val patientId: Int, val name: String)

private fun slotToTwentyFourHour(slot: String): String {
    return when (slot) {
        "09:00 AM" -> "09:00:00"
        "09:30 AM" -> "09:30:00"
        "10:00 AM" -> "10:00:00"
        "10:30 AM" -> "10:30:00"
        "11:00 AM" -> "11:00:00"
        "11:30 AM" -> "11:30:00"
        else -> "09:30:00"
    }
}

@Composable
fun DoctorScheduleAppointmentScreen(
    patientOptions: List<SchedulePatientOption> = listOf(
        SchedulePatientOption(patientId = 1, name = "John Doe"),
        SchedulePatientOption(patientId = 2, name = "Anita Singh"),
    ),
    onBack: () -> Unit = {},
    onConfirmSchedule: (patientId: Int, isoDateTime: String, mode: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    val upcomingDates = remember {
        val formatter = DateTimeFormatter.ofPattern("EEE", Locale.US)
        (0..4).map { offset ->
            val date = LocalDate.now().plusDays(offset.toLong())
            ScheduleDate(
                day = date.format(formatter).uppercase(Locale.US),
                date = date.dayOfMonth.toString(),
            )
        }
    }
    val slots = remember {
        listOf(
            ScheduleSlot("09:00 AM"),
            ScheduleSlot("09:30 AM"),
            ScheduleSlot("10:00 AM"),
            ScheduleSlot("10:30 AM"),
            ScheduleSlot("11:00 AM", enabled = false),
            ScheduleSlot("11:30 AM"),
        )
    }

    var selectedDate by remember { mutableStateOf(0) }
    var selectedSlot by remember { mutableStateOf("09:30 AM") }
    var selectedPatientId by remember { mutableStateOf(patientOptions.firstOrNull()?.patientId ?: 0) }
    val selectedCalendarDate = LocalDate.now().plusDays(selectedDate.toLong())
    val monthTitle = selectedCalendarDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F6)),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE7EEF6))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DoctorDashboardPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DoctorDashboardText,
                        modifier = Modifier.weight(1f),
                    )

                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = monthTitle,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = DoctorDashboardText,
                        )
                        Text(
                            text = "Select your consultation date",
                            color = DoctorDashboardMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )
                    }
                    Text(
                        text = "Monthly\nView",
                        color = DoctorDashboardPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        upcomingDates.forEachIndexed { index, date ->
                            val selected = index == selectedDate
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) DoctorDashboardPrimary else Color.Transparent)
                                    .clickable { selectedDate = index }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = date.day,
                                    color = if (selected) Color.White else DoctorDashboardMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                )
                                Text(
                                    text = date.date,
                                    color = if (selected) Color.White else DoctorDashboardText,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) Color.White else Color.Transparent),
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "CONSULTATION MODE",
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    fontSize = 14.sp,
                    color = DoctorDashboardText,
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE1E8)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ScheduleModeChip(
                        selected = true,
                        icon = Icons.Filled.LocalHospital,
                        label = "Clinic",
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )
                }
            }

            item {
                Text(
                    text = "PATIENT DETAILS",
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    fontSize = 14.sp,
                    color = DoctorDashboardText,
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDEE3EA))
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = "Select patient...",
                        color = DoctorDashboardMuted,
                        fontSize = 15.sp,
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    patientOptions.take(3).forEachIndexed { index, patient ->
                        PatientTag(
                            initials = patient.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                            name = patient.name,
                            tint = if (selectedPatientId == patient.patientId) Color(0xFFB8DDF5) else Color(0xFFEEDDB7),
                            onClick = { selectedPatientId = patient.patientId },
                        )
                        if (index == 2) return@forEachIndexed
                    }
                }
            }

            item {
                Text(
                    text = "AVAILABLE SLOTS",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.4.sp,
                    fontSize = 14.sp,
                    color = DoctorDashboardText,
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(132.dp),
                    userScrollEnabled = false,
                ) {
                    items(slots) { slot ->
                        val selected = slot.label == selectedSlot
                        val borderColor = if (selected) Color(0xFF0F6FA2) else Color(0xFFD2D8E0)
                        val container = when {
                            !slot.enabled -> Color(0xFFE9EDF2)
                            selected -> Color(0xFF3EA8DD)
                            else -> Color(0xFFF3F5F8)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(container)
                                .border(2.dp, borderColor, RoundedCornerShape(999.dp))
                                .clickable(enabled = slot.enabled) { selectedSlot = slot.label }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = slot.label,
                                color = if (slot.enabled) DoctorDashboardText else DoctorDashboardMuted.copy(alpha = 0.45f),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFFF1F3F6))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = {
                    val time = slotToTwentyFourHour(selectedSlot)
                    val isoDateTime = "${selectedCalendarDate}T${time}Z"
                    onConfirmSchedule(selectedPatientId, isoDateTime, "video")
                },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF0F6FA2), Color(0xFF3BA8E0)),
                            ),
                            shape = RoundedCornerShape(999.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Confirm & Schedule",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                    )
                }
            }

            Text(
                text = "Notification will be sent to the patient immediately.",
                color = DoctorDashboardMuted,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ScheduleModeChip(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) DoctorDashboardText else DoctorDashboardMuted,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                color = if (selected) DoctorDashboardText else DoctorDashboardMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun PatientTag(initials: String, name: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFF1F4F8))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(tint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = Color(0xFF355A77),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
        Text(
            text = name,
            color = DoctorDashboardText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
    }
}
