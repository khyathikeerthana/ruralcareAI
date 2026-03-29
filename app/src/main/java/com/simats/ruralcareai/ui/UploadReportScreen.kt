package com.simats.ruralcareai.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val UploadBackground = Color(0xFFF2F4F8)
private val UploadSurface = Color(0xFFFFFFFF)
private val UploadText = Color(0xFF131A22)
private val UploadMuted = Color(0xFF6F7A89)
private val UploadPrimary = Color(0xFF0B6FA2)
private val UploadOutline = Color(0xFFD6DEE9)

@Composable
fun UploadReportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenHeightDp <= 820 || configuration.screenWidthDp <= 392
    val horizontalPadding = if (isCompact) 14.dp else 16.dp

    var selectedFileLabel by rememberSaveable { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileLabel = uri?.lastPathSegment?.substringAfterLast('/')
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(UploadBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            top = if (isCompact) 10.dp else 12.dp,
            bottom = 28.dp,
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
                    UploadTopAction(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                    )

                    Text(
                        text = "RuralCareAI",
                        color = UploadPrimary,
                        fontSize = if (isCompact) 22.sp / 1.15f else 23.sp / 1.15f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Upload Report",
                    color = UploadText,
                    fontSize = if (isCompact) 44.sp / 1.5f else 46.sp / 1.5f,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Analyze patient records with clinical-grade AI.",
                    color = UploadMuted,
                    fontSize = if (isCompact) 16.sp / 1.12f else 17.sp / 1.12f,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { filePickerLauncher.launch("*/*") }),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = UploadSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UploadOutline),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFDDEFFD)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = "Upload",
                            tint = UploadPrimary,
                            modifier = Modifier.size(38.dp),
                        )
                    }

                    Text(
                        text = "Drag and drop file here",
                        color = UploadText,
                        fontSize = if (isCompact) 24.sp / 1.35f else 26.sp / 1.35f,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "or use the actions below",
                        color = UploadMuted,
                        fontSize = if (isCompact) 16.sp / 1.15f else 17.sp / 1.15f,
                        fontWeight = FontWeight.Medium,
                    )
                    if (!selectedFileLabel.isNullOrBlank()) {
                        Text(
                            text = "Selected: ${selectedFileLabel}",
                            color = UploadPrimary,
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFF2D9CDB))
                        .clickable(onClick = { filePickerLauncher.launch("*/*") })
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = "Select file",
                        tint = Color(0xFF003049),
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "  Select File from Device",
                        color = Color(0xFF003049),
                        fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color(0xFFDCE1E8))
                        .clickable(onClick = { filePickerLauncher.launch("image/*") })
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Take photo",
                        tint = UploadText,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "  Take Photo of Report",
                        color = UploadText,
                        fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE7ECF2)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "SUPPORTED FORMATS",
                        color = UploadMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FormatChip("PDF")
                        FormatChip("JPG")
                        FormatChip("PNG")
                    }

                    Text(
                        text = "Maximum file size: 10MB",
                        color = UploadText.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = UploadSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UploadOutline),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Privacy",
                            tint = UploadPrimary,
                            modifier = Modifier.size(24.dp),
                        )

                        Box(
                            modifier = Modifier
                                .size(width = 42.dp, height = 4.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFD3DBE5)),
                        )
                    }

                    Text(
                        text = "Privacy & Encryption",
                        color = UploadText,
                        fontSize = if (isCompact) 38.sp / 1.7f else 40.sp / 1.7f,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Your data is encrypted and secure. We adhere to clinical data sovereignty standards.",
                        color = UploadText.copy(alpha = 0.84f),
                        fontSize = if (isCompact) 17.sp / 1.12f else 18.sp / 1.12f,
                        lineHeight = if (isCompact) 30.sp / 1.12f else 31.sp / 1.12f,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadTopAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.White)
            .border(1.dp, UploadOutline, androidx.compose.foundation.shape.CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = UploadPrimary,
            modifier = Modifier.size(19.dp),
        )
    }
}

@Composable
private fun FormatChip(text: String) {
    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, UploadOutline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = UploadPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
