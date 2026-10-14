package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.filter.FilterEngine
import com.example.data.model.*
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.ui.localization.AppLanguage

enum class Screen {
    HOME,
    REELS,
    CREATE,
    CAMERA,
    EXPLORE,
    MESSAGES,
    ANALYTICS,
    PROFILE,
    NOTIFICATIONS,
    EDIT_PROFILE,
    SAVED_POSTS,
    AUDIO_ROOM,
    CREATE_STORY
}

enum class AuthState {
    SPLASH,
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    AUTHENTICATED
}

data class UiPreferences(
    val language: AppLanguage = AppLanguage.ARABIC,
    val isDarkMode: Boolean = true,
    val isGhostMode: Boolean = false,
    val selectedAccentHex: Long = 0xFF802FFF,
    val feedLayout: FeedLayout = FeedLayout.CARDS,
    val cloudBackupActive: Boolean = true,
    val lastBackupText: String = "اليوم 04:15 ص"
) {
    val isArabic: Boolean get() = language == AppLanguage.ARABIC
}

data class SupportMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val time: String = "الآن"
)

class SocialViewModel(
    private val repository: SocialRepository = SocialRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.SPLASH)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _uiPrefs = MutableStateFlow(UiPreferences())
    val uiPrefs: StateFlow<UiPreferences> = _uiPrefs.asStateFlow()

    // Data from repository
    val currentUser = repository.currentUser
    val currentUserFlow = repository.currentUserFlow
    val posts = repository.posts
    val stories = repository.stories
    val scheduledPosts = repository.scheduledPosts
    val chats = repository.chats
    val notifications = repository.notifications
    val analytics = repository.analytics
    val savedCollections = repository.savedCollections
    val liveAudioRooms = repository.liveAudioRooms

    // Active Audio Room
    private val _activeAudioRoom = MutableStateFlow<LiveAudioRoom?>(null)
    val activeAudioRoom: StateFlow<LiveAudioRoom?> = _activeAudioRoom.asStateFlow()

    // Audio Playback
    private val _audioPlaybackState = MutableStateFlow(AudioPlaybackState())
    val audioPlaybackState: StateFlow<AudioPlaybackState> = _audioPlaybackState.asStateFlow()

    // Active Story Viewer
    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    // Active Chat in Messages
    private val _activeChat = MutableStateFlow<DirectChat?>(null)
    val activeChat: StateFlow<DirectChat?> = _activeChat.asStateFlow()

    // Search query & filter in Explore
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchCategory = MutableStateFlow("الكل")
    val searchCategory: StateFlow<String> = _searchCategory.asStateFlow()

    // Post creation state
    private val _selectedFilter = MutableStateFlow(FilterEngine.availableFilters[0])
    val selectedFilter: StateFlow<PhotoFilter> = _selectedFilter.asStateFlow()

    private val _filterBrightness = MutableStateFlow(0f)
    val filterBrightness: StateFlow<Float> = _filterBrightness.asStateFlow()

    private val _filterContrast = MutableStateFlow(0f)
    val filterContrast: StateFlow<Float> = _filterContrast.asStateFlow()

    private val _filterSaturation = MutableStateFlow(0f)
    val filterSaturation: StateFlow<Float> = _filterSaturation.asStateFlow()

    private val _postCaption = MutableStateFlow("")
    val postCaption: StateFlow<String> = _postCaption.asStateFlow()

    private val _isScheduled = MutableStateFlow(false)
    val isScheduled: StateFlow<Boolean> = _isScheduled.asStateFlow()

    private val _scheduledDate = MutableStateFlow("غداً")
    val scheduledDate: StateFlow<String> = _scheduledDate.asStateFlow()

    private val _scheduledTime = MutableStateFlow("08:00 م")
    val scheduledTime: StateFlow<String> = _scheduledTime.asStateFlow()

    private val _selectedMediaType = MutableStateFlow(MediaType.PHOTO)
    val selectedMediaType: StateFlow<MediaType> = _selectedMediaType.asStateFlow()

    private val _capturedMediaUri = MutableStateFlow<String?>(null)
    val capturedMediaUri: StateFlow<String?> = _capturedMediaUri.asStateFlow()

    fun onMediaCaptured(uriString: String, type: MediaType) {
        _capturedMediaUri.value = uriString
        _selectedMediaType.value = type
        _currentScreen.value = Screen.CREATE
    }

    // Voice recording simulation in chat
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0)
    val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()

    // 24/7 AI Support Desk messages
    private val _supportMessages = MutableStateFlow<List<SupportMessage>>(
        listOf(
            SupportMessage(
                id = "s1",
                isUser = false,
                text = "مرحباً بك في مركز الدعم الفني الذكي المتاح 24/7 لنبض Pulse! كيف يمكنني مساعدتك اليوم بخصوص الخصوصية، الفلاتر، أو جدولة النشر؟"
            )
        )
    )
    val supportMessages: StateFlow<List<SupportMessage>> = _supportMessages.asStateFlow()

    // Comments dialog state
    private val _activeCommentPost = MutableStateFlow<Post?>(null)
    val activeCommentPost: StateFlow<Post?> = _activeCommentPost.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _uiPrefs.value = _uiPrefs.value.copy(isDarkMode = !_uiPrefs.value.isDarkMode)
    }

    fun toggleGhostMode() {
        _uiPrefs.value = _uiPrefs.value.copy(isGhostMode = !_uiPrefs.value.isGhostMode)
    }

    fun onSplashFinished() {
        if (_authState.value == AuthState.SPLASH) {
            _authState.value = AuthState.LOGIN
        }
    }

    fun navigateToSignUp() {
        _authState.value = AuthState.SIGNUP
    }

    fun navigateToLogin() {
        _authState.value = AuthState.LOGIN
    }

    fun navigateToForgotPassword() {
        _authState.value = AuthState.FORGOT_PASSWORD
    }

    fun onPasswordResetSuccess() {
        _authState.value = AuthState.LOGIN
    }

    fun login(email: String) {
        _authState.value = AuthState.AUTHENTICATED
        _currentScreen.value = Screen.HOME
    }

    fun signup(fullName: String, email: String) {
        _authState.value = AuthState.AUTHENTICATED
        _currentScreen.value = Screen.HOME
    }

    fun continueAsGuest() {
        _authState.value = AuthState.AUTHENTICATED
        _currentScreen.value = Screen.HOME
    }

    fun logout() {
        _authState.value = AuthState.LOGIN
    }

    fun setLanguage(lang: AppLanguage) {
        _uiPrefs.value = _uiPrefs.value.copy(language = lang)
    }

    fun toggleLanguage() {
        val allLangs = AppLanguage.entries
        val nextIdx = (allLangs.indexOf(_uiPrefs.value.language) + 1) % allLangs.size
        setLanguage(allLangs[nextIdx])
    }

    fun setAccentColor(colorHex: Long) {
        _uiPrefs.value = _uiPrefs.value.copy(selectedAccentHex = colorHex)
    }

    fun setFeedLayout(layout: FeedLayout) {
        _uiPrefs.value = _uiPrefs.value.copy(feedLayout = layout)
    }

    fun openStory(story: Story) {
        _activeStory.value = story
        if (!uiPrefs.value.isGhostMode) {
            repository.markStoryAsViewed(story.id)
        }
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openComments(post: Post) {
        _activeCommentPost.value = post
    }

    fun closeComments() {
        _activeCommentPost.value = null
    }

    fun toggleLike(postId: String) {
        repository.toggleLike(postId)
    }

    fun toggleSave(postId: String) {
        repository.toggleSave(postId)
    }

    fun addComment(postId: String, text: String) {
        repository.addComment(postId, text)
        _activeCommentPost.value = repository.posts.value.find { it.id == postId }
    }

    // Filter adjustments
    fun selectFilter(filter: PhotoFilter) {
        _selectedFilter.value = filter
    }

    fun setBrightness(value: Float) {
        _filterBrightness.value = value
    }

    fun setContrast(value: Float) {
        _filterContrast.value = value
    }

    fun setSaturation(value: Float) {
        _filterSaturation.value = value
    }

    fun autoEnhance() {
        val enhanceFilter = FilterEngine.availableFilters.find { it.id == "ai_enhance" }
            ?: FilterEngine.availableFilters[1]
        _selectedFilter.value = enhanceFilter
        _filterBrightness.value = 0.05f
        _filterContrast.value = 0.2f
        _filterSaturation.value = 0.25f
    }

    fun resetAdjustments() {
        _selectedFilter.value = FilterEngine.availableFilters[0]
        _filterBrightness.value = 0f
        _filterContrast.value = 0f
        _filterSaturation.value = 0f
    }

    // AI Caption Generation
    fun generateAiCaption(topic: String) {
        val captions = when (topic) {
            "gaming" -> "التركيز، الاستراتيجية، ولحظة الانتصار الحاسم 🎮⚡ فخورين بكل خطوة في هذه البطولة! #رياضات_إلكترونية #ألعاب"
            "night" -> "ليالي نيون تنبض بالحياة، عندما يتحول الضوء إلى لوحة فنية ساحرة 🌌💜 #سايبربانك #تصوير_ليلي"
            "photo" -> "الجمال يكمن في التفاصيل الصغيرة والزوايا غير المتوقعة 📸✨ تم التعديل التلقائي عبر الذكاء الاصطناعي #فن_التصوير"
            else -> "نبضات إبداعية جديدة تشعل اليوم بالحماس والإلهام 🚀🔥 #Pulse #إبداع #محتوى"
        }
        _postCaption.value = captions
    }

    fun setPostCaption(caption: String) {
        _postCaption.value = caption
    }

    fun setMediaType(type: MediaType) {
        _selectedMediaType.value = type
    }

    fun setIsScheduled(scheduled: Boolean) {
        _isScheduled.value = scheduled
    }

    fun setScheduledDate(date: String) {
        _scheduledDate.value = date
    }

    fun setScheduledTime(time: String) {
        _scheduledTime.value = time
    }

    fun publishPost() {
        val scheduledDateTime = if (_isScheduled.value) {
            "${_scheduledDate.value} ${_scheduledTime.value}"
        } else null

        val tags = _postCaption.value
            .split(" ")
            .filter { it.startsWith("#") }
            .map { it.removePrefix("#") }

        repository.createPost(
            caption = _postCaption.value.ifBlank { "منشور جديد عبر نبض Pulse ✨" },
            mediaType = _selectedMediaType.value,
            filterName = _selectedFilter.value.nameAr,
            isScheduled = _isScheduled.value,
            scheduledDateTime = scheduledDateTime,
            tags = tags.ifEmpty { listOf("نبض", "مشاركة") }
        )

        // Reset creation state
        _postCaption.value = ""
        resetAdjustments()
        _isScheduled.value = false
        _currentScreen.value = Screen.HOME
    }

    fun publishScheduledNow(postId: String) {
        repository.publishScheduledNow(postId)
    }

    fun deleteScheduled(postId: String) {
        repository.deleteScheduled(postId)
    }

    fun addStory(caption: String) {
        repository.addStory(caption)
    }

    // Direct Messages & Audio Notes
    fun openChat(chat: DirectChat) {
        _activeChat.value = chat
    }

    fun closeChat() {
        _activeChat.value = null
    }

    fun sendTextMessage(chatId: String, text: String) {
        if (text.isBlank()) return
        repository.sendChatMessage(chatId, text, isVoice = false)
        _activeChat.value = repository.chats.value.find { it.id == chatId }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingDuration.value = 0
        viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordingDuration.value += 1
            }
        }
    }

    fun stopAndSendVoiceRecording(chatId: String) {
        val duration = _recordingDuration.value
        _isRecordingVoice.value = false
        if (duration > 0) {
            repository.sendChatMessage(chatId, "رسالة صوتية", isVoice = true, voiceDuration = duration)
            _activeChat.value = repository.chats.value.find { it.id == chatId }
        }
        _recordingDuration.value = 0
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _recordingDuration.value = 0
    }

    // Search
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchCategory(category: String) {
        _searchCategory.value = category
    }

    // 24/7 Support Desk
    fun sendSupportQuestion(userQuestion: String) {
        if (userQuestion.isBlank()) return
        val userMsg = SupportMessage(id = "u_${System.currentTimeMillis()}", isUser = true, text = userQuestion)
        _supportMessages.value = _supportMessages.value + userMsg

        viewModelScope.launch {
            delay(1200)
            val replyText = when {
                userQuestion.contains("خفي") || userQuestion.contains("تخفي") || userQuestion.contains("ghost") ->
                    "وضع التصفح الخفي (Ghost Mode) يتيح لك تصفح المنشورات ومشاهدة القصص دون ترك أثر أو تسجيل اسمك في قائمة المشاهدين، مع إخفاء حالة الاتصال في الدردشات."
                userQuestion.contains("جدول") || userQuestion.contains("نشر") ->
                    "جدولة النشر تتيح لك تحديد تاريخ وساعة دقيقة لنشر محتواك تلقائياً حتى عندما يكون هاتفك مغلقاً، مع إرسال تنبيه قبل النشر بدقائق."
                userQuestion.contains("تشفير") || userQuestion.contains("أمان") || userQuestion.contains("خصوصية") ->
                    "كافة المحادثات والرسائل الصوتية في نبض مشفرة بتقنية التشفير التام بين الطرفين (End-to-End Encryption)، ولا يمكن لأي طرف ثالث الاطلاع عليها."
                userQuestion.contains("صوت") || userQuestion.contains("دردشة") ->
                    "ميزة الدردشة الصوتية الفورية تتيح لك تسجيل وإرسال ملاحظات صوتية فورية مع رسم بياني حي للترددات الصوتية بجودة استوديو نقية."
                userQuestion.contains("نسخ") || userQuestion.contains("سحاب") ->
                    "النسخ الاحتياطي السحابي نشط حالياً، ويتم حفظ إعداداتك ومسوداتك ومنشوراتك تلقائياً وبشكل دوري مشفر."
                else ->
                    "شكراً لتواصلك مع الدعم الفني لنبض! استفسارك مسجل وسيقوم فريقنا الذكي بمساعدتك فوراً. يمكنك أيضاً مراجعة إعدادات الخصوصية والتحليلات من ملفك الشخصي."
            }
            val botMsg = SupportMessage(id = "b_${System.currentTimeMillis()}", isUser = false, text = replyText)
            _supportMessages.value = _supportMessages.value + botMsg
        }
    }

    fun triggerCloudBackup() {
        viewModelScope.launch {
            delay(800)
            _uiPrefs.value = _uiPrefs.value.copy(
                lastBackupText = "الآن (اكتمل بنجاح 100%)"
            )
        }
    }

    // Audio Room methods
    fun openAudioRoom(room: LiveAudioRoom) {
        _activeAudioRoom.value = room
        _currentScreen.value = Screen.AUDIO_ROOM
    }

    fun closeAudioRoom() {
        _activeAudioRoom.value = null
        if (_currentScreen.value == Screen.AUDIO_ROOM) {
            _currentScreen.value = Screen.HOME
        }
    }

    fun toggleRoomSpeaking(roomId: String) {
        repository.toggleRoomSpeaking(roomId)
        _activeAudioRoom.value?.let { room ->
            if (room.id == roomId) {
                _activeAudioRoom.value = repository.liveAudioRooms.value.find { it.id == roomId }
            }
        }
    }

    fun raiseHandInRoom(roomId: String) {
        repository.raiseHandInRoom(roomId)
        _activeAudioRoom.value?.let { room ->
            if (room.id == roomId) {
                _activeAudioRoom.value = repository.liveAudioRooms.value.find { it.id == roomId }
            }
        }
    }

    // Collections & Bookmarks
    fun createCollection(title: String, iconName: String = "bookmark") {
        repository.createCollection(title, iconName)
    }

    fun togglePostInCollection(collectionId: String, postId: String) {
        repository.togglePostInCollection(collectionId, postId)
    }

    // User Profile
    fun updateUserProfile(name: String, bio: String, handle: String, avatarUrl: String? = null) {
        repository.updateUserProfile(name, bio, handle, avatarUrl)
    }

    // Notifications
    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    // Story Stickers
    fun voteStoryPoll(storyId: String, stickerId: String, optionIndex: Int) {
        repository.voteStoryPoll(storyId, stickerId, optionIndex)
        _activeStory.value?.let { current ->
            if (current.id == storyId) {
                _activeStory.value = repository.stories.value.find { it.id == storyId }
            }
        }
    }

    fun answerStoryQuestion(storyId: String, stickerId: String, answer: String) {
        repository.answerStoryQuestion(storyId, stickerId, answer)
        _activeStory.value?.let { current ->
            if (current.id == storyId) {
                _activeStory.value = repository.stories.value.find { it.id == storyId }
            }
        }
    }

    fun sendStoryReaction(storyId: String, stickerId: String) {
        repository.sendStoryReaction(storyId, stickerId)
        _activeStory.value?.let { current ->
            if (current.id == storyId) {
                _activeStory.value = repository.stories.value.find { it.id == storyId }
            }
        }
    }

    fun publishStoryWithMedia(mediaUrl: String, caption: String, isVideo: Boolean, stickers: List<StorySticker>) {
        repository.addStory(caption, mediaUrl, isVideo, stickers)
    }

    // Audio Playback Controller
    fun toggleAudioPlayback(messageId: String, durationSec: Int) {
        val current = _audioPlaybackState.value
        if (current.activeMessageId == messageId && current.isPlaying) {
            _audioPlaybackState.value = current.copy(isPlaying = false)
        } else {
            _audioPlaybackState.value = AudioPlaybackState(
                activeMessageId = messageId,
                isPlaying = true,
                currentProgress = if (current.activeMessageId == messageId && current.currentProgress < 0.95f) current.currentProgress else 0.05f,
                currentDurationSec = durationSec,
                playbackSpeed = current.playbackSpeed
            )
            viewModelScope.launch {
                while (_audioPlaybackState.value.activeMessageId == messageId && _audioPlaybackState.value.isPlaying) {
                    delay(250)
                    val state = _audioPlaybackState.value
                    if (state.activeMessageId != messageId || !state.isPlaying) break
                    val step = (0.25f * state.playbackSpeed) / durationSec.coerceAtLeast(1)
                    val nextProgress = state.currentProgress + step
                    if (nextProgress >= 1f) {
                        _audioPlaybackState.value = state.copy(isPlaying = false, currentProgress = 1f)
                        break
                    } else {
                        _audioPlaybackState.value = state.copy(currentProgress = nextProgress)
                    }
                }
            }
        }
    }

    fun toggleAudioPlaybackSpeed() {
        val current = _audioPlaybackState.value
        val nextSpeed = when (current.playbackSpeed) {
            1.0f -> 1.5f
            1.5f -> 2.0f
            else -> 1.0f
        }
        _audioPlaybackState.value = current.copy(playbackSpeed = nextSpeed)
    }

    fun seekAudioProgress(progress: Float) {
        _audioPlaybackState.value = _audioPlaybackState.value.copy(currentProgress = progress.coerceIn(0f, 1f))
    }
}
