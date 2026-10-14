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
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

@Composable
fun SignUpScreen(
    selectedLanguage: AppLanguage,
    onSignUpSuccess: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            .testTag("signup_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Pigeon Logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.dp, Brush.linearGradient(listOf(NeonPurple, NeonPink)), CircleShape)
                .padding(3.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = Strings.signupTitle(selectedLanguage),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Text(
            text = "انضم إلى مجتمع الزاجل لمشاركة أروع اللحظات",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.65f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Full Name Field
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                errorMessage = null
            },
            label = { Text(Strings.fullNameLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonPurple)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_fullname_field"),
            shape = RoundedCornerShape(16.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Username Field
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = null
            },
            label = { Text(Strings.usernameLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.AlternateEmail, contentDescription = null, tint = NeonPurple)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_username_field"),
            shape = RoundedCornerShape(16.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = { Text(Strings.emailLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonPurple)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_email_field"),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = { Text(Strings.passwordLabel(selectedLanguage)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurple)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "تبديل الرؤية",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_password_field"),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            label = {
                Text(
                    if (selectedLanguage == AppLanguage.ARABIC) "تأكيد كلمة المرور" else "Confirm Password"
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = NeonPurple)
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("signup_confirm_password_field"),
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

        // Error message
        errorMessage?.let { error ->
            Text(
                text = error,
                color = NeonPink,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Sign Up Action Button
        Button(
            onClick = {
                when {
                    fullName.isBlank() || email.isBlank() || password.isBlank() -> {
                        errorMessage = if (selectedLanguage == AppLanguage.ARABIC)
                            "يرجى ملء جميع الحقول المطلوبة"
                        else "Please fill all required fields"
                    }
                    password != confirmPassword -> {
                        errorMessage = if (selectedLanguage == AppLanguage.ARABIC)
                            "كلمات المرور غير متطابقة"
                        else "Passwords do not match"
                    }
                    else -> {
                        onSignUpSuccess(fullName, email)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_signup_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            Text(
                text = Strings.signupTitle(selectedLanguage),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Already have an account prompt
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.testTag("navigate_login_button")
        ) {
            Text(
                text = Strings.haveAccountPrompt(selectedLanguage),
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
