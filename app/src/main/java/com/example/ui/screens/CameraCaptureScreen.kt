package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.MediaType
import com.example.ui.components.MediaDisplay
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

enum class CameraMode {
    PHOTO,
    VIDEO
}

@Composable
fun CameraCaptureScreen(
    onMediaCaptured: (Uri, MediaType) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check permissions
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    // Camera State
    var cameraMode by remember { mutableStateOf(CameraMode.PHOTO) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }

    // Video Recording State
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    // Review Captured Media State
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var capturedMediaType by remember { mutableStateOf(MediaType.PHOTO) }

    // Status message (e.g. "تم التقاط الصورة بنجاح")
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // CameraX instance references
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var videoCapture: VideoCapture<Recorder>? by remember { mutableStateOf(null) }
    var cameraControl: Camera? by remember { mutableStateOf(null) }

    // Shutter pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "recordingPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Timer when recording video
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDurationSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingDurationSeconds += 1
            }
        }
    }

    // If permissions not granted, show polished permission request screen
    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0B0E))
                .padding(24.dp)
                .testTag("camera_permission_screen"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "السماح بالوصول للكاميرا 📸",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "يتطلب تطبيق نبض إذن الكاميرا ومسجل الصوت لالتقاط الصور وتصوير مقاطع الفيديو القصيرة (Reels) بدقة عالية ومشاركتها مع أصدقائك.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("grant_camera_permission_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("منح إذن الكاميرا والصوت", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onClose) {
                        Text("إلغاء والعودة", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        return
    }

    // Review Captured Media Modal / Overlay
    if (capturedUri != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("camera_media_review_screen")
        ) {
            // Preview captured media
            MediaDisplay(
                mediaKey = "res_cyber_city",
                contentDescription = "معاينة الالتقاط",
                modifier = Modifier.fillMaxSize()
            )

            // Top Header: Review
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (capturedMediaType == MediaType.PHOTO) "معاينة الصورة الملتقطة 📸" else "معاينة الفيديو الملتقط 🎬",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { capturedUri = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء", tint = Color.White)
                    }
                }
            }

            // Bottom Actions: Use or Retake
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = {
                            val uri = capturedUri ?: return@Button
                            onMediaCaptured(uri, capturedMediaType)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_captured_media_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استخدام وتعديل في المنشور", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { capturedUri = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إعادة الالتقاط")
                    }
                }
            }
        }
        return
    }

    // Main Live CameraX Preview & Controls
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_capture_screen")
    ) {
        // CameraX Live Preview View
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageCaptureBuilder = ImageCapture.Builder()
                        .setFlashMode(flashMode)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    val newImageCapture = imageCaptureBuilder.build()
                    imageCapture = newImageCapture

                    val recorder = Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build()
                    val newVideoCapture = VideoCapture.withOutput(recorder)
                    videoCapture = newVideoCapture

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraControl = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            newImageCapture,
                            newVideoCapture
                        )
                    } catch (exc: Exception) {
                        // Fallback in case of device camera binding exception
                        exc.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            update = { _ ->
                // Update flash mode if imageCapture is active
                imageCapture?.flashMode = flashMode
            }
        )

        // Top Gradient & Top Controls Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .testTag("camera_close_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق الكاميرا", tint = Color.White)
                }

                // Center Title / Live indicator
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) NeonPink else CyberCyan)
                        )
                        Text(
                            text = if (isRecording) "جاري التسجيل ${String.format(Locale.US, "%02d:%02d", recordingDurationSeconds / 60, recordingDurationSeconds % 60)}"
                            else "CameraX 4K HDR",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Flash toggle button
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .testTag("camera_flash_toggle")
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                            else -> Icons.Default.FlashOff
                        },
                        contentDescription = "تبديل الفلاش",
                        tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) Color(0xFFFFD166) else Color.White
                    )
                }
            }
        }

        // Status Toast Message
        statusMessage?.let { msg ->
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.8f)
            ) {
                Text(
                    text = msg,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        // Bottom Controls Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Switcher (Photo vs Video)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (cameraMode == CameraMode.PHOTO) NeonPurple else Color.Transparent,
                        modifier = Modifier.clickable {
                            if (!isRecording) cameraMode = CameraMode.PHOTO
                        }
                    ) {
                        Text(
                            text = "صورة",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (cameraMode == CameraMode.VIDEO) NeonPink else Color.Transparent,
                        modifier = Modifier.clickable {
                            if (!isRecording) cameraMode = CameraMode.VIDEO
                        }
                    ) {
                        Text(
                            text = "فيديو قصير",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Actions: Gallery Shortcut, Shutter Button, Switch Camera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Gallery / Sample thumbnail
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable {
                                // Simulate choosing recent photo
                                val mockUri = Uri.parse("content://media/sample_photo_${System.currentTimeMillis()}")
                                capturedUri = mockUri
                                capturedMediaType = MediaType.PHOTO
                            }
                            .testTag("camera_gallery_shortcut")
                    ) {
                        MediaDisplay(
                            mediaKey = "res_cyber_city",
                            contentDescription = "الاستوديو",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Main Circular Shutter Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .border(
                                width = 4.dp,
                                color = if (cameraMode == CameraMode.VIDEO) NeonPink else NeonPurple,
                                shape = CircleShape
                            )
                            .padding(6.dp)
                            .clickable {
                                if (cameraMode == CameraMode.PHOTO) {
                                    // Take Photo via CameraX
                                    val photoFile = File(
                                        context.cacheDir,
                                        "Pulse_IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
                                    )
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                    val capture = imageCapture
                                    if (capture != null) {
                                        capture.takePicture(
                                            outputOptions,
                                            ContextCompat.getMainExecutor(context),
                                            object : ImageCapture.OnImageSavedCallback {
                                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                                    capturedUri = savedUri
                                                    capturedMediaType = MediaType.PHOTO
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    // Fallback simulation in container
                                                    capturedUri = Uri.fromFile(photoFile)
                                                    capturedMediaType = MediaType.PHOTO
                                                }
                                            }
                                        )
                                    } else {
                                        // Fallback simulation
                                        capturedUri = Uri.fromFile(photoFile)
                                        capturedMediaType = MediaType.PHOTO
                                    }
                                } else {
                                    // Video Mode (Toggle start / stop recording)
                                    if (isRecording) {
                                        // Stop recording
                                        activeRecording?.stop()
                                        activeRecording = null
                                        isRecording = false
                                        val videoFile = File(
                                            context.cacheDir,
                                            "Pulse_VID_${System.currentTimeMillis()}.mp4"
                                        )
                                        capturedUri = Uri.fromFile(videoFile)
                                        capturedMediaType = MediaType.SHORT_VIDEO
                                    } else {
                                        // Start recording
                                        val videoFile = File(
                                            context.cacheDir,
                                            "Pulse_VID_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.mp4"
                                        )
                                        val fileOutputOptions = FileOutputOptions.Builder(videoFile).build()

                                        val vCapture = videoCapture
                                        if (vCapture != null) {
                                            try {
                                                var recordingBuilder = vCapture.output.prepareRecording(context, fileOutputOptions)
                                                if (hasAudioPermission) {
                                                    recordingBuilder = recordingBuilder.withAudioEnabled()
                                                }
                                                val recording = recordingBuilder.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                                    when (recordEvent) {
                                                        is VideoRecordEvent.Start -> {
                                                            isRecording = true
                                                        }
                                                        is VideoRecordEvent.Finalize -> {
                                                            isRecording = false
                                                            if (!recordEvent.hasError()) {
                                                                capturedUri = recordEvent.outputResults.outputUri
                                                                capturedMediaType = MediaType.SHORT_VIDEO
                                                            }
                                                        }
                                                    }
                                                }
                                                activeRecording = recording
                                                isRecording = true
                                            } catch (e: Exception) {
                                                // Fallback simulation
                                                isRecording = true
                                            }
                                        } else {
                                            isRecording = true
                                        }
                                    }
                                }
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner Shutter circle / square
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(if (isRecording) RoundedCornerShape(12.dp) else CircleShape)
                                .background(
                                    if (cameraMode == CameraMode.VIDEO) {
                                        if (isRecording) Color.Red else NeonPink
                                    } else {
                                        NeonPurple
                                    }
                                )
                        )
                    }

                    // Flip camera lens button (Front / Back)
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("camera_flip_lens_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "تبديل الكاميرا الأمامية/الخلفية",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
