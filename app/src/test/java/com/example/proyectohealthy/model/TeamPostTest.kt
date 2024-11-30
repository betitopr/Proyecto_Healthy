package com.example.proyectohealthy.model

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class TeamPostTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var teamPost: TeamPost

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        teamPost = TeamPost(
            id = "teampost123",
            autorId = "user123",
            content = "Contenido del post",
            imageUrl = "https://example.com/image.jpg",
            categoria = TeamPost.PostCategory.TIPS_NUTRICION,
            likes = mapOf("user1" to true),
            comments = mutableMapOf(),
            timestamp = System.currentTimeMillis()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should create team post with valid data`() = runTest {
        // When
        val result = teamPost

        // Then
        assertNotNull(result)
        assertEquals("teampost123", result.id)
        assertEquals("user123", result.autorId)
        assertEquals("Contenido del post", result.content)
        assertEquals(TeamPost.PostCategory.TIPS_NUTRICION, result.categoria)
        assertTrue(result.timestamp > 0)
    }

    @Test
    fun `should calculate like count correctly`() = runTest {
        // When
        val result = teamPost.likeCount

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `should calculate comment count correctly`() = runTest {
        // Given
        val comment = Comment(
            id = "comment1",
            postId = teamPost.id,
            autorId = "user1",
            content = "Comentario de prueba",
            timestamp = System.currentTimeMillis()
        )
        teamPost.comments["comment1"] = comment

        // When
        val result = teamPost.commentCount

        // Then
        assertEquals(1, result)
    }

    @Test
    fun `should validate post content is not empty`() = runTest {
        // When
        val result = teamPost.content

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should have valid category`() = runTest {
        // When
        val result = teamPost.categoria

        // Then
        assertTrue(result in TeamPost.PostCategory.values())
    }
}