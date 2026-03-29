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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.DashboardApiClient
import com.simats.ruralcareai.network.DoctorSummaryDto
import com.simats.ruralcareai.network.SearchResult

private val ConsultsBackground = Color(0xFFF2F5FA)
private val ConsultsPrimary = Color(0xFF1F9BE6)
private val ConsultsText = Color(0xFF0F1730)
private val ConsultsMuted = Color(0xFF61738C)
private val ConsultsOutline = Color(0xFFE1E8F2)

private data class SpecialtyItem(
    val title: String,
    val count: Int,
)

@Composable
fun ConsultsSpecialtiesScreen(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenAshaAI: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onSpecialtyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val doctors = remember { mutableStateOf<List<DoctorSummaryDto>>(emptyList()) }
    val searchQuery = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery.value) {
        isLoading.value = true
        errorMessage.value = null
        when (val result = DashboardApiClient.searchDoctors(query = searchQuery.value)) {
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

    val specialtyItems = doctors.value
        .groupBy { it.specialization.trim() }
        .map { (specialization, doctorItems) ->
            SpecialtyItem(
                title = specialization,
                count = doctorItems.size,
            )
        }
        .sortedBy { it.title.lowercase() }
        .chunked(2)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(ConsultsBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 208.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                TopBar(
                    title = "Specialties",
                    onBack = onBack,
                )
            }

            item {
                SearchBar(
                    query = searchQuery.value,
                    onQueryChange = { searchQuery.value = it },
                )
            }

            item {
                SectionHeader()
            }

            when {
                isLoading.value -> {
                    item {
                        EmptyStateCard("Loading specialties...")
                    }
                }
                errorMessage.value != null -> {
                    item {
                        EmptyStateCard("Unable to load specialties. ${errorMessage.value}")
                    }
                }
                specialtyItems.isEmpty() -> {
                    item {
                        EmptyStateCard("No doctors available right now.")
                    }
                }
                else -> {
                    items(specialtyItems) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            SpecialtyCard(
                                title = rowItems[0].title,
                                subtitle = "${rowItems[0].count} doctors available",
                                icon = specialtyIconFor(rowItems[0].title),
                                modifier = Modifier.weight(1f),
                                onClick = { onSpecialtyClick(rowItems[0].title) },
                            )

                            if (rowItems.size > 1) {
                                SpecialtyCard(
                                    title = rowItems[1].title,
                                    subtitle = "${rowItems[1].count} doctors available",
                                    icon = specialtyIconFor(rowItems[1].title),
                                    modifier = Modifier.weight(1f),
                                    onClick = { onSpecialtyClick(rowItems[1].title) },
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        ConsultsBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            onHome = onHome,
            onAshaAi = onOpenAshaAI,
            onChat = onOpenChat,
            onProfile = onOpenProfile,
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ConsultsPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        Text(
            text = title,
            color = ConsultsText,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = ConsultsPrimary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search by doctor or specialty",
                color = Color(0xFF7F92AA),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ConsultsPrimary,
            unfocusedBorderColor = ConsultsOutline,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
    )
}

@Composable
private fun SectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Medical Specialties",
            color = ConsultsText,
            fontSize = 24.sp / 1.3f,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Real-time",
            color = ConsultsPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SpecialtyCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF2FD)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ConsultsPrimary,
                    modifier = Modifier.size(30.dp),
                )
            }

            Text(
                text = title,
                color = ConsultsText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Text(
                text = subtitle,
                color = ConsultsMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                color = ConsultsMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ConsultsBottomBar(
    modifier: Modifier = Modifier,
    onHome: () -> Unit,
    onAshaAi: () -> Unit,
    onChat: () -> Unit,
    onProfile: () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavItem(icon = Icons.Filled.Home, label = "Home", selected = false, onClick = onHome)
            BottomNavItem(icon = Icons.Filled.MedicalServices, label = "Consults", selected = true, onClick = {})

            Spacer(modifier = Modifier.width(56.dp))

            BottomNavItem(icon = Icons.Outlined.Forum, label = "Chat", selected = false, onClick = onChat)
            BottomNavItem(icon = Icons.Outlined.Person, label = "Profile", selected = false, onClick = onProfile)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(68.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
                .clickable(onClick = onAshaAi),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(ConsultsPrimary, Color(0xFF1578BE)))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) ConsultsPrimary else Color(0xFF94A3B8)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun specialtyIconFor(label: String): ImageVector {
    val normalized = label.lowercase()
    return when {
        "pedia" in normalized || "child" in normalized -> Icons.Filled.ChildCare
        "gyn" in normalized || "women" in normalized || "obst" in normalized -> Icons.Filled.Female
        "ortho" in normalized || "bone" in normalized || "joint" in normalized -> Icons.Filled.AccessibilityNew
        "eye" in normalized || "oph" in normalized -> Icons.Filled.Visibility
        "dent" in normalized -> Icons.Filled.Face
        "derma" in normalized || "skin" in normalized -> Icons.Filled.Spa
        "cardio" in normalized || "heart" in normalized -> Icons.Filled.Favorite
        else -> Icons.Filled.MedicalServices
    }
}
