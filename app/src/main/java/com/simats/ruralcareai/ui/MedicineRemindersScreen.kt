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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.PrescriptionDto
import com.simats.ruralcareai.network.PrescriptionsResult
import com.simats.ruralcareai.network.DashboardApiClient
import com.simats.ruralcareai.viewmodel.AppUiState
import java.text.SimpleDateFormat
import java.util.Locale

private val RMBackground = Color(0xFFF3F6FB)
private val RMCardBg = Color.White
private val RMText = Color(0xFF0F1730)
private val RMMuted = Color(0xFF5A6A82)
private val RMPrimary = Color(0xFF1F9BE6)
private val RMOutline = Color(0xFFE0E9F2)
private val RMActiveColor = Color(0xFF4CAF50)
private val RMWarningColor = Color(0xFFFFC107)

@Composable
fun MedicineRemindersScreen(
    uiState: AppUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val patientId = uiState.currentPatientId
    val prescriptions = remember { mutableStateOf<List<PrescriptionDto>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(patientId) {
        if (patientId == null) {
            prescriptions.value = emptyList()
            errorMessage.value = null
            isLoading.value = false
            return@LaunchedEffect
        }

        when (val result = DashboardApiClient.getPrescriptionsByPatient(patientId)) {
            is PrescriptionsResult.Success -> {
                prescriptions.value = result.prescriptions
                isLoading.value = false
            }
            is PrescriptionsResult.Error -> {
                errorMessage.value = result.message
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RMBackground)
            .statusBarsPadding(),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = RMText,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Medication",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RMText,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TabButtonSmall("Today", true)
            TabButtonSmall("Weekly", false)
            TabButtonSmall("History", false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading reminders...", color = RMMuted)
            }
        } else if (errorMessage.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Error: ${errorMessage.value}", color = Color(0xFFEF4A4A))
            }
        } else if (prescriptions.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "No reminders set",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RMText,
                    )
                    Text(
                        "Add a new reminder to get started",
                        fontSize = 13.sp,
                        color = RMMuted,
                    )
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = RMPrimary),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(18.dp),
                            )
                            Text("Add Reminder", fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                item {
                    Text(
                        "UPCOMING TODAY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RMMuted,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(prescriptions.value) { prescription ->
                    MedicineReminderCard(prescription)
                }
            }
        }

        // Add Reminder FAB
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
        ) {
            FloatingActionButton(
                onClick = { },
                containerColor = RMPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun TabButtonSmall(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) RMPrimary else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) RMPrimary else RMOutline,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else RMMuted,
        )
    }
}

@Composable
private fun MedicineReminderCard(prescription: PrescriptionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RMCardBg),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Medicine icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = prescription.medicationName.take(1).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800),
                )
            }

            // Medicine details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = prescription.medicationName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RMText,
                )
                Text(
                    text = prescription.dosage,
                    fontSize = 12.sp,
                    color = RMMuted,
                )
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    "UPCOMING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RMActiveColor,
                )
            }
        }

        // Time and mark taken button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F7FA))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Time",
                    tint = RMMuted,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    "08:30 AM • 08:00 PM",
                    fontSize = 11.sp,
                    color = RMText,
                    fontWeight = FontWeight.Medium,
                )
            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = RMPrimary),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp),
            ) {
                Text("Mark Taken", fontSize = 11.sp)
            }
        }
    }
}
