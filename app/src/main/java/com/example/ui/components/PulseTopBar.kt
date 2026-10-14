package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseTopBar(
    isGhostMode: Boolean,
    isDarkMode: Boolean,
    selectedLanguage: AppLanguage,
    unreadMessagesCount: Int,
    unreadNotificationsCount: Int = 2,
    onGhostModeToggle: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAudioRoom: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Glowing Alzajel Pigeon Logo
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonPurple, NeonPink)
                            )
                        )
                        .border(1.5.dp, CyberCyan, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    ) {
                        MediaDisplay(
                            mediaKey = "alzajel_logo",
                            contentDescription = "Alzajel Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Alzajel",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "الزاجل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        actions = {
            // Language Switch Action
            IconButton(
                onClick = onToggleLanguage,
                modifier = Modifier.testTag("topbar_language_button")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = selectedLanguage.code.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Ghost Mode / Incognito Toggle Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onGhostModeToggle)
                    .testTag("ghost_mode_toggle_pill"),
                color = if (isGhostMode) GhostGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isGhostMode) Icons.Default.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = Strings.ghostMode(selectedLanguage),
                        tint = if (isGhostMode) GhostGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isGhostMode) Strings.ghostActive(selectedLanguage) else Strings.ghostVisible(selectedLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGhostMode) GhostGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Analytics Shortcut
            IconButton(
                onClick = onNavigateToAnalytics,
                modifier = Modifier.testTag("analytics_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = "تحليلات الأداء",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Notifications Button with badge
            IconButton(
                onClick = onNavigateToNotifications,
                modifier = Modifier.testTag("notifications_icon_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(
                                containerColor = NeonPurple,
                                contentColor = Color.White
                            ) {
                                Text("$unreadNotificationsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "مركز التنبيهات",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Live Audio Room Shortcut
            IconButton(
                onClick = onNavigateToAudioRoom,
                modifier = Modifier.testTag("audio_rooms_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = "الصالونات الصوتية المباشرة",
                    tint = CyberCyan
                )
            }

            // Messages Button with badge
            IconButton(
                onClick = onNavigateToMessages,
                modifier = Modifier.testTag("messages_icon_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadMessagesCount > 0) {
                            Badge(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ) {
                                Text("$unreadMessagesCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "الرسائل الفورية",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Dark Mode Quick Toggle
            IconButton(
                onClick = onDarkModeToggle,
                modifier = Modifier.testTag("dark_mode_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "تبديل الوضع الليلي",
                    tint = if (isDarkMode) NeonPurple else Color(0xFFFFB300)
                )
            }
        }
    )
}
