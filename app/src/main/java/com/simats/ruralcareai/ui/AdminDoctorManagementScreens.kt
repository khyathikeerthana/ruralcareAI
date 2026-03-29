package com.simats.ruralcareai.ui

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.simats.ruralcareai.network.AdminAnalyticsDto
import com.simats.ruralcareai.network.AdminClinicPerformanceDto
import com.simats.ruralcareai.network.AdminConsultationTrendDto
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.graphics.BitmapFactory

private val DoctorBg = Color(0xFFF2F4F8)
private val DoctorSurface = Color(0xFFFFFFFF)
private val DoctorText = Color(0xFF131A22)
private val DoctorMuted = Color(0xFF6F7A89)
private val DoctorPrimary = Color(0xFF0B6FA2)
private val DoctorOutline = Color(0xFFD6DEE9)
private val DoctorChipBg = Color(0xFFE9ECF1)

private val DoctorSpecializationOptions = listOf(
    "General Medicine",
    "Family Medicine",
    "Internal Medicine",
    "Pediatrics",
    "Obstetrics and Gynecology",
    "Orthopedics (Ortho)",
    "Cardiology",
    "Dermatology",
    "Neurology",
    "Psychiatry",
    "Pulmonology",
    "ENT",
    "Ophthalmology",
    "Urology",
    "Nephrology",
    "Gastroenterology",
    "Endocrinology",
    "Oncology",
    "Emergency Medicine",
    "Anesthesiology",
    "Radiology",
    "Pathology",
)

private val WorkerRoleOptions = listOf(
    "Community Health Worker",
    "Field Operations Worker",
    "ASHA Worker",
    "ANM",
    "Village Health Nurse",
    "Health Educator",
    "Maternal Care Worker",
    "Immunization Worker",
    "Outreach Coordinator",
)

enum class AdminDoctorStatus {
    ACTIVE,
    PENDING,
    DISABLED,
}

enum class AdminDoctorFilter {
    ALL,
    ACTIVE,
    PENDING,
}

enum class AdminWorkerStatus {
    ACTIVE,
    ON_FIELD,
    OFFLINE,
}

data class AdminDoctorUi(
    val backendId: Int,
    val id: String,
    val fullName: String,
    val specialty: String,
    val joinDate: String,
    val phone: String,
    val email: String,
    val experienceYears: Int,
    val qualification: String,
    val languages: List<String>,
    val hospital: String,
    val location: String,
    val photoPath: String? = null,
    val status: AdminDoctorStatus,
    val password: String,
)

data class AdminWorkerUi(
    val backendId: Int,
    val id: String,
    val fullName: String,
    val village: String,
    val joinDate: String,
    val phone: String,
    val email: String,
    val role: String,
    val assignedVillage: String,
    val photoPath: String? = null,
    val status: AdminWorkerStatus,
    val password: String,
)

data class AdminDoctorFormInput(
    val fullName: String,
    val phone: String,
    val email: String,
    val password: String,
    val specialization: String,
    val experienceYears: Int,
    val qualification: String,
    val hospital: String,
    val location: String,
    val photoPath: String? = null,
    val languages: List<String>,
)

data class AdminWorkerFormInput(
    val fullName: String,
    val phone: String,
    val email: String,
    val password: String,
    val workerCode: String,
    val assignedVillage: String,
    val roleTitle: String,
    val photoPath: String? = null,
)

fun sampleAdminDoctors(): List<AdminDoctorUi> {
    return listOf(
        AdminDoctorUi(
            backendId = 9921,
            id = "#DR-9921",
            fullName = "Dr. Sarah Jenkins",
            specialty = "Senior Pediatrician",
            joinDate = "Jan 2012",
            phone = "+1 (555) 012-3456",
            email = "sarah.j@ruralcareai.com",
            experienceYears = 12,
            qualification = "MD, PhD",
            languages = listOf("English", "Hindi", "Punjabi"),
            hospital = "Riverside Community Hospital",
            location = "Sector 4, Nabha",
            status = AdminDoctorStatus.ACTIVE,
            password = "Doctor@123",
        ),
        AdminDoctorUi(
            backendId = 1034,
            id = "#DR-1034",
            fullName = "Dr. Michael Chen",
            specialty = "Cardiology Resident",
            joinDate = "Apr 2020",
            phone = "+1 (555) 891-2345",
            email = "michael.c@ruralcareai.com",
            experienceYears = 6,
            qualification = "MD",
            languages = listOf("English", "Hindi"),
            hospital = "Bihar Cardiac Center",
            location = "Sector 2, Nabha",
            status = AdminDoctorStatus.PENDING,
            password = "Doctor@123",
        ),
        AdminDoctorUi(
            backendId = 1170,
            id = "#DR-1170",
            fullName = "Dr. Elena Rodriguez",
            specialty = "Emergency Medicine",
            joinDate = "Aug 2016",
            phone = "+1 (555) 662-0098",
            email = "elena.r@ruralcareai.com",
            experienceYears = 9,
            qualification = "MD",
            languages = listOf("English", "Spanish", "Hindi"),
            hospital = "North Emergency Unit",
            location = "Sector 7, Nabha",
            status = AdminDoctorStatus.ACTIVE,
            password = "Doctor@123",
        ),
        AdminDoctorUi(
            backendId = 1207,
            id = "#DR-1207",
            fullName = "Dr. Marcus Thorne",
            specialty = "General Practitioner",
            joinDate = "Nov 2011",
            phone = "+1 (555) 120-7823",
            email = "marcus.t@ruralcareai.com",
            experienceYears = 14,
            qualification = "MBBS",
            languages = listOf("English", "Punjabi"),
            hospital = "Community Wellness Clinic",
            location = "Sector 1, Nabha",
            status = AdminDoctorStatus.DISABLED,
            password = "Doctor@123",
        ),
        AdminDoctorUi(
            backendId = 1339,
            id = "#DR-1339",
            fullName = "Dr. Alisha Patel",
            specialty = "Obstetrician",
            joinDate = "Mar 2018",
            phone = "+1 (555) 335-4477",
            email = "alisha.p@ruralcareai.com",
            experienceYears = 8,
            qualification = "MD, DGO",
            languages = listOf("English", "Hindi", "Gujarati"),
            hospital = "Riverside Community Hospital",
            location = "Sector 4, Nabha",
            status = AdminDoctorStatus.ACTIVE,
            password = "Doctor@123",
        ),
    )
}

fun sampleAdminWorkers(): List<AdminWorkerUi> {
    return listOf(
        AdminWorkerUi(
            backendId = 2001,
            id = "#WK-2001",
            fullName = "Sarah Mbeki",
            village = "Kibera North",
            joinDate = "Feb 2021",
            phone = "+254 701 120 001",
            email = "sarah.m@ruralcare.ai",
            role = "Community Health Worker",
            assignedVillage = "Kibera North",
            status = AdminWorkerStatus.ACTIVE,
            password = "Worker@123",
        ),
        AdminWorkerUi(
            backendId = 2002,
            id = "#WK-2002",
            fullName = "David Chen",
            village = "East Valley",
            joinDate = "Aug 2022",
            phone = "+254 701 120 002",
            email = "david.c@ruralcare.ai",
            role = "Field Operations Worker",
            assignedVillage = "East Valley",
            status = AdminWorkerStatus.ON_FIELD,
            password = "Worker@123",
        ),
        AdminWorkerUi(
            backendId = 2003,
            id = "#WK-2003",
            fullName = "Amara Lawson",
            village = "River Delta",
            joinDate = "Nov 2020",
            phone = "+254 701 120 003",
            email = "amara.l@ruralcare.ai",
            role = "Community Health Worker",
            assignedVillage = "River Delta",
            status = AdminWorkerStatus.ACTIVE,
            password = "Worker@123",
        ),
        AdminWorkerUi(
            backendId = 2004,
            id = "#WK-2004",
            fullName = "Elena Rodriguez",
            village = "Mountain Ridge",
            joinDate = "Jan 2023",
            phone = "+254 701 120 004",
            email = "elena.r@ruralcare.ai",
            role = "Field Operations Worker",
            assignedVillage = "Mountain Ridge",
            status = AdminWorkerStatus.ON_FIELD,
            password = "Worker@123",
        ),
        AdminWorkerUi(
            backendId = 2005,
            id = "#WK-2005",
            fullName = "John Kwesi",
            village = "South Bend",
            joinDate = "Sep 2019",
            phone = "+254 701 120 005",
            email = "john.k@ruralcare.ai",
            role = "Community Health Worker",
            assignedVillage = "South Bend",
            status = AdminWorkerStatus.OFFLINE,
            password = "Worker@123",
        ),
    )
}

@Composable
fun AdminDoctorsScreen(
    doctors: List<AdminDoctorUi>,
    onOpenDoctor: (AdminDoctorUi) -> Unit,
    onAddDoctor: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenWorkers: () -> Unit,
    onOpenAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    var selectedFilter by rememberSaveable { mutableStateOf(AdminDoctorFilter.ALL.name) }
    val currentFilter = AdminDoctorFilter.valueOf(selectedFilter)

    val filteredDoctors = remember(doctors, currentFilter) {
        doctors.filter {
            when (currentFilter) {
                AdminDoctorFilter.ALL -> true
                AdminDoctorFilter.ACTIVE -> it.status == AdminDoctorStatus.ACTIVE
                AdminDoctorFilter.PENDING -> it.status == AdminDoctorStatus.PENDING
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = if (isCompact) 16.dp else 18.dp,
                bottom = if (isCompact) 150.dp else 160.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Doctors",
                        color = DoctorText,
                        fontSize = if (isCompact) 28.sp / 1.45f else 30.sp / 1.45f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Manage healthcare providers across rural clinics.",
                        color = DoctorMuted,
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                SearchBarPlaceholder(placeholder = "Search doctors...")
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminFilterChip(
                        label = "All",
                        selected = currentFilter == AdminDoctorFilter.ALL,
                        onClick = { selectedFilter = AdminDoctorFilter.ALL.name },
                    )
                    AdminFilterChip(
                        label = "Active",
                        selected = currentFilter == AdminDoctorFilter.ACTIVE,
                        onClick = { selectedFilter = AdminDoctorFilter.ACTIVE.name },
                    )
                    AdminFilterChip(
                        label = "Pending",
                        selected = currentFilter == AdminDoctorFilter.PENDING,
                        onClick = { selectedFilter = AdminDoctorFilter.PENDING.name },
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DoctorSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (filteredDoctors.isEmpty()) {
                            Text(
                                text = "No doctors yet.",
                                color = DoctorMuted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
                            )
                        } else {
                            filteredDoctors.forEachIndexed { index, doctor ->
                                DoctorListRow(
                                    doctor = doctor,
                                    onClick = { onOpenDoctor(doctor) },
                                    compact = isCompact,
                                )

                                if (index < filteredDoctors.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(DoctorOutline.copy(alpha = 0.9f)),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddDoctor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = if (isCompact) 110.dp else 118.dp)
                .height(if (isCompact) 52.dp else 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary),
            contentPadding = PaddingValues(horizontal = 22.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Doctor",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = "  Add Doctor",
                color = Color.White,
                fontSize = if (isCompact) 16.sp / 1.2f else 17.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        AdminBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = if (isCompact) 10.dp else 12.dp),
            selectedTab = AdminBottomTab.DOCTORS,
            onDashboardClick = onOpenDashboard,
            onDoctorsClick = {},
            onWorkersClick = onOpenWorkers,
            onAnalyticsClick = onOpenAnalytics,
        )
    }
}

@Composable
private fun DoctorListRow(
    doctor: AdminDoctorUi,
    onClick: () -> Unit,
    compact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (compact) 12.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarCircle(
            text = initialsForName(doctor.fullName),
            containerColor = Color(0xFFCCE6F8),
            textColor = DoctorPrimary,
            size = if (compact) 54.dp else 60.dp,
            textSize = if (compact) 16.sp else 17.sp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = doctor.fullName,
                color = DoctorText,
                fontSize = if (compact) 17.sp / 1.25f else 18.sp / 1.25f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = doctor.specialty,
                color = DoctorText.copy(alpha = 0.88f),
                fontSize = if (compact) 14.sp else 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        DoctorStatusBadge(status = doctor.status)

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open doctor",
            tint = DoctorMuted,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun DoctorStatusBadge(status: AdminDoctorStatus) {
    val (container, textColor, label) = when (status) {
        AdminDoctorStatus.ACTIVE -> Triple(Color(0xFFD1F2DF), Color(0xFF0B7B4E), "ACTIVE")
        AdminDoctorStatus.PENDING -> Triple(Color(0xFFFBE9D2), Color(0xFFBF6A00), "PENDING")
        AdminDoctorStatus.DISABLED -> Triple(Color(0xFFE6EAF0), Color(0xFF667A93), "DISABLED")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun AdminDoctorProfileScreen(
    doctor: AdminDoctorUi,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                ProfileHeader(
                    title = "Doctor Profile",
                    onBack = onBack,
                    onEdit = onEdit,
                )
            }

            item {
                ProfileIdentityBlock(
                    name = doctor.fullName,
                    subtitle = doctor.specialty,
                    status = when (doctor.status) {
                        AdminDoctorStatus.ACTIVE -> "ACTIVE"
                        AdminDoctorStatus.PENDING -> "PENDING"
                        AdminDoctorStatus.DISABLED -> "DISABLED"
                    },
                    statusContainer = when (doctor.status) {
                        AdminDoctorStatus.ACTIVE -> Color(0xFFD1F2DF)
                        AdminDoctorStatus.PENDING -> Color(0xFFFBE9D2)
                        AdminDoctorStatus.DISABLED -> Color(0xFFE6EAF0)
                    },
                    statusTextColor = when (doctor.status) {
                        AdminDoctorStatus.ACTIVE -> Color(0xFF0B7B4E)
                        AdminDoctorStatus.PENDING -> Color(0xFFBF6A00)
                        AdminDoctorStatus.DISABLED -> Color(0xFF667A93)
                    },
                    avatarText = initialsForName(doctor.fullName),
                    infoChips = listOf("ID: ${doctor.id}", "Joined: ${doctor.joinDate}"),
                )
            }

            item {
                AdminInfoCard(title = "CONTACT INFORMATION", icon = Icons.Filled.PersonAdd) {
                    ContactRow(icon = Icons.Filled.Call, label = "PHONE", value = doctor.phone)
                    ContactRow(icon = Icons.Filled.Email, label = "EMAIL", value = doctor.email)
                }
            }

            item {
                AdminInfoCard(title = "PROFESSIONAL CREDENTIALS", icon = Icons.Filled.Verified) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MetricCard(
                            label = "EXPERIENCE",
                            value = "${doctor.experienceYears}",
                            suffix = "Years",
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "QUALIFICATION",
                            value = doctor.qualification,
                            suffix = doctor.specialty,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp),
                    ) {
                        Text(
                            text = "LANGUAGES",
                            color = DoctorMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            doctor.languages.forEach { lang ->
                                ProfileChip(text = lang)
                            }
                        }
                    }
                }
            }

            item {
                AdminInfoCard(title = "WORK DETAILS", icon = Icons.Filled.MedicalServices) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "HOSPITAL / CLINIC",
                            color = DoctorMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = doctor.hospital,
                            color = DoctorText,
                            fontSize = 28.sp / 1.65f,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text(
                            text = "ASSIGNED LOCATION",
                            color = DoctorMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = DoctorPrimary,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = doctor.location,
                                color = DoctorText,
                                fontSize = 18.sp / 1.2f,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            item {
                ProfileActionButton(
                    text = "Manage Schedule",
                    containerColor = Color(0xFF3AA0D8),
                    textColor = Color(0xFF032F4B),
                    onClick = {},
                )
            }

            item {
                ProfileActionButton(
                    text = "Deactivate Profile",
                    containerColor = Color(0xFFF4D7D3),
                    textColor = Color(0xFFA31818),
                    onClick = onDeactivate,
                )
            }

            item {
                ProfileActionButton(
                    text = "Delete Profile",
                    containerColor = Color(0xFFEFC6C2),
                    textColor = Color(0xFF8E1212),
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
fun AdminAddDoctorScreen(
    onBack: () -> Unit,
    onCreateDoctor: (AdminDoctorFormInput) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    AdminDoctorFormScreen(
        modifier = modifier,
        formTitle = "Add Doctor",
        heroTitle = "New Provider",
        heroSubtitle = "Enter clinical credentials to expand the network.",
        submitLabel = "Create Doctor",
        initialDoctor = null,
        formErrorMessage = errorMessage,
        onBack = onBack,
        onSubmit = onCreateDoctor,
    )
}

@Composable
fun AdminEditDoctorScreen(
    doctor: AdminDoctorUi,
    onBack: () -> Unit,
    onSaveDoctor: (AdminDoctorFormInput) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    AdminDoctorFormScreen(
        modifier = modifier,
        formTitle = "Edit Doctor",
        heroTitle = "Update Provider",
        heroSubtitle = "Edit doctor details and reset credentials if needed.",
        submitLabel = "Save Changes",
        initialDoctor = doctor,
        formErrorMessage = errorMessage,
        onBack = onBack,
        onSubmit = onSaveDoctor,
    )
}

@Composable
private fun AdminDoctorFormScreen(
    formTitle: String,
    heroTitle: String,
    heroSubtitle: String,
    submitLabel: String,
    initialDoctor: AdminDoctorUi?,
    formErrorMessage: String? = null,
    onBack: () -> Unit,
    onSubmit: (AdminDoctorFormInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val context = LocalContext.current

    var fullName by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.fullName ?: "")
    }
    var phone by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.phone ?: "")
    }
    var email by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.email ?: "")
    }
    var password by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.password ?: "")
    }
    var specialization by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.specialty ?: "General Medicine")
    }
    var experience by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.experienceYears?.toString() ?: "")
    }
    var qualification by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.qualification ?: "")
    }
    var hospital by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.hospital ?: "")
    }
    var location by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.location ?: "")
    }
    var photoPath by rememberSaveable(initialDoctor?.id) {
        mutableStateOf(initialDoctor?.photoPath ?: "")
    }
    var localErrorMessage by rememberSaveable(initialDoctor?.id) {
        mutableStateOf<String?>(null)
    }

    val doctorImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val encoded = encodeImageAsBase64(context, uri)
            if (!encoded.isNullOrBlank()) {
                photoPath = encoded
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                SimpleFormHeader(title = formTitle, onBack = onBack)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = heroTitle,
                        color = DoctorText,
                        fontSize = if (isCompact) 40.sp / 1.75f else 42.sp / 1.75f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = heroSubtitle,
                        color = DoctorMuted,
                        fontSize = if (isCompact) 15.sp else 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            val visibleError = localErrorMessage ?: formErrorMessage
            if (!visibleError.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE7E7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0B8B8)),
                    ) {
                        Text(
                            text = visibleError,
                            color = Color(0xFF8D1F1F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E6EC))
                            .border(3.dp, Color(0xFFF6F7F9), CircleShape)
                            .clickable { doctorImageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (photoPath.isNotBlank()) {
                            val bitmap = remember(photoPath) {
                                decodeBase64ToBitmap(photoPath)
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Doctor profile image",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Profile image",
                                    tint = Color(0xFF7A8594),
                                    modifier = Modifier.size(38.dp),
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile image",
                                tint = Color(0xFF7A8594),
                                modifier = Modifier.size(38.dp),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DoctorPrimary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Upload profile photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Text(
                        text = if (photoPath.isBlank()) "Upload profile picture" else "Profile image selected",
                        color = DoctorText,
                        fontSize = 16.sp / 1.2f,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { doctorImageLauncher.launch("image/*") },
                    )
                }
            }

            item {
                AdminFormSection(title = "BASIC INFO", icon = Icons.Filled.PersonAdd) {
                    AdminInputField(
                        label = "Full Name",
                        value = fullName,
                        placeholder = "Enter doctor full name",
                        onValueChange = { fullName = it },
                    )
                    AdminInputField(
                        label = "Phone Number",
                        value = phone,
                        placeholder = "+1 (555) 000-0000",
                        onValueChange = { phone = it },
                    )
                    AdminInputField(
                        label = "Email",
                        value = email,
                        placeholder = "name@hospital.org",
                        onValueChange = { email = it },
                    )
                    AdminInputField(
                        label = "Password",
                        value = password,
                        placeholder = "Assign temporary password",
                        onValueChange = { password = it },
                        leadingIcon = Icons.Filled.Lock,
                    )
                }
            }

            item {
                AdminFormSection(title = "PROFESSIONAL DETAILS", icon = Icons.Filled.Verified) {
                    AdminDropdownField(
                        label = "Specialization",
                        value = specialization,
                        placeholder = "General Medicine",
                        options = DoctorSpecializationOptions,
                        onOptionSelected = { specialization = it },
                    )
                    AdminInputField(
                        label = "Experience (years)",
                        value = experience,
                        placeholder = "12",
                        onValueChange = { experience = it },
                    )
                    AdminInputField(
                        label = "Qualification",
                        value = qualification,
                        placeholder = "MD, PhD",
                        onValueChange = { qualification = it },
                    )
                }
            }

            item {
                AdminFormSection(title = "WORK DETAILS", icon = Icons.Filled.MedicalServices) {
                    AdminInputField(
                        label = "Hospital / Clinic",
                        value = hospital,
                        placeholder = "Riverside Community Hospital",
                        onValueChange = { hospital = it },
                    )
                    AdminInputField(
                        label = "Location",
                        value = location,
                        placeholder = "Search hospital location...",
                        onValueChange = { location = it },
                        leadingIcon = Icons.Filled.LocationOn,
                    )
                }
            }

            item {
                PrimarySubmitButton(
                    label = submitLabel,
                    onClick = {
                        val trimmedName = fullName.trim()
                        val trimmedPhone = phone.trim()
                        val trimmedEmail = email.trim()
                        val trimmedSpecialization = specialization.trim()
                        val trimmedPassword = password.trim()

                        val validationError = when {
                            trimmedName.length < 2 -> "Doctor name must be at least 2 characters."
                            trimmedPhone.length < 7 -> "Enter a valid phone number."
                            trimmedEmail.length < 5 || !trimmedEmail.contains("@") -> "Enter a valid email address."
                            trimmedSpecialization.length < 2 -> "Select a specialization."
                            initialDoctor == null && trimmedPassword.length < 8 -> "Password must be at least 8 characters for doctor login."
                            else -> null
                        }

                        if (validationError != null) {
                            localErrorMessage = validationError
                            return@PrimarySubmitButton
                        }

                        localErrorMessage = null
                        onSubmit(
                            AdminDoctorFormInput(
                                fullName = trimmedName,
                                phone = trimmedPhone,
                                email = trimmedEmail,
                                password = password,
                                specialization = trimmedSpecialization,
                                experienceYears = experience.toIntOrNull() ?: 0,
                                qualification = qualification.trim(),
                                hospital = hospital.trim(),
                                location = location.trim(),
                                photoPath = photoPath.trim().ifBlank { null },
                                languages = initialDoctor?.languages ?: listOf("English"),
                            )
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun AdminWorkersScreen(
    workers: List<AdminWorkerUi>,
    activeNowCount: Int? = null,
    onFieldCount: Int? = null,
    onOpenWorker: (AdminWorkerUi) -> Unit,
    onAddWorker: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenDoctors: () -> Unit,
    onOpenAnalytics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    val resolvedActiveNowCount = activeNowCount ?: workers.count { it.status != AdminWorkerStatus.OFFLINE }
    val resolvedOnFieldCount = onFieldCount ?: workers.count { it.status == AdminWorkerStatus.ON_FIELD }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Text(
                    text = "Health Workers",
                    color = DoctorText,
                    fontSize = if (isCompact) 22.sp / 1.2f else 24.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                SearchBarPlaceholder(placeholder = "Search health workers...")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    WorkerCountCard(
                        label = "ACTIVE NOW",
                        value = resolvedActiveNowCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                    WorkerCountCard(
                        label = "ON FIELD",
                        value = resolvedOnFieldCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DoctorSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (workers.isEmpty()) {
                            Text(
                                text = "No health workers yet.",
                                color = DoctorMuted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
                            )
                        } else {
                            workers.forEachIndexed { index, worker ->
                                WorkerListRow(
                                    worker = worker,
                                    compact = isCompact,
                                    onClick = { onOpenWorker(worker) },
                                )

                                if (index < workers.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(DoctorOutline.copy(alpha = 0.9f)),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddWorker,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = if (isCompact) 110.dp else 118.dp)
                .height(if (isCompact) 52.dp else 56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary),
            contentPadding = PaddingValues(horizontal = 22.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Health Worker",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = "  Add Health Worker",
                color = Color.White,
                fontSize = if (isCompact) 16.sp / 1.2f else 17.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        AdminBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
            selectedTab = AdminBottomTab.WORKERS,
            onDashboardClick = onOpenDashboard,
            onDoctorsClick = onOpenDoctors,
            onWorkersClick = {},
            onAnalyticsClick = onOpenAnalytics,
        )
    }
}

@Composable
private fun WorkerCountCard(
    label: String,
    value: String,
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = label,
                color = DoctorMuted,
                fontSize = 18.sp / 1.2f,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value,
                color = DoctorPrimary,
                fontSize = 34.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WorkerListRow(
    worker: AdminWorkerUi,
    compact: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (compact) 12.dp else 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarCircle(
            text = initialsForName(worker.fullName),
            containerColor = Color(0xFFCCE6F8),
            textColor = DoctorPrimary,
            size = if (compact) 54.dp else 60.dp,
            textSize = if (compact) 16.sp else 17.sp,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = worker.fullName,
                color = DoctorText,
                fontSize = if (compact) 17.sp / 1.25f else 18.sp / 1.25f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Village: ${worker.village}",
                color = DoctorMuted,
                fontSize = if (compact) 14.sp else 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        WorkerStatusBadge(status = worker.status)

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open worker",
            tint = DoctorMuted,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun WorkerStatusBadge(status: AdminWorkerStatus) {
    val (container, textColor, label) = when (status) {
        AdminWorkerStatus.ACTIVE -> Triple(Color(0xFFD1F2DF), Color(0xFF0B7B4E), "Active")
        AdminWorkerStatus.ON_FIELD -> Triple(Color(0xFFDCEBFB), Color(0xFF0B6FA2), "On Field")
        AdminWorkerStatus.OFFLINE -> Triple(Color(0xFFE6EAF0), Color(0xFF667A93), "Offline")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun AdminWorkerProfileScreen(
    worker: AdminWorkerUi,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                ProfileHeader(
                    title = "Worker Profile",
                    onBack = onBack,
                    onEdit = onEdit,
                )
            }

            item {
                ProfileIdentityBlock(
                    name = worker.fullName,
                    subtitle = "Village: ${worker.village}",
                    status = when (worker.status) {
                        AdminWorkerStatus.ACTIVE -> "ACTIVE"
                        AdminWorkerStatus.ON_FIELD -> "ON FIELD"
                        AdminWorkerStatus.OFFLINE -> "OFFLINE"
                    },
                    statusContainer = when (worker.status) {
                        AdminWorkerStatus.ACTIVE -> Color(0xFFD1F2DF)
                        AdminWorkerStatus.ON_FIELD -> Color(0xFFDCEBFB)
                        AdminWorkerStatus.OFFLINE -> Color(0xFFE6EAF0)
                    },
                    statusTextColor = when (worker.status) {
                        AdminWorkerStatus.ACTIVE -> Color(0xFF0B7B4E)
                        AdminWorkerStatus.ON_FIELD -> Color(0xFF0B6FA2)
                        AdminWorkerStatus.OFFLINE -> Color(0xFF667A93)
                    },
                    avatarText = initialsForName(worker.fullName),
                    infoChips = listOf("ID: ${worker.id}", "Joined: ${worker.joinDate}"),
                )
            }

            item {
                AdminInfoCard(title = "CONTACT INFORMATION", icon = Icons.Filled.PersonAdd) {
                    ContactRow(icon = Icons.Filled.Call, label = "PHONE", value = worker.phone)
                    ContactRow(icon = Icons.Filled.Email, label = "EMAIL", value = worker.email)
                }
            }

            item {
                AdminInfoCard(title = "WORK DETAILS", icon = Icons.Filled.Group) {
                    ProfileDetailRow(label = "ASSIGNED VILLAGE", value = worker.assignedVillage)
                    ProfileDetailRow(label = "ROLE", value = worker.role)
                    ProfileDetailRow(label = "WORKER ID", value = worker.id)
                }
            }

            item {
                ProfileActionButton(
                    text = "Manage Field Schedule",
                    containerColor = Color(0xFF3AA0D8),
                    textColor = Color(0xFF032F4B),
                    onClick = {},
                )
            }

            item {
                ProfileActionButton(
                    text = "Deactivate Worker",
                    containerColor = Color(0xFFF4D7D3),
                    textColor = Color(0xFFA31818),
                    onClick = onDeactivate,
                )
            }

            item {
                ProfileActionButton(
                    text = "Delete Profile",
                    containerColor = Color(0xFFEFC6C2),
                    textColor = Color(0xFF8E1212),
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
fun AdminAddWorkerScreen(
    onBack: () -> Unit,
    onCreateWorker: (AdminWorkerFormInput) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    AdminWorkerFormScreen(
        modifier = modifier,
        formTitle = "Add Health Worker",
        submitLabel = "Create Health Worker",
        initialWorker = null,
        formErrorMessage = errorMessage,
        onBack = onBack,
        onSubmit = onCreateWorker,
    )
}

@Composable
fun AdminEditWorkerScreen(
    worker: AdminWorkerUi,
    onBack: () -> Unit,
    onSaveWorker: (AdminWorkerFormInput) -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    AdminWorkerFormScreen(
        modifier = modifier,
        formTitle = "Edit Health Worker",
        submitLabel = "Save Changes",
        initialWorker = worker,
        formErrorMessage = errorMessage,
        onBack = onBack,
        onSubmit = onSaveWorker,
    )
}

@Composable
private fun AdminWorkerFormScreen(
    formTitle: String,
    submitLabel: String,
    initialWorker: AdminWorkerUi?,
    onBack: () -> Unit,
    onSubmit: (AdminWorkerFormInput) -> Unit,
    formErrorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var fullName by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.fullName ?: "")
    }
    var phone by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.phone ?: "")
    }
    var email by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.email ?: "")
    }
    var password by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.password ?: "")
    }
    var village by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.assignedVillage ?: "")
    }
    var workerId by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.id ?: "")
    }
    var role by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.role ?: "")
    }
    var photoPath by rememberSaveable(initialWorker?.id) {
        mutableStateOf(initialWorker?.photoPath ?: "")
    }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val workerImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val encoded = encodeImageAsBase64(context, uri)
            if (!encoded.isNullOrBlank()) {
                photoPath = encoded
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SimpleFormHeader(title = formTitle, onBack = onBack)
            }

            val visibleError = localErrorMessage ?: formErrorMessage
            if (!visibleError.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Error",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = visibleError,
                                color = Color(0xFFEF5350),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E6EC))
                            .border(3.dp, Color(0xFFF6F7F9), CircleShape)
                            .clickable { workerImageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (photoPath.isNotBlank()) {
                            val bitmap = remember(photoPath) {
                                decodeBase64ToBitmap(photoPath)
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Worker profile image",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PersonAdd,
                                    contentDescription = "Upload profile",
                                    tint = Color(0xFF7A8594),
                                    modifier = Modifier.size(38.dp),
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.PersonAdd,
                                contentDescription = "Upload profile",
                                tint = Color(0xFF7A8594),
                                modifier = Modifier.size(38.dp),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DoctorPrimary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Text(
                        text = if (photoPath.isBlank()) "Upload profile picture" else "Profile image selected",
                        color = DoctorText,
                        fontSize = 16.sp / 1.2f,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { workerImageLauncher.launch("image/*") },
                    )
                }
            }

            item {
                AdminFormSection(title = "Basic Info", icon = Icons.Filled.PersonAdd) {
                    AdminInputField(
                        label = "Full Name",
                        value = fullName,
                        placeholder = "Enter worker full name",
                        onValueChange = { fullName = it },
                    )
                    AdminInputField(
                        label = "Phone Number",
                        value = phone,
                        placeholder = "+254 --- --- ---",
                        onValueChange = { phone = it },
                    )
                    AdminInputField(
                        label = "Email",
                        value = email,
                        placeholder = "name@ruralcare.ai",
                        onValueChange = { email = it },
                    )
                    AdminInputField(
                        label = "Password",
                        value = password,
                        placeholder = "Assign temporary password",
                        onValueChange = { password = it },
                        leadingIcon = Icons.Filled.Lock,
                    )
                }
            }

            item {
                AdminFormSection(title = "Work Details", icon = Icons.Filled.LocationOn) {
                    AdminInputField(
                        label = "Assigned Village",
                        value = village,
                        placeholder = "Enter assigned village",
                        onValueChange = { village = it },
                    )
                    AdminInputField(
                        label = "Worker ID",
                        value = workerId,
                        placeholder = "RC-2024-XXX",
                        onValueChange = { workerId = it },
                    )
                    AdminDropdownField(
                        label = "Role",
                        value = role,
                        placeholder = "Community Health Worker",
                        options = WorkerRoleOptions,
                        onOptionSelected = { role = it },
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEBFA)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = DoctorPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "The health worker will receive an automated invitation email to set up their RuralCareAI credentials once you click create.",
                            color = Color(0xFF1F4D73),
                            fontSize = 16.sp / 1.2f,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                PrimarySubmitButton(
                    label = submitLabel,
                    onClick = {
                        val trimmedName = fullName.trim()
                        val trimmedPhone = phone.trim()
                        val trimmedEmail = email.trim()
                        val trimmedRole = role.trim()
                        val trimmedPassword = password.trim()
                        val trimmedVillage = village.trim()

                        val validationError = when {
                            trimmedName.length < 2 -> "Worker name must be at least 2 characters."
                            trimmedPhone.length < 7 -> "Enter a valid phone number."
                            trimmedEmail.length < 5 || !trimmedEmail.contains("@") -> "Enter a valid email address."
                            trimmedRole.isEmpty() -> "Please select a worker role."
                            initialWorker == null && trimmedPassword.length < 8 -> "Password must be at least 8 characters."
                            trimmedVillage.length < 2 -> "Assigned village must be at least 2 characters."
                            else -> null
                        }

                        if (validationError != null) {
                            localErrorMessage = validationError
                            return@PrimarySubmitButton
                        }

                        localErrorMessage = null
                        onSubmit(
                            AdminWorkerFormInput(
                                fullName = trimmedName,
                                phone = trimmedPhone,
                                email = trimmedEmail,
                                password = trimmedPassword,
                                workerCode = workerId.trim(),
                                assignedVillage = trimmedVillage,
                                roleTitle = trimmedRole,
                                photoPath = photoPath.trim().ifBlank { null },
                            )
                        )
                    },
                )
            }
        }
    }
}

private fun encodeImageAsBase64(context: Context, uri: Uri): String? {
    return runCatching {
        val input: InputStream = context.contentResolver.openInputStream(uri) ?: return null
        input.use { stream ->
            val bytes = stream.readBytes()
            val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
            "data:image/jpeg;base64,$encoded"
        }
    }.getOrNull()
}

private fun decodeBase64ToBitmap(value: String): android.graphics.Bitmap? {
    return runCatching {
        val payload = value.substringAfter("base64,", value)
        val bytes = Base64.decode(payload, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}

@Composable
fun AdminAnalyticsScreen(
    analytics: AdminAnalyticsDto?,
    selectedRange: String,
    isLoading: Boolean,
    onSelectRange: (String) -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenDoctors: () -> Unit,
    onOpenWorkers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val ranges = remember { listOf("Week", "Month", "Year", "All Time") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DoctorBg)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Analytics",
                    color = DoctorText,
                    fontSize = if (isCompact) 32.sp / 1.45f else 34.sp / 1.45f,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                AnalyticsRangeSelector(
                    ranges = ranges,
                    selectedRange = selectedRange,
                    onSelect = onSelectRange,
                )
            }

            if (isLoading) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
                    ) {
                        Text(
                            text = "Loading analytics...",
                            color = DoctorMuted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            } else {
                val metrics = analytics

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AnalyticsConsultationGrowthCard(
                            modifier = Modifier.weight(1f),
                            compact = isCompact,
                            consultationCount = metrics?.consultationCount ?: 0,
                            growthPercent = metrics?.consultationGrowthPercent ?: 0.0,
                            trendSource = metrics?.consultationTrends.orEmpty(),
                        )
                        AnalyticsPatientSatisfactionCard(
                            modifier = Modifier.weight(1f),
                            compact = isCompact,
                            score = metrics?.patientSatisfactionScore ?: 0.0,
                            samples = metrics?.patientSatisfactionSamples ?: 0,
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AnalyticsEfficiencyCard(
                            modifier = Modifier.weight(1f),
                            compact = isCompact,
                            efficiencyPercent = metrics?.efficiencyPercent ?: 0.0,
                        )
                        AnalyticsActiveChwsCard(
                            modifier = Modifier.weight(1f),
                            compact = isCompact,
                            activeChws = metrics?.activeChws ?: 0,
                            activePercent = metrics?.activeChwsPercent ?: 0.0,
                        )
                    }
                }

                item {
                    AnalyticsConsultationTrendsCard(
                        compact = isCompact,
                        points = metrics?.consultationTrends.orEmpty(),
                    )
                }

                item {
                    AnalyticsRegionalClinicPerformanceCard(
                        clinics = metrics?.regionalClinicPerformance.orEmpty(),
                    )
                }
            }
        }

        AdminBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
            selectedTab = AdminBottomTab.ANALYTICS,
            onDashboardClick = onOpenDashboard,
            onDoctorsClick = onOpenDoctors,
            onWorkersClick = onOpenWorkers,
            onAnalyticsClick = {},
        )
    }
}

@Composable
private fun AnalyticsRangeSelector(
    ranges: List<String>,
    selectedRange: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFFD7DBE0))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ranges.forEach { range ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (range == selectedRange) DoctorSurface else Color.Transparent)
                    .clickable(onClick = { onSelect(range) })
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = range,
                    color = if (range == selectedRange) DoctorPrimary else DoctorText,
                    fontSize = if (range == "All Time") 13.sp else 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun AnalyticsConsultationGrowthCard(
    modifier: Modifier,
    compact: Boolean,
    consultationCount: Int,
    growthPercent: Double,
    trendSource: List<AdminConsultationTrendDto>,
) {
    val bars = if (trendSource.isEmpty()) {
        listOf(0.24f, 0.46f, 0.32f, 0.64f, 0.86f, 0.72f, 0.74f)
    } else {
        val maxValue = (trendSource.maxOfOrNull { it.value } ?: 0).coerceAtLeast(1)
        trendSource.map { ((it.value.toFloat() / maxValue.toFloat()) * 0.72f + 0.18f).coerceIn(0.18f, 0.9f) }
    }

    val formattedGrowth = if (growthPercent >= 0) "+${"%.1f".format(growthPercent)}%" else "${"%.1f".format(growthPercent)}%"

    AnalyticsStatCard(modifier = modifier, compact = compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = "CONSULTATION\nGROWTH",
                color = DoctorText,
                fontSize = 16.sp / 1.22f,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formattedGrowth,
                color = if (growthPercent >= 0) Color(0xFF0B9857) else Color(0xFFB64040),
                fontSize = 14.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 42.dp else 50.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            bars.forEach { fraction ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF8FC9EA)),
                )
            }
        }

        Text(
            text = consultationCount.toString(),
            color = DoctorText,
            fontSize = 38.sp / 1.75f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AnalyticsPatientSatisfactionCard(
    modifier: Modifier,
    compact: Boolean,
    score: Double,
    samples: Int,
) {
    AnalyticsStatCard(modifier = modifier, compact = compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = "PATIENT\nSATISFACTION",
                color = DoctorText,
                fontSize = 16.sp / 1.22f,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "★",
                color = Color(0xFFF1AE2E),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${"%.1f".format(score)}",
                color = DoctorText,
                fontSize = 58.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "/ 5.0",
                color = DoctorText,
                fontSize = 24.sp / 1.2f,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MiniAvatarDot(initial = "A", color = Color(0xFFF5D7C5))
            MiniAvatarDot(initial = "R", color = Color(0xFFE1E8F5))
            MiniAvatarDot(initial = "K", color = Color(0xFFE6F1E0))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE6EAF0))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$samples",
                    color = DoctorMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun AnalyticsEfficiencyCard(
    modifier: Modifier,
    compact: Boolean,
    efficiencyPercent: Double,
) {
    val formattedEfficiency = if (efficiencyPercent >= 0) "+${"%.1f".format(efficiencyPercent)}%" else "${"%.1f".format(efficiencyPercent)}%"

    AnalyticsStatCard(modifier = modifier, compact = compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "EFFICIENCY",
                color = DoctorText,
                fontSize = 16.sp / 1.22f,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "Efficiency",
                tint = DoctorPrimary,
                modifier = Modifier.size(15.dp),
            )
        }

        Text(
            text = formattedEfficiency,
            color = DoctorText,
            fontSize = 56.sp / 1.75f,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Reduction in response time",
            color = DoctorText.copy(alpha = 0.84f),
            fontSize = 14.sp / 1.2f,
            fontWeight = FontWeight.Medium,
        )

        AnalyticsProgressBar(progress = (efficiencyPercent / 100.0).toFloat().coerceIn(0f, 1f))
    }
}

@Composable
private fun AnalyticsActiveChwsCard(
    modifier: Modifier,
    compact: Boolean,
    activeChws: Int,
    activePercent: Double,
) {
    AnalyticsStatCard(modifier = modifier, compact = compact) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "ACTIVE CHWS",
                color = DoctorText,
                fontSize = 16.sp / 1.22f,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = "Workers",
                tint = DoctorText.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp),
            )
        }

        Text(
            text = activeChws.toString(),
            color = DoctorText,
            fontSize = 56.sp / 1.75f,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "${"%.1f".format(activePercent)}% active today",
            color = Color(0xFF0B9857),
            fontSize = 14.sp / 1.2f,
            fontWeight = FontWeight.Bold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF34C38F)),
            )
            Text(
                text = "Live Operations",
                color = DoctorText.copy(alpha = 0.82f),
                fontSize = 14.sp / 1.2f,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AnalyticsStatCard(
    modifier: Modifier,
    compact: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.height(if (compact) 154.dp else 164.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content,
        )
    }
}

@Composable
private fun MiniAvatarDot(
    initial: String,
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = DoctorText.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AnalyticsConsultationTrendsCard(
    compact: Boolean,
    points: List<AdminConsultationTrendDto>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Patient Consultation Trends",
                    color = DoctorText,
                    fontSize = 30.sp / 1.75f,
                    fontWeight = FontWeight.Bold,
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(DoctorText.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "i",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (points.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 180.dp else 200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE9EEF4)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No consultation trend data yet.",
                        color = DoctorMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            } else {
                val trendValues = points.map { it.value.toFloat() }
                AnalyticsTrendLineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 180.dp else 200.dp),
                    values = trendValues,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    points.forEach { point ->
                        Text(
                            text = point.label,
                            color = DoctorText.copy(alpha = 0.86f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsTrendLineChart(
    modifier: Modifier,
    values: List<Float>,
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas

        val maxValue = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
        val clamped = values.map { ((it / maxValue) * 0.72f + 0.14f).coerceIn(0.14f, 0.92f) }
        val spacing = size.width / (clamped.lastIndex)
        val points = clamped.mapIndexed { index, value ->
            Offset(
                x = spacing * index,
                y = size.height * (1f - value),
            )
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val previous = points[i - 1]
                val current = points[i]
                val midX = (previous.x + current.x) / 2f
                cubicTo(midX, previous.y, midX, current.y, current.x, current.y)
            }
        }

        drawPath(
            path = path,
            color = DoctorPrimary,
            style = Stroke(width = 5f, cap = StrokeCap.Round),
        )

        points.forEach { point ->
            drawCircle(
                color = DoctorPrimary,
                radius = 4.5f,
                center = point,
            )
        }
    }
}

@Composable
private fun AnalyticsRegionalClinicPerformanceCard(
    clinics: List<AdminClinicPerformanceDto>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Regional Clinic Performance",
                color = DoctorText,
                fontSize = 38.sp / 1.75f,
                fontWeight = FontWeight.Bold,
            )

            if (clinics.isEmpty()) {
                Text(
                    text = "No clinic performance data available yet.",
                    color = DoctorMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            } else {
                clinics.forEach { clinic ->
                    AnalyticsClinicPerformanceRow(
                        name = clinic.clinicName,
                        progress = (clinic.performancePercent / 100.0).toFloat(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsClinicPerformanceRow(
    name: String,
    progress: Float,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                color = DoctorText,
                fontSize = 18.sp / 1.2f,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "${(progress.coerceIn(0f, 1f) * 100).toInt()}%",
                color = DoctorPrimary,
                fontSize = 17.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        AnalyticsProgressBar(progress = progress)
    }
}

@Composable
private fun AnalyticsProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFD8DDE3)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1EA2E5)),
        )
    }
}
@Composable
private fun ProfileHeader(
    title: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, DoctorOutline, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DoctorPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = title,
                color = DoctorText,
                fontSize = 20.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = "Edit",
            color = DoctorPrimary,
            fontSize = 15.sp / 1.2f,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onEdit),
        )
    }
}

@Composable
private fun ProfileIdentityBlock(
    name: String,
    subtitle: String,
    status: String,
    statusContainer: Color,
    statusTextColor: Color,
    avatarText: String,
    infoChips: List<String>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AvatarCircle(
            text = avatarText,
            containerColor = Color(0xFFDDEEF8),
            textColor = DoctorPrimary,
            size = 98.dp,
            textSize = 26.sp,
            ring = true,
        )

        Text(
            text = name,
            color = DoctorText,
            fontSize = 35.sp / 1.75f,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(statusContainer)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = status,
                color = statusTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = subtitle,
            color = DoctorText,
            fontSize = 16.sp / 1.2f,
            fontWeight = FontWeight.Medium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            infoChips.forEach { chip ->
                ProfileChip(text = chip)
            }
        }
    }
}

@Composable
private fun ProfileChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DoctorChipBg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = DoctorText.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = DoctorMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = value,
            color = DoctorText,
            fontSize = 18.sp / 1.2f,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ProfileActionButton(
    text: String,
    containerColor: Color,
    textColor: Color,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 17.sp / 1.2f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AdminInfoCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DoctorPrimary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = title,
                    color = DoctorMuted,
                    fontSize = 14.sp / 1.2f,
                    fontWeight = FontWeight.Bold,
                )
            }

            content()
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDEEF8)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = DoctorPrimary,
                modifier = Modifier.size(19.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = DoctorMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = DoctorText,
                fontSize = 17.sp / 1.2f,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    suffix: String,
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F8)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = label,
                color = DoctorMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = DoctorText,
                fontSize = 18.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = suffix,
                color = DoctorMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun SimpleFormHeader(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, DoctorOutline, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DoctorPrimary,
                modifier = Modifier.size(18.dp),
            )
        }

        Text(
            text = title,
            color = DoctorText,
            fontSize = 20.sp / 1.2f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AdminFormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = DoctorPrimary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = title,
                color = DoctorText,
                fontSize = 18.sp / 1.2f,
                fontWeight = FontWeight.Bold,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DoctorSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AdminInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = DoctorText,
            fontSize = 14.sp / 1.15f,
            fontWeight = FontWeight.SemiBold,
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = DoctorMuted,
                    fontSize = 16.sp / 1.2f,
                )
            },
            leadingIcon = if (leadingIcon == null) null else {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = label,
                        tint = DoctorMuted,
                    )
                }
            },
            trailingIcon = if (trailingIcon == null) null else {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = label,
                        tint = DoctorMuted,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE1E6ED),
                unfocusedContainerColor = Color(0xFFE1E6ED),
                focusedTextColor = DoctorText,
                unfocusedTextColor = DoctorText,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = DoctorPrimary,
            ),
        )
    }
}

@Composable
private fun AdminDropdownField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    leadingIcon: ImageVector? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = DoctorText,
            fontSize = 14.sp / 1.15f,
            fontWeight = FontWeight.SemiBold,
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = DoctorMuted,
                        fontSize = 16.sp / 1.2f,
                    )
                },
                leadingIcon = if (leadingIcon == null) null else {
                    {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = label,
                            tint = DoctorMuted,
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "$label options",
                        tint = DoctorMuted,
                        modifier = Modifier.clickable { expanded = true },
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE1E6ED),
                    unfocusedContainerColor = Color(0xFFE1E6ED),
                    focusedTextColor = DoctorText,
                    unfocusedTextColor = DoctorText,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = DoctorPrimary,
                ),
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = DoctorText,
                                fontSize = 15.sp,
                                fontWeight = if (option == value) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimarySubmitButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary),
    ) {
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(19.dp),
        )
        Text(
            text = "  $label",
            color = Color.White,
            fontSize = 18.sp / 1.2f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SearchBarPlaceholder(placeholder: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE1E6ED))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = DoctorMuted,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = placeholder,
            color = DoctorMuted,
            fontSize = 17.sp / 1.2f,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun AdminFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Color(0xFF3AA0D8) else Color(0xFFF0F2F5))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color(0xFF042E49) else DoctorText.copy(alpha = 0.88f),
            fontSize = 16.sp / 1.2f,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AnalyticsTile(
    title: String,
    value: String,
    subtitle: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DoctorSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DoctorOutline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                color = DoctorMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                color = DoctorText,
                fontSize = 28.sp / 1.6f,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = DoctorPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AvatarCircle(
    text: String,
    containerColor: Color,
    textColor: Color,
    size: androidx.compose.ui.unit.Dp,
    textSize: androidx.compose.ui.unit.TextUnit,
    ring: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(
                width = if (ring) 3.dp else 0.dp,
                color = if (ring) DoctorPrimary else Color.Transparent,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun initialsForName(name: String): String {
    return name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
}

