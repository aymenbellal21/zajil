package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SocialRepository {

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            name = "زياد الشمري",
            handle = "@ziad_pulse",
            avatarUrl = "res_avatar",
            bio = "مصور فوتوغرافي وصانع محتوى ألعاب 🎮 | مؤسس مجتمع السايبر العربي 💜",
            isVerified = true,
            followersCount = 14200,
            followingCount = 480,
            postsCount = 68
        )
    )
    val currentUser: User get() = _currentUser.value
    val currentUserFlow: StateFlow<User> = _currentUser.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _scheduledPosts = MutableStateFlow<List<Post>>(emptyList())
    val scheduledPosts: StateFlow<List<Post>> = _scheduledPosts.asStateFlow()

    private val _chats = MutableStateFlow<List<DirectChat>>(emptyList())
    val chats: StateFlow<List<DirectChat>> = _chats.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _savedCollections = MutableStateFlow<List<SavedCollection>>(emptyList())
    val savedCollections: StateFlow<List<SavedCollection>> = _savedCollections.asStateFlow()

    private val _liveAudioRooms = MutableStateFlow<List<LiveAudioRoom>>(emptyList())
    val liveAudioRooms: StateFlow<List<LiveAudioRoom>> = _liveAudioRooms.asStateFlow()

    private val _analytics = MutableStateFlow(AnalyticsReport())
    val analytics: StateFlow<AnalyticsReport> = _analytics.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val userSara = User(
            id = "user_sara",
            name = "سارة المهندس",
            handle = "@sara_tech",
            avatarUrl = "avatar_sara",
            bio = "مهتمة بالذكاء الاصطناعي وتطوير التطبيقات ⚡",
            isVerified = true,
            followersCount = 8900
        )
        val userFounders = User(
            id = "user_founders",
            name = "فريق الفاوندرز Esports",
            handle = "@founders_esports",
            avatarUrl = "avatar_founders",
            bio = "الفريق الرسمي للبطولات الإلكترونية | THE FOUNDERS 🏆",
            isVerified = true,
            followersCount = 54200
        )
        val userOmar = User(
            id = "user_omar",
            name = "عمر المصور",
            handle = "@omar_visuals",
            avatarUrl = "avatar_omar",
            bio = "فوتوغراف سينمائي | عدسات وفلاتر 📸",
            isVerified = false,
            followersCount = 3200
        )
        val userNouf = User(
            id = "user_nouf",
            name = "نوف ديزاين",
            handle = "@nouf_ui",
            avatarUrl = "avatar_nouf",
            bio = "مصممة واجهات ومتحمسة لتجارب المستخدمين 🎨",
            isVerified = true,
            followersCount = 11500
        )

        // Stories with interactive stickers
        _stories.value = listOf(
            Story(
                id = "story_me",
                user = currentUser,
                mediaUrl = "res_cyber_city",
                caption = "كواليس التصوير اليوم 💜",
                isVideo = false,
                hoursRemaining = 24,
                isViewed = false,
                stickers = listOf(
                    StorySticker.Poll(
                        id = "poll_1",
                        question = "ما رأيكم في إضاءة وتفاصيل النيون؟",
                        options = listOf("أسطوري 🔥", "يحتاج تعديل ✨"),
                        voteCounts = listOf(48, 6)
                    ),
                    StorySticker.ReactionBurst(
                        id = "burst_1",
                        emoji = "💜",
                        count = 84
                    )
                )
            ),
            Story(
                id = "story_founders",
                user = userFounders,
                mediaUrl = "res_avatar",
                caption = "استعدادات نهائيات بطولة الرياض 🎮🔥",
                isVideo = true,
                hoursRemaining = 16,
                isViewed = false,
                stickers = listOf(
                    StorySticker.ReactionBurst(
                        id = "burst_2",
                        emoji = "🔥",
                        count = 132
                    )
                )
            ),
            Story(
                id = "story_sara",
                user = userSara,
                mediaUrl = "res_cyber_city",
                caption = "مؤتمر تقنيات الذكاء الاصطناعي 2026 🤖",
                isVideo = false,
                hoursRemaining = 11,
                isViewed = false,
                stickers = listOf(
                    StorySticker.Question(
                        id = "q_1",
                        prompt = "ما هي الميزة القادمة الأكثر حماساً بالنسبة لك؟",
                        answersCount = 31
                    )
                )
            ),
            Story(
                id = "story_omar",
                user = userOmar,
                mediaUrl = "res_cyber_city",
                caption = "تجربة فلتر نيون الجديد، تفاصيل أسطورية!",
                isVideo = false,
                hoursRemaining = 7,
                isViewed = true
            ),
            Story(
                id = "story_nouf",
                user = userNouf,
                mediaUrl = "res_avatar",
                caption = "واجهة الوضع الليلي الجديدة مريحة جداً للعين ✨",
                isVideo = false,
                hoursRemaining = 4,
                isViewed = true
            )
        )

        // Feed Posts
        val initialPosts = listOf(
            Post(
                id = "post_1",
                author = currentUser,
                caption = "أضواء طوكيو السايبرانية وانعكاسات البنفسج في الليل 🌃 تم تطبيق فلتر نيون سايبر التلقائي لتوضيح التفاصيل والتباين اللوني المذهل 💜✨",
                mediaUrl = "res_cyber_city",
                mediaType = MediaType.PHOTO,
                filterName = "نيون سايبر",
                likesCount = 1840,
                commentsCount = 142,
                sharesCount = 89,
                isLiked = true,
                isSaved = true,
                createdAt = "منذ 45 دقيقة",
                viewsCount = 6840,
                reachCount = 5210,
                engagementRate = 9.2f,
                tags = listOf("سايبربانك", "تصوير_ليلي", "فلاتر_احترافية", "طوكيو"),
                comments = listOf(
                    Comment("c1", userSara, "تدرج درجات البنفسجي مع انعكاس الماء خرافي! 🔥", "منذ 30 دقيقة", 14),
                    Comment("c2", userFounders, "ألوان الهوية الرسمية للفاوندرز تماماً 💜", "منذ 15 دقيقة", 28)
                )
            ),
            Post(
                id = "post_2",
                author = userFounders,
                caption = "بطلة العالم للرياضات الإلكترونية في صفوف فريقنا 🎮 جاهزون للمنافسة العالمية! شاركونا تشجيعكم في التعليقات ⚡🏆",
                mediaUrl = "res_avatar",
                mediaType = MediaType.SHORT_VIDEO,
                filterName = "تحسين ذكي AI",
                likesCount = 3920,
                commentsCount = 310,
                sharesCount = 420,
                isLiked = false,
                isSaved = false,
                createdAt = "منذ 3 ساعات",
                viewsCount = 15400,
                reachCount = 12900,
                engagementRate = 11.4f,
                tags = listOf("رياضات_إلكترونية", "بطولات", "ألعاب", "THE_FOUNDERS"),
                comments = listOf(
                    Comment("c3", currentUser, "كل التوفيق يا أبطال، فخورين بكم 👏", "منذ ساعتين", 45)
                )
            ),
            Post(
                id = "post_3",
                author = userOmar,
                caption = "مقارنة حية قبل وبعد استخدام ميزة التحسين التلقائي AI للصور ذات الإضاءة المنخفضة 💡 النتيجة واضحة وخالية من التشويش!",
                mediaUrl = "res_cyber_city",
                mediaType = MediaType.PHOTO,
                filterName = "ساعة ذهبية",
                likesCount = 890,
                commentsCount = 64,
                sharesCount = 35,
                isLiked = false,
                isSaved = false,
                createdAt = "منذ 6 ساعات",
                viewsCount = 3900,
                reachCount = 3100,
                engagementRate = 6.8f,
                tags = listOf("تعديل_تلقائي", "إضاءة_ذكية", "تصوير")
            )
        )
        _posts.value = initialPosts

        // Scheduled Posts
        _scheduledPosts.value = listOf(
            Post(
                id = "sched_1",
                author = currentUser,
                caption = "إطلاق السلسلة الأسبوعية: أسرار الإخراج البصري للهواتف الذكية 🎬 موعدنا بعد قليل!",
                mediaUrl = "res_cyber_city",
                mediaType = MediaType.SHORT_VIDEO,
                filterName = "تحسين ذكي AI",
                isScheduled = true,
                scheduledFor = "اليوم 09:30 م",
                tags = listOf("سلسلة_أسبوعية", "إخراج", "فيديو_قصير")
            ),
            Post(
                id = "sched_2",
                author = currentUser,
                caption = "مفاجأة خاصة للمتابعين: توزيع هدايا اشتراكات ومعدات ألعاب بالتعاون مع الرعاة 🎁🔥",
                mediaUrl = "res_avatar",
                mediaType = MediaType.PHOTO,
                filterName = "نيون سايبر",
                isScheduled = true,
                scheduledFor = "غداً 08:00 م",
                tags = listOf("جوائز", "سحب", "جيمنج")
            )
        )

        // Direct Chats
        _chats.value = listOf(
            DirectChat(
                id = "chat_sara",
                peerUser = userSara,
                lastMessage = "🎙️ رسالة صوتية مشفرة (0:24)",
                lastTimestamp = "10:15 م",
                unreadCount = 2,
                isEncrypted = true,
                isOnline = true,
                messages = listOf(
                    ChatMessage(
                        id = "m1",
                        senderId = "user_sara",
                        text = "مرحباً زياد، شفت منشورك الأخير عن الفلاتر، فكرة التحسين التلقائي ممتازة!",
                        timestamp = "09:40 م"
                    ),
                    ChatMessage(
                        id = "m2",
                        senderId = "user_me",
                        text = "أهلاً سارة! تسلمي، الخوارزمية تضبط التباين وتشبع الألوان تلقائياً بدون تشويه التفاصيل 👍",
                        timestamp = "09:45 م"
                    ),
                    ChatMessage(
                        id = "m3",
                        senderId = "user_sara",
                        isVoice = true,
                        voiceDurationSeconds = 24,
                        voiceWaveform = listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 0.7f, 1.0f, 0.6f, 0.3f, 0.7f, 0.5f),
                        timestamp = "10:15 م"
                    )
                )
            ),
            DirectChat(
                id = "chat_founders",
                peerUser = userFounders,
                lastMessage = "تم تثبيت البث المشترك لنهائي البطولة يوم الخميس القادم 🎮",
                lastTimestamp = "08:30 م",
                unreadCount = 0,
                isEncrypted = true,
                isOnline = true,
                messages = listOf(
                    ChatMessage(
                        id = "m4",
                        senderId = "user_founders",
                        text = "تم تثبيت البث المشترك لنهائي البطولة يوم الخميس القادم 🎮",
                        timestamp = "08:30 م"
                    )
                )
            ),
            DirectChat(
                id = "chat_omar",
                peerUser = userOmar,
                lastMessage = "الفلاتر في التحديث الجديد سريعة جداً وتدعم الـ 4K",
                lastTimestamp = "أمس",
                unreadCount = 0,
                isEncrypted = true,
                isOnline = false,
                messages = listOf(
                    ChatMessage(
                        id = "m5",
                        senderId = "user_omar",
                        text = "الفلاتر في التحديث الجديد سريعة جداً وتدعم الـ 4K",
                        timestamp = "أمس"
                    )
                )
            )
        )

        // Notifications
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_1",
                title = "تفاعل جديد",
                description = "أعجب فريق الفاوندرز بمنشورك: أضواء طوكيو السايبرانية",
                timeAgo = "منذ 15 دقيقة",
                type = NotificationType.LIKE,
                relatedUser = userFounders
            ),
            NotificationItem(
                id = "notif_2",
                title = "تعليق جديد",
                description = "علقت سارة المهندس: تدرج درجات البنفسجي مع انعكاس الماء خرافي!",
                timeAgo = "منذ 30 دقيقة",
                type = NotificationType.COMMENT,
                relatedUser = userSara
            ),
            NotificationItem(
                id = "notif_3",
                title = "تنبيه جدولة ذكي",
                description = "منشورك المجدول 'إطلاق السلسلة الأسبوعية' سينشر تلقائياً اليوم في 09:30 م",
                timeAgo = "منذ ساعة",
                type = NotificationType.SCHEDULED
            ),
            NotificationItem(
                id = "notif_4",
                title = "اقتراح ذكي AI",
                description = "المتابعون يتفاعلون بكثافة الآن مع وسم #سايبربانك. ننصحك بمشاركة قصة جديدة الآن!",
                timeAgo = "منذ ساعتين",
                type = NotificationType.AI_RECOMMENDATION
            )
        )

        // Saved Collections
        _savedCollections.value = listOf(
            SavedCollection(
                id = "col_1",
                title = "المفضلة السايبرانية 💜",
                postIds = listOf("post_1"),
                coverUrl = "res_cyber_city",
                iconName = "bookmark"
            ),
            SavedCollection(
                id = "col_2",
                title = "تصاميم وواجهات 🎨",
                postIds = listOf("post_1"),
                coverUrl = "avatar_nouf",
                iconName = "palette"
            ),
            SavedCollection(
                id = "col_3",
                title = "مباريات وبطولات 🏆",
                postIds = emptyList(),
                coverUrl = "avatar_founders",
                iconName = "trophy"
            )
        )

        // Live Audio Rooms
        _liveAudioRooms.value = listOf(
            LiveAudioRoom(
                id = "room_1",
                title = "صالون تقنيات الذكاء الاصطناعي 2026",
                topic = "الذكاء الاصطناعي وصناعة المحتوى الإبداعي",
                host = userSara,
                speakers = listOf(userSara, currentUser),
                listeners = listOf(userOmar, userNouf),
                listenersCount = 142,
                isLive = true
            ),
            LiveAudioRoom(
                id = "room_2",
                title = "ديوانية مطوري ومصممي الزاجل 🕊️",
                topic = "واجهات المستخدم الحديثة وأمان الحسابات",
                host = userNouf,
                speakers = listOf(userNouf),
                listeners = listOf(userFounders),
                listenersCount = 88,
                isLive = true
            )
        )
    }

    fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                post.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                )
            } else post
        }
    }

    fun toggleSave(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            user = currentUser,
            text = text,
            timestamp = "الآن"
        )
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(
                    comments = listOf(newComment) + post.comments,
                    commentsCount = post.commentsCount + 1
                )
            } else post
        }
    }

    fun createPost(
        caption: String,
        mediaType: MediaType,
        filterName: String,
        isScheduled: Boolean,
        scheduledDateTime: String?,
        tags: List<String>
    ) {
        val newPost = Post(
            id = "post_${UUID.randomUUID()}",
            author = currentUser,
            caption = caption,
            mediaUrl = if (mediaType == MediaType.PHOTO) "res_cyber_city" else "res_avatar",
            mediaType = mediaType,
            filterName = filterName,
            isScheduled = isScheduled,
            scheduledFor = scheduledDateTime,
            createdAt = if (isScheduled) "مجدول" else "الآن",
            tags = tags
        )
        if (isScheduled) {
            _scheduledPosts.value = listOf(newPost) + _scheduledPosts.value
        } else {
            _posts.value = listOf(newPost) + _posts.value
        }
    }

    fun publishScheduledNow(postId: String) {
        val found = _scheduledPosts.value.find { it.id == postId }
        if (found != null) {
            _scheduledPosts.value = _scheduledPosts.value.filter { it.id != postId }
            val published = found.copy(
                isScheduled = false,
                scheduledFor = null,
                createdAt = "الآن"
            )
            _posts.value = listOf(published) + _posts.value
        }
    }

    fun deleteScheduled(postId: String) {
        _scheduledPosts.value = _scheduledPosts.value.filter { it.id != postId }
    }

    fun addStory(
        caption: String,
        mediaUrl: String = "res_cyber_city",
        isVideo: Boolean = false,
        stickers: List<StorySticker> = emptyList()
    ) {
        val newStory = Story(
            id = "story_${UUID.randomUUID()}",
            user = currentUser,
            mediaUrl = mediaUrl,
            caption = caption,
            isVideo = isVideo,
            hoursRemaining = 24,
            isViewed = false,
            stickers = stickers
        )
        _stories.value = listOf(newStory) + _stories.value
    }

    fun voteStoryPoll(storyId: String, stickerId: String, optionIndex: Int) {
        _stories.value = _stories.value.map { story ->
            if (story.id == storyId) {
                val updatedStickers = story.stickers.map { sticker ->
                    if (sticker is StorySticker.Poll && sticker.id == stickerId) {
                        if (sticker.userVotedIndex == null) {
                            val newCounts = sticker.voteCounts.toMutableList()
                            if (optionIndex in newCounts.indices) {
                                newCounts[optionIndex] = newCounts[optionIndex] + 1
                            }
                            sticker.copy(voteCounts = newCounts, userVotedIndex = optionIndex)
                        } else sticker
                    } else sticker
                }
                story.copy(stickers = updatedStickers)
            } else story
        }
    }

    fun answerStoryQuestion(storyId: String, stickerId: String, answer: String) {
        _stories.value = _stories.value.map { story ->
            if (story.id == storyId) {
                val updatedStickers = story.stickers.map { sticker ->
                    if (sticker is StorySticker.Question && sticker.id == stickerId) {
                        sticker.copy(
                            answersCount = sticker.answersCount + 1,
                            userSubmitted = true
                        )
                    } else sticker
                }
                story.copy(stickers = updatedStickers)
            } else story
        }
    }

    fun sendStoryReaction(storyId: String, stickerId: String) {
        _stories.value = _stories.value.map { story ->
            if (story.id == storyId) {
                val updatedStickers = story.stickers.map { sticker ->
                    if (sticker is StorySticker.ReactionBurst && sticker.id == stickerId) {
                        sticker.copy(
                            count = sticker.count + 1,
                            userReacted = true
                        )
                    } else sticker
                }
                story.copy(stickers = updatedStickers)
            } else story
        }
    }

    fun updateUserProfile(name: String, bio: String, handle: String, avatarUrl: String? = null) {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            name = name,
            bio = bio,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            avatarUrl = avatarUrl ?: current.avatarUrl
        )
    }

    fun createCollection(title: String, iconName: String = "bookmark") {
        if (title.isBlank()) return
        val newCol = SavedCollection(
            id = "col_${UUID.randomUUID()}",
            title = title,
            postIds = emptyList(),
            coverUrl = "res_cyber_city",
            iconName = iconName
        )
        _savedCollections.value = _savedCollections.value + newCol
    }

    fun togglePostInCollection(collectionId: String, postId: String) {
        _savedCollections.value = _savedCollections.value.map { col ->
            if (col.id == collectionId) {
                val exists = col.postIds.contains(postId)
                val newPostIds = if (exists) col.postIds - postId else col.postIds + postId
                col.copy(postIds = newPostIds)
            } else col
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun toggleRoomSpeaking(roomId: String) {
        _liveAudioRooms.value = _liveAudioRooms.value.map { room ->
            if (room.id == roomId) {
                val me = currentUser
                val isSpeaker = room.speakers.any { it.id == me.id }
                if (isSpeaker) {
                    room.copy(
                        speakers = room.speakers.filter { it.id != me.id },
                        listeners = room.listeners + me,
                        isMuted = false
                    )
                } else {
                    room.copy(
                        speakers = room.speakers + me,
                        listeners = room.listeners.filter { it.id != me.id },
                        hasRaisedHand = false
                    )
                }
            } else room
        }
    }

    fun raiseHandInRoom(roomId: String) {
        _liveAudioRooms.value = _liveAudioRooms.value.map { room ->
            if (room.id == roomId) {
                room.copy(hasRaisedHand = !room.hasRaisedHand)
            } else room
        }
    }

    fun sendChatMessage(chatId: String, text: String, isVoice: Boolean = false, voiceDuration: Int = 0) {
        val newMessage = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            senderId = currentUser.id,
            text = text,
            isVoice = isVoice,
            voiceDurationSeconds = voiceDuration,
            voiceWaveform = if (isVoice) listOf(0.2f, 0.5f, 0.8f, 0.9f, 0.6f, 0.7f, 1.0f, 0.4f, 0.3f) else emptyList(),
            timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
            isEncrypted = true
        )
        _chats.value = _chats.value.map { chat ->
            if (chat.id == chatId) {
                chat.copy(
                    lastMessage = if (isVoice) "🎙️ رسالة صوتية (${voiceDuration}ث)" else text,
                    lastTimestamp = "الآن",
                    messages = chat.messages + newMessage
                )
            } else chat
        }
    }

    fun markStoryAsViewed(storyId: String) {
        _stories.value = _stories.value.map {
            if (it.id == storyId) it.copy(isViewed = true) else it
        }
    }
}
