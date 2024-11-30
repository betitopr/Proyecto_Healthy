package com.example.proyectohealthy.model

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var user: User

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        user = User(
            id = "user123",
            username = "fituser",
            email = "user@example.com",
            karma = 100
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should create user with valid data`() = runTest {
        // When
        val result = user

        // Then
        assertNotNull(result)
        assertEquals("user123", result.id)
        assertEquals("fituser", result.username)
        assertEquals("user@example.com", result.email)
        assertEquals(100, result.karma)
    }

    @Test
    fun `should validate user id is not empty`() = runTest {
        // When
        val result = user.id

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate username is not empty`() = runTest {
        // When
        val result = user.username

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate email is not empty`() = runTest {
        // When
        val result = user.email

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `should validate karma is non-negative`() = runTest {
        // When
        val result = user.karma

        // Then
        assertTrue(result >= 0)
    }

    @Test
    fun `should validate email format`() = runTest {
        // When
        val result = user.email

        // Then
        assertTrue(result.contains("@"))
        assertTrue(result.contains("."))
    }
}