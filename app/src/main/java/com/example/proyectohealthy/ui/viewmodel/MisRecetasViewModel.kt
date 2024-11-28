package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.RecetaApi
import com.example.proyectohealthy.data.local.entity.RecetaGuardada
import com.example.proyectohealthy.data.repository.RecetaRepository
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.example.proyectohealthy.util.toMiAlimento
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisRecetasViewModel @Inject constructor(
    private val recetaRepository: RecetaRepository,
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<MisRecetasUiState>(MisRecetasUiState.Loading)
    val uiState: StateFlow<MisRecetasUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        cargarRecetas()
    }

    private fun cargarRecetas() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                recetaRepository.getRecetasFlow(userId).collect { recetas ->
                    _uiState.value = if (recetas.isEmpty()) {
                        MisRecetasUiState.Empty
                    } else {
                        MisRecetasUiState.Success(recetas)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = MisRecetasUiState.Error("Error al cargar recetas: ${e.message}")
            }
        }
    }

    fun buscarRecetas(query: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                _uiState.value = MisRecetasUiState.Loading
                recetaRepository.searchRecetasFlow(userId, query).collect { recetas ->
                    _uiState.value = if (recetas.isEmpty()) {
                        MisRecetasUiState.Empty
                    } else {
                        MisRecetasUiState.Success(recetas)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al buscar recetas: ${e.message}"
                _uiState.value = MisRecetasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun agregarAMisAlimentos(receta: RecetaGuardada) {
        viewModelScope.launch {
            try {
                _uiState.value = MisRecetasUiState.Loading
                val alimento = receta.toMiAlimento()
                misAlimentosRepository.createOrUpdateMiAlimento(alimento)
                _uiState.value = MisRecetasUiState.AlimentoAgregado
                cargarRecetas() // Recargar las recetas después de agregar
            } catch (e: Exception) {
                _error.value = "Error al agregar a mis alimentos: ${e.message}"
                _uiState.value = MisRecetasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarReceta(idReceta: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                _uiState.value = MisRecetasUiState.Loading
                recetaRepository.deleteReceta(userId, idReceta)
                // La recarga de recetas se hará automáticamente por el Flow
            } catch (e: Exception) {
                _error.value = "Error al eliminar receta: ${e.message}"
                _uiState.value = MisRecetasUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    sealed class MisRecetasUiState {
        object Loading : MisRecetasUiState()
        object Empty : MisRecetasUiState()
        object AlimentoAgregado : MisRecetasUiState()
        data class Success(val recetas: List<RecetaGuardada>) : MisRecetasUiState()
        data class Error(val message: String) : MisRecetasUiState()
    }
}