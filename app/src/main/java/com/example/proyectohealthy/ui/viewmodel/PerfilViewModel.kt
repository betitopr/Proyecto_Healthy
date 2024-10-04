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

    init {
        loadCurrentPerfil()
    }

    private fun loadCurrentPerfil() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                try {
                    perfilRepository.getPerfilFlow(user.uid).collect { perfil ->
                        _currentPerfil.value = perfil ?: createDefaultPerfil(user.uid)
                    }
                } catch (e: Exception) {
                    _error.value = "Error al cargar el perfil: ${e.message}"
                }
            }
        }
    }

    private fun createDefaultPerfil(uid: String): Perfil {
        return Perfil(
            uid_firebase = uid,
            Nombre = "",
            Apellido = "",
            Genero = "",
            Altura = 0f,
            Edad = 0,
            Peso_Actual = 0f,
            Peso_Objetivo = 0f,
            Nivel_Actividad = "",
            Objetivo = "",
            Como_Conseguirlo = "",
            Entrenamiento_Fuerza = "",
            Perfil_Imagen = "",
            Biografia = ""
        )
    }


    private fun updatePerfilField(updateFunction: suspend (String) -> Unit) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                try {
                    updateFunction(user.uid)
                    loadCurrentPerfil() // Recargar el perfil después de la actualización
                } catch (e: Exception) {
                    _error.value = "Error al actualizar el perfil: ${e.message}"
                }
            }
        }
    }

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

    fun createOrUpdatePerfil(perfil: Perfil) {
        viewModelScope.launch {
            try {
                perfilRepository.createOrUpdatePerfil(perfil)
                _currentPerfil.value = perfil
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar el perfil: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}