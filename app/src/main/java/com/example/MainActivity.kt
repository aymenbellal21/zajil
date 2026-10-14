package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.SocialViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SocialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authState by viewModel.authState.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val uiPrefs by viewModel.uiPrefs.collectAsStateWithLifecycle()

            val currentUser by viewModel.currentUserFlow.collectAsStateWithLifecycle()
            val posts by viewModel.posts.collectAsStateWithLifecycle()
            val stories by viewModel.stories.collectAsStateWithLifecycle()
            val scheduledPosts by viewModel.scheduledPosts.collectAsStateWithLifecycle()
            val chats by viewModel.chats.collectAsStateWithLifecycle()
            val notifications by viewModel.notifications.collectAsStateWithLifecycle()
            val savedCollections by viewModel.savedCollections.collectAsStateWithLifecycle()
            val liveAudioRooms by viewModel.liveAudioRooms.collectAsStateWithLifecycle()
            val activeAudioRoom by viewModel.activeAudioRoom.collectAsStateWithLifecycle()
            val audioPlaybackState by viewModel.audioPlaybackState.collectAsStateWithLifecycle()

            val analyticsReport by viewModel.analytics.collectAsStateWithLifecycle()
            val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
            val activeChat by viewModel.activeChat.collectAsStateWithLifecycle()
            val activeCommentPost by viewModel.activeCommentPost.collectAsStateWithLifecycle()

            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val searchCategory by viewModel.searchCategory.collectAsStateWithLifecycle()

            val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
            val filterBrightness by viewModel.filterBrightness.collectAsStateWithLifecycle()
            val filterContrast by viewModel.filterContrast.collectAsStateWithLifecycle()
            val filterSaturation by viewModel.filterSaturation.collectAsStateWithLifecycle()
            val postCaption by viewModel.postCaption.collectAsStateWithLifecycle()
            val isScheduled by viewModel.isScheduled.collectAsStateWithLifecycle()
            val scheduledDate by viewModel.scheduledDate.collectAsStateWithLifecycle()
            val scheduledTime by viewModel.scheduledTime.collectAsStateWithLifecycle()
            val selectedMediaType by viewModel.selectedMediaType.collectAsStateWithLifecycle()
            val capturedMediaUri by viewModel.capturedMediaUri.collectAsStateWithLifecycle()

            val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
            val recordingDuration by viewModel.recordingDuration.collectAsStateWithLifecycle()
            val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()

            var showSupportDialog by remember { mutableStateOf(false) }

            // Handle system back navigation
            BackHandler(enabled = authState != AuthState.AUTHENTICATED || currentScreen != Screen.HOME || activeChat != null || activeAudioRoom != null) {
                if (activeChat != null) {
                    viewModel.closeChat()
                } else if (activeAudioRoom != null) {
                    viewModel.closeAudioRoom()
                } else if (authState == AuthState.SIGNUP || authState == AuthState.FORGOT_PASSWORD) {
                    viewModel.navigateToLogin()
                } else if (currentScreen == Screen.CAMERA) {
                    viewModel.navigateTo(Screen.CREATE)
                } else if (currentScreen == Screen.CREATE_STORY) {
                    viewModel.navigateTo(Screen.HOME)
                } else if (currentScreen == Screen.EDIT_PROFILE || currentScreen == Screen.SAVED_POSTS) {
                    viewModel.navigateTo(Screen.PROFILE)
                } else if (currentScreen != Screen.HOME) {
                    viewModel.navigateTo(Screen.HOME)
                }
            }

            // Multi-language RTL / LTR layout direction
            val layoutDirection = if (uiPrefs.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MyApplicationTheme(
                    darkTheme = uiPrefs.isDarkMode,
                    isGhostMode = uiPrefs.isGhostMode
                ) {
                    when (authState) {
                        AuthState.SPLASH -> {
                            SplashScreen(
                                selectedLanguage = uiPrefs.language,
                                onAnimationFinished = { viewModel.onSplashFinished() }
                            )
                        }

                        AuthState.LOGIN -> {
                            LoginScreen(
                                selectedLanguage = uiPrefs.language,
                                onLoginSuccess = { email -> viewModel.login(email) },
                                onNavigateToSignUp = { viewModel.navigateToSignUp() },
                                onContinueAsGuest = { viewModel.continueAsGuest() },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onNavigateToForgotPassword = { viewModel.navigateToForgotPassword() }
                            )
                        }

                        AuthState.SIGNUP -> {
                            SignUpScreen(
                                selectedLanguage = uiPrefs.language,
                                onSignUpSuccess = { name, email -> viewModel.signup(name, email) },
                                onNavigateToLogin = { viewModel.navigateToLogin() }
                            )
                        }

                        AuthState.FORGOT_PASSWORD -> {
                            ForgotPasswordScreen(
                                selectedLanguage = uiPrefs.language,
                                onBackToLogin = { viewModel.navigateToLogin() },
                                onResetCompleted = { viewModel.onPasswordResetSuccess() }
                            )
                        }

                        AuthState.AUTHENTICATED -> {
                            val unreadMessagesCount = remember(chats) {
                                chats.sumOf { it.unreadCount }
                            }
                            val unreadNotificationsCount = remember(notifications) {
                                notifications.count { !it.isRead }
                            }

                            val hideTopAndBottomBars = currentScreen == Screen.REELS ||
                                    currentScreen == Screen.CAMERA ||
                                    currentScreen == Screen.CREATE_STORY ||
                                    currentScreen == Screen.AUDIO_ROOM ||
                                    activeChat != null

                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    if (!hideTopAndBottomBars) {
                                        PulseTopBar(
                                            isGhostMode = uiPrefs.isGhostMode,
                                            isDarkMode = uiPrefs.isDarkMode,
                                            selectedLanguage = uiPrefs.language,
                                            unreadMessagesCount = unreadMessagesCount,
                                            unreadNotificationsCount = unreadNotificationsCount,
                                            onGhostModeToggle = { viewModel.toggleGhostMode() },
                                            onDarkModeToggle = { viewModel.toggleDarkMode() },
                                            onToggleLanguage = { viewModel.toggleLanguage() },
                                            onNavigateToMessages = { viewModel.navigateTo(Screen.MESSAGES) },
                                            onNavigateToAnalytics = { viewModel.navigateTo(Screen.ANALYTICS) },
                                            onNavigateToNotifications = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                                            onNavigateToAudioRoom = { viewModel.navigateTo(Screen.AUDIO_ROOM) }
                                        )
                                    }
                                },
                                bottomBar = {
                                    if (!hideTopAndBottomBars && currentScreen != Screen.NOTIFICATIONS && currentScreen != Screen.EDIT_PROFILE && currentScreen != Screen.SAVED_POSTS) {
                                        PulseBottomBar(
                                            currentScreen = currentScreen,
                                            selectedLanguage = uiPrefs.language,
                                            onNavigate = { viewModel.navigateTo(it) }
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(if (hideTopAndBottomBars) PaddingValues(0.dp) else innerPadding)
                                ) {
                                    when (currentScreen) {
                                        Screen.HOME -> {
                                            HomeScreen(
                                                posts = posts,
                                                stories = stories,
                                                scheduledPosts = scheduledPosts,
                                                currentUser = currentUser,
                                                onStoryClick = { viewModel.openStory(it) },
                                                onAddStoryClick = { viewModel.navigateTo(Screen.CREATE_STORY) },
                                                onLikePost = { viewModel.toggleLike(it) },
                                                onSavePost = { viewModel.toggleSave(it) },
                                                onCommentPost = { viewModel.openComments(it) },
                                                onAnalyticsClick = { viewModel.navigateTo(Screen.ANALYTICS) },
                                                onPublishScheduledNow = { viewModel.publishScheduledNow(it) },
                                                onDeleteScheduled = { viewModel.deleteScheduled(it) },
                                                onNavigateToCreate = { viewModel.navigateTo(Screen.CREATE) },
                                                onNavigateToAudioRoom = { viewModel.navigateTo(Screen.AUDIO_ROOM) }
                                            )
                                        }

                                        Screen.REELS -> {
                                            ReelsScreen(
                                                reels = posts,
                                                onLikePost = { viewModel.toggleLike(it) },
                                                onCommentPost = { viewModel.openComments(it) }
                                            )
                                        }

                                        Screen.CAMERA -> {
                                            CameraCaptureScreen(
                                                onMediaCaptured = { uri, mediaType ->
                                                    viewModel.onMediaCaptured(uri.toString(), mediaType)
                                                },
                                                onClose = {
                                                    viewModel.navigateTo(Screen.CREATE)
                                                }
                                            )
                                        }

                                        Screen.CREATE -> {
                                            CreatePostScreen(
                                                caption = postCaption,
                                                selectedFilter = selectedFilter,
                                                brightness = filterBrightness,
                                                contrast = filterContrast,
                                                saturation = filterSaturation,
                                                isScheduled = isScheduled,
                                                scheduledDate = scheduledDate,
                                                scheduledTime = scheduledTime,
                                                selectedMediaType = selectedMediaType,
                                                capturedMediaUri = capturedMediaUri,
                                                onOpenCamera = { viewModel.navigateTo(Screen.CAMERA) },
                                                onCaptionChange = { viewModel.setPostCaption(it) },
                                                onFilterSelect = { viewModel.selectFilter(it) },
                                                onBrightnessChange = { viewModel.setBrightness(it) },
                                                onContrastChange = { viewModel.setContrast(it) },
                                                onSaturationChange = { viewModel.setSaturation(it) },
                                                onAutoEnhance = { viewModel.autoEnhance() },
                                                onResetAdjustments = { viewModel.resetAdjustments() },
                                                onMediaTypeChange = { viewModel.setMediaType(it) },
                                                onScheduledToggle = { viewModel.setIsScheduled(it) },
                                                onScheduledDateChange = { viewModel.setScheduledDate(it) },
                                                onScheduledTimeChange = { viewModel.setScheduledTime(it) },
                                                onGenerateAiCaption = { viewModel.generateAiCaption(it) },
                                                onPublish = { viewModel.publishPost() }
                                            )
                                        }

                                        Screen.CREATE_STORY -> {
                                            CreateStoryScreen(
                                                onClose = { viewModel.navigateTo(Screen.HOME) },
                                                onOpenCamera = { viewModel.navigateTo(Screen.CAMERA) },
                                                onPublishStory = { mediaUrl, caption, isVideo, stickers ->
                                                    viewModel.publishStoryWithMedia(mediaUrl, caption, isVideo, stickers)
                                                    viewModel.navigateTo(Screen.HOME)
                                                }
                                            )
                                        }

                                        Screen.EXPLORE -> {
                                            ExploreSearchScreen(
                                                searchQuery = searchQuery,
                                                selectedCategory = searchCategory,
                                                posts = posts,
                                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                                onCategoryChange = { viewModel.setSearchCategory(it) },
                                                onPostClick = { viewModel.openComments(it) }
                                            )
                                        }

                                        Screen.MESSAGES -> {
                                            MessagesScreen(
                                                chats = chats,
                                                activeChat = activeChat,
                                                isGhostMode = uiPrefs.isGhostMode,
                                                isRecordingVoice = isRecordingVoice,
                                                recordingDuration = recordingDuration,
                                                audioPlaybackState = audioPlaybackState,
                                                onOpenChat = { viewModel.openChat(it) },
                                                onCloseChat = { viewModel.closeChat() },
                                                onSendTextMessage = { chatId, text -> viewModel.sendTextMessage(chatId, text) },
                                                onStartVoiceRecording = { viewModel.startVoiceRecording() },
                                                onStopAndSendVoiceRecording = { chatId -> viewModel.stopAndSendVoiceRecording(chatId) },
                                                onCancelVoiceRecording = { viewModel.cancelVoiceRecording() },
                                                onToggleAudioPlay = { id, dur -> viewModel.toggleAudioPlayback(id, dur) },
                                                onToggleAudioSpeed = { viewModel.toggleAudioPlaybackSpeed() },
                                                onSeekAudio = { viewModel.seekAudioProgress(it) }
                                            )
                                        }

                                        Screen.ANALYTICS -> {
                                            AnalyticsScreen(report = analyticsReport)
                                        }

                                        Screen.PROFILE -> {
                                            ProfileAndSettingsScreen(
                                                user = currentUser,
                                                posts = posts,
                                                uiPrefs = uiPrefs,
                                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                                onToggleGhostMode = { viewModel.toggleGhostMode() },
                                                onToggleLanguage = { viewModel.toggleLanguage() },
                                                onSelectLanguage = { viewModel.setLanguage(it) },
                                                onSelectAccent = { viewModel.setAccentColor(it) },
                                                onSelectFeedLayout = { viewModel.setFeedLayout(it) },
                                                onTriggerBackup = { viewModel.triggerCloudBackup() },
                                                onOpenSupport = { showSupportDialog = true },
                                                onLogout = { viewModel.logout() },
                                                onNavigateToEditProfile = { viewModel.navigateTo(Screen.EDIT_PROFILE) },
                                                onNavigateToSaved = { viewModel.navigateTo(Screen.SAVED_POSTS) },
                                                onNavigateToForgotPassword = { viewModel.navigateToForgotPassword() }
                                            )
                                        }

                                        Screen.NOTIFICATIONS -> {
                                            NotificationsScreen(
                                                notifications = notifications,
                                                selectedLanguage = uiPrefs.language,
                                                onBack = { viewModel.navigateTo(Screen.HOME) },
                                                onMarkAllRead = { viewModel.markAllNotificationsAsRead() }
                                            )
                                        }

                                        Screen.EDIT_PROFILE -> {
                                            EditProfileScreen(
                                                user = currentUser,
                                                selectedLanguage = uiPrefs.language,
                                                onBack = { viewModel.navigateTo(Screen.PROFILE) },
                                                onSaveProfile = { name, bio, handle, avatar ->
                                                    viewModel.updateUserProfile(name, bio, handle, avatar)
                                                }
                                            )
                                        }

                                        Screen.SAVED_POSTS -> {
                                            SavedPostsScreen(
                                                savedPosts = posts.filter { it.isSaved },
                                                collections = savedCollections,
                                                selectedLanguage = uiPrefs.language,
                                                onBack = { viewModel.navigateTo(Screen.PROFILE) },
                                                onCreateCollection = { title, icon ->
                                                    viewModel.createCollection(title, icon)
                                                },
                                                onPostClick = { viewModel.openComments(it) },
                                                onUnsavePost = { viewModel.toggleSave(it) }
                                            )
                                        }

                                        Screen.AUDIO_ROOM -> {
                                            LiveAudioRoomScreen(
                                                rooms = liveAudioRooms,
                                                activeRoom = activeAudioRoom,
                                                currentUser = currentUser,
                                                selectedLanguage = uiPrefs.language,
                                                onBack = {
                                                    if (activeAudioRoom != null) viewModel.closeAudioRoom()
                                                    else viewModel.navigateTo(Screen.HOME)
                                                },
                                                onSelectRoom = { viewModel.openAudioRoom(it) },
                                                onLeaveRoom = { viewModel.closeAudioRoom() },
                                                onToggleSpeaking = { viewModel.toggleRoomSpeaking(it) },
                                                onRaiseHand = { viewModel.raiseHandInRoom(it) }
                                            )
                                        }
                                    }
                                }

                                // Dialogs & Sheets
                                activeStory?.let { story ->
                                    StoryViewerDialog(
                                        story = story,
                                        isGhostMode = uiPrefs.isGhostMode,
                                        onDismiss = { viewModel.closeStory() },
                                        onVotePoll = { storyId, stickerId, optionIndex ->
                                            viewModel.voteStoryPoll(storyId, stickerId, optionIndex)
                                        },
                                        onAnswerQuestion = { storyId, stickerId, answer ->
                                            viewModel.answerStoryQuestion(storyId, stickerId, answer)
                                        },
                                        onSendReaction = { storyId, stickerId ->
                                            viewModel.sendStoryReaction(storyId, stickerId)
                                        }
                                    )
                                }

                                activeCommentPost?.let { post ->
                                    CommentsBottomSheet(
                                        post = post,
                                        onDismiss = { viewModel.closeComments() },
                                        onAddComment = { text -> viewModel.addComment(post.id, text) }
                                    )
                                }

                                if (showSupportDialog) {
                                    SupportDialog(
                                        messages = supportMessages,
                                        onDismiss = { showSupportDialog = false },
                                        onSendMessage = { viewModel.sendSupportQuestion(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
