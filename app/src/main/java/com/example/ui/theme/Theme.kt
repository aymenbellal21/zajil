package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PulseDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = DeepViolet.copy(alpha = 0.35f),
    onPrimaryContainer = Color(0xFFEADBFF),
    secondary = CyberCyan,
    onSecondary = Color(0xFF00363A),
    secondaryContainer = CyberCyan.copy(alpha = 0.2f),
    onSecondaryContainer = Color(0xFFA6F5FF),
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    outlineVariant = DarkOutline.copy(alpha = 0.5f)
)

private val PulseGhostColorScheme = darkColorScheme(
    primary = GhostGreen,
    onPrimary = Color.Black,
    primaryContainer = GhostGreen.copy(alpha = 0.25f),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = CyberCyan,
    onSecondary = Color.Black,
    background = GhostDarkBackground,
    onBackground = Color(0xFFECFDF5),
    surface = GhostDarkSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF142922),
    onSurfaceVariant = Color(0xFF6EE7B7),
    outline = Color(0xFF1F4337)
)

private val PulseLightColorScheme = lightColorScheme(
    primary = DeepViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF),
    onPrimaryContainer = DeepViolet,
    secondary = Color(0xFF00838F),
    onSecondary = Color.White,
    tertiary = Color(0xFFC2185B),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    isGhostMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isGhostMode -> PulseGhostColorScheme
        darkTheme -> PulseDarkColorScheme
        else -> PulseLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
