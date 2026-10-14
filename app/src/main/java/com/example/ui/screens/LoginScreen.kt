package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun LoginScreen(
    selectedLanguage: AppLanguage,
    onLoginSuccess: (String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0D11),
                        Color(0xFF141021),
                        Color(0xFF09090C)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("login_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Top Language & Brand Bar
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
                    MediaDisplay(
                        mediaKey = "alzajel_logo",
                        contentDescription = "شعار الزاجل",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = "Alzajel",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            // Language Switch Button
            OutlinedButton(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(selectedLanguage.displayName, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Logo with violet halo
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(2.dp, Brush.linearGradient(listOf(NeonPurple, NeonPink)), CircleShape)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            ) {
                MediaDisplay(
                    mediaKey = "alzajel_logo",
                    contentDescription = "شعار الزاجل",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = Strings.loginTitle(selectedLanguage),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Text(
            text = Strings.slogan(selectedLanguage),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text(Strings.emailLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = NeonPurple
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_field"),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedContainerColor = Color(0xFF1B182B),
                unfocusedContainerColor = Color(0xFF13111E)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text(Strings.passwordLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NeonPurple
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "إظهار/إخفاء كلمة المرور",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_field"),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                focusedContainerColor = Color(0xFF1B182B),
                unfocusedContainerColor = Color(0xFF13111E)
            ),
            singleLine = true
        )

        // Forgot Password Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.testTag("login_forgot_password_button")
            ) {
                Text(
                    text = if (selectedLanguage == AppLanguage.ARABIC) "نسيت كلمة المرور؟" else "Forgot password?",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Error message if any
        errorMessage?.let { error ->
            Text(
                text = error,
                color = NeonPink,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sign in with Email Button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = if (selectedLanguage == AppLanguage.ARABIC)
                        "يرجى إدخال البريد الإلكتروني وكلمة المرور"
                    else "Please enter email and password"
                } else {
                    onLoginSuccess(email)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("sign_in_email_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple
            )
        ) {
            Text(
                text = Strings.signInWithEmail(selectedLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.2f)
            )
            Text(
                text = if (selectedLanguage == AppLanguage.ARABIC) "أو" else "OR",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.2f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Continue with Google Button
        Button(
            onClick = { onLoginSuccess("user_google@alzajel.app") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("continue_google_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C2842)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = Strings.continueWithGoogle(selectedLanguage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Continue with Apple Button
        Button(
            onClick = { onLoginSuccess("user_apple@alzajel.app") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("continue_apple_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D1B26)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = Strings.continueWithApple(selectedLanguage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Continue as Guest Button
        OutlinedButton(
            onClick = onContinueAsGuest,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("continue_guest_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White.copy(alpha = 0.85f)
            )
        ) {
            Text(
                text = Strings.continueAsGuest(selectedLanguage),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Don't have an account prompt
        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.testTag("navigate_signup_button")
        ) {
            Text(
                text = Strings.noAccountPrompt(selectedLanguage),
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
