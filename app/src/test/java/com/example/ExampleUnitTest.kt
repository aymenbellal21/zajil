package com.example

import com.example.data.filter.FilterEngine
import com.example.data.model.MediaType
import com.example.data.repository.SocialRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testInitialRepositoryState() {
        val repo = SocialRepository()
        assertTrue(repo.posts.value.isNotEmpty())
        assertTrue(repo.stories.value.isNotEmpty())
        assertTrue(repo.chats.value.isNotEmpty())
        assertTrue(repo.scheduledPosts.value.isNotEmpty())
    }

    @Test
    fun testPostLikeAndComment() {
        val repo = SocialRepository()
        val firstPost = repo.posts.value.first()
        val initialLikes = firstPost.likesCount
        val wasLiked = firstPost.isLiked

        repo.toggleLike(firstPost.id)
        val updatedPost = repo.posts.value.first { it.id == firstPost.id }
        assertEquals(!wasLiked, updatedPost.isLiked)
        assertEquals(if (wasLiked) initialLikes - 1 else initialLikes + 1, updatedPost.likesCount)

        repo.addComment(firstPost.id, "تعليق تجريبي رائع!")
        val postAfterComment = repo.posts.value.first { it.id == firstPost.id }
        assertTrue(postAfterComment.comments.any { it.text == "تعليق تجريبي رائع!" })
    }

    @Test
    fun testSchedulingAndPublish() {
        val repo = SocialRepository()
        val initialScheduledCount = repo.scheduledPosts.value.size
        val initialFeedCount = repo.posts.value.size

        repo.createPost(
            caption = "منشور مجدول للاختبار",
            mediaType = MediaType.PHOTO,
            filterName = "نيون سايبر",
            isScheduled = true,
            scheduledDateTime = "غداً 08:00 م",
            tags = listOf("اختبار")
        )

        assertEquals(initialScheduledCount + 1, repo.scheduledPosts.value.size)

        val newScheduled = repo.scheduledPosts.value.first()
        repo.publishScheduledNow(newScheduled.id)

        assertEquals(initialScheduledCount, repo.scheduledPosts.value.size)
        assertEquals(initialFeedCount + 1, repo.posts.value.size)
    }

    @Test
    fun testFilterEngine() {
        assertTrue(FilterEngine.availableFilters.size >= 5)
        val neonFilter = FilterEngine.availableFilters.find { it.id == "cyber_neon" }
        assertNotNull(neonFilter)

        val matrix = FilterEngine.createColorMatrix(neonFilter!!)
        assertNotNull(matrix)
    }

    @Test
    fun testCameraNavigationAndCapture() {
        val viewModel = com.example.ui.viewmodel.SocialViewModel()
        assertEquals(com.example.ui.viewmodel.Screen.HOME, viewModel.currentScreen.value)

        viewModel.navigateTo(com.example.ui.viewmodel.Screen.CAMERA)
        assertEquals(com.example.ui.viewmodel.Screen.CAMERA, viewModel.currentScreen.value)

        viewModel.onMediaCaptured("content://test_photo_captured.jpg", MediaType.PHOTO)
        assertEquals(com.example.ui.viewmodel.Screen.CREATE, viewModel.currentScreen.value)
        assertEquals("content://test_photo_captured.jpg", viewModel.capturedMediaUri.value)
        assertEquals(MediaType.PHOTO, viewModel.selectedMediaType.value)
    }

    @Test
    fun testAuthFlow() {
        val viewModel = com.example.ui.viewmodel.SocialViewModel()
        assertEquals(com.example.ui.viewmodel.AuthState.SPLASH, viewModel.authState.value)

        viewModel.onSplashFinished()
        assertEquals(com.example.ui.viewmodel.AuthState.LOGIN, viewModel.authState.value)

        viewModel.navigateToSignUp()
        assertEquals(com.example.ui.viewmodel.AuthState.SIGNUP, viewModel.authState.value)

        viewModel.navigateToLogin()
        assertEquals(com.example.ui.viewmodel.AuthState.LOGIN, viewModel.authState.value)

        viewModel.login("test@alzajel.app")
        assertEquals(com.example.ui.viewmodel.AuthState.AUTHENTICATED, viewModel.authState.value)

        viewModel.logout()
        assertEquals(com.example.ui.viewmodel.AuthState.LOGIN, viewModel.authState.value)
    }

    @Test
    fun testLanguageSwitching() {
        val viewModel = com.example.ui.viewmodel.SocialViewModel()
        assertEquals(com.example.ui.localization.AppLanguage.ARABIC, viewModel.uiPrefs.value.language)
        assertTrue(viewModel.uiPrefs.value.isArabic)

        viewModel.setLanguage(com.example.ui.localization.AppLanguage.ENGLISH)
        assertEquals(com.example.ui.localization.AppLanguage.ENGLISH, viewModel.uiPrefs.value.language)
        assertFalse(viewModel.uiPrefs.value.isArabic)

        viewModel.toggleLanguage()
        assertEquals(com.example.ui.localization.AppLanguage.FRENCH, viewModel.uiPrefs.value.language)
    }
}
