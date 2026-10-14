package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    selectedLanguage: AppLanguage,
    onAnimationFinished: () -> Unit
) {
    // Flight floating vertical offset
    val infiniteTransition = rememberInfiniteTransition(label = "pigeonFlight")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flightOffset"
    )

    // Glow pulse scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Fade-in animation
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "splashAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0D11),
                        Color(0xFF130E24),
                        Color(0xFF07070A)
                    )
                )
            )
            .testTag("alzajel_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .alpha(alphaAnim)
        ) {
            // Glowing Pigeon Emblem with flight animation
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .offset(y = offsetY.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Outermost pulsing glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    NeonPurple.copy(alpha = 0.45f),
                                    NeonPink.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Pigeon Logo Asset
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(listOf(NeonPurple, NeonPink, CyberCyan)),
                            shape = CircleShape
                        )
                ) {
                    MediaDisplay(
                        mediaKey = "alzajel_logo",
                        contentDescription = "شعار الزاجل",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // English Title: ALZAJEL
            Text(
                text = "ALZAJEL",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Arabic Title + Tagline
            Text(
                text = "الزاجل",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Strings.slogan(selectedLanguage),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Glowing indeterminate progress indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NeonPurple,
                trackColor = Color.White.copy(alpha = 0.15f)
            )
        }

        // Skip button at bottom
        TextButton(
            onClick = onAnimationFinished,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (selectedLanguage == AppLanguage.ARABIC) "تخطي ❯" else "Skip ❯",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}
