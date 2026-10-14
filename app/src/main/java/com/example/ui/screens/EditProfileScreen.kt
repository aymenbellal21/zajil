package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun EditProfileScreen(
    user: User,
    selectedLanguage: AppLanguage,
    onBack: () -> Unit,
    onSaveProfile: (name: String, bio: String, handle: String, avatarUrl: String?) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var handle by remember { mutableStateOf(user.handle.removePrefix("@")) }
    var bio by remember { mutableStateOf(user.bio) }
    var selectedAvatar by remember { mutableStateOf(user.avatarUrl) }
    var isSavedSnackbar by remember { mutableStateOf(false) }

    val avatarPresets = listOf(
        Pair("الافتراضي 🎮", "res_avatar"),
        Pair("سارة التقنية ⚡", "avatar_sara"),
        Pair("عمر المصور 📸", "avatar_omar"),
        Pair("نوف ديزاين 🎨", "avatar_nouf"),
        Pair("سايبر طوكيو 🌃", "res_cyber_city")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("edit_profile_screen")
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
                        .testTag("edit_profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "تعديل الملف الشخصي",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Button(
                onClick = {
                    onSaveProfile(name, bio, handle, selectedAvatar)
                    isSavedSnackbar = true
                    onBack()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("حفظ التغييرات", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar Header with Badge
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, Brush.linearGradient(listOf(NeonPurple, CyberCyan)), CircleShape)
                    .padding(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().clip(CircleShape)) {
                    MediaDisplay(
                        mediaKey = selectedAvatar,
                        contentDescription = "الصورة الرمزية الحالية",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (user.isVerified) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberCyan.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                        Text("حساب موثق ومحمٍ رسمياً", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Avatar selector row
        Text("اختر الصورة الرمزية المخصصة:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(avatarPresets) { (label, key) ->
                val isSelected = selectedAvatar == key
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedAvatar = key }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) NeonPurple else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        MediaDisplay(
                            mediaKey = key,
                            contentDescription = label,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = if (isSelected) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form Fields
        Text("الاسم الكامل:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_name_field"),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = NeonPurple) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("اسم المستخدم (المعرف):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = handle,
            onValueChange = { handle = it },
            prefix = { Text("@", color = NeonPurple, fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_handle_field"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("النبذة التعريفية (Bio):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("edit_bio_field"),
            shape = RoundedCornerShape(14.dp),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Save Bottom Button
        Button(
            onClick = {
                onSaveProfile(name, bio, handle, selectedAvatar)
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("confirm_save_profile_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تأكيد وحفظ التعديلات", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
