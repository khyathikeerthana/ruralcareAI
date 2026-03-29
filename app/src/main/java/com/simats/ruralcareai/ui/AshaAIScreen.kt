package com.simats.ruralcareai.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ruralcareai.network.AshaAiApiClient
import com.simats.ruralcareai.network.SymptomAnalysisDto
import com.simats.ruralcareai.network.SymptomAnalysisResult

private val AshaBackground = Color(0xFFF3F6FB)
private val AshaPrimary = Color(0xFF2D9CDB)
private val AshaPrimaryDark = Color(0xFF1A87D3)
private val AshaText = Color(0xFF0F1730)
private val AshaMuted = Color(0xFF60738F)

private enum class AshaFlowState {
    LANDING,
    ENTER_SYMPTOMS,
    ANALYZING,
    ASSESSMENT_RESULT,
    HEALTH_ADVICE,
    ASSESSMENT_SAVED,
    SUGGESTED_SPECIALISTS,
}

private enum class AnalysisStepState {
    COMPLETED,
    IN_PROGRESS,
    PENDING,
}

private data class AssessmentItem(
    val title: String,
    val completedOn: String,
    val status: String,
    val statusBg: Color,
    val statusText: Color,
    val icon: ImageVector,
)

private data class SymptomChip(
    val label: String,
    val icon: ImageVector,
)

private data class AnalysisStep(
    val title: String,
    val state: AnalysisStepState,
    val icon: ImageVector,
)

private data class AnalysisStepCardStyle(
    val background: Color,
    val border: Color,
    val titleColor: Color,
    val stateColor: Color,
    val stateText: String,
)

private data class MatchedSymptom(
    val title: String,
    val note: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBackground: Color,
)

private data class SpecialistDoctor(
    val specialty: String,
    val name: String,
    val experience: String,
    val availability: String,
    val distance: String,
    val availableToday: Boolean,
    val avatarGradientStart: Color,
    val avatarGradientEnd: Color,
)

@Composable
fun AshaAIScreen(
    patientName: String,
    patientId: Int? = null,
    onOpenHome: () -> Unit,
    onOpenConsults: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var flowState by remember { mutableStateOf(AshaFlowState.LANDING) }
    var symptomsInput by remember { mutableStateOf("") }
    var analysisRunId by remember { mutableStateOf(0) }
    var analysisInProgress by remember { mutableStateOf(false) }
    var analysisErrorMessage by remember { mutableStateOf<String?>(null) }
    var analysisResult by remember { mutableStateOf<SymptomAnalysisDto?>(null) }
    var lastAnalyzedSymptoms by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(analysisRunId) {
        if (analysisRunId <= 0) {
            return@LaunchedEffect
        }

        analysisInProgress = true
        analysisErrorMessage = null
        analysisResult = null

        when (val result = AshaAiApiClient.analyzeSymptoms(patientId = patientId, symptoms = symptomsInput)) {
            is SymptomAnalysisResult.Success -> {
                analysisResult = result.analysis
            }
            is SymptomAnalysisResult.Error -> {
                analysisErrorMessage = result.message
            }
        }

        analysisInProgress = false
    }

    when (flowState) {
        AshaFlowState.ENTER_SYMPTOMS -> {
            EnterSymptomsScreen(
                onBack = { flowState = AshaFlowState.LANDING },
                symptomsText = symptomsInput,
                onSymptomsTextChange = { symptomsInput = it },
                onStartAnalysis = {
                    val normalizedSymptoms = symptomsInput.trim()
                    if (analysisResult != null && lastAnalyzedSymptoms == normalizedSymptoms) {
                        flowState = AshaFlowState.ASSESSMENT_RESULT
                        return@EnterSymptomsScreen
                    }

                    lastAnalyzedSymptoms = normalizedSymptoms
                    analysisRunId += 1
                    flowState = AshaFlowState.ANALYZING
                },
                analysisErrorMessage = analysisErrorMessage,
                modifier = modifier,
            )
            return
        }

        AshaFlowState.ANALYZING -> {
            AnalyzingSymptomsScreen(
                onBack = { flowState = AshaFlowState.ENTER_SYMPTOMS },
                onViewResult = {
                    if (analysisResult != null) {
                        flowState = AshaFlowState.ASSESSMENT_RESULT
                    }
                },
                isAnalyzing = analysisInProgress,
                hasResult = analysisResult != null,
                errorMessage = analysisErrorMessage,
                modifier = modifier,
            )
            return
        }

        AshaFlowState.ASSESSMENT_RESULT -> {
            val resolvedAnalysis = analysisResult
            if (resolvedAnalysis == null) {
                AnalyzingSymptomsScreen(
                    onBack = { flowState = AshaFlowState.ENTER_SYMPTOMS },
                    onViewResult = {},
                    isAnalyzing = analysisInProgress,
                    hasResult = false,
                    errorMessage = analysisErrorMessage ?: "Analysis not ready yet.",
                    modifier = modifier,
                )
                return
            }

            AIAssessmentResultScreen(
                onBack = { flowState = AshaFlowState.ANALYZING },
                onGetAdvice = { flowState = AshaFlowState.HEALTH_ADVICE },
                onBookAppointment = onOpenConsults,
                analysis = resolvedAnalysis,
                modifier = modifier,
            )
            return
        }

        AshaFlowState.HEALTH_ADVICE -> {
            val resolvedAnalysis = analysisResult
            if (resolvedAnalysis == null) {
                AnalyzingSymptomsScreen(
                    onBack = { flowState = AshaFlowState.ENTER_SYMPTOMS },
                    onViewResult = {},
                    isAnalyzing = analysisInProgress,
                    hasResult = false,
                    errorMessage = analysisErrorMessage ?: "Analysis not ready yet.",
                    modifier = modifier,
                )
                return
            }

            AIHealthAdviceScreen(
                onBack = { flowState = AshaFlowState.ASSESSMENT_RESULT },
                onSaveToRecords = { flowState = AshaFlowState.ASSESSMENT_SAVED },
                onTalkToDoctorNow = { flowState = AshaFlowState.SUGGESTED_SPECIALISTS },
                analysis = resolvedAnalysis,
                modifier = modifier,
            )
            return
        }

        AshaFlowState.ASSESSMENT_SAVED -> {
            AssessmentSavedScreen(
                onBack = { flowState = AshaFlowState.HEALTH_ADVICE },
                onViewFullReport = { flowState = AshaFlowState.ASSESSMENT_RESULT },
                onBackToHome = onOpenHome,
                onShareWithDoctor = { flowState = AshaFlowState.SUGGESTED_SPECIALISTS },
                modifier = modifier,
            )
            return
        }

        AshaFlowState.SUGGESTED_SPECIALISTS -> {
            AISuggestedSpecialistsScreen(
                onBack = { flowState = AshaFlowState.HEALTH_ADVICE },
                onOpenHome = onOpenHome,
                onOpenConsults = onOpenConsults,
                onOpenProfile = onOpenProfile,
                onSelectDoctor = onOpenConsults,
                modifier = modifier,
            )
            return
        }

        AshaFlowState.LANDING -> Unit
    }

    val assessments = listOf(
        AssessmentItem(
            title = "General Wellness",
            completedOn = "Completed Oct 12, 2023",
            status = "Stable",
            statusBg = Color(0xFFD8F5DF),
            statusText = Color(0xFF16803C),
            icon = Icons.Filled.MedicalServices,
        ),
        AssessmentItem(
            title = "Heart Health Check",
            completedOn = "Completed Sep 28, 2023",
            status = "Follow-up",
            statusBg = Color(0xFFFFF0C7),
            statusText = Color(0xFFB96A00),
            icon = Icons.Filled.Favorite,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AshaHeader(patientName = patientName)
            }

            item {
                HeroAshaCard(onStartAssessment = { flowState = AshaFlowState.ENTER_SYMPTOMS })
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Recent Assessments",
                        color = AshaText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "See All",
                        color = AshaPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    assessments.forEach { item ->
                        AssessmentCard(item = item)
                    }
                }
            }
        }

        AshaBottomNav(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
            onOpenHome = onOpenHome,
            onOpenConsults = onOpenConsults,
            onOpenChat = onOpenChat,
            onOpenProfile = onOpenProfile,
            centerIcon = Icons.Filled.Bolt,
            homeSelected = true,
            consultsSelected = false,
        )
    }
}

@Composable
private fun EnterSymptomsScreen(
    onBack: () -> Unit,
    symptomsText: String,
    onSymptomsTextChange: (String) -> Unit,
    onStartAnalysis: () -> Unit,
    analysisErrorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val canStartAnalysis = symptomsText.trim().length >= 5

    val symptomRows = listOf(
        listOf(
            SymptomChip("Fever", Icons.Filled.Thermostat),
            SymptomChip("Cough", Icons.Filled.WaterDrop),
        ),
        listOf(
            SymptomChip("Headache", Icons.Filled.Psychology),
            SymptomChip("Stomach Pain", Icons.Filled.MedicalServices),
        ),
        listOf(
            SymptomChip("Fatigue", Icons.Filled.Info),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "Enter Your Symptoms",
                        color = AshaText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.size(42.dp))
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Describe how you\nare feeling",
                        color = AshaText,
                        fontSize = 24.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = buildAnnotatedString {
                            append("Our AI understands your symptoms in ")
                            withStyle(SpanStyle(color = AshaPrimary, fontWeight = FontWeight.SemiBold)) {
                                append("local languages")
                            }
                            append(" to provide better care.")
                        },
                        color = AshaMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(178.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    OutlinedTextField(
                        value = symptomsText,
                        onValueChange = onSymptomsTextChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        placeholder = {
                            Text(
                                text = "Type your symptoms here (e.g., I've had a dry cough for 3 days and a mild fever)...",
                                color = Color(0xFF8FA1B7),
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                    )
                }
            }

            if (!analysisErrorMessage.isNullOrBlank()) {
                item {
                    Text(
                        text = analysisErrorMessage,
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                Text(
                    text = "COMMON SYMPTOMS",
                    color = Color(0xFF8A99B0),
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(symptomRows.size) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    symptomRows[rowIndex].forEach { chip ->
                        SymptomChipCard(
                            chip = chip,
                            onClick = {
                                onSymptomsTextChange(
                                    appendSymptom(symptomsText, chip.label)
                                )
                            },
                        )
                    }
                }
            }
        }

        Button(
            onClick = onStartAnalysis,
            enabled = canStartAnalysis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(58.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AshaPrimary,
                disabledContainerColor = Color(0xFFB9D6EA),
                disabledContentColor = Color.White.copy(alpha = 0.9f),
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Start Analysis",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AnalyzingSymptomsScreen(
    onBack: () -> Unit,
    onViewResult: () -> Unit,
    isAnalyzing: Boolean,
    hasResult: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val steps = when {
        hasResult -> listOf(
            AnalysisStep(
                title = "Understanding Symptoms",
                state = AnalysisStepState.COMPLETED,
                icon = Icons.Filled.CheckCircle,
            ),
            AnalysisStep(
                title = "Checking Medical History",
                state = AnalysisStepState.COMPLETED,
                icon = Icons.Filled.Storage,
            ),
            AnalysisStep(
                title = "Generating Recommendations",
                state = AnalysisStepState.COMPLETED,
                icon = Icons.Filled.Description,
            ),
        )

        isAnalyzing -> listOf(
            AnalysisStep(
                title = "Understanding Symptoms",
                state = AnalysisStepState.COMPLETED,
                icon = Icons.Filled.CheckCircle,
            ),
            AnalysisStep(
                title = "Checking Medical History",
                state = AnalysisStepState.IN_PROGRESS,
                icon = Icons.Filled.Storage,
            ),
            AnalysisStep(
                title = "Generating Recommendations",
                state = AnalysisStepState.PENDING,
                icon = Icons.Filled.Description,
            ),
        )

        else -> listOf(
            AnalysisStep(
                title = "Understanding Symptoms",
                state = AnalysisStepState.COMPLETED,
                icon = Icons.Filled.CheckCircle,
            ),
            AnalysisStep(
                title = "Checking Medical History",
                state = AnalysisStepState.PENDING,
                icon = Icons.Filled.Storage,
            ),
            AnalysisStep(
                title = "Generating Recommendations",
                state = AnalysisStepState.PENDING,
                icon = Icons.Filled.Description,
            ),
        )
    }

    val statusTitle = when {
        hasResult -> "Analysis Complete"
        isAnalyzing -> "Asha AI is Analyzing..."
        else -> "Analysis Paused"
    }

    val statusSubtitle = when {
        hasResult -> "Your assessment is ready. Open the report to review recommendations."
        isAnalyzing -> "Processing your symptoms to provide personalized guidance."
        else -> errorMessage ?: "Analysis is waiting to continue."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 112.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "Analyzing Symptoms",
                        color = AshaText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.size(42.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(168.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF79B8EA), AshaPrimary))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(66.dp),
                        )
                    }

                    Text(
                        text = statusTitle,
                        color = AshaText,
                        fontSize = 30.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = statusSubtitle,
                        color = AshaMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.8f),
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C2C2)),
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFB42318),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            items(steps.size) { index ->
                AnalysisStepCard(step = steps[index])
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    val bars = listOf(16.dp, 28.dp, 40.dp, 52.dp, 34.dp, 22.dp, 36.dp, 46.dp, 24.dp)
                    bars.forEachIndexed { idx, h ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width(5.dp)
                                .height(h)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (idx == 3) AshaPrimary else AshaPrimary.copy(
                                        alpha = when {
                                            h >= 40.dp -> 0.7f
                                            h >= 28.dp -> 0.45f
                                            else -> 0.25f
                                        }
                                    )
                                )
                        )
                    }
                }
            }
        }

        Button(
            onClick = onViewResult,
            enabled = hasResult,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AshaPrimary,
                disabledContainerColor = Color(0xFFB9D6EA),
                disabledContentColor = Color.White.copy(alpha = 0.9f),
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (hasResult) "View Assessment Result" else if (isAnalyzing) "Analyzing..." else "Waiting for Result",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun AIAssessmentResultScreen(
    onBack: () -> Unit,
    onGetAdvice: () -> Unit,
    onBookAppointment: () -> Unit,
    analysis: SymptomAnalysisDto,
    modifier: Modifier = Modifier,
) {
    val matchedSymptoms = analysis.matchedSymptoms
        .ifEmpty { listOf("Reported symptoms") }
        .take(6)
        .mapIndexed { index, symptom ->
            val icon = when (index % 3) {
                0 -> Icons.Filled.Thermostat
                1 -> Icons.Filled.WaterDrop
                else -> Icons.Filled.MedicalServices
            }
            val iconTint = when (index % 3) {
                0 -> Color(0xFFE53935)
                1 -> AshaPrimary
                else -> Color(0xFFEF7D32)
            }
            val iconBg = when (index % 3) {
                0 -> Color(0xFFFFECE9)
                1 -> Color(0xFFE8F4FF)
                else -> Color(0xFFFFF1E4)
            }

            MatchedSymptom(
                title = symptom,
                note = "Matched from your symptom report",
                icon = icon,
                iconTint = iconTint,
                iconBackground = iconBg,
            )
        }

    val triageZone = when {
        analysis.severityScore < 35 -> "GREEN ZONE"
        analysis.severityScore < 70 -> "YELLOW ZONE"
        else -> "RED ZONE"
    }

    val triageZoneColor = when (triageZone) {
        "GREEN ZONE" -> Color(0xFF16A34A)
        "YELLOW ZONE" -> Color(0xFFDD9D08)
        else -> Color(0xFFDC2626)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 206.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "AI Assessment Result",
                        color = AshaText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(onClick = {}),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = AshaText,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = AshaPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "NABHA, PUNJAB",
                        color = Color(0xFF4E6484),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF2CC)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE2A80D),
                            modifier = Modifier.size(42.dp),
                        )
                    }

                    Text(
                        text = analysis.severityLabel,
                        color = AshaText,
                        fontSize = 42.sp / 1.7f,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = analysis.triageNote,
                        color = AshaMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.86f),
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Triage Level",
                                color = AshaText,
                                fontSize = 22.sp / 1.35f,
                                fontWeight = FontWeight.Bold,
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFF2C9))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${analysis.severityScore} / 100",
                                    color = triageZoneColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        ) {
                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4DB1D2),
                                        Color(0xFFF0C342),
                                        Color(0xFFF38A38),
                                        Color(0xFFE0A8A8),
                                    )
                                ),
                                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                            )

                            val markerX = size.width * (analysis.severityScore.coerceIn(0, 100) / 100f)
                            drawLine(
                                color = Color(0xFF1F2937),
                                start = Offset(markerX, 0f),
                                end = Offset(markerX, size.height),
                                strokeWidth = 4f,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "• $triageZone",
                                color = triageZoneColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = analysis.triageNote,
                                color = Color(0xFF90A1B9),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Potential Causes",
                            color = AshaText,
                            fontSize = 22.sp / 1.35f,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = analysis.possibleCauses.joinToString("\n") { "• $it" },
                            color = Color(0xFF334155),
                            fontSize = 16.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.Medium,
                        )

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0DEEE)),
                        ) {
                            Text(
                                text = "\"Commonly observed in Nabha region during current harvest season due to increased air particulates.\"",
                                color = Color(0xFF2F7CC5),
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Symptoms Matching",
                    color = AshaText,
                    fontSize = 22.sp / 1.35f,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            items(matchedSymptoms.size) { index ->
                SymptomMatchCard(item = matchedSymptoms[index])
            }

            item {
                Text(
                    text = analysis.disclaimer,
                    color = Color(0xFF8A99B0),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onGetAdvice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AshaPrimary),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Get Advice",
                        color = Color.White,
                        fontSize = 19.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(
                onClick = onBookAppointment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD2DAE6),
                    contentColor = Color(0xFF0F1A2F),
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                    )
                    Text(
                        text = "Book Appointment",
                        fontSize = 19.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun AIHealthAdviceScreen(
    onBack: () -> Unit,
    onSaveToRecords: () -> Unit,
    onTalkToDoctorNow: () -> Unit,
    analysis: SymptomAnalysisDto,
    modifier: Modifier = Modifier,
) {
    val immediateCare = analysis.immediateActions.ifEmpty {
        listOf("Stay hydrated", "Take rest", "Monitor symptoms closely")
    }
    val monitoring = analysis.homeCare.ifEmpty {
        listOf("Track symptom progression", "Seek doctor advice if symptoms persist")
    }
    val redFlags = analysis.redFlags.ifEmpty {
        listOf("Severe breathing difficulty", "Chest pain", "Confusion or fainting")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 168.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "AI Health Advice",
                        color = AshaText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = AshaText,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEAF5FF),
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1E6FA)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = AshaPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                            Text(
                                text = "Personalized guidance for your symptoms",
                                color = AshaText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "These recommendations are based on your symptom report analyzed by ${analysis.provider}.",
                            color = Color(0xFF4A607D),
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item {
                AdviceSectionCard(
                    title = "Immediate Care",
                    icon = Icons.Filled.Favorite,
                    accent = Color(0xFF0EA5E9),
                    tips = immediateCare,
                )
            }

            item {
                AdviceSectionCard(
                    title = "Monitor at Home",
                    icon = Icons.Filled.Info,
                    accent = Color(0xFF0EA5E9),
                    tips = monitoring,
                )
            }

            item {
                AdviceSectionCard(
                    title = "Seek Urgent Care If",
                    icon = Icons.Filled.Warning,
                    accent = Color(0xFFDC2626),
                    tips = redFlags,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E0)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0DEAD)),
                ) {
                    Text(
                        text = analysis.disclaimer,
                        color = Color(0xFF7A5A00),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onSaveToRecords,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3D4F69),
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storage,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                    )
                    Text(
                        text = "Save to Records",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Button(
                onClick = onTalkToDoctorNow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AshaPrimary),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp),
                    )
                    Text(
                        text = "Talk to a Doctor Now",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun AssessmentSavedScreen(
    onBack: () -> Unit,
    onViewFullReport: () -> Unit,
    onBackToHome: () -> Unit,
    onShareWithDoctor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 122.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "Assessment Saved",
                        color = AshaText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.size(42.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(136.dp)
                            .clip(CircleShape)
                            .background(AshaPrimary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(AshaPrimary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    }

                    Text(
                        text = "Report Saved\nSuccessfully",
                        color = AshaText,
                        fontSize = 44.sp / 1.7f,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Your AI-powered health assessment has been safely stored in your medical records.",
                        color = Color(0xFF64778F),
                        fontSize = 17.sp / 1.05f,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.86f),
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onViewFullReport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AshaPrimary),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(21.dp),
                            )
                            Text(
                                text = "View Full Report",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Button(
                        onClick = onBackToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF3D4F69),
                        ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null,
                                modifier = Modifier.size(21.dp),
                            )
                            Text(
                                text = "Back to Home",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ASSESSMENT SUMMARY",
                    color = Color(0xFF8A99B0),
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AshaPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = AshaPrimary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "RURALCAREAI PORTAL",
                                    color = AshaPrimary,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "AI Health Assessment",
                                    color = AshaText,
                                    fontSize = 21.sp / 1.2f,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFF0F2F6))
                        )

                        SummaryInfoRow(label = "Report ID", value = "AI-RURAL-2024-0042")
                        SummaryInfoRow(label = "Timestamp", value = "May 24, 2024 • 10:15")
                        SummaryInfoRow(label = "Location", value = "Village Center, Rural")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Status",
                                color = Color(0xFF8A99B0),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFE6F7EF))
                                    .border(1.dp, Color(0xFFD0F0E0), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = "COMPLETED",
                                    color = Color(0xFF0F9D63),
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF5F8FC),
                                contentColor = Color(0xFF50627C),
                            ),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Storage,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = "Download PDF Report",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onShareWithDoctor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                .height(66.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEAF4FC),
                contentColor = AshaPrimary,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(AshaPrimary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp),
                        )
                    }

                    Text(
                        text = "Share report with Doctor",
                        fontSize = 18.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
private fun SummaryInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFF8A99B0),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            color = AshaText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AISuggestedSpecialistsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenConsults: () -> Unit,
    onOpenProfile: () -> Unit,
    onSelectDoctor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val doctors = listOf(
        SpecialistDoctor(
            specialty = "CARDIOLOGIST",
            name = "Dr. Sarah Jenkins",
            experience = "12 Years Experience",
            availability = "Available Today",
            distance = "1.2 km",
            availableToday = true,
            avatarGradientStart = Color(0xFFB7CED2),
            avatarGradientEnd = Color(0xFF536D73),
        ),
        SpecialistDoctor(
            specialty = "PULMONOLOGIST",
            name = "Dr. Michael Chen",
            experience = "8 Years Experience",
            availability = "Available tomorrow",
            distance = "3.8 km",
            availableToday = false,
            avatarGradientStart = Color(0xFF2F7A80),
            avatarGradientEnd = Color(0xFF1B4550),
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(AshaBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 128.dp),
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
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    Text(
                        text = "AI Suggested Specialists",
                        color = AshaText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = AshaText,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF4EE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAEBDD)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBCE8DB)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF0EAD72),
                                modifier = Modifier.size(36.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "AI ANALYSIS RESULT",
                                color = Color(0xFF0EAD72),
                                fontSize = 13.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "AI Diagnosis: Mild Chest Congestion",
                                color = AshaText,
                                fontSize = 22.sp / 1.25f,
                                lineHeight = 29.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .background(AshaPrimary)
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "All Specialists",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE6ECF5), RoundedCornerShape(28.dp))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Available Today v",
                            color = Color(0xFF6B7688),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE6ECF5), RoundedCornerShape(28.dp))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Near You",
                            color = Color(0xFF6B7688),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            items(doctors.size) { index ->
                SpecialistDoctorCard(
                    doctor = doctors[index],
                    onSelectDoctor = onSelectDoctor,
                )
            }
        }

        SpecialistsBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenHome = onOpenHome,
            onOpenConsults = onOpenConsults,
            onOpenProfile = onOpenProfile,
        )
    }
}

@Composable
private fun SpecialistDoctorCard(
    doctor: SpecialistDoctor,
    onSelectDoctor: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val initials = doctor.name
                    .split(" ")
                    .takeLast(2)
                    .mapNotNull { part -> part.firstOrNull()?.toString() }
                    .joinToString("")

                Box(modifier = Modifier.size(width = 96.dp, height = 98.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(doctor.avatarGradientStart, doctor.avatarGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    if (doctor.availableToday) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(2.dp, Color.White, CircleShape),
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = doctor.specialty,
                        color = AshaPrimary,
                        fontSize = 14.sp,
                        letterSpacing = 1.6.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = doctor.name,
                        color = AshaText,
                        fontSize = 21.sp / 1.15f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = doctor.experience,
                        color = Color(0xFF6E7E96),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(if (doctor.availableToday) Color(0xFF10B981) else Color(0xFFD1D5DB)),
                            )
                            Text(
                                text = doctor.availability,
                                color = if (doctor.availableToday) Color(0xFF10B981) else Color(0xFF6B7280),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF6E7E96),
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = doctor.distance,
                                color = Color(0xFF6E7E96),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onSelectDoctor,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AshaPrimary),
                ) {
                    Text(
                        text = "Select Doctor",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF7F9FD))
                        .border(1.dp, Color(0xFFE7ECF4), CircleShape)
                        .clickable(onClick = {}),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = null,
                        tint = Color(0xFF7B8798),
                        modifier = Modifier.size(24.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF7F9FD))
                        .border(1.dp, Color(0xFFE7ECF4), CircleShape)
                        .clickable(onClick = {}),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color(0xFF7B8798),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialistsBottomNav(
    modifier: Modifier = Modifier,
    onOpenHome: () -> Unit,
    onOpenConsults: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF0F3F8), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 28.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpecialistsBottomNavItem(
            icon = Icons.Filled.Home,
            label = "Home",
            selected = true,
            onClick = onOpenHome,
        )
        SpecialistsBottomNavItem(
            icon = Icons.Outlined.CalendarToday,
            label = "Calendar",
            selected = false,
            onClick = onOpenConsults,
        )
        SpecialistsBottomNavItem(
            icon = Icons.Outlined.Person,
            label = "Profile",
            selected = false,
            onClick = onOpenProfile,
        )
    }
}

@Composable
private fun SpecialistsBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) AshaPrimary else Color(0xFFB2BAC8)

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun SymptomMatchCard(item: MatchedSymptom) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(item.iconBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.title,
                        color = AshaText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = item.note,
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AshaPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun AdviceSectionCard(
    title: String,
    icon: ImageVector,
    accent: Color,
    tips: List<String>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6EDF7)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = title,
                    color = AshaText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            tips.forEach { tip ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accent),
                    )
                    Text(
                        text = tip,
                        color = Color(0xFF41556F),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisStepCard(step: AnalysisStep) {
    val style = when (step.state) {
        AnalysisStepState.COMPLETED -> AnalysisStepCardStyle(
            background = Color.White,
            border = Color(0xFFE7EDF5),
            titleColor = AshaText,
            stateColor = Color(0xFF8A99B0),
            stateText = "COMPLETED",
        )

        AnalysisStepState.IN_PROGRESS -> AnalysisStepCardStyle(
            background = Color.White,
            border = Color(0xFFB6DCF5),
            titleColor = AshaText,
            stateColor = AshaPrimary,
            stateText = "IN PROGRESS",
        )

        AnalysisStepState.PENDING -> AnalysisStepCardStyle(
            background = Color.White,
            border = Color(0xFFE9EEF4),
            titleColor = Color(0xFF7B8798),
            stateColor = Color(0xFFADB8C8),
            stateText = "PENDING",
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = style.background),
        border = androidx.compose.foundation.BorderStroke(1.dp, style.border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            when (step.state) {
                                AnalysisStepState.COMPLETED -> Color(0xFFD8F5DF)
                                AnalysisStepState.IN_PROGRESS -> Color(0xFFE8F4FE)
                                AnalysisStepState.PENDING -> Color(0xFFF0F4F8)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = when (step.state) {
                            AnalysisStepState.COMPLETED -> Color(0xFF16A34A)
                            AnalysisStepState.IN_PROGRESS -> AshaPrimary
                            AnalysisStepState.PENDING -> Color(0xFFB0BBC9)
                        },
                        modifier = Modifier.size(26.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = step.title,
                        color = style.titleColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = style.stateText,
                        color = style.stateColor,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (step.state == AnalysisStepState.IN_PROGRESS) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    color = AshaPrimary,
                    trackColor = AshaPrimary.copy(alpha = 0.2f),
                    strokeWidth = 2.6.dp,
                )
            }
        }
    }
}

@Composable
private fun SymptomChipCard(
    chip: SymptomChip,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEAF0F7), RoundedCornerShape(18.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = chip.icon,
            contentDescription = chip.label,
            tint = AshaPrimary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = chip.label,
            color = AshaText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun appendSymptom(current: String, symptom: String): String {
    val trimmed = current.trim()
    if (trimmed.isEmpty()) {
        return symptom
    }

    val existing = trimmed.lowercase()
    if (existing.contains(symptom.lowercase())) {
        return trimmed
    }

    return "$trimmed, $symptom"
}

@Composable
private fun AshaHeader(patientName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFC39A))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Good morning,",
                    color = AshaMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = patientName,
                    color = AshaText,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = Color(0xFF56627A),
                modifier = Modifier.size(22.dp),
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .offset(x = 9.dp, y = (-9).dp)
                    .clip(CircleShape)
                    .background(AshaPrimary),
            )
        }
    }
}

@Composable
private fun HeroAshaCard(onStartAssessment: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(30.dp)),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A636E),
                                Color(0xFF0D242C),
                                Color(0xFF060E12),
                            ),
                            radius = 680f,
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerY = size.height * 0.56f
                    val glow = Color(0xFF42E8FF)
                    drawLine(
                        color = glow.copy(alpha = 0.58f),
                        start = Offset(size.width * 0.14f, centerY),
                        end = Offset(size.width * 0.86f, centerY),
                        strokeWidth = 2.6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 14f), 0f),
                    )
                    drawCircle(
                        color = glow.copy(alpha = 0.9f),
                        radius = 6f,
                        center = Offset(size.width * 0.14f, centerY),
                    )
                    drawCircle(
                        color = glow.copy(alpha = 0.9f),
                        radius = 6f,
                        center = Offset(size.width * 0.86f, centerY),
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                        .size(width = 38.dp, height = 100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF4B98A2), RoundedCornerShape(10.dp)),
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF47DFFF).copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AshaPrimary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MedicalServices,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 12.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AshaPrimary)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "MEET ASHA",
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Your AI Health Guide",
                    color = AshaText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Asha can help you understand your symptoms and provide personalized guidance on your wellness journey.",
                    color = AshaMuted,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )

                Button(
                    onClick = onStartAssessment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AshaPrimary),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Start Health Assessment",
                            color = Color.White,
                            fontSize = 18.sp,
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
}

@Composable
private fun AssessmentCard(item: AssessmentItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF3FB)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = AshaPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.title,
                        color = AshaText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.completedOn,
                        color = AshaMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(item.statusBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.status,
                        color = item.statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFC6D0DE),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun AshaBottomNav(
    modifier: Modifier = Modifier,
    onOpenHome: () -> Unit,
    onOpenConsults: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    centerIcon: ImageVector,
    homeSelected: Boolean,
    consultsSelected: Boolean,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(1.dp, Color.White.copy(alpha = 0.65f), RoundedCornerShape(32.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AshaBottomItem(
                icon = Icons.Filled.Home,
                label = "Home",
                selected = homeSelected,
                onClick = onOpenHome,
            )
            AshaBottomItem(
                icon = Icons.Outlined.CalendarToday,
                label = "Consults",
                selected = consultsSelected,
                onClick = onOpenConsults,
            )

            Spacer(modifier = Modifier.width(56.dp))

            AshaBottomItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "Chat",
                selected = false,
                onClick = onOpenChat,
            )
            AshaBottomItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                selected = false,
                onClick = onOpenProfile,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AshaPrimary, AshaPrimaryDark))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = centerIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Text(
                text = "Asha\nAI",
                color = AshaPrimary,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AshaBottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (selected) AshaPrimary else Color(0xFF94A3B8)

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(23.dp),
        )
        Text(
            text = label,
            color = tint,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}
