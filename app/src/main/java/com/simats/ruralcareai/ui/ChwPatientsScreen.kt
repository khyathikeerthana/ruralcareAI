package com.simats.ruralcareai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AdminApiClient
import com.simats.ruralcareai.network.AdminPatientDto
import com.simats.ruralcareai.network.AdminPatientsFetchResult

private val PatientScreenBg = Color(0xFFF2F4F8)
private val PatientScreenSurface = Color(0xFFFDFEFF)
private val PatientScreenText = Color(0xFF121820)
private val PatientScreenMuted = Color(0xFF6D7886)
private val PatientScreenPrimary = Color(0xFF0D6796)
private val PatientScreenBorder = Color(0xFFD8DEE7)

@Composable
fun ChwPatientsScreen(
    onBack: () -> Unit,
    onPatientSelected: (AdminPatientDto) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val config = LocalConfiguration.current
    val isCompactHeight = config.screenHeightDp <= 830

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var patients by remember { mutableStateOf<List<AdminPatientDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        when (val result = AdminApiClient.listPatients()) {
            is AdminPatientsFetchResult.Success -> {
                patients = result.patients
                isLoading = false
            }

            is AdminPatientsFetchResult.Error -> {
                errorMessage = result.message
                isLoading = false
            }
        }
    }

    val filteredPatients = if (searchQuery.isBlank()) {
        patients
    } else {
        patients.filter { patient ->
            patient.fullName.contains(searchQuery, ignoreCase = true) ||
                    patient.village?.contains(searchQuery, ignoreCase = true) == true ||
                    patient.phone?.contains(searchQuery, ignoreCase = true) == true ||
                    patient.id.toString().contains(searchQuery)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PatientScreenBg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = if (isCompactHeight) 10.dp else 12.dp,
                bottom = 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PatientScreenPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = onBack),
                        )
                        Text(
                            text = "Patients",
                            color = PatientScreenText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Box(modifier = Modifier.width(40.dp))
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    placeholder = { Text("Search name, ID or village...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = PatientScreenMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PatientScreenSurface,
                        unfocusedContainerColor = PatientScreenSurface,
                        focusedBorderColor = PatientScreenPrimary.copy(alpha = 0.3f),
                        unfocusedBorderColor = PatientScreenBorder,
                        cursorColor = PatientScreenPrimary,
                    ),
                    singleLine = true,
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = PatientScreenPrimary,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E8)),
                    ) {
                        Text(
                            text = errorMessage ?: "Unable to load patients.",
                            color = Color(0xFF8A1C1C),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }
                }
            } else if (filteredPatients.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "No patients found." else "No matching patients.",
                            color = PatientScreenMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }
                }
            } else {
                items(filteredPatients.size) { index ->
                    val patient = filteredPatients[index]
                    ChwPatientListItem(
                        patient = patient,
                        onClick = { onPatientSelected(patient) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChwPatientListItem(
    patient: AdminPatientDto,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PatientScreenSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFD6E5EE)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Patient",
                    tint = Color(0xFF3E627C),
                    modifier = Modifier.size(28.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = patient.fullName,
                    color = PatientScreenText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${patient.village ?: "N/A"} • #${patient.id}",
                    color = PatientScreenMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
