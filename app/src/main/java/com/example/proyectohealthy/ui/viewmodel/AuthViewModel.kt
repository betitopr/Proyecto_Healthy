package com.example.proyectohealthy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: PerfilRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    init {
        // Verificar el estado de autenticación al iniciar el ViewModel
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

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                createOrUpdateUserProfile()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.NotAuthenticated
    }

    private suspend fun createOrUpdateUserProfile() {
        val user = auth.currentUser ?: run {
            _authState.value = AuthState.Error("No authenticated user found")
            return
        }
        try {
            val perfil = Perfil(
                uid_firebase = user.uid,
                Nombre = user.displayName?.split(" ")?.firstOrNull() ?: "",
                Apellido = user.displayName?.split(" ")?.lastOrNull() ?: "",
                Genero = "",
                Altura = 0f,
                Edad = 0,
                Peso_Actual = 0f,
                Peso_Objetivo = 0f,
                Nivel_Actividad = "",
                Objetivo = "",
                Como_Conseguirlo = "",
                Entrenamiento_Fuerza = "",
                Perfil_Imagen = user.photoUrl?.toString(),
                Biografia = ""
            )
            userRepository.createOrUpdatePerfil(perfil)
            Log.d("AuthViewModel", "Perfil de usuario creado o actualizado exitosamente")
            _authState.value = AuthState.Authenticated
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error al crear o actualizar el perfil de usuario", e)
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
}


