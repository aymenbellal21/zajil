package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MediaDisplay
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay

enum class ForgotPasswordStep {
    ENTER_EMAIL,
    VERIFY_CODE,
    SET_NEW_PASSWORD,
    SUCCESS
}

@Composable
fun ForgotPasswordScreen(
    selectedLanguage: AppLanguage,
    onBackToLogin: () -> Unit,
    onResetCompleted: () -> Unit
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.ENTER_EMAIL) }
    var emailOrPhone by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var timerSeconds by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    // Resend countdown timer
    LaunchedEffect(step, canResend) {
        if (step == ForgotPasswordStep.VERIFY_CODE && !canResend) {
            timerSeconds = 60
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            canResend = true
        }
    }

    // Password strength evaluation
    val passLengthOk = newPassword.length >= 8
    val hasNumber = newPassword.any { it.isDigit() }
    val hasSpecial = newPassword.any { !it.isLetterOrDigit() }
    val passwordStrength = remember(newPassword) {
        var score = 0
        if (passLengthOk) score++
        if (hasNumber) score++
        if (hasSpecial) score++
        score
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0D14),
                        Color(0xFF140F22),
                        Color(0xFF09080E)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("forgot_password_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Back Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    when (step) {
                        ForgotPasswordStep.ENTER_EMAIL -> onBackToLogin()
                        ForgotPasswordStep.VERIFY_CODE -> step = ForgotPasswordStep.ENTER_EMAIL
                        ForgotPasswordStep.SET_NEW_PASSWORD -> step = ForgotPasswordStep.VERIFY_CODE
                        ForgotPasswordStep.SUCCESS -> onBackToLogin()
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .testTag("forgot_password_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "الرجوع",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "استعادة الحساب والأمان",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Shield Security Icon Header
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonPurple.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .border(2.dp, Brush.linearGradient(listOf(NeonPurple, CyberCyan)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (step) {
                    ForgotPasswordStep.ENTER_EMAIL -> Icons.Default.LockReset
                    ForgotPasswordStep.VERIFY_CODE -> Icons.Default.VerifiedUser
                    ForgotPasswordStep.SET_NEW_PASSWORD -> Icons.Default.Key
                    ForgotPasswordStep.SUCCESS -> Icons.Default.CheckCircle
                },
                contentDescription = "أيقونة الأمان",
                tint = if (step == ForgotPasswordStep.SUCCESS) GhostGreen else CyberCyan,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Step Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepDot(active = true, completed = step != ForgotPasswordStep.ENTER_EMAIL, label = "1")
            StepDivider(active = step != ForgotPasswordStep.ENTER_EMAIL)
            StepDot(active = step != ForgotPasswordStep.ENTER_EMAIL, completed = step == ForgotPasswordStep.SET_NEW_PASSWORD || step == ForgotPasswordStep.SUCCESS, label = "2")
            StepDivider(active = step == ForgotPasswordStep.SET_NEW_PASSWORD || step == ForgotPasswordStep.SUCCESS)
            StepDot(active = step == ForgotPasswordStep.SET_NEW_PASSWORD || step == ForgotPasswordStep.SUCCESS, completed = step == ForgotPasswordStep.SUCCESS, label = "3")
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Dynamic Step Content
        AnimatedContent(targetState = step, label = "forgot_pass_step") { currentStep ->
            when (currentStep) {
                ForgotPasswordStep.ENTER_EMAIL -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "نسيت كلمة المرور؟",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "أدخل بريدك الإلكتروني أو رقم الهاتف المرتبط بحسابك على الزاجل لإرسال رمز تحقق أمني مشفر.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 19.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = emailOrPhone,
                            onValueChange = {
                                emailOrPhone = it
                                errorMessage = null
                            },
                            label = { Text("البريد الإلكتروني أو رقم الهاتف") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = NeonPurple)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_pass_email_field"),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedContainerColor = Color(0xFF191629),
                                unfocusedContainerColor = Color(0xFF13111E)
                            ),
                            singleLine = true
                        )

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = NeonPink,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (emailOrPhone.isBlank()) {
                                    errorMessage = "يرجى كتابة البريد الإلكتروني أو رقم الهاتف أولاً"
                                } else {
                                    step = ForgotPasswordStep.VERIFY_CODE
                                    canResend = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("send_code_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text(
                                text = "إرسال رمز التحقق الأمني (OTP)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                ForgotPasswordStep.VERIFY_CODE -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "التحقق من الكود الأمني",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "أدخل رمز التحقق (OTP) المكون من 6 أرقام المرسل إلى:\n$emailOrPhone",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 19.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Security Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GhostGreen.copy(alpha = 0.15f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GhostGreen, CyberCyan)))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GhostGreen, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "حماية مشفرة ضد الهجمات والتخمين",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GhostGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = {
                                if (it.length <= 6) {
                                    otpCode = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("رمز التحقق (مثال: 482910)") },
                            leadingIcon = {
                                Icon(Icons.Default.Pin, contentDescription = null, tint = CyberCyan)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_field"),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedContainerColor = Color(0xFF191629),
                                unfocusedContainerColor = Color(0xFF13111E)
                            ),
                            singleLine = true
                        )

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = NeonPink,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resend timer row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (!canResend) "إعادة الإرسال بعد $timerSeconds ثانية" else "يمكنك طلب رمز جديد الآن",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            TextButton(
                                onClick = {
                                    if (canResend) {
                                        canResend = false
                                        errorMessage = "تمت إعادة إرسال رمز التحقق بنجاح ✓"
                                    }
                                },
                                enabled = canResend
                            ) {
                                Text(
                                    text = "إعادة إرسال",
                                    color = if (canResend) CyberCyan else Color.White.copy(alpha = 0.3f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (otpCode.length < 4) {
                                    errorMessage = "يرجى إدخال رمز التحقق كاملاً (4 إلى 6 أرقام)"
                                } else {
                                    step = ForgotPasswordStep.SET_NEW_PASSWORD
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("verify_otp_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text(
                                text = "تأكيد الرمز والمتابعة",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                ForgotPasswordStep.SET_NEW_PASSWORD -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تعيين كلمة مرور جديدة",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اختر كلمة مرور قوية تتضمن أرقاماً ورموزاً لحماية حسابك من الاختراق.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 19.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // New password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                errorMessage = null
                            },
                            label = { Text("كلمة المرور الجديدة") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_password_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedContainerColor = Color(0xFF191629),
                                unfocusedContainerColor = Color(0xFF13111E)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Strength meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (passwordStrength >= 1) NeonPink else Color.White.copy(alpha = 0.15f))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (passwordStrength >= 2) Color(0xFFFFB300) else Color.White.copy(alpha = 0.15f))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (passwordStrength >= 3) GhostGreen else Color.White.copy(alpha = 0.15f))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (passwordStrength) {
                                    0 -> "قوة كلمة المرور: ضعيفة"
                                    1 -> "قوة كلمة المرور: متوسطة"
                                    2 -> "قوة كلمة المرور: جيدة"
                                    else -> "قوة كلمة المرور: ممتازة وقوية جداً 🛡️"
                                },
                                fontSize = 11.sp,
                                color = when (passwordStrength) {
                                    0 -> NeonPink
                                    1 -> Color(0xFFFFB300)
                                    else -> GhostGreen
                                },
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (passLengthOk) "8+ أحرف ✓" else "8 أحرف كحد أدنى",
                                fontSize = 11.sp,
                                color = if (passLengthOk) GhostGreen else Color.White.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Confirm password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                errorMessage = null
                            },
                            label = { Text("تأكيد كلمة المرور الجديدة") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurple)
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_field"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                                focusedContainerColor = Color(0xFF191629),
                                unfocusedContainerColor = Color(0xFF13111E)
                            ),
                            singleLine = true
                        )

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = NeonPink,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                when {
                                    newPassword.length < 8 -> errorMessage = "يجب ألا تقل كلمة المرور عن 8 خانات"
                                    newPassword != confirmPassword -> errorMessage = "كلمتا المرور غير متطابقتين"
                                    else -> {
                                        step = ForgotPasswordStep.SUCCESS
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_new_password_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text(
                                text = "حفظ وتحديث كلمة المرور",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                ForgotPasswordStep.SUCCESS -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تم تحديث كلمة المرور بنجاح! 🎉",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "تم تأمين حسابك بنجاح بواسطة بروتوكول التحقق الأمني المزدوج. يمكنك الآن تسجيل الدخول بكلمة المرور الجديدة.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(
                            onClick = onResetCompleted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("back_to_login_success_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GhostGreen)
                        ) {
                            Text(
                                text = "العودة لتسجيل الدخول الآن",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StepDot(active: Boolean, completed: Boolean, label: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                when {
                    completed -> GhostGreen
                    active -> NeonPurple
                    else -> Color.White.copy(alpha = 0.15f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (completed) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
        } else {
            Text(
                text = label,
                color = if (active) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun StepDivider(active: Boolean) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(3.dp)
            .background(if (active) NeonPurple else Color.White.copy(alpha = 0.15f))
    )
}
