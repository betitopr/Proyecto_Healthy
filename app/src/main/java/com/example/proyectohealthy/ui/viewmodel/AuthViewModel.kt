package com.example.proyectohealthy.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import com.example.proyectohealthy.R
import com.example.proyectohealthy.data.local.entity.AuthType
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: PerfilRepository,
    @ApplicationContext private val context: Context // Inyectar el contexto
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    private val _passwordUpdateState = MutableStateFlow<PasswordUpdateState>(PasswordUpdateState.Initial)
    val passwordUpdateState = _passwordUpdateState.asStateFlow()

    init {
        // Verifica el estado de autenticación al iniciar el ViewModel
        checkAuthState()
    }

    fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated
            Log.d("AuthViewModel", "Usuario ya autenticado: ${currentUser.uid}")
        } else {
            _authState.value = AuthState.NotAuthenticated
            Log.d("AuthViewModel", "Usuario no autenticado")
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                createOrUpdateUserProfile(isGmailLogin = false)
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }


    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Iniciando autenticación con Google")
                val idToken = account.idToken
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(credential).await()
                    // Después de la autenticación exitosa, creamos o actualizamos el perfil
                    createOrUpdateUserProfile(isGmailLogin = true)
                    _authState.value = AuthState.Authenticated
                    Log.d("AuthViewModel", "Autenticación con Google exitosa")
                } else {
                    Log.e("AuthViewModel", "Token de ID de Google es null")
                    _authState.value = AuthState.Error("Token de ID inválido")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en la autenticación con Google", e)
                _authState.value = AuthState.Error("Error de autenticación con Google: ${e.message}")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                createOrUpdateUserProfile(isGmailLogin = false)
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _passwordUpdateState.value = PasswordUpdateState.Loading

                val user = auth.currentUser ?: throw Exception("No hay usuario autenticado")
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                // Paso 1: Reautenticar
                try {
                    user.reauthenticate(credential).await()
                } catch (e: Exception) {
                    _passwordUpdateState.value = PasswordUpdateState.Error("Contraseña actual incorrecta")
                    return@launch
                }

                // Paso 2: Actualizar contraseña
                try {
                    user.updatePassword(newPassword).await()

                    // Paso 3: Verificar que la actualización fue exitosa intentando autenticar con la nueva contraseña
                    val newCredential = EmailAuthProvider.getCredential(user.email!!, newPassword)
                    user.reauthenticate(newCredential).await()

                    _passwordUpdateState.value = PasswordUpdateState.Success
                } catch (e: Exception) {
                    _passwordUpdateState.value = PasswordUpdateState.Error(
                        "Error al actualizar contraseña: ${e.localizedMessage}"
                    )
                    return@launch
                }
            } catch (e: Exception) {
                _passwordUpdateState.value = PasswordUpdateState.Error(
                    "Error inesperado: ${e.localizedMessage}"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                // Obtener el cliente de Google Sign In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                // Cerrar sesión de Google
                try {
                    googleSignInClient.signOut().await()
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error al cerrar sesión de Google", e)
                }

                auth.signOut()

                try {
                    googleSignInClient.revokeAccess().await()
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error al revocar acceso", e)
                }

                _authState.value = AuthState.NotAuthenticated

                Log.d("AuthViewModel", "Sesión cerrada exitosamente")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al cerrar sesión: ${e.message}")
                Log.e("AuthViewModel", "Error al cerrar sesión", e)
            }
        }
    }

    private suspend fun createOrUpdateUserProfile(isGmailLogin: Boolean = false) {
        val user = auth.currentUser ?: run {
            _authState.value = AuthState.Error("No authenticated user found")
            return
        }
        try {
            // IMPORTANTE: Primero verificar si ya existe un perfil
            val existingPerfil = userRepository.getPerfil(user.uid)

            // Solo crear/actualizar si NO existe un perfil
            if (existingPerfil == null) {
                val randomNum = (1..1000).random()
                val defaultUsername = "user$randomNum"
                // Es un usuario nuevo, crear perfil inicial
                val newPerfil = Perfil(
                    uid = user.uid,
                    nombre = user.displayName?.split(" ")?.firstOrNull() ?: "",
                    apellido = user.displayName?.split(" ")?.lastOrNull() ?: "",
                    username = defaultUsername,
                    email = user.email ?: "",
                    authType = if (isGmailLogin) AuthType.GMAIL else AuthType.APP,
                    genero = "",
                    altura = 0f,
                    edad = 0,
                    pesoActual = 0f,
                    pesoObjetivo = 0f,
                    nivelActividad = "",
                    objetivo = "",
                    perfilCompleto = false,
                    comoConseguirlo = "",
                    entrenamientoFuerza = "",
                    perfilImagen = user.photoUrl?.toString(),
                    biografia = ""
                )
                userRepository.createOrUpdatePerfil(newPerfil)
                Log.d("AuthViewModel", "Nuevo perfil de usuario creado")
            } else {
                // El perfil ya existe, NO actualizamos nada
                Log.d("AuthViewModel", "Perfil existente encontrado, omitiendo actualización")
            }
            _authState.value = AuthState.Authenticated
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error al crear o verificar el perfil de usuario", e)
            _authState.value = AuthState.Error("Failed to create or update user profile: ${e.message}")
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Authenticated : AuthState()
        object NotAuthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class PasswordUpdateState {
        object Initial : PasswordUpdateState()
        object Loading : PasswordUpdateState()
        object Success : PasswordUpdateState()
        data class Error(val message: String) : PasswordUpdateState()
    }

}


