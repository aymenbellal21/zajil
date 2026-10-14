package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPlaybackState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun AudioWaveformPlayer(
    messageId: String,
    voiceDurationSeconds: Int,
    waveform: List<Float> = emptyList(),
    playbackState: AudioPlaybackState,
    onTogglePlay: (String, Int) -> Unit,
    onToggleSpeed: () -> Unit,
    onSeek: (Float) -> Unit,
    isSenderMe: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isThisPlaying = playbackState.activeMessageId == messageId && playbackState.isPlaying
    val progress = if (playbackState.activeMessageId == messageId) playbackState.currentProgress else 0f
    val speed = if (playbackState.activeMessageId == messageId) playbackState.playbackSpeed else 1.0f

    val defaultBars = remember(messageId) {
        if (waveform.isNotEmpty()) waveform
        else listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 1.0f, 0.5f, 0.8f, 0.3f, 0.6f, 0.4f, 0.7f)
    }

    val elapsedSeconds = (progress * voiceDurationSeconds).toInt()
    val elapsedMinutes = elapsedSeconds / 60
    val elapsedRemSec = elapsedSeconds % 60
    val totalMinutes = voiceDurationSeconds / 60
    val totalRemSec = voiceDurationSeconds % 60
    val timeLabel = String.format("%02d:%02d / %02d:%02d", elapsedMinutes, elapsedRemSec, totalMinutes, totalRemSec)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("audio_waveform_player_$messageId"),
        color = if (isSenderMe) Color(0xFF281C45) else Color(0xFF1B192A),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Play / Pause Circle Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isThisPlaying) NeonPink else NeonPurple)
                    .clickable { onTogglePlay(messageId, voiceDurationSeconds) }
                    .testTag("audio_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isThisPlaying) "إيقاف مؤقت" else "تشغيل",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Waveform and Duration Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Interactive Waveform Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .pointerInput(messageId) {
                            detectTapGestures { offset ->
                                val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                onSeek(fraction)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    defaultBars.forEachIndexed { index, heightFactor ->
                        val barFraction = index.toFloat() / defaultBars.size.toFloat()
                        val isPassed = barFraction <= progress

                        val targetHeight = (8 + (heightFactor * 18)).dp
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isThisPlaying && isPassed) 1.15f else 1f,
                            animationSpec = tween(150),
                            label = "wave_scale"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(targetHeight * animatedScale)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        isPassed -> CyberCyan
                                        else -> Color.White.copy(alpha = 0.25f)
                                    }
                                )
                        )
                    }
                }

                // Time label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeLabel,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "🎙️ رسالة صوتية مشفرة",
                        fontSize = 10.sp,
                        color = CyberCyan.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Playback speed toggle pill (1.0x, 1.5x, 2.0x)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleSpeed() }
                    .testTag("audio_speed_toggle_button"),
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${speed}x",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}
