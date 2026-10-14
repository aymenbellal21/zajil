package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Story
import com.example.data.model.User
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun StoryAvatarRow(
    stories: List<Story>,
    currentUser: User,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("stories_horizontal_row"),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "Add Story" / My Story Item
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable(onClick = onAddStoryClick)
                    .testTag("add_story_item")
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        MediaDisplay(
                            mediaKey = currentUser.avatarUrl,
                            contentDescription = "قصتك"
                        )
                    }

                    // Plus badge
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(NeonPurple)
                            .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة قصة",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "قصتك",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Friends stories
        items(stories) { story ->
            val gradientBorder = if (story.isViewed) {
                Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.5f), Color.DarkGray))
            } else {
                Brush.linearGradient(listOf(NeonPurple, NeonPink))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onStoryClick(story) }
                    .testTag("story_item_${story.id}")
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glowing gradient ring
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, gradientBorder, CircleShape)
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            MediaDisplay(
                                mediaKey = story.user.avatarUrl,
                                contentDescription = story.user.name
                            )
                        }
                    }

                    // 24h Expiration badge countdown
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${story.hoursRemaining}س",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (story.hoursRemaining <= 6) NeonPink else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.user.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
