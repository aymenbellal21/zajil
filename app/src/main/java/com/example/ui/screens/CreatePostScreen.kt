package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.filter.FilterEngine
import com.example.data.model.MediaType
import com.example.data.model.PhotoFilter
import com.example.ui.components.MediaDisplay
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun CreatePostScreen(
    caption: String,
    selectedFilter: PhotoFilter,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    isScheduled: Boolean,
    scheduledDate: String,
    scheduledTime: String,
    selectedMediaType: MediaType,
    capturedMediaUri: String? = null,
    onOpenCamera: () -> Unit,
    onCaptionChange: (String) -> Unit,
    onFilterSelect: (PhotoFilter) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onAutoEnhance: () -> Unit,
    onResetAdjustments: () -> Unit,
    onMediaTypeChange: (MediaType) -> Unit,
    onScheduledToggle: (Boolean) -> Unit,
    onScheduledDateChange: (String) -> Unit,
    onScheduledTimeChange: (String) -> Unit,
    onGenerateAiCaption: (String) -> Unit,
    onPublish: () -> Unit
) {
    var showAdjustSliders by remember { mutableStateOf(false) }

    val currentColorMatrix = remember(selectedFilter, brightness, contrast, saturation) {
        FilterEngine.createColorMatrix(
            filter = selectedFilter,
            brightnessAdj = brightness,
            contrastAdj = contrast,
            saturationAdj = saturation
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("create_post_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "استوديو النشر والإبداع",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Media type selector (Photo vs Short Video)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedMediaType == MediaType.PHOTO) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.clickable { onMediaTypeChange(MediaType.PHOTO) }
                    ) {
                        Text(
                            text = "صورة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMediaType == MediaType.PHOTO) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedMediaType == MediaType.SHORT_VIDEO) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.clickable { onMediaTypeChange(MediaType.SHORT_VIDEO) }
                    ) {
                        Text(
                            text = "فيديو قصير",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMediaType == MediaType.SHORT_VIDEO) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // CameraX Fast Launcher Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenCamera)
                    .testTag("open_camera_capture_button"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NeonPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "التقاط من الكاميرا مباشرة (CameraX)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (capturedMediaUri != null) "تم التقاط وسائط بالكاميرا جاهزة للنشر ✓" else "التقاط صورة فوتوغرافية أو تسجيل فيديو قصير",
                                fontSize = 11.sp,
                                color = if (capturedMediaUri != null) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Live Photo / Video Preview Box with Active Filter Applied
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .testTag("create_preview_box"),
                contentAlignment = Alignment.Center
            ) {
                MediaDisplay(
                    mediaKey = if (selectedMediaType == MediaType.PHOTO) "res_cyber_city" else "res_avatar",
                    contentDescription = "معاينة الوسائط",
                    colorMatrix = currentColorMatrix,
                    modifier = Modifier.fillMaxSize()
                )

                // Filter Pill at Top-Left
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterVintage,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = selectedFilter.nameAr,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // CameraX captured badge at Bottom-Start
                if (capturedMediaUri != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "ملتقط عبر CameraX",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // AI Auto Enhance Magic Wand Button
                FloatingActionButton(
                    onClick = onAutoEnhance,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .testTag("auto_enhance_button"),
                    containerColor = NeonPurple,
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "تعديل تلقائي بالذكاء الاصطناعي",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "تعديل تلقائي AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter Selection Carousel
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فلاتر احترافية جذابة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = { showAdjustSliders = !showAdjustSliders }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAdjustSliders) "إخفاء الضبط" else "ضبط متقدم")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FilterEngine.availableFilters) { filter ->
                        val isSelected = filter.id == selectedFilter.id
                        val itemMatrix = remember(filter) { FilterEngine.createColorMatrix(filter) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(74.dp)
                                .clickable { onFilterSelect(filter) }
                                .testTag("filter_option_${filter.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(66.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                MediaDisplay(
                                    mediaKey = "res_cyber_city",
                                    contentDescription = filter.nameAr,
                                    colorMatrix = itemMatrix,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = filter.nameAr,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Advanced Adjustment Sliders (Brightness, Contrast, Saturation)
        if (showAdjustSliders) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "معايرة الإضاءة والألوان", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            TextButton(onClick = onResetAdjustments) {
                                Text("إعادة تعيين", fontSize = 12.sp)
                            }
                        }

                        // Brightness
                        Text("السطوع", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = brightness,
                            onValueChange = onBrightnessChange,
                            valueRange = -0.3f..0.3f
                        )

                        // Contrast
                        Text("التباين", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = contrast,
                            onValueChange = onContrastChange,
                            valueRange = -0.5f..0.5f
                        )

                        // Saturation
                        Text("تشبع الألوان", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = saturation,
                            onValueChange = onSaturationChange,
                            valueRange = -0.5f..0.8f
                        )
                    }
                }
            }
        }

        // Caption Input & AI Generator
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الوصف والهاشتاجات",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonPurple.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(14.dp))
                            Text("ذكاء اصطناعي", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = onCaptionChange,
                    placeholder = { Text("اكتب وصفاً جذاباً لمنشورك...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("post_caption_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // AI Suggestion Chips
                Text(
                    text = "اقتراحات كابشن ذكية:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        ActionChip(
                            label = "🎮 ألعاب وبطولات",
                            onClick = { onGenerateAiCaption("gaming") }
                        )
                    }
                    item {
                        ActionChip(
                            label = "🌌 تصوير ليلي وسايبر",
                            onClick = { onGenerateAiCaption("night") }
                        )
                    }
                    item {
                        ActionChip(
                            label = "📸 فوتوغراف وتعديل",
                            onClick = { onGenerateAiCaption("photo") }
                        )
                    }
                }
            }
        }

        // Post Scheduling Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isScheduled) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "جدولة النشر التلقائي بدقة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "نشر المنشور آلياً في أفضل وقت لتفاعل الجمهور",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isScheduled,
                            onCheckedChange = onScheduledToggle,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPurple)
                        )
                    }

                    if (isScheduled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("حدد اليوم:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("اليوم", "غداً", "الجمعة القادمة").forEach { date ->
                                FilterChip(
                                    selected = scheduledDate == date,
                                    onClick = { onScheduledDateChange(date) },
                                    label = { Text(date) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("حدد التوقيت (أفضل وقت مقترح 9:00 م):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("08:00 م", "09:30 م", "11:00 م").forEach { time ->
                                FilterChip(
                                    selected = scheduledTime == time,
                                    onClick = { onScheduledTimeChange(time) },
                                    label = { Text(time) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Publish Button
        item {
            Button(
                onClick = onPublish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("publish_post_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isScheduled) Icons.Default.ScheduleSend else Icons.Default.RocketLaunch,
                        contentDescription = null
                    )
                    Text(
                        text = if (isScheduled) "جدولة المنشور ($scheduledDate $scheduledTime)" else "نشر المنشور الآن",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp) },
        shape = RoundedCornerShape(12.dp)
    )
}
