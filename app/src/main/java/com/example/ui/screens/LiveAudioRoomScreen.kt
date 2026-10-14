package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveAudioRoom
import com.example.data.model.User
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun LiveAudioRoomScreen(
    rooms: List<LiveAudioRoom>,
    activeRoom: LiveAudioRoom?,
    currentUser: User,
    selectedLanguage: AppLanguage,
    onBack: () -> Unit,
    onSelectRoom: (LiveAudioRoom) -> Unit,
    onLeaveRoom: () -> Unit,
    onToggleSpeaking: (String) -> Unit,
    onRaiseHand: (String) -> Unit
) {
    var isMicMuted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0A17))
            .padding(16.dp)
            .testTag("live_audio_room_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .testTag("audio_room_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = Color.White
                    )
                }

                Column {
                    Text(
                        text = "الصالونات الصوتية المباشرة 🎙️",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "بث حي ومحادثات جماعية مشفرة",
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NeonPink.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonPink)
                    )
                    Text(
                        text = "مباشر LIVE",
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeRoom != null) {
            // Active Live Room View
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161228))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Room Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeRoom.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "موضوع الغرفة: ${activeRoom.topic}",
                                fontSize = 12.sp,
                                color = CyberCyan
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Headphones, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${activeRoom.listenersCount + activeRoom.speakers.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stage Speakers Section
                    Text(
                        text = "منصة المتحدثين (${activeRoom.speakers.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 160.dp)
                    ) {
                        items(activeRoom.speakers) { speaker ->
                            SpeakerAvatarItem(
                                speaker = speaker,
                                isHost = speaker.id == activeRoom.host.id,
                                isSpeaking = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Listeners Audience Section
                    Text(
                        text = "المستمعون في الغرفة (${activeRoom.listeners.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(activeRoom.listeners) { listener ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                ) {
                                    MediaDisplay(
                                        mediaKey = listener.avatarUrl,
                                        contentDescription = listener.name,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = listener.name.split(" ").firstOrNull() ?: listener.name,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Bottom Stage Controls Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF221D3A))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Leave Room
                        OutlinedButton(
                            onClick = onLeaveRoom,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink)
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مغادرة", fontSize = 12.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Raise Hand Button
                            IconButton(
                                onClick = { onRaiseHand(activeRoom.id) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (activeRoom.hasRaisedHand) CyberCyan else Color.White.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = "✋",
                                    fontSize = 20.sp
                                )
                            }

                            // Mic Mute / Unmute
                            IconButton(
                                onClick = {
                                    isMicMuted = !isMicMuted
                                    onToggleSpeaking(activeRoom.id)
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isMicMuted) NeonPink else NeonPurple)
                            ) {
                                Icon(
                                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "الميكروفون",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Browse Available Live Rooms
            Text("الصالونات المتاحة الآن:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRoom(room) }
                            .testTag("audio_room_card_${room.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18142A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NeonPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(room.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                        Text("مضيف: ${room.host.name}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = NeonPurple.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${room.listenersCount} مستمع",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = room.topic,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onSelectRoom(room) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                            ) {
                                Icon(Icons.Default.Headphones, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الانضمام للصالون الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakerAvatarItem(speaker: User, isHost: Boolean, isSpeaking: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(if (isSpeaking) pulseScale else 1f)
                .clip(CircleShape)
                .border(2.dp, if (isHost) CyberCyan else NeonPurple, CircleShape)
        ) {
            MediaDisplay(
                mediaKey = speaker.avatarUrl,
                contentDescription = speaker.name,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = speaker.name.split(" ").firstOrNull() ?: speaker.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            if (isHost) {
                Spacer(modifier = Modifier.width(2.dp))
                Text("👑", fontSize = 10.sp)
            }
        }
    }
}
