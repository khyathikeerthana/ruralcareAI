package com.simats.ruralcareai.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class EditRecordStatus {
    ONGOING,
    RECOVERED,
    URGENT,
}

private val EditRecordBackground = Color(0xFFF2F4F8)
private val EditRecordSurface = Color(0xFFFFFFFF)
private val EditRecordText = Color(0xFF131A22)
private val EditRecordMuted = Color(0xFF6F7A89)
private val EditRecordPrimary = Color(0xFF0B6FA2)
private val EditRecordOutline = Color(0xFFD6DEE9)
private val EditRecordInputBg = Color(0xFFE4E8EE)

@Composable
fun EditMedicalRecordScreen(
    initialDetails: ConditionDetailsUi,
    onBack: () -> Unit,
    onSave: (ConditionDetailsUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    var conditionName by rememberSaveable(initialDetails.conditionName) { mutableStateOf(initialDetails.conditionName) }
    var diagnosisDate by rememberSaveable(initialDetails.diagnosedDate) { mutableStateOf(initialDetails.diagnosedDate) }
    var description by rememberSaveable(initialDetails.notes) { mutableStateOf(initialDetails.notes) }
    var selectedStatus by rememberSaveable(initialDetails.statusLabel) {
        mutableStateOf(
            when (initialDetails.statusLabel.trim().lowercase()) {
                "urgent" -> EditRecordStatus.URGENT.name
                "recovered" -> EditRecordStatus.RECOVERED.name
                else -> EditRecordStatus.ONGOING.name
            },
        )
    }
    var aiSyncEnabled by rememberSaveable { mutableStateOf(true) }

    val currentStatus = EditRecordStatus.valueOf(selectedStatus)
    val fieldShape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EditRecordBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isCompact) 10.dp else 12.dp,
                bottom = 114.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        EditTopAction(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                        )

                        Text(
                            text = "Edit Record",
                            color = EditRecordText,
                            fontSize = if (isCompact) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "Update",
                        color = Color(0xFF1A9AE8),
                        fontSize = if (isCompact) 18.sp / 1.2f else 19.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onSave(
                                initialDetails.copy(
                                    conditionName = conditionName.trim().ifBlank { initialDetails.conditionName },
                                    diagnosedDate = diagnosisDate.trim().ifBlank { initialDetails.diagnosedDate },
                                    statusLabel = when (currentStatus) {
                                        EditRecordStatus.ONGOING -> "Ongoing"
                                        EditRecordStatus.RECOVERED -> "Recovered"
                                        EditRecordStatus.URGENT -> "Urgent"
                                    },
                                    notes = description.trim().ifBlank { initialDetails.notes },
                                ),
                            )
                        },
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "UPDATE ENTRY",
                        color = EditRecordPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = "Patient Health Log",
                        color = EditRecordText,
                        fontSize = if (isCompact) 28.sp / 1.35f else 30.sp / 1.35f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Update the clinical details below for RuralCareAI synchronization.",
                        color = EditRecordMuted,
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                EditSectionCard(title = "CORE CLINICAL DATA") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        EditFieldLabel(text = "Condition Name")
                        OutlinedTextField(
                            value = conditionName,
                            onValueChange = { conditionName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "e.g. Chronic Hypertension",
                                    color = EditRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            singleLine = true,
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = EditRecordInputBg,
                                unfocusedContainerColor = EditRecordInputBg,
                                focusedBorderColor = EditRecordOutline,
                                unfocusedBorderColor = EditRecordOutline,
                                cursorColor = EditRecordPrimary,
                            ),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        EditFieldLabel(text = "Diagnosis Date")
                        OutlinedTextField(
                            value = diagnosisDate,
                            onValueChange = { diagnosisDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "mm/dd/yyyy",
                                    color = EditRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = "Select date",
                                    tint = EditRecordText,
                                    modifier = Modifier.size(19.dp),
                                )
                            },
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = EditRecordInputBg,
                                unfocusedContainerColor = EditRecordInputBg,
                                focusedBorderColor = EditRecordOutline,
                                unfocusedBorderColor = EditRecordOutline,
                                cursorColor = EditRecordPrimary,
                            ),
                        )
                    }
                }
            }

            item {
                EditSectionCard(title = "PATIENT STATUS") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFE0E5EC))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        EditStatusChoice(
                            text = "Ongoing",
                            selected = currentStatus == EditRecordStatus.ONGOING,
                            onClick = { selectedStatus = EditRecordStatus.ONGOING.name },
                            modifier = Modifier.weight(1f),
                        )
                        EditStatusChoice(
                            text = "Recovered",
                            selected = currentStatus == EditRecordStatus.RECOVERED,
                            onClick = { selectedStatus = EditRecordStatus.RECOVERED.name },
                            modifier = Modifier.weight(1f),
                        )
                        EditStatusChoice(
                            text = "Urgent",
                            selected = currentStatus == EditRecordStatus.URGENT,
                            onClick = { selectedStatus = EditRecordStatus.URGENT.name },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item {
                EditSectionCard(title = "CLINICAL OBSERVATIONS") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        EditFieldLabel(text = "Description")
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Detail the symptoms, observations and recommended steps...",
                                    color = EditRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            minLines = 4,
                            maxLines = 5,
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = EditRecordInputBg,
                                unfocusedContainerColor = EditRecordInputBg,
                                focusedBorderColor = EditRecordOutline,
                                unfocusedBorderColor = EditRecordOutline,
                                cursorColor = EditRecordPrimary,
                            ),
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDDEFFD)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCFE4F4)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(EditRecordPrimary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "AI sync",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            Switch(
                                checked = aiSyncEnabled,
                                onCheckedChange = { aiSyncEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EditRecordPrimary,
                                    uncheckedTrackColor = Color(0xFFC7D0DC),
                                ),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "AI Sync Enabled",
                                color = Color(0xFF0E3252),
                                fontSize = if (isCompact) 18.sp / 1.15f else 19.sp / 1.15f,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Automatically cross-reference with global clinical guidelines.",
                                color = Color(0xFF32536E),
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = EditRecordSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditRecordOutline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFE7ECF2)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Privacy",
                                tint = EditRecordText,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Privacy First",
                                color = EditRecordText,
                                fontSize = if (isCompact) 18.sp / 1.15f else 19.sp / 1.15f,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Data is encrypted end-to-end and HIPAA compliant.",
                                color = EditRecordMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xF2F2F4F8))
                .padding(horizontal = horizontalPadding, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0E74A8), Color(0xFF2EA2E3))))
                    .clickable {
                        onSave(
                            initialDetails.copy(
                                conditionName = conditionName.trim().ifBlank { initialDetails.conditionName },
                                diagnosedDate = diagnosisDate.trim().ifBlank { initialDetails.diagnosedDate },
                                statusLabel = when (currentStatus) {
                                    EditRecordStatus.ONGOING -> "Ongoing"
                                    EditRecordStatus.RECOVERED -> "Recovered"
                                    EditRecordStatus.URGENT -> "Urgent"
                                },
                                notes = description.trim().ifBlank { initialDetails.notes },
                            ),
                        )
                    }
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Confirm and Update Record",
                    color = Color.White,
                    fontSize = if (isCompact) 30.sp / 1.7f else 31.sp / 1.7f,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun EditTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, EditRecordOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = EditRecordPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun EditSectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = EditRecordSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                color = EditRecordMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
            )
            content()
        }
    }
}

@Composable
private fun EditFieldLabel(text: String) {
    Text(
        text = text,
        color = EditRecordText,
        fontSize = 16.sp / 1.15f,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun EditStatusChoice(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) EditRecordPrimary else EditRecordText.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}