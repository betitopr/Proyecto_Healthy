package com.example.proyectohealthy.ui.viewmodel

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @RelaxedMockK
    private lateinit var fauth: FirebaseAuth

    @RelaxedMockK
    private lateinit var userRepository: PerfilRepository

    @RelaxedMockK
    private lateinit var context: Context

    private lateinit var authViewModel: AuthViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun onBefore() {
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        // Mock context resources
        every { context.getString(any()) } returns "test_web_client_id"
        // Mock initial Firebase Auth state
        every { fauth.currentUser } returns null


        authViewModel = AuthViewModel(fauth, userRepository, context)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() // Limpiar todos los mocks estáticos
    }

    @Test
    fun `when initialize viewModel then check initial state`() {
        // Given
        every { fauth.currentUser } returns null

        // When
        authViewModel.checkAuthState()

        // Then
        assertEquals(AuthViewModel.AuthState.NotAuthenticated, authViewModel.authState.value)
    }

    @Test
    fun `when current user exists then state is Authenticated`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { fauth.currentUser } returns mockUser

        // When
        authViewModel.checkAuthState()

        // Then
        assertEquals(AuthViewModel.AuthState.Authenticated, authViewModel.authState.value)
    }

    @Test
    fun `when signInWithEmailAndPassword success then state is Authenticated`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password123"
        val mockUser = mockk<FirebaseUser>()
        val authResult = mockk<AuthResult>()
        val successTask = Tasks.forResult(authResult)

        every { fauth.signInWithEmailAndPassword(email, password) } returns successTask
        every { fauth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"
        every { mockUser.displayName } returns "Test User"
        every { mockUser.photoUrl } returns null
        coEvery { userRepository.getPerfil(any()) } returns null
        coEvery { userRepository.createOrUpdatePerfil(any()) } just runs

        // When
        authViewModel.signInWithEmailAndPassword(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(AuthViewModel.AuthState.Authenticated, authViewModel.authState.value)
    }

    @Test
    fun `when signInWithEmailAndPassword fails then state is Error`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password123"
        val errorMessage = "Authentication failed"
        val failureTask = Tasks.forException<AuthResult>(Exception(errorMessage))

        every { fauth.signInWithEmailAndPassword(email, password) } returns failureTask

        // When
        authViewModel.signInWithEmailAndPassword(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(authViewModel.authState.value is AuthViewModel.AuthState.Error)
        assertEquals(errorMessage, (authViewModel.authState.value as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `when signUp success then state is Authenticated`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password123"
        val authResult = mockk<AuthResult>()
        val mockUser = mockk<FirebaseUser>()
        val successTask = Tasks.forResult(authResult)

        every { fauth.createUserWithEmailAndPassword(email, password) } returns successTask
        every { fauth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"
        every { mockUser.displayName } returns null
        every { mockUser.photoUrl } returns null
        coEvery { userRepository.getPerfil(any()) } returns null
        coEvery { userRepository.createOrUpdatePerfil(any()) } just runs

        // When
        authViewModel.signUp(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(AuthViewModel.AuthState.Authenticated, authViewModel.authState.value)
    }

    @Test
    fun `when signUp fails then state is Error`() = runTest {
        // Given
        val email = "test@test.com"
        val password = "password123"
        val errorMessage = "Sign up failed"
        val failureTask = Tasks.forException<AuthResult>(Exception(errorMessage))

        every { fauth.createUserWithEmailAndPassword(email, password) } returns failureTask

        // When
        authViewModel.signUp(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(authViewModel.authState.value is AuthViewModel.AuthState.Error)
        assertEquals(errorMessage, (authViewModel.authState.value as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `when signInWithGoogle success then state is Authenticated`() = runTest {
        // Given
        val mockAccount = mockk<GoogleSignInAccount>()
        val authResult = mockk<AuthResult>()
        val mockUser = mockk<FirebaseUser>()
        val idToken = "fake_token"
        val successTask = Tasks.forResult(authResult)

        every { mockAccount.idToken } returns idToken
        every { fauth.signInWithCredential(any()) } returns successTask
        every { fauth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"
        every { mockUser.displayName } returns "Test User"
        every { mockUser.photoUrl } returns null
        coEvery { userRepository.getPerfil(any()) } returns null
        coEvery { userRepository.createOrUpdatePerfil(any()) } just runs

        // When
        authViewModel.signInWithGoogle(mockAccount)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(AuthViewModel.AuthState.Authenticated, authViewModel.authState.value)
    }

    @Test
    fun `when signInWithGoogle fails due to null token then state is Error`() = runTest {
        // Given
        val mockAccount = mockk<GoogleSignInAccount>()

        every { mockAccount.idToken } returns null

        // When
        authViewModel.signInWithGoogle(mockAccount)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(authViewModel.authState.value is AuthViewModel.AuthState.Error)
    }

    @Test
    fun `when signOut success then state is NotAuthenticated`() = runTest {
        // Given
        val mockGoogleSignInClient = mockk<GoogleSignInClient>()

        // Crear Tasks.TaskCompletionSource para simular Tasks
        val signOutTaskSource = Tasks.forResult(null as Void?)
        val revokeTaskSource = Tasks.forResult(null as Void?)

        // Mock GoogleSignIn.getClient
        mockkStatic(GoogleSignIn::class)
        every { GoogleSignIn.getClient(any(), any()) } returns mockGoogleSignInClient

        // Mock operaciones del cliente
        every { mockGoogleSignInClient.signOut() } returns signOutTaskSource
        every { mockGoogleSignInClient.revokeAccess() } returns revokeTaskSource
        every { fauth.signOut() } just runs

        // When
        authViewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(AuthViewModel.AuthState.NotAuthenticated, authViewModel.authState.value)

        // Verify
        verify {
            fauth.signOut()
            mockGoogleSignInClient.signOut()
            mockGoogleSignInClient.revokeAccess()
        }
    }
    @Test
    fun `when signOut fails then state is Error`() = runTest {
        // Given
        val mockGoogleSignInClient = mockk<GoogleSignInClient>()
        val errorMessage = "Sign out failed"

        mockkStatic(GoogleSignIn::class)
        every { GoogleSignIn.getClient(any(), any()) } returns mockGoogleSignInClient
        every { mockGoogleSignInClient.signOut() } throws Exception(errorMessage)
        every { fauth.signOut() } throws Exception(errorMessage)

        // When
        authViewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(authViewModel.authState.value is AuthViewModel.AuthState.Error)
        assertEquals(errorMessage, (authViewModel.authState.value as AuthViewModel.AuthState.Error).message)
    }

    @Test
    fun `when initial state then state is Initial`() {
        // Then
        assertEquals(AuthViewModel.AuthState.Initial, authViewModel.authState.value)
    }

    @Test
    fun `when createOrUpdateUserProfile called with null current user then state is Error`() = runTest {
        // Given
        every { fauth.currentUser } returns null

        // When
        authViewModel.signInWithEmailAndPassword("test@test.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(authViewModel.authState.value is AuthViewModel.AuthState.Error)
    }

    @Test
    fun `when getCurrentUser called then returns current user`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { fauth.currentUser } returns mockUser

        // When
        val result = authViewModel.getCurrentUser()

        // Then
        assertEquals(mockUser, result)
    }

    @Test
    fun `when createOrUpdateUserProfile called with existing profile then updates profile`() = runTest {
        // Given
        val mockUser = mockk<FirebaseUser>()
        val existingPerfil = Perfil(
            uid = "test_uid",
            nombre = "John",
            apellido = "Doe",
            genero = "",
            altura = 0f,
            edad = 0,
            pesoActual = 0f,
            pesoObjetivo = 0f,
            nivelActividad = "",
            objetivo = "",
            comoConseguirlo = "",
            entrenamientoFuerza = "",
            perfilImagen = "",
            biografia = ""
        )

        val authResult = mockk<AuthResult>()
        val successTask = Tasks.forResult(authResult)

        every { fauth.signInWithEmailAndPassword(any(), any()) } returns successTask
        every { fauth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"
        every { mockUser.displayName } returns "Jane Smith"
        every { mockUser.photoUrl } returns mockk<Uri>()
        coEvery { userRepository.getPerfil("test_uid") } returns existingPerfil
        coEvery { userRepository.createOrUpdatePerfil(any()) } just runs

        // When
        authViewModel.signInWithEmailAndPassword("test@test.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { userRepository.createOrUpdatePerfil(any()) }
        assertEquals(AuthViewModel.AuthState.Authenticated, authViewModel.authState.value)
    }


}
