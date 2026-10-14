package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.Screen

@Composable
fun PulseBottomBar(
    currentScreen: Screen,
    selectedLanguage: AppLanguage = AppLanguage.ARABIC,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = Strings.navHome(selectedLanguage)
                )
            },
            label = { Text(Strings.navHome(selectedLanguage)) },
            modifier = Modifier.testTag("nav_home_button")
        )

        // Explore
        NavigationBarItem(
            selected = currentScreen == Screen.EXPLORE,
            onClick = { onNavigate(Screen.EXPLORE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = Strings.navExplore(selectedLanguage)
                )
            },
            label = { Text(Strings.navExplore(selectedLanguage)) },
            modifier = Modifier.testTag("nav_explore_button")
        )

        // Create (+) Elevated Neon Button
        NavigationBarItem(
            selected = currentScreen == Screen.CREATE,
            onClick = { onNavigate(Screen.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonPurple, NeonPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = Strings.navPost(selectedLanguage),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text(Strings.navPost(selectedLanguage)) },
            modifier = Modifier.testTag("nav_create_button")
        )

        // Reels
        NavigationBarItem(
            selected = currentScreen == Screen.REELS,
            onClick = { onNavigate(Screen.REELS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.REELS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                    contentDescription = Strings.navReels(selectedLanguage)
                )
            },
            label = { Text(Strings.navReels(selectedLanguage)) },
            modifier = Modifier.testTag("nav_reels_button")
        )

        // Profile
        NavigationBarItem(
            selected = currentScreen == Screen.PROFILE,
            onClick = { onNavigate(Screen.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = Strings.navProfile(selectedLanguage)
                )
            },
            label = { Text(Strings.navProfile(selectedLanguage)) },
            modifier = Modifier.testTag("nav_profile_button")
        )
    }
}
