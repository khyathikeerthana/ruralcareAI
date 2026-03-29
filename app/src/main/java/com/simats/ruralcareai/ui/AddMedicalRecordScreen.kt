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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
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

private enum class AddRecordStatus {
    ONGOING,
    RECOVERED,
    URGENT,
}

private val AddRecordBackground = Color(0xFFF2F4F8)
private val AddRecordSurface = Color(0xFFFFFFFF)
private val AddRecordText = Color(0xFF131A22)
private val AddRecordMuted = Color(0xFF6F7A89)
private val AddRecordPrimary = Color(0xFF0B6FA2)
private val AddRecordOutline = Color(0xFFD6DEE9)
private val AddRecordInputBg = Color(0xFFE4E8EE)

@Composable
fun AddMedicalRecordScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    var conditionName by rememberSaveable { mutableStateOf("") }
    var diagnosisDate by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedStatus by rememberSaveable { mutableStateOf(AddRecordStatus.ONGOING.name) }
    var aiSyncEnabled by rememberSaveable { mutableStateOf(true) }

    val currentStatus = AddRecordStatus.valueOf(selectedStatus)
    val fieldShape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AddRecordBackground)
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
                        AddTopAction(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                        )

                        Text(
                            text = "Add Record",
                            color = AddRecordText,
                            fontSize = if (isCompact) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "Save",
                        color = Color(0xFF1A9AE8),
                        fontSize = if (isCompact) 18.sp / 1.2f else 19.sp / 1.2f,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = onSave),
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NEW ENTRY",
                        color = AddRecordPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = "Patient Health Log",
                        color = AddRecordText,
                        fontSize = if (isCompact) 28.sp / 1.35f else 30.sp / 1.35f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Fill in the clinical details below for RuralCareAI synchronization.",
                        color = AddRecordMuted,
                        fontSize = if (isCompact) 14.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                AddSectionCard(title = "CORE CLINICAL DATA") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FieldLabel(text = "Condition Name")
                        OutlinedTextField(
                            value = conditionName,
                            onValueChange = { conditionName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "e.g. Chronic Hypertension",
                                    color = AddRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            singleLine = true,
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AddRecordInputBg,
                                unfocusedContainerColor = AddRecordInputBg,
                                focusedBorderColor = AddRecordOutline,
                                unfocusedBorderColor = AddRecordOutline,
                                cursorColor = AddRecordPrimary,
                            ),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FieldLabel(text = "Diagnosis Date")
                        OutlinedTextField(
                            value = diagnosisDate,
                            onValueChange = { diagnosisDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "mm/dd/yyyy",
                                    color = AddRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            singleLine = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = "Select date",
                                    tint = AddRecordText,
                                    modifier = Modifier.size(19.dp),
                                )
                            },
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AddRecordInputBg,
                                unfocusedContainerColor = AddRecordInputBg,
                                focusedBorderColor = AddRecordOutline,
                                unfocusedBorderColor = AddRecordOutline,
                                cursorColor = AddRecordPrimary,
                            ),
                        )
                    }
                }
            }

            item {
                AddSectionCard(title = "PATIENT STATUS") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFE0E5EC))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        StatusChoice(
                            text = "Ongoing",
                            selected = currentStatus == AddRecordStatus.ONGOING,
                            onClick = { selectedStatus = AddRecordStatus.ONGOING.name },
                            modifier = Modifier.weight(1f),
                        )
                        StatusChoice(
                            text = "Recovered",
                            selected = currentStatus == AddRecordStatus.RECOVERED,
                            onClick = { selectedStatus = AddRecordStatus.RECOVERED.name },
                            modifier = Modifier.weight(1f),
                        )
                        StatusChoice(
                            text = "Urgent",
                            selected = currentStatus == AddRecordStatus.URGENT,
                            onClick = { selectedStatus = AddRecordStatus.URGENT.name },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            item {
                AddSectionCard(title = "CLINICAL OBSERVATIONS") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FieldLabel(text = "Description")
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Detail the symptoms, observations and recommended steps...",
                                    color = AddRecordMuted,
                                    fontSize = 16.sp,
                                )
                            },
                            minLines = 4,
                            maxLines = 5,
                            shape = fieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AddRecordInputBg,
                                unfocusedContainerColor = AddRecordInputBg,
                                focusedBorderColor = AddRecordOutline,
                                unfocusedBorderColor = AddRecordOutline,
                                cursorColor = AddRecordPrimary,
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
                                    .background(AddRecordPrimary),
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
                                    checkedTrackColor = AddRecordPrimary,
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
                    colors = CardDefaults.cardColors(containerColor = AddRecordSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AddRecordOutline),
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
                                tint = AddRecordText,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Privacy First",
                                color = AddRecordText,
                                fontSize = if (isCompact) 18.sp / 1.15f else 19.sp / 1.15f,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Data is encrypted end-to-end and HIPAA compliant.",
                                color = AddRecordMuted,
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
                    .clickable(onClick = onSave)
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Confirm and Save Record",
                    color = Color.White,
                    fontSize = if (isCompact) 30.sp / 1.7f else 31.sp / 1.7f,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun AddTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, AddRecordOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = AddRecordPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun AddSectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AddRecordSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                color = AddRecordMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
            )
            content()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = AddRecordText,
        fontSize = 16.sp / 1.15f,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun StatusChoice(
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
            color = if (selected) AddRecordPrimary else AddRecordText.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
