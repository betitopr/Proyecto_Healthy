package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.RecetaApi
import com.example.proyectohealthy.data.repository.RecetaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExplorarRecetasViewModel @Inject constructor(
    private val recetaRepository: RecetaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExplorarRecetasUiState>(ExplorarRecetasUiState.Initial)
    val uiState: StateFlow<ExplorarRecetasUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun buscarRecetas() {
        val query = _searchQuery.value
        if (query.length < 3) return

        viewModelScope.launch {
            try {
                _uiState.value = ExplorarRecetasUiState.Loading
                val recetas = recetaRepository.buscarRecetas(query)
                _uiState.value = if (recetas.isEmpty()) {
                    ExplorarRecetasUiState.Empty
                } else {
                    ExplorarRecetasUiState.Success(recetas)
                }
            } catch (e: Exception) {
                _error.value = e.message
                _uiState.value = ExplorarRecetasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun guardarReceta(receta: RecetaApi) {
        viewModelScope.launch {
            try {
                _uiState.value = ExplorarRecetasUiState.Loading
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

                // Primero procesamos con IA para traducir y obtener valores nutricionales
                val recetaProcesada = recetaRepository.procesarRecetaConIA(receta)
                    ?: throw IllegalStateException("Error al procesar la receta")

                // Luego guardamos la receta procesada
                val recetaCompleta = recetaProcesada.copy(idPerfil = userId)
                recetaRepository.createOrUpdateReceta(recetaCompleta)

                _uiState.value = ExplorarRecetasUiState.RecetaGuardada(recetaCompleta)
            } catch (e: Exception) {
                _uiState.value = ExplorarRecetasUiState.Error("Error al guardar receta: ${e.message}")
            }
        }
    }

    // Solo se ejecuta al presionar generar
    fun generarReceta(descripcion: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ExplorarRecetasUiState.Loading
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

                val recetaGenerada = recetaRepository.generarRecetaConIA(descripcion)
                    ?: throw IllegalStateException("Error al generar receta")

                val recetaCompleta = recetaGenerada.copy(idPerfil = userId)
                recetaRepository.createOrUpdateReceta(recetaCompleta)

                // Primero mostramos mensaje de éxito
                _uiState.value = ExplorarRecetasUiState.GeneracionExitosa("Receta generada exitosamente y guardada en Mis Recetas")

                // Después de un delay, navegamos a mis recetas
                delay(1500)
                _uiState.value = ExplorarRecetasUiState.RecetaGuardada(recetaCompleta)
            } catch (e: Exception) {
                _uiState.value = ExplorarRecetasUiState.Error("Error al generar receta: ${e.message}")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    sealed class ExplorarRecetasUiState {
        object Initial : ExplorarRecetasUiState()
        object Loading : ExplorarRecetasUiState()
        object Empty : ExplorarRecetasUiState()
        data class GeneracionExitosa(val mensaje: String) : ExplorarRecetasUiState()
        data class Success(val recetas: List<RecetaApi>) : ExplorarRecetasUiState()
        data class Error(val message: String) : ExplorarRecetasUiState()
        data class RecetaGuardada(val receta: com.example.proyectohealthy.data.local.entity.RecetaGuardada) : ExplorarRecetasUiState()
    }
}