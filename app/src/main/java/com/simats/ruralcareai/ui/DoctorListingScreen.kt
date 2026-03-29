package com.simats.ruralcareai.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.DashboardApiClient
import com.simats.ruralcareai.network.DoctorSummaryDto
import com.simats.ruralcareai.network.SearchResult

private val ListingBackground = Color(0xFFEFF7FE)
private val ListingPrimary = Color(0xFF2D9CDB)
private val ListingText = Color(0xFF0F1730)
private val ListingMuted = Color(0xFF6F7687)
private val ListingOutline = Color(0xFFE3E8F0)

@Composable
fun DoctorListingScreen(
    specialty: String,
    onBack: () -> Unit,
    patientName: String? = null,
    patientPhone: String? = null,
    modifier: Modifier = Modifier,
) {
    val doctors = remember { mutableStateOf<List<DoctorSummaryDto>>(emptyList()) }
    val searchText = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val specialtyFilter = normalizeSpecialtyForQuery(specialty)

    LaunchedEffect(specialtyFilter, searchText.value) {
        isLoading.value = true
        errorMessage.value = null
        when (
            val result = DashboardApiClient.searchDoctors(
                query = searchText.value,
                specialization = specialtyFilter,
            )
        ) {
            is SearchResult.Success -> {
                doctors.value = result.doctors
            }
            is SearchResult.Error -> {
                doctors.value = emptyList()
                errorMessage.value = result.message
            }
        }
        isLoading.value = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ListingBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ListingText,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (specialtyFilter.isBlank()) "Doctors" else specialty,
                            color = ListingText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Real-time doctor directory",
                            color = ListingMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = ListingMuted,
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search doctors",
                            color = ListingMuted,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ListingPrimary,
                        unfocusedBorderColor = ListingOutline,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                )
            }

            if (specialtyFilter.isNotBlank()) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = {
                            Text(
                                text = specialty,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        enabled = false,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ListingPrimary.copy(alpha = 0.14f),
                            selectedLabelColor = ListingPrimary,
                            disabledContainerColor = ListingPrimary.copy(alpha = 0.14f),
                            disabledLabelColor = ListingPrimary,
                        ),
                    )
                }
            }

            when {
                isLoading.value -> {
                    item {
                        EmptyStateCard(message = "Loading doctors...")
                    }
                }
                errorMessage.value != null -> {
                    item {
                        EmptyStateCard(message = "Unable to load doctors. ${errorMessage.value}")
                    }
                }
                doctors.value.isEmpty() -> {
                    item {
                        EmptyStateCard(message = "No doctors available right now.")
                    }
                }
                else -> {
                    items(doctors.value) { doctor ->
                        DoctorCard(doctor = doctor)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Patient: ${patientName ?: "-"}",
                    color = ListingMuted,
                    fontSize = 11.sp,
                )
                Text(
                    text = "Phone: ${patientPhone ?: "-"}",
                    color = ListingMuted,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun DoctorCard(doctor: DoctorSummaryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F2FD)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = doctor.fullName
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                            .take(2)
                            .joinToString("")
                            .ifBlank { "DR" },
                        color = ListingPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.fullName,
                        color = ListingText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = doctor.specialization,
                        color = ListingMuted,
                        fontSize = 13.sp,
                    )
                }

                if (doctor.isVerified) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F7EF))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF14A44D),
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Verified",
                            color = Color(0xFF14A44D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F8FC))
                    .border(1.dp, ListingOutline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Experience: ${doctor.yearsExperience} years",
                    color = ListingText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = doctor.bio?.takeIf { it.isNotBlank() } ?: "No doctor bio available.",
                color = ListingMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message,
                color = ListingMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun normalizeSpecialtyForQuery(value: String): String {
    val trimmed = value.trim()
    if (trimmed.isBlank()) {
        return ""
    }

    return when (trimmed.lowercase()) {
        "general physician" -> "general"
        "gynecology" -> "gyn"
        else -> trimmed
    }
}
