package com.example.proyectohealthy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _currentPerfil = MutableStateFlow<Perfil?>(null)
    val currentPerfil = _currentPerfil.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isPerfilCompleto = MutableStateFlow(false)
    val isPerfilCompleto = _isPerfilCompleto.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    init {
        observeAuthChanges()
    }

    private fun observeAuthChanges() {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                loadCurrentPerfil(user.uid)
            } ?: run {
                clearCurrentPerfil()
            }
        }
    }

    private fun loadCurrentPerfil(uid: String) {
        viewModelScope.launch {
            perfilRepository.getPerfilFlow(uid).collect { perfil ->
                _currentPerfil.value = perfil ?: createDefaultPerfil(uid)
                checkPerfilCompleto()
            }
        }
    }

    fun clearCurrentPerfil() {
        _currentPerfil.value = null
        _isPerfilCompleto.value = false
        _isEditing.value = false
        _error.value = null
        Log.d("PerfilViewModel", "Perfil actual limpiado")
    }

    private fun checkPerfilCompleto() {
        _currentPerfil.value?.let { perfil ->
            _isPerfilCompleto.value = perfil.altura > 0f && perfil.pesoActual > 0f && perfil.edad > 0
        }
    }

    private fun createDefaultPerfil(uid: String): Perfil {
        return Perfil(
            uid = uid,
            nombre = "",
            apellido = "",
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
    }

    fun updatePerfil(perfil: Perfil) {
        viewModelScope.launch {
            try {
                perfilRepository.createOrUpdatePerfil(perfil)
                _currentPerfil.value = perfil
                checkPerfilCompleto()
            } catch (e: Exception) {
                _error.value = "Error al actualizar el perfil: ${e.message}"
            }
        }
    }

    fun setEditing(isEditing: Boolean) {
        _isEditing.value = isEditing
    }

    fun updatePerfilField(updateFunction: suspend (String) -> Unit) {
        viewModelScope.launch {
            try {
                auth.currentUser?.uid?.let { uid ->
                    updateFunction(uid)
                    loadCurrentPerfil(uid)
                }
            } catch (e: Exception) {
                _error.value = "Error al actualizar el perfil: ${e.message}"
            }
        }
    }

    // Funciones de actualización específicas
    fun updateObjetivo(objetivo: String) = updatePerfilField { uid ->
        perfilRepository.updateObjetivo(uid, objetivo)
    }

    fun updateEdad(edad: Int) = updatePerfilField { uid ->
        perfilRepository.updateEdad(uid, edad)
    }

    fun updateGenero(genero: String) = updatePerfilField { uid ->
        perfilRepository.updateGenero(uid, genero)
    }

    fun updateAltura(altura: Float) = updatePerfilField { uid ->
        perfilRepository.updateAltura(uid, altura)
    }

    fun updatePesoActual(pesoActual: Float) = updatePerfilField { uid ->
        perfilRepository.updatePesoActual(uid, pesoActual)
    }

    fun updatePesoObjetivo(pesoObjetivo: Float) = updatePerfilField { uid ->
        perfilRepository.updatePesoObjetivo(uid, pesoObjetivo)
    }

    fun updateNivelActividad(nivelActividad: String) = updatePerfilField { uid ->
        perfilRepository.updateNivelActividad(uid, nivelActividad)
    }

    fun updateEntrenamientoFuerza(entrenamientoFuerza: String) = updatePerfilField { uid ->
        perfilRepository.updateEntrenamientoFuerza(uid, entrenamientoFuerza)
    }

    fun updateComoConseguirlo(comoConseguirlo: String) = updatePerfilField { uid ->
        perfilRepository.updateComoConseguirlo(uid, comoConseguirlo)
    }

    fun updatePremium(premium: Boolean) = updatePerfilField { uid ->
        perfilRepository.updatePremium(uid, premium)
    }

    fun addAlimentoFavorito(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.addAlimentoFavorito(uid, alimentoId)
    }

    fun removeAlimentoFavorito(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.removeAlimentoFavorito(uid, alimentoId)
    }

    fun addAlimentoReciente(alimentoId: String) = updatePerfilField { uid ->
        perfilRepository.addAlimentoReciente(uid, alimentoId)
    }

    fun clearError() {
        _error.value = null
    }
    fun signOut() {
        auth.signOut()
        clearCurrentPerfil()
    }
}