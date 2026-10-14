package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPlaybackState
import com.example.data.model.ChatMessage
import com.example.data.model.DirectChat
import com.example.ui.components.AudioWaveformPlayer
import com.example.ui.components.MediaDisplay
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun MessagesScreen(
    chats: List<DirectChat>,
    activeChat: DirectChat?,
    isGhostMode: Boolean,
    isRecordingVoice: Boolean,
    recordingDuration: Int,
    audioPlaybackState: AudioPlaybackState = AudioPlaybackState(),
    onOpenChat: (DirectChat) -> Unit,
    onCloseChat: () -> Unit,
    onSendTextMessage: (String, String) -> Unit,
    onStartVoiceRecording: () -> Unit,
    onStopAndSendVoiceRecording: (String) -> Unit,
    onCancelVoiceRecording: () -> Unit,
    onToggleAudioPlay: (String, Int) -> Unit = { _, _ -> },
    onToggleAudioSpeed: () -> Unit = {},
    onSeekAudio: (Float) -> Unit = {}
) {
    if (activeChat == null) {
        // Chat List Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("messages_list_screen")
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الرسائل المباشرة",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "مشفرة E2EE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ghost Mode Banner if active
            if (isGhostMode) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = GhostGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = GhostGreen
                        )
                        Text(
                            text = "وضع التصفح الخفي نشط: لن تظهر علامات قراءة الرسائل أو حالة اتصالك للآخرين.",
                            fontSize = 12.sp,
                            color = GhostGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chats) { chat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenChat(chat) }
                            .testTag("chat_item_${chat.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(50.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                ) {
                                    MediaDisplay(
                                        mediaKey = chat.peerUser.avatarUrl,
                                        contentDescription = chat.peerUser.name
                                    )
                                }

                                if (chat.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(if (isGhostMode) GhostGreen else Color(0xFF00E676))
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = chat.peerUser.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = chat.lastTimestamp,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = chat.lastMessage,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (chat.unreadCount > 0) {
                                        Badge(
                                            containerColor = NeonPurple,
                                            contentColor = Color.White
                                        ) {
                                            Text("${chat.unreadCount}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Active Chat Conversation Room
        var messageInput by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("chat_conversation_room")
        ) {
            // Chat Room Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCloseChat) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        MediaDisplay(
                            mediaKey = activeChat.peerUser.avatarUrl,
                            contentDescription = activeChat.peerUser.name
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeChat.peerUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (activeChat.isOnline) "متصل الآن (تشفير تام)" else "آخر ظهور حديثاً",
                            fontSize = 11.sp,
                            color = if (activeChat.isOnline) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Voice call action
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال صوتي",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // End-to-End Encryption Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "كافة الرسائل والملاحظات الصوتية مشفرة بتقنية E2EE",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Message History List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(activeChat.messages) { msg ->
                    val isMe = msg.senderId != activeChat.peerUser.id
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (msg.isVoice) {
                                    AudioWaveformPlayer(
                                        messageId = msg.id,
                                        voiceDurationSeconds = msg.voiceDurationSeconds.coerceAtLeast(4),
                                        waveform = msg.voiceWaveform,
                                        playbackState = audioPlaybackState,
                                        onTogglePlay = onToggleAudioPlay,
                                        onToggleSpeed = onToggleAudioSpeed,
                                        onSeek = onSeekAudio,
                                        isSenderMe = isMe
                                    )
                                } else {
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = msg.timestamp,
                                        fontSize = 10.sp,
                                        color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (isMe) {
                                        Icon(
                                            imageVector = Icons.Default.DoneAll,
                                            contentDescription = "تمت القراءة",
                                            tint = if (isGhostMode) Color.White.copy(alpha = 0.7f) else CyberCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Voice Recording in Progress Indicator
            AnimatedVisibility(visible = isRecordingVoice) {
                Surface(
                    color = NeonPink.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(NeonPink)
                            )
                            Text(
                                text = "جاري تسجيل رسالة صوتية مشفرة: 0:${recordingDuration.toString().padStart(2, '0')}",
                                color = NeonPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row {
                            TextButton(onClick = onCancelVoiceRecording) {
                                Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onStopAndSendVoiceRecording(activeChat.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Text("إرسال")
                            }
                        }
                    }
                }
            }

            // Message Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("اكتب رسالة...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (messageInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onSendTextMessage(activeChat.id, messageInput)
                                messageInput = ""
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("send_text_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال نصي",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        // Instant Voice Message Record Button
                        IconButton(
                            onClick = {
                                if (isRecordingVoice) {
                                    onStopAndSendVoiceRecording(activeChat.id)
                                } else {
                                    onStartVoiceRecording()
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isRecordingVoice) NeonPink else MaterialTheme.colorScheme.primary)
                                .testTag("record_voice_button")
                        ) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "تسجيل رسالة صوتية",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
