package com.simats.ruralcareai.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DoctorPatientItem(
    val id: Int,
    val name: String,
    val location: String,
    val status: String,
    val photo: String? = null,
)

@Composable
fun DoctorPatientsScreen(
    doctorName: String = "Dr. Jane Smith",
    doctorPhoto: String? = null,
    patients: List<DoctorPatientItem> = listOf(
        DoctorPatientItem(id = 1, name = "Rajesh Kumar", location = "Kakrala Village", status = "URGENT"),
        DoctorPatientItem(id = 2, name = "Priya Sharma", location = "Dundahera Colony", status = "STABLE"),
        DoctorPatientItem(id = 3, name = "Anita Devi", location = "Mandi Hills", status = "NEW"),
        DoctorPatientItem(id = 4, name = "Vikram Singh", location = "Kakrala Village", status = "STABLE"),
        DoctorPatientItem(id = 5, name = "Meera Bai", location = "Mandi Hills", status = "COMPLETED"),
    ),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenQueue: () -> Unit = {},
    onOpenPatientProfile: (DoctorPatientItem) -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var selectedFilter by remember { mutableStateOf("All Patients") }

    val filteredPatients = remember(selectedFilter, patients, searchQuery) {
        val statusFiltered = when (selectedFilter) {
            "Active" -> patients.filter { it.status == "STABLE" || it.status == "NEW" }
            "Urgent" -> patients.filter { it.status == "URGENT" }
            else -> patients
        }

        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) {
            statusFiltered
        } else {
            statusFiltered.filter {
                it.name.lowercase().contains(query) ||
                    it.location.lowercase().contains(query) ||
                    it.id.toString().contains(query)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorDashboardBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = "Patients",
                    tint = DoctorDashboardPrimary,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "Patients",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DoctorDashboardPrimary,
                    modifier = Modifier.weight(1f),
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 118.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3E7EC)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Text(
                                    text = "Search by name, ID or village...",
                                    color = DoctorDashboardMuted.copy(alpha = 0.6f),
                                    fontSize = 14.sp,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = DoctorDashboardMuted.copy(alpha = 0.65f),
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFFD4DBE3),
                                unfocusedBorderColor = Color(0xFFD4DBE3),
                            ),
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        listOf("All Patients", "Active", "Urgent").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Box(
                                modifier = Modifier
                                    .weight(if (filter == "All Patients") 1.3f else 1f)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) DoctorDashboardPrimaryContainer else Color(0xFFEAEDEF))
                                    .clickable { selectedFilter = filter }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF06395A) else DoctorDashboardText,
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "TODAY'S APPOINTMENTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DoctorDashboardMuted,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "${filteredPatients.size} Patients",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DoctorDashboardPrimary,
                        )
                    }
                }

                items(filteredPatients) { patient ->
                    DoctorPatientCard(
                        patient = patient,
                        onClick = { onOpenPatientProfile(patient) },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Home,
                        label = "Home",
                        isSelected = false,
                        onClick = onOpenHome,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Dashboard,
                        label = "Appointments",
                        isSelected = false,
                        onClick = onOpenQueue,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Group,
                        label = "Patients",
                        isSelected = true,
                        onClick = { },
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.BarChart,
                        label = "Analytics",
                        isSelected = false,
                        onClick = onOpenAnalytics,
                    )
                    DoctorBottomNavItem(
                        icon = Icons.Filled.Person,
                        label = "Profile",
                        isSelected = false,
                        onClick = onOpenProfile,
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorPatientCard(
    patient: DoctorPatientItem,
    onClick: () -> Unit,
) {
    val statusStyle = when (patient.status) {
        "URGENT" -> Pair(Color(0xFFF6D6D2), Color(0xFFC62828))
        "STABLE" -> Pair(Color(0xFFCDE3F7), Color(0xFF355A77))
        "NEW" -> Pair(Color(0xFFCDE3F7), Color(0xFF006EA4))
        else -> Pair(Color(0xFFE2E6EA), Color(0xFF6F7881))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorDashboardCard),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCFE8F9)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Patient",
                    tint = DoctorDashboardPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DoctorDashboardText,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = DoctorDashboardMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = patient.location,
                        fontSize = 12.sp,
                        color = DoctorDashboardMuted,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = patient.status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusStyle.second,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(statusStyle.first)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open",
                    tint = DoctorDashboardMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
