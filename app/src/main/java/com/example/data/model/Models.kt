package com.example.data.model

enum class MediaType {
    PHOTO,
    SHORT_VIDEO
}

enum class FeedLayout {
    CARDS,
    COMPACT,
    GRID
}

enum class NotificationType {
    LIKE,
    COMMENT,
    MENTION,
    SCHEDULED,
    SYSTEM,
    AI_RECOMMENDATION
}

data class User(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val bio: String = "",
    val isVerified: Boolean = false,
    val followersCount: Int = 1240,
    val followingCount: Int = 380,
    val postsCount: Int = 42
)

data class Comment(
    val id: String,
    val user: User,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0
)

data class Post(
    val id: String,
    val author: User,
    val caption: String,
    val mediaUrl: String,
    val mediaType: MediaType = MediaType.PHOTO,
    val filterName: String = "Normal",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val createdAt: String = "منذ ساعة",
    val scheduledFor: String? = null,
    val isScheduled: Boolean = false,
    val viewsCount: Int = 1250,
    val reachCount: Int = 980,
    val engagementRate: Float = 8.4f,
    val tags: List<String> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class Story(
    val id: String,
    val user: User,
    val mediaUrl: String,
    val caption: String = "",
    val isVideo: Boolean = false,
    val hoursRemaining: Int = 24,
    val isViewed: Boolean = false,
    val stickers: List<StorySticker> = emptyList()
)

sealed class StorySticker {
    abstract val id: String

    data class Poll(
        override val id: String,
        val question: String,
        val options: List<String>,
        val voteCounts: List<Int>,
        val userVotedIndex: Int? = null
    ) : StorySticker()

    data class Question(
        override val id: String,
        val prompt: String,
        val answersCount: Int = 0,
        val userSubmitted: Boolean = false
    ) : StorySticker()

    data class ReactionBurst(
        override val id: String,
        val emoji: String,
        val count: Int = 0,
        val userReacted: Boolean = false
    ) : StorySticker()
}

data class SavedCollection(
    val id: String,
    val title: String,
    val postIds: List<String> = emptyList(),
    val coverUrl: String? = null,
    val iconName: String = "bookmark"
)

data class LiveAudioRoom(
    val id: String,
    val title: String,
    val topic: String,
    val host: User,
    val speakers: List<User> = emptyList(),
    val listeners: List<User> = emptyList(),
    val listenersCount: Int = 0,
    val isLive: Boolean = true,
    val isMuted: Boolean = false,
    val hasRaisedHand: Boolean = false
)

data class AudioPlaybackState(
    val activeMessageId: String? = null,
    val isPlaying: Boolean = false,
    val currentProgress: Float = 0f,
    val currentDurationSec: Int = 0,
    val playbackSpeed: Float = 1.0f
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String = "",
    val isVoice: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val voiceWaveform: List<Float> = emptyList(),
    val timestamp: String = "10:30 م",
    val isEncrypted: Boolean = true
)

data class DirectChat(
    val id: String,
    val peerUser: User,
    val lastMessage: String,
    val lastTimestamp: String,
    val unreadCount: Int = 0,
    val isEncrypted: Boolean = true,
    val isOnline: Boolean = true,
    val messages: List<ChatMessage> = emptyList()
)

data class PhotoFilter(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val hueShift: Float = 0f,
    val tintColorHex: Long? = null
)

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val relatedUser: User? = null
)

data class AnalyticsReport(
    val totalImpressions: Int = 48720,
    val totalReach: Int = 34150,
    val totalInteractions: Int = 6890,
    val avgEngagementRate: Float = 7.8f,
    val bestPostingTimeAr: String = "9:00 م - 11:30 م",
    val topCitiesAr: List<Pair<String, Int>> = listOf(
        "الرياض" to 42,
        "دبي" to 28,
        "القاهرة" to 18,
        "الدوحة" to 12
    ),
    val dailyViews: List<Pair<String, Int>> = listOf(
        "السبت" to 4200,
        "الأحد" to 5800,
        "الإثنين" to 6100,
        "الثلاثاء" to 7300,
        "الأربعاء" to 8900,
        "الخميس" to 11200,
        "الجمعة" to 9500
    )
)
