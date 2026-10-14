package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Story
import com.example.data.model.StorySticker
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay

@Composable
fun StoryViewerDialog(
    story: Story,
    isGhostMode: Boolean,
    onDismiss: () -> Unit,
    onVotePoll: (storyId: String, stickerId: String, optionIndex: Int) -> Unit = { _, _, _ -> },
    onAnswerQuestion: (storyId: String, stickerId: String, answer: String) -> Unit = { _, _, _ -> },
    onSendReaction: (storyId: String, stickerId: String) -> Unit = { _, _ -> }
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isLiked by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var replySent by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Auto progress animation
    LaunchedEffect(story.id, isPaused) {
        if (!isPaused) {
            while (progress < 1f) {
                delay(50)
                progress += 0.007f
            }
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_dialog")
        ) {
            // Fullscreen story media
            MediaDisplay(
                mediaKey = story.mediaUrl,
                contentDescription = story.caption,
                modifier = Modifier.fillMaxSize()
            )

            // Overlays for readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
                    .align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .align(Alignment.BottomCenter)
            )

            // Top Header: Progress Bar & Author Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = NeonPurple,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .border(1.5.dp, NeonPurple, CircleShape)
                        ) {
                            MediaDisplay(
                                mediaKey = story.user.avatarUrl,
                                contentDescription = story.user.name,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = story.user.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (story.isVideo) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonPink.copy(alpha = 0.85f)
                                    ) {
                                        Text(
                                            text = "فيديو 📹",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "متبقي ${story.hoursRemaining} ساعة",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isGhostMode) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GhostGreen.copy(alpha = 0.25f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "تصفح خفي",
                                        tint = GhostGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "مشاهدة مخفية",
                                        fontSize = 11.sp,
                                        color = GhostGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق القصة",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Interactive Stickers & Caption Container (Centered)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Render Interactive Stickers
                story.stickers.forEach { sticker ->
                    when (sticker) {
                        is StorySticker.Poll -> {
                            val totalVotes = sticker.voteCounts.sum().coerceAtLeast(1)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("story_poll_${sticker.id}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xDD121020))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Poll, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = sticker.question,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    sticker.options.forEachIndexed { index, option ->
                                        val votes = sticker.voteCounts.getOrElse(index) { 0 }
                                        val percentage = ((votes.toFloat() / totalVotes) * 100).toInt()
                                        val hasVoted = sticker.userVotedIndex != null
                                        val isSelected = sticker.userVotedIndex == index

                                        val animPercent by animateFloatAsState(
                                            targetValue = if (hasVoted) (votes.toFloat() / totalVotes) else 0f,
                                            animationSpec = tween(400),
                                            label = "poll_fill"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White.copy(alpha = 0.12f))
                                                .clickable(enabled = !hasVoted) {
                                                    onVotePoll(story.id, sticker.id, index)
                                                }
                                        ) {
                                            if (hasVoted) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(fraction = animPercent)
                                                        .background(if (isSelected) NeonPurple else Color.White.copy(alpha = 0.25f))
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = option,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = Color.White
                                                )
                                                if (hasVoted) {
                                                    Text(
                                                        text = "$percentage%",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) CyberCyan else Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        is StorySticker.Question -> {
                            var questionAnswer by remember { mutableStateOf("") }
                            var hasAnswered by remember { mutableStateOf(sticker.userSubmitted) }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("story_question_${sticker.id}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xDD18122B))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = sticker.prompt,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (hasAnswered) {
                                        Surface(
                                            color = CyberCyan.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "شكراً لمشاركتك! تم إرسال إجابتك لصاحب القصة ✓",
                                                color = CyberCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = questionAnswer,
                                                onValueChange = {
                                                    questionAnswer = it
                                                    isPaused = it.isNotEmpty()
                                                },
                                                placeholder = { Text("اكتب إجابتك هنا...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.White,
                                                    focusedBorderColor = NeonPink,
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                                ),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = {
                                                    if (questionAnswer.isNotBlank()) {
                                                        onAnswerQuestion(story.id, sticker.id, questionAnswer)
                                                        hasAnswered = true
                                                        isPaused = false
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(NeonPink)
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "إرسال", tint = Color.White, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        is StorySticker.ReactionBurst -> {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp))
                                    .clickable {
                                        onSendReaction(story.id, sticker.id)
                                    }
                                    .testTag("story_reaction_${sticker.id}"),
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(30.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NeonPurple, NeonPink)))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = sticker.emoji, fontSize = 24.sp)
                                    Text(
                                        text = "${sticker.count} تفاعل",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Story Caption
                if (story.caption.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = story.caption,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Story Reply & Heart
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                if (replySent) {
                    Text(
                        text = "تم إرسال الرد إلى الدردشة المشفرة بنجاح ✓",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = {
                            replyText = it
                            isPaused = it.isNotEmpty()
                        },
                        placeholder = { Text("أرسل رداً على القصة...", color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("story_reply_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.4f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                replySent = true
                                replyText = ""
                                isPaused = false
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "إرسال الرد",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { isLiked = !isLiked },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "إعجاب بالقصة",
                            tint = if (isLiked) NeonPink else Color.White
                        )
                    }
                }
            }
        }
    }
}
