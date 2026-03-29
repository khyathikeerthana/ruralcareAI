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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

data class ConditionDetailsUi(
    val conditionName: String,
    val statusLabel: String,
    val diagnosedDate: String,
    val symptoms: List<String>,
    val treatmentName: String,
    val treatmentDose: String,
    val notes: String,
    val nextCheckupDate: String,
)

private val DetailsBackground = Color(0xFFF2F4F8)
private val DetailsSurface = Color(0xFFFFFFFF)
private val DetailsText = Color(0xFF131A22)
private val DetailsMuted = Color(0xFF6F7A89)
private val DetailsPrimary = Color(0xFF0B6FA2)
private val DetailsOutline = Color(0xFFD6DEE9)

@Composable
fun ConditionDetailsScreen(
    details: ConditionDetailsUi,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    var showDeletePrompt by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DetailsBackground)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isCompact) 10.dp else 12.dp,
                bottom = 112.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DetailsTopAction(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                        )

                        Text(
                            text = "Condition Details",
                            color = DetailsText,
                            fontSize = if (isCompact) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DetailsSurface),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(120.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0x142D9CDB)),
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "MEDICAL RECORD",
                                        color = DetailsPrimary.copy(alpha = 0.75f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                    )
                                    Text(
                                        text = details.conditionName,
                                        color = DetailsText,
                                        fontSize = if (isCompact) 24.sp else 26.sp,
                                        lineHeight = if (isCompact) 30.sp else 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }

                                StatusBadge(status = details.statusLabel)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                                    .background(DetailsOutline)
                                    .padding(top = 1.dp),
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(Color(0xFFEAF1F9)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarToday,
                                        contentDescription = "Diagnosed date",
                                        tint = DetailsPrimary,
                                        modifier = Modifier.size(19.dp),
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                    Text(
                                        text = "DIAGNOSED DATE",
                                        color = DetailsMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                    )
                                    Text(
                                        text = details.diagnosedDate,
                                        color = DetailsText,
                                        fontSize = if (isCompact) 18.sp else 19.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                DetailsSectionCard(
                    icon = Icons.Filled.Healing,
                    title = "Symptoms",
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        details.symptoms.forEach { symptom ->
                            Box(
                                modifier = Modifier
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE6EBF1))
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                            ) {
                                Text(
                                    text = symptom,
                                    color = DetailsText.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }

            item {
                DetailsSectionCard(
                    icon = Icons.Filled.Medication,
                    title = "Treatment History",
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F1F8)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD7E4F1)),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = "ACTIVE PRESCRIPTION",
                                color = DetailsPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            )
                            Text(
                                text = details.treatmentName,
                                color = DetailsText,
                                fontSize = if (isCompact) 18.sp else 19.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = details.treatmentDose,
                                color = DetailsText.copy(alpha = 0.85f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            item {
                DetailsSectionCard(
                    icon = Icons.Filled.Summarize,
                    title = "Notes & Description",
                ) {
                    Text(
                        text = details.notes,
                        color = DetailsText.copy(alpha = 0.86f),
                        fontSize = if (isCompact) 16.sp / 1.12f else 17.sp / 1.12f,
                        lineHeight = if (isCompact) 27.sp / 1.12f else 28.sp / 1.12f,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF0E74A8), Color(0xFF2EA2E3))))
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Next Checkup Recommendation",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = details.nextCheckupDate,
                            color = Color.White,
                            fontSize = if (isCompact) 32.sp / 1.5f else 34.sp / 1.5f,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Next checkup",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xF2F2F4F8))
                .padding(horizontal = horizontalPadding, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFF2D9CDB))
                    .clickable(onClick = onEdit)
                    .padding(vertical = 13.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit record",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp),
                )
                Text(
                    text = "  Edit Record",
                    color = Color.White,
                    fontSize = if (isCompact) 16.sp else 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color(0xFFFFE2E0))
                    .clickable(onClick = { showDeletePrompt = true }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete record",
                    tint = Color(0xFFB42318),
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        if (showDeletePrompt) {
            AlertDialog(
                onDismissRequest = { showDeletePrompt = false },
                title = {
                    Text(
                        text = "Are you sure to proceed?",
                        color = DetailsText,
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Text(
                        text = "This medical record will be removed from the history.",
                        color = DetailsMuted,
                        fontWeight = FontWeight.Medium,
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showDeletePrompt = false }) {
                        Text(
                            text = "Confirm",
                            color = DetailsPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeletePrompt = false
                            onDelete()
                        },
                    ) {
                        Text(
                            text = "Proceed",
                            color = Color(0xFFB42318),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                containerColor = Color.White,
            )
        }
    }
}

@Composable
private fun DetailsTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, DetailsOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = DetailsPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (background, textColor) = when (status.lowercase()) {
        "urgent" -> Pair(Color(0xFFF9D8D4), Color(0xFF9A1D1A))
        "recovered" -> Pair(Color(0xFFB6D7F3), Color(0xFF385A7A))
        else -> Pair(Color(0xFFDDF0FF), Color(0xFF145E86))
    }

    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DetailsSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DetailsSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DetailsPrimary,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = title,
                    color = DetailsText,
                    fontSize = 20.sp / 1.15f,
                    fontWeight = FontWeight.Bold,
                )
            }
            content()
        }
    }
}
