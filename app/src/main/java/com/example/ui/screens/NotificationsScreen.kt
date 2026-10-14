package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    selectedLanguage: AppLanguage,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("الكل", "الإعجابات ❤️", "التعليقات 💬", "الذكاء الاصطناعي والجدولة ⚡")

    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            1 -> notifications.filter { it.type == NotificationType.LIKE }
            2 -> notifications.filter { it.type == NotificationType.COMMENT }
            3 -> notifications.filter { it.type == NotificationType.SCHEDULED || it.type == NotificationType.AI_RECOMMENDATION }
            else -> notifications
        }
    }

    val unreadCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("notifications_screen")
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("notifications_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = "مركز الإشعارات والنشاط",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (unreadCount > 0) "لديك $unreadCount تنبيهات جديدة لم تقرأ" else "جميع الإشعارات مقروءة ومحدثة ✓",
                        fontSize = 11.sp,
                        color = if (unreadCount > 0) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mark All as Read button
            if (unreadCount > 0) {
                TextButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Text(
                        text = "تحديد الكل كمقروء",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters.size) { index ->
                val isSelected = selectedFilter == index
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { selectedFilter = index }
                        .testTag("notif_filter_$index"),
                    color = if (isSelected) NeonPurple else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = filters[index],
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notifications List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد تنبيهات في هذا القسم حالياً",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { item ->
                    NotificationCard(item = item)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    val cardBg = if (!item.isRead) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar or Type Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.type) {
                            NotificationType.LIKE -> NeonPink.copy(alpha = 0.2f)
                            NotificationType.COMMENT -> NeonPurple.copy(alpha = 0.2f)
                            NotificationType.SCHEDULED -> CyberCyan.copy(alpha = 0.2f)
                            NotificationType.AI_RECOMMENDATION -> GhostGreen.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.relatedUser != null) {
                    MediaDisplay(
                        mediaKey = item.relatedUser.avatarUrl,
                        contentDescription = item.relatedUser.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = when (item.type) {
                            NotificationType.LIKE -> Icons.Default.Favorite
                            NotificationType.COMMENT -> Icons.Default.ChatBubble
                            NotificationType.SCHEDULED -> Icons.Default.Schedule
                            NotificationType.AI_RECOMMENDATION -> Icons.Default.AutoAwesome
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = when (item.type) {
                            NotificationType.LIKE -> NeonPink
                            NotificationType.COMMENT -> NeonPurple
                            NotificationType.SCHEDULED -> CyberCyan
                            NotificationType.AI_RECOMMENDATION -> GhostGreen
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Notification Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.timeAgo,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }

            // Unread Dot Indicator
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonPink)
                )
            }
        }
    }
}
