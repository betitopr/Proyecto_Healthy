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
class CommentTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var comment: Comment

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        comment = Comment(
            id = "test_id",
            postId = "post123",
            autorId = "user123",
            content = "Test comment",
            timestamp = System.currentTimeMillis()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should create comment with valid data`() = runTest {
        // When
        val result = comment

        // Then
        assertNotNull(result)
        assertEquals("test_id", result.id)
        assertEquals("post123", result.postId)
        assertEquals("user123", result.autorId)
        assertEquals("Test comment", result.content)
        assertTrue(result.timestamp > 0)
    }

    @Test
    fun `should validate comment content is not empty`() = runTest {
        // When
        val result = comment.content

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate autorId is not empty`() = runTest {
        // When
        val result = comment.autorId

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate postId is not empty`() = runTest {
        // When
        val result = comment.postId

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate timestamp is valid`() = runTest {
        // When
        val result = comment.timestamp

        // Then
        assertTrue(result > 0)
    }
}