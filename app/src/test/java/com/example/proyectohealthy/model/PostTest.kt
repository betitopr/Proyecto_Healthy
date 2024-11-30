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
import com.google.firebase.Timestamp

@ExperimentalCoroutinesApi
class PostTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var post: Post

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        post = Post(
            id = "post123",
            title = "Título del post",
            content = "Contenido del post",
            authorId = "author123",
            communityId = "community123",
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now(),
            upvotes = 10,
            downvotes = 2,
            commentCount = 5
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should create post with valid data`() = runTest {
        // When
        val result = post

        // Then
        assertNotNull(result)
        assertEquals("post123", result.id)
        assertEquals("Título del post", result.title)
        assertEquals("Contenido del post", result.content)
        assertEquals("author123", result.authorId)
        assertEquals("community123", result.communityId)
        assertEquals(10, result.upvotes)
        assertEquals(2, result.downvotes)
        assertEquals(5, result.commentCount)
    }

    @Test
    fun `should validate post title is not empty`() = runTest {
        // When
        val result = post.title

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate post content is not empty`() = runTest {
        // When
        val result = post.content

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate upvotes are non-negative`() = runTest {
        // When
        val result = post.upvotes

        // Then
        assertTrue(result >= 0)
    }

    @Test
    fun `should validate timestamps are present`() = runTest {
        // When & Then
        assertNotNull(post.createdAt)
        assertNotNull(post.updatedAt)
    }
}