package com.example.proyectohealthy.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import com.example.proyectohealthy.R
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                createOrUpdateUserProfile()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }
    // Método para registro/login con Google
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Iniciando autenticación con Google")
                // Obtener el token ID de la cuenta de Google
                val idToken = account.idToken
                if (idToken != null) {
                    // Crear credencial de Firebase con el token
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    // Autenticar con Firebase
                    auth.signInWithCredential(credential).await()
                    // Después de la autenticación exitosa, creamos o actualizamos el perfil
                    createOrUpdateUserProfile()
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
    // Método para registro tradicional con email/password
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                // Crear usuario en Firebase Auth
                auth.createUserWithEmailAndPassword(email, password).await()
                // Crear o actualizar perfil del usuario
                createOrUpdateUserProfile()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
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
    // Método para crear o actualizar el perfil del usuario
    private suspend fun createOrUpdateUserProfile() {
        val user = auth.currentUser ?: run {
            _authState.value = AuthState.Error("No authenticated user found")
            return
        }
        try {
            val existingPerfil = userRepository.getPerfil(user.uid)
            val updatedPerfil = existingPerfil?.copy(
                nombre = user.displayName?.split(" ")?.firstOrNull() ?: existingPerfil.nombre,
                apellido = user.displayName?.split(" ")?.lastOrNull() ?: existingPerfil.apellido,
                perfilImagen = user.photoUrl?.toString() ?: existingPerfil.perfilImagen
            ) ?: Perfil(
                uid = user.uid,
                nombre = user.displayName?.split(" ")?.firstOrNull() ?: "",
                apellido = user.displayName?.split(" ")?.lastOrNull() ?: "",
                genero = "",
                altura = 0f,
                edad = 0,
                pesoActual = 0f,
                pesoObjetivo = 0f,
                nivelActividad = "",
                objetivo = "",
                comoConseguirlo = "",
                entrenamientoFuerza = "",
                perfilImagen = user.photoUrl?.toString() ?: "",
                biografia = ""
            )
            userRepository.createOrUpdatePerfil(updatedPerfil)
            Log.d("AuthViewModel", "Perfil de usuario creado o actualizado exitosamente")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error al crear o actualizar el perfil de usuario", e)
            _authState.value = AuthState.Error("Failed to create or update user profile: ${e.message}")
        }
    }
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
//Los estados de autentificacion
    sealed class AuthState {
        object Initial : AuthState()//Estado inicial
        object Loading : AuthState()//Durante el proceso de autenticación
        object Authenticated : AuthState()//Autenticación exitosa
        object NotAuthenticated : AuthState()//No hay usuario autenticado
        data class Error(val message: String) : AuthState()//Error durante la autenticación
    }
}


