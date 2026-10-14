package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.filter.FilterEngine
import com.example.data.model.MediaType
import com.example.data.model.PhotoFilter
import com.example.data.model.StorySticker
import com.example.ui.components.MediaDisplay
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import java.util.UUID

@Composable
fun CreateStoryScreen(
    onClose: () -> Unit,
    onOpenCamera: () -> Unit,
    onPublishStory: (mediaUrl: String, caption: String, isVideo: Boolean, stickers: List<StorySticker>) -> Unit
) {
    var selectedMediaKey by remember { mutableStateOf("res_cyber_city") }
    var isVideo by remember { mutableStateOf(false) }
    var caption by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterEngine.availableFilters[0]) }

    // Stickers builder state
    var showPollEditor by remember { mutableStateOf(false) }
    var pollQuestion by remember { mutableStateOf("ما رأيكم في هذا المحتوى؟") }
    var pollOption1 by remember { mutableStateOf("أسطوري 🔥") }
    var pollOption2 by remember { mutableStateOf("جيد ✨") }
    var hasPollSticker by remember { mutableStateOf(false) }

    var showQuestionEditor by remember { mutableStateOf(false) }
    var questionPrompt by remember { mutableStateOf("اسألني سؤالاً عن التصوير أو الألعاب...") }
    var hasQuestionSticker by remember { mutableStateOf(false) }

    var selectedReactionEmoji by remember { mutableStateOf<String?>("💜") }

    // Zero-permission Android Photo Picker for user photos / videos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaKey = uri.toString()
        }
    }

    val sampleMediaPresets = listOf(
        Pair("كواليس السايبر 🌃", "res_cyber_city"),
        Pair("صورة شخصية 🎮", "res_avatar"),
        Pair("طوكيو نيون 💜", "res_cyber_city"),
        Pair("الألعاب والبطولات 🏆", "res_avatar")
    )

    val currentColorMatrix = remember(selectedFilter) {
        FilterEngine.createColorMatrix(selectedFilter, 0f, 0f, 0f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0912))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("create_story_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .testTag("close_create_story_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = Color.White
                )
            }

            Text(
                text = "إضافة قصة يومية جديدة 📸",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White
            )

            // Direct Publish Button
            Button(
                onClick = {
                    val stickers = mutableListOf<StorySticker>()
                    if (hasPollSticker) {
                        stickers.add(
                            StorySticker.Poll(
                                id = "poll_${UUID.randomUUID()}",
                                question = pollQuestion,
                                options = listOf(pollOption1, pollOption2),
                                voteCounts = listOf(0, 0)
                            )
                        )
                    }
                    if (hasQuestionSticker) {
                        stickers.add(
                            StorySticker.Question(
                                id = "q_${UUID.randomUUID()}",
                                prompt = questionPrompt
                            )
                        )
                    }
                    selectedReactionEmoji?.let { emoji ->
                        stickers.add(
                            StorySticker.ReactionBurst(
                                id = "burst_${UUID.randomUUID()}",
                                emoji = emoji,
                                count = 1
                            )
                        )
                    }
                    onPublishStory(selectedMediaKey, caption, isVideo, stickers)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("publish_story_button")
            ) {
                Text("نشر الآن", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Media Source Options: Gallery vs Camera vs Type Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pick from Gallery (PhotoPicker)
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            if (isVideo) ActivityResultContracts.PickVisualMedia.VideoOnly
                            else ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("story_gallery_picker_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF201B36))
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("اختيار من المعرض", fontSize = 12.sp, color = Color.White)
            }

            // Open Camera (CameraX)
            Button(
                onClick = onOpenCamera,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("story_camera_launcher_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF201B36))
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("فتح الكاميرا", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Media Type Selector (Photo vs Short Video)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF161426))
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isVideo = false },
                color = if (!isVideo) NeonPurple else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("صورة فوتوغرافية", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isVideo = true },
                color = if (isVideo) NeonPink else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فيديو قصير (15-30ث)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Backgrounds Carousel
        Text("أو اختر من وسائط الاستوديو الجاهزة:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sampleMediaPresets) { (title, key) ->
                val isSelected = selectedMediaKey == key
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedMediaKey = key }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) CyberCyan else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    color = Color(0xFF1B182B),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title, fontSize = 12.sp, color = if (isSelected) CyberCyan else Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 9:16 Story Preview Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(2.dp, Brush.verticalGradient(listOf(NeonPurple, CyberCyan)), RoundedCornerShape(22.dp))
                .testTag("story_preview_container")
        ) {
            MediaDisplay(
                mediaKey = selectedMediaKey,
                contentDescription = "معاينة القصة",
                colorMatrix = currentColorMatrix,
                modifier = Modifier.fillMaxSize()
            )

            // Video Tag Badge if video
            if (isVideo) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Text("فيديو 15ث", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Interactive Stickers Overlay Preview
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasPollSticker) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonPurple, CyberCyan)))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📊 $pollQuestion", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(shape = RoundedCornerShape(8.dp), color = NeonPurple.copy(alpha = 0.5f)) {
                                    Text(text = pollOption1, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                                    Text(text = pollOption2, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                if (hasQuestionSticker) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xEE1E1435),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonPink, NeonPurple)))
                    ) {
                        Text(
                            text = "💬 $questionPrompt",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                selectedReactionEmoji?.let { emoji ->
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.75f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonPurple, NeonPink)))
                    ) {
                        Text(text = emoji, fontSize = 28.sp, modifier = Modifier.padding(8.dp))
                    }
                }

                if (caption.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = caption,
                            color = Color.White,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("اكتب تعليقاً لقصتك اليومية...", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("story_caption_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = Color(0xFF161426),
                unfocusedContainerColor = Color(0xFF161426)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Stickers Creator Toolbar
        Text("إضافة ملصقات تفاعلية للقصة:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Poll sticker button
            OutlinedButton(
                onClick = {
                    hasPollSticker = !hasPollSticker
                    showPollEditor = hasPollSticker
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_poll_sticker_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (hasPollSticker) CyberCyan else Color.White.copy(alpha = 0.8f)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(if (hasPollSticker) listOf(NeonPurple, CyberCyan) else listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f)))
                )
            ) {
                Icon(Icons.Default.Poll, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (hasPollSticker) "استطلاع ✓" else "+ استطلاع", fontSize = 11.sp)
            }

            // Question sticker button
            OutlinedButton(
                onClick = {
                    hasQuestionSticker = !hasQuestionSticker
                    showQuestionEditor = hasQuestionSticker
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("add_question_sticker_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (hasQuestionSticker) NeonPink else Color.White.copy(alpha = 0.8f)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(if (hasQuestionSticker) listOf(NeonPink, NeonPurple) else listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.3f)))
                )
            ) {
                Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (hasQuestionSticker) "سؤال ✓" else "+ سؤال", fontSize = 11.sp)
            }
        }

        // Emoji reactions row
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ملصق تفاعل سريع:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("💜", "🔥", "👏", "😂", "✨").forEach { emoji ->
                    val isChosen = selectedReactionEmoji == emoji
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isChosen) NeonPurple.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f))
                            .clickable {
                                selectedReactionEmoji = if (isChosen) null else emoji
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                    }
                }
            }
        }

        // Poll Customization Box if open
        if (showPollEditor && hasPollSticker) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191629))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("تعديل سؤال الاستطلاع:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = pollQuestion,
                        onValueChange = { pollQuestion = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pollOption1,
                            onValueChange = { pollOption1 = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("الخيار 1") },
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = pollOption2,
                            onValueChange = { pollOption2 = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            label = { Text("الخيار 2") },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Question Customization Box if open
        if (showQuestionEditor && hasQuestionSticker) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191629))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("تعديل نص السؤال:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonPink)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = questionPrompt,
                        onValueChange = { questionPrompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters Carousel
        Text("فلاتر القصة الذكية:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FilterEngine.availableFilters) { filter ->
                val isSelected = selectedFilter.id == filter.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedFilter = filter },
                    color = if (isSelected) NeonPurple else Color(0xFF1B182B),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = filter.nameAr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Big Neon Publish Button
        Button(
            onClick = {
                val stickers = mutableListOf<StorySticker>()
                if (hasPollSticker) {
                    stickers.add(
                        StorySticker.Poll(
                            id = "poll_${UUID.randomUUID()}",
                            question = pollQuestion,
                            options = listOf(pollOption1, pollOption2),
                            voteCounts = listOf(0, 0)
                        )
                    )
                }
                if (hasQuestionSticker) {
                    stickers.add(
                        StorySticker.Question(
                            id = "q_${UUID.randomUUID()}",
                            prompt = questionPrompt
                        )
                    )
                }
                selectedReactionEmoji?.let { emoji ->
                    stickers.add(
                        StorySticker.ReactionBurst(
                            id = "burst_${UUID.randomUUID()}",
                            emoji = emoji,
                            count = 1
                        )
                    )
                }
                onPublishStory(selectedMediaKey, caption, isVideo, stickers)
                onClose()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_publish_story_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("مشاركة القصة الآن على الزاجل", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
