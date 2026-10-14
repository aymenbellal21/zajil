package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.data.model.SavedCollection
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun SavedPostsScreen(
    savedPosts: List<Post>,
    collections: List<SavedCollection>,
    selectedLanguage: AppLanguage,
    onBack: () -> Unit,
    onCreateCollection: (String, String) -> Unit,
    onPostClick: (Post) -> Unit,
    onUnsavePost: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var activeCollectionId by remember { mutableStateOf<String?>(null) }

    val currentCollection = remember(collections, activeCollectionId) {
        collections.find { it.id == activeCollectionId }
    }

    val displayPosts = remember(savedPosts, currentCollection) {
        if (currentCollection != null) {
            savedPosts.filter { currentCollection.postIds.contains(it.id) }
        } else {
            savedPosts
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("saved_posts_screen")
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
                    onClick = {
                        if (activeCollectionId != null) {
                            activeCollectionId = null
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("saved_posts_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = currentCollection?.title ?: "العناصر والمجموعات المحفوظة",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${displayPosts.size} منشور محفوظ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Create collection button
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.2f))
                    .testTag("create_collection_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = "إنشاء مجموعة جديدة",
                    tint = NeonPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs if at top-level
        if (activeCollectionId == null) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("المجموعات المنظمة (${collections.size})") },
                    icon = { Icon(Icons.Default.FolderSpecial, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("جميع المنشورات (${savedPosts.size})") },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Content
        if (activeCollectionId == null && selectedTab == 0) {
            // Collections Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(collections) { col ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clickable { activeCollectionId = col.id }
                            .testTag("collection_card_${col.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            col.coverUrl?.let { cover ->
                                MediaDisplay(
                                    mediaKey = cover,
                                    contentDescription = col.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.55f))
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeonPurple),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = col.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${col.postIds.size} عنصر",
                                        fontSize = 11.sp,
                                        color = CyberCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Posts Grid
            if (displayPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "لا توجد عناصر محفوظة هنا حالياً",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(displayPosts) { post ->
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onPostClick(post) }
                                .testTag("saved_post_item_${post.id}"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                MediaDisplay(
                                    mediaKey = post.mediaUrl,
                                    contentDescription = post.caption,
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = { onUnsavePost(post.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkRemove,
                                        contentDescription = "إلغاء الحفظ",
                                        tint = NeonPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Collection Dialog
        if (showCreateDialog) {
            var colTitle by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("إنشاء مجلد محفوظات جديد 📁") },
                text = {
                    Column {
                        Text("قم بتنظيم منشوراتك وتصاميمك المفضلة في مجلد مخصص.")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = colTitle,
                            onValueChange = { colTitle = it },
                            placeholder = { Text("اسم المجلد (مثال: أفكار المحتوى)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (colTitle.isNotBlank()) {
                                onCreateCollection(colTitle, "folder")
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("إنشاء المجلد")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
