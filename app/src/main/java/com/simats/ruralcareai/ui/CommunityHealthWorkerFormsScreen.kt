package com.simats.ruralcareai.ui

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.ChwApiClient
import com.simats.ruralcareai.network.ChwCampSchedulePayload
import com.simats.ruralcareai.network.ChwRegisterPatientPayload
import com.simats.ruralcareai.network.ChwRegisterPatientResult
import com.simats.ruralcareai.network.ChwScheduleCampResult
import com.simats.ruralcareai.network.ChwRecordVitalsPayload
import com.simats.ruralcareai.network.ChwRecordVitalsResult
import java.io.InputStream
import kotlinx.coroutines.launch

private val ChwFormBackground = Color(0xFFF2F4F8)
private val ChwFormSurface = Color(0xFFEFF1F5)
private val ChwFormInput = Color(0xFFFFFFFF)
private val ChwFormText = Color(0xFF141A23)
private val ChwFormMuted = Color(0xFF6D7584)
private val ChwFormPrimary = Color(0xFF0B6FA2)
private val ChwFormAccent = Color(0xFFF7E9D0)
private val ChwFormAccentText = Color(0xFF7A4B00)
private val ChwFormSecondary = Color(0xFF3E627C)

@Composable
fun ChwAddPatientRecordScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    workerId: Int?,
    workerVillage: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val compact = config.screenHeightDp <= 820 || config.screenWidthDp <= 392

    var patientName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var aadhaar by rememberSaveable { mutableStateOf("") }
    val village = workerVillage
    var height by rememberSaveable { mutableStateOf("170") }
    var weight by rememberSaveable { mutableStateOf("65") }
    var bloodGroup by rememberSaveable { mutableStateOf("A+") }
    var gender by rememberSaveable { mutableStateOf("Male") }
    var photoBase64 by rememberSaveable { mutableStateOf<String?>(null) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            photoBase64 = chwEncodeImageAsBase64(context, uri)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChwFormBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = if (compact) 10.dp else 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ChwSimpleTopBar(title = "Register Patient", onBack = onBack, showProfile = false)
            }

            item {
                ChwCaptureCard(
                    title = if (photoBase64.isNullOrBlank()) "Upload or take a photo of the patient" else "Patient photo selected",
                    photoBase64 = photoBase64,
                    onClick = { imagePicker.launch("image/*") },
                )
            }

            item {
                Text(text = "Personal Details", color = ChwFormText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ChwFormSurface),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ChwLabel("FULL NAME")
                        ChwInputField(value = patientName, placeholder = "e.g. Rahul Sharma", onValueChange = { patientName = it })

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ChwLabel("AGE")
                                ChwInputField(value = age, placeholder = "24", onValueChange = { age = it })
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ChwLabel("AADHAAR / ID")
                                ChwInputField(value = aadhaar, placeholder = "0000 0000 0000", onValueChange = { aadhaar = it })
                            }
                        }

                        ChwLabel("GENDER")
                        ChwSegmentedChoice(selected = gender, options = listOf("Male", "Female", "Other"), onSelected = { gender = it })

                        ChwLabel("VILLAGE")
                        ChwInputField(
                            value = village,
                            placeholder = "Select Village",
                            onValueChange = {},
                            enabled = false,
                        )
                    }
                }
            }

            if (!formError.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8E8)),
                    ) {
                        Text(
                            text = formError.orEmpty(),
                            color = Color(0xFF8A1C1C),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            item { Text(text = "Health Vitals", color = ChwFormText, fontSize = 16.sp, fontWeight = FontWeight.Bold) }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChwSmallVitalCard(label = "HEIGHT\n(CM)", value = height, modifier = Modifier.weight(1f), onValueChange = { height = it })
                    ChwSmallVitalCard(label = "WEIGHT\n(KG)", value = weight, modifier = Modifier.weight(1f), onValueChange = { weight = it })
                    ChwSmallVitalCard(label = "BLOOD", value = bloodGroup, modifier = Modifier.weight(1f), onValueChange = { bloodGroup = it })
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ChwFormAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2C79B)),
                ) {
                    Text(
                        text = "Please ensure all Aadhaar details match the patient identity before saving.",
                        color = ChwFormAccentText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        if (isSaving) {
                            return@Button
                        }

                        val trimmedName = patientName.trim()
                        val parsedAge = age.trim().toIntOrNull()
                        val trimmedAadhaar = aadhaar.trim()
                        val parsedHeight = height.trim().toDoubleOrNull()
                        val parsedWeight = weight.trim().toDoubleOrNull()
                        val trimmedBlood = bloodGroup.trim().uppercase()

                        val validationError = when {
                            workerId == null -> "Unable to detect health worker profile. Please log in again."
                            village.isBlank() -> "Your assigned village is missing. Please contact admin."
                            trimmedName.length < 2 -> "Enter a valid patient name."
                            parsedAge == null || parsedAge !in 1..120 -> "Enter a valid patient age (1-120)."
                            trimmedAadhaar.isBlank() -> "Aadhaar / ID is required."
                            parsedHeight == null || parsedHeight <= 0.0 -> "Enter a valid height in cm."
                            parsedWeight == null || parsedWeight <= 0.0 -> "Enter a valid weight in kg."
                            trimmedBlood.isBlank() -> "Enter blood group."
                            else -> null
                        }

                        if (validationError != null) {
                            formError = validationError
                            return@Button
                        }

                        val safeWorkerId = workerId ?: return@Button
                        val safeAge = parsedAge ?: return@Button

                        formError = null
                        isSaving = true
                        scope.launch {
                            when (
                                val result = ChwApiClient.registerPatient(
                                    workerId = safeWorkerId,
                                    payload = ChwRegisterPatientPayload(
                                        fullName = trimmedName,
                                        age = safeAge,
                                        gender = gender,
                                        aadhaarId = trimmedAadhaar,
                                        heightCm = parsedHeight,
                                        weightKg = parsedWeight,
                                        bloodType = trimmedBlood,
                                        photoBase64 = photoBase64,
                                    ),
                                )
                            ) {
                                is ChwRegisterPatientResult.Success -> {
                                    isSaving = false
                                    onSave()
                                }

                                is ChwRegisterPatientResult.Error -> {
                                    isSaving = false
                                    formError = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = ChwFormPrimary),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "Save", tint = Color.White)
                        Text(
                            text = if (isSaving) "Saving..." else "Register & Save",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChwRecordVitalsScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    workerId: Int?,
    workerVillage: String,
    modifier: Modifier = Modifier,
) {
    val config = LocalConfiguration.current
    val compact = config.screenHeightDp <= 820 || config.screenWidthDp <= 392

    var patientIdInput by rememberSaveable { mutableStateOf("") }
    var patientName by rememberSaveable { mutableStateOf("") }
    var systolic by rememberSaveable { mutableStateOf("145") }
    var diastolic by rememberSaveable { mutableStateOf("95") }
    var glucoseMode by rememberSaveable { mutableStateOf("Fasting") }
    var glucoseReading by rememberSaveable { mutableStateOf("98") }
    var temperature by rememberSaveable { mutableStateOf("98.6") }
    var spo2 by rememberSaveable { mutableStateOf("98") }
    var notes by rememberSaveable { mutableStateOf("") }
    var formError by rememberSaveable { mutableStateOf<String?>(null) }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChwFormBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = if (compact) 10.dp else 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ChwFormPrimary,
                            modifier = Modifier.size(24.dp).clickable(onClick = onBack),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "Patient Vitals", color = ChwFormText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "CHW Village: ${workerVillage.ifBlank { "Not Assigned" }}", color = ChwFormSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6F9))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChwLabel("PATIENT ID")
                        ChwInputField(value = patientIdInput, placeholder = "Enter patient id", onValueChange = { patientIdInput = it })
                        ChwLabel("PATIENT NAME")
                        ChwInputField(value = patientName, placeholder = "Enter patient name", onValueChange = { patientName = it })
                        Text(text = "CHW area: ${workerVillage.ifBlank { "No village assigned" }}", color = ChwFormMuted, fontSize = 12.sp)
                    }
                }
            }


            item { ChwSectionHeaderWithPill(title = "Blood Pressure", pillText = "HIGH", pillColor = Color(0xFFF5D1D1), pillTextColor = Color(0xFFAF1F1F)) }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Systolic (mmHg)", color = ChwFormMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            ChwInputField(value = systolic, placeholder = "0", onValueChange = { systolic = it })
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Diastolic (mmHg)", color = ChwFormMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            ChwInputField(value = diastolic, placeholder = "0", onValueChange = { diastolic = it })
                        }
                    }
                }
            }

            item { ChwSectionHeaderWithPill(title = "Blood Glucose", pillText = "NORMAL", pillColor = Color(0xFFD3EFD9), pillTextColor = Color(0xFF0A7A33)) }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChwSegmentedChoice(selected = glucoseMode, options = listOf("Fasting", "Random"), onSelected = { glucoseMode = it })
                        Text(text = "Reading (mg/dL)", color = ChwFormMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        ChwInputField(value = glucoseReading, placeholder = "0", onValueChange = { glucoseReading = it })
                    }
                }
            }

            item {
                Text(text = "TEMPERATURE & OXYGEN", color = ChwFormText, fontSize = 16.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Temp (F)", color = ChwFormSecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            ChwInputField(value = temperature, placeholder = "0", onValueChange = { temperature = it })
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "SpO2 (%)", color = ChwFormSecondary, fontSize = 16.sp / 1.2f, fontWeight = FontWeight.SemiBold)
                            ChwInputField(value = spo2, placeholder = "0", onValueChange = { spo2 = it })
                        }
                    }
                }
            }

            item {
                Text(text = "CLINICAL NOTES", color = ChwFormText, fontSize = 16.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChwInputField(value = notes, placeholder = "Add worker observations...", onValueChange = { notes = it }, singleLine = false)
                    }
                }
            }

            item {
                if (formError != null) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAEA))) {
                        Text(text = formError ?: "", color = Color(0xFF8B1E1E), modifier = Modifier.padding(12.dp))
                    }
                }

                Button(
                    onClick = {
                        if (workerId == null) {
                            formError = "Unable to save vitals: unknown worker context."
                            return@Button
                        }
                        val patientId = patientIdInput.toIntOrNull()
                        if (patientId == null || patientId <= 0) {
                            formError = "Enter a valid patient ID."
                            return@Button
                        }

                        val systolicValue = systolic.toIntOrNull()
                        val diastolicValue = diastolic.toIntOrNull()
                        val glucoseValue = glucoseReading.toIntOrNull()
                        val temperatureValue = temperature.toDoubleOrNull()
                        val spo2Value = spo2.toIntOrNull()

                        if (systolicValue == null || diastolicValue == null || glucoseValue == null || temperatureValue == null || spo2Value == null) {
                            formError = "Please enter valid numeric vitals values."
                            return@Button
                        }

                        isSaving = true
                        formError = null
                        scope.launch {
                            val result = ChwApiClient.submitVitalsRecord(
                                ChwRecordVitalsPayload(
                                    patientId = patientId,
                                    systolic = systolicValue,
                                    diastolic = diastolicValue,
                                    glucoseMode = glucoseMode,
                                    glucoseReading = glucoseValue,
                                    temperature = temperatureValue,
                                    spo2 = spo2Value,
                                    notes = notes.ifBlank { null },
                                )
                            )
                            isSaving = false
                            when (result) {
                                is ChwRecordVitalsResult.Success -> {
                                    formError = null
                                    onSave()
                                }
                                is ChwRecordVitalsResult.Error -> {
                                    formError = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChwFormPrimary),
                    enabled = !isSaving,
                ) {
                    Text(if (isSaving) "Saving..." else "Submit & Sync Record", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun ChwScheduleCampScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    workerId: Int?,
    workerVillage: String,
    modifier: Modifier = Modifier,
) {
    val config = LocalConfiguration.current
    val compact = config.screenHeightDp <= 820 || config.screenWidthDp <= 392
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val calendar = java.util.Calendar.getInstance()
    var selectedDateInMs by rememberSaveable { mutableStateOf(calendar.timeInMillis) }
    val selectedDateText by remember(selectedDateInMs) {
        mutableStateOf(java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(selectedDateInMs)))
    }

    var villageName by rememberSaveable { mutableStateOf(workerVillage.ifBlank { "Not Assigned" }) }
    var primaryFocus by rememberSaveable { mutableStateOf("Maternal Health") }
    var focusExpanded by rememberSaveable { mutableStateOf(false) }
    val focusOptions = listOf("Maternal Health", "Immunization", "Nutrition", "Child Health", "NCD Screening")
    val focusSubOptions = mapOf(
        "Maternal Health" to listOf("Antenatal Care", "Postnatal Follow-up", "Nutrition Counseling"),
        "Immunization" to listOf("Polio", "DTP", "MMR", "Hepatitis B"),
        "Nutrition" to listOf("Growth Monitoring", "Diet Advice", "Supplement Distribution"),
        "Child Health" to listOf("Under-5 Checkup", "Development Tracking", "Deworming"),
        "NCD Screening" to listOf("Blood Pressure", "Blood Sugar", "BMI"),
    )
    var selectedSubFocus by rememberSaveable { mutableStateOf(focusSubOptions[primaryFocus] ?: emptyList()) }
    var selectedSlot by rememberSaveable { mutableStateOf("09:00 AM") }
    val slots = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:30 PM")
    var formError by rememberSaveable { mutableStateOf<String?>(null) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = java.util.Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            selectedDateInMs = picked.timeInMillis
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChwFormBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = if (compact) 10.dp else 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ChwFormPrimary, modifier = Modifier.size(24.dp).clickable(onClick = onBack))
                        Text(text = "Schedule Camp", color = ChwFormPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More", tint = ChwFormText, modifier = Modifier.size(22.dp))
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "LOCATION DETAILS", color = ChwFormMuted, fontSize = 15.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)

                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EBF0))) {
                            Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("VILLAGE NAME", color = ChwFormPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(villageName, color = ChwFormText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Text(text = "FOCUS AREA", color = ChwFormMuted, fontSize = 15.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)

                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { focusExpanded = true }, modifier = Modifier.fillMaxWidth().background(Color(0xFFE8EBF0), RoundedCornerShape(8.dp)).padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("PRIMARY FOCUS", color = ChwFormPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(primaryFocus, color = ChwFormText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Select focus", tint = ChwFormMuted)
                                }
                            }

                            DropdownMenu(expanded = focusExpanded, onDismissRequest = { focusExpanded = false }) {
                                focusOptions.forEach { option ->
                                    DropdownMenuItem(text = { Text(option) }, onClick = {
                                        primaryFocus = option
                                        selectedSubFocus = focusSubOptions[option] ?: emptyList()
                                        focusExpanded = false
                                    })
                                }
                            }
                        }

                        Text(text = "RELEVANT SUB-TOPICS", color = ChwFormMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            focusSubOptions[primaryFocus]?.forEach { topic ->
                                val checked = selectedSubFocus.contains(topic)
                                Row(modifier = Modifier.fillMaxWidth().clickable {
                                    selectedSubFocus = if (checked) selectedSubFocus - topic else selectedSubFocus + topic
                                }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = checked, onCheckedChange = {
                                        selectedSubFocus = if (it) selectedSubFocus + topic else selectedSubFocus - topic
                                    })
                                    Text(text = topic, modifier = Modifier.padding(start = 8.dp), color = ChwFormText)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Schedule Date", color = ChwFormText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = selectedDateText,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                            enabled = false,
                            readOnly = true,
                            label = { Text("Selected Date") },
                        )
                    }
                }
            }

            item { Text(text = "AVAILABLE SLOTS", color = ChwFormMuted, fontSize = 13.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold) }

            item {
                val slots = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:30 PM")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        slots.take(2).forEach { slot ->
                            ChwSlotChip(slot = slot, selected = slot == selectedSlot, onClick = { selectedSlot = slot }, modifier = Modifier.weight(1f))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        slots.drop(2).forEach { slot ->
                            ChwSlotChip(slot = slot, selected = slot == selectedSlot, onClick = { selectedSlot = slot }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }


            item {
                if (formError != null) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAEA))) {
                        Text(text = formError ?: "", color = Color(0xFF8B1E1E), modifier = Modifier.padding(12.dp))
                    }
                }

                Button(
                    onClick = {
                        if (workerId == null) {
                            formError = "Unable to schedule camp: unknown worker context."
                            return@Button
                        }
                        if (villageName.isBlank() || villageName == "Not Assigned") {
                            formError = "Village must be assigned to CHW before scheduling camp."
                            return@Button
                        }

                        isSaving = true
                        formError = null
                        scope.launch {
                            val result = ChwApiClient.scheduleCamp(
                                workerId,
                                ChwCampSchedulePayload(
                                    village = villageName,
                                    primaryFocus = primaryFocus,
                                    subFocus = selectedSubFocus,
                                    scheduledDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(selectedDateInMs)),
                                    slot = selectedSlot,
                                ),
                            )
                            isSaving = false
                            when (result) {
                                is ChwScheduleCampResult.Success -> {
                                    formError = null
                                    onSave()
                                }

                                is ChwScheduleCampResult.Error -> {
                                    formError = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChwFormPrimary),
                    enabled = !isSaving,
                ) {
                    Text(
                        if (isSaving) "Saving..." else "Broadcast & Schedule",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChwSimpleTopBar(
    title: String,
    onBack: () -> Unit,
    showProfile: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ChwFormText, modifier = Modifier.size(24.dp).clickable(onClick = onBack))
            Text(text = title, color = ChwFormText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        if (showProfile) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF95D3C8)).border(2.dp, Color(0xFF7DC6B9), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Profile", tint = ChwFormSecondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ChwCaptureCard(
    title: String,
    photoBase64: String?,
    onClick: () -> Unit,
) {
    val bitmap = remember(photoBase64) {
        photoBase64?.let {
            runCatching {
                val bytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(130.dp).clip(CircleShape).background(Color(0xFFE9ECF1)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            if (bitmap != null) {
                Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Selected patient photo", modifier = Modifier.fillMaxSize().clip(CircleShape))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Capture", tint = ChwFormPrimary, modifier = Modifier.size(22.dp))
                    Text(text = "CAPTURE", color = ChwFormPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }

                Box(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 10.dp).size(34.dp).clip(CircleShape).background(Color(0xFF2F9EDD)), contentAlignment = Alignment.Center) {
                    Text(text = "+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }

        Text(text = title, color = ChwFormMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ChwSectionHeaderWithPill(
    title: String,
    pillText: String,
    pillColor: Color,
    pillTextColor: Color,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title.uppercase(), color = ChwFormText, fontSize = 14.sp, letterSpacing = 0.7.sp, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.clip(CircleShape).background(pillColor).padding(horizontal = 10.dp, vertical = 5.dp), contentAlignment = Alignment.Center) {
            Text(text = pillText, color = pillTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChwSegmentedChoice(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(CircleShape).background(Color(0xFFDEE2E8)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        options.forEach { option ->
            val active = option == selected
            Box(
                modifier = Modifier.weight(1f).clip(CircleShape).background(if (active) Color.White else Color.Transparent).clickable { onSelected(option) }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = option, color = if (active) ChwFormPrimary else ChwFormMuted, fontSize = 13.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ChwDropdownLikeField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ChwFormInput).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = if (value.isBlank()) placeholder else value, color = if (value.isBlank()) ChwFormMuted else ChwFormText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Select", tint = ChwFormMuted)
    }
}

@Composable
private fun ChwSlotChip(
    slot: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clip(CircleShape).background(if (selected) Color(0xFF2F9EDD) else Color(0xFFF4F5F7)).clickable(onClick = onClick).padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = slot, color = if (selected) Color.White else ChwFormText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ChwSmallVitalCard(
    label: String,
    value: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ChwFormSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = label, color = ChwFormMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp)
            ChwInputField(value = value, placeholder = "0", onValueChange = onValueChange)
        }
    }
}

@Composable
private fun ChwLabel(text: String) {
    Text(text = text, color = ChwFormMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
}

@Composable
private fun ChwInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder, color = ChwFormMuted.copy(alpha = 0.8f), fontSize = 13.sp) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 4,
        maxLines = if (singleLine) 1 else 6,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ChwFormInput,
            unfocusedContainerColor = ChwFormInput,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = ChwFormPrimary,
        ),
    )
}

private fun chwEncodeImageAsBase64(context: Context, uri: Uri): String? {
    return runCatching {
        val input: InputStream = context.contentResolver.openInputStream(uri) ?: return null
        input.use { stream ->
            val bytes = stream.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
    }.getOrNull()
}
