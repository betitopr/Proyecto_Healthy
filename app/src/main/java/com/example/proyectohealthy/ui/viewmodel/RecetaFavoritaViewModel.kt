package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.RecetaFavorita
import com.example.proyectohealthy.data.repository.RecetaFavoritaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecetaFavoritaViewModel @Inject constructor(
    private val recetaFavoritaRepository: RecetaFavoritaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _recetasFavoritas = MutableStateFlow<List<RecetaFavorita>>(emptyList())
    val recetasFavoritas: StateFlow<List<RecetaFavorita>> = _recetasFavoritas.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                recetaFavoritaRepository.getRecetasFavoritasFlow(user.uid).collect {
                    _recetasFavoritas.value = it
                }
            }
        }
    }

    fun createOrUpdateRecetaFavorita(nombreReceta: String, ingredientes: String, instrucciones: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val receta = RecetaFavorita(
                        idPerfil = user.uid,
                        nombreReceta = nombreReceta,
                        ingredientes = ingredientes,
                        instrucciones = instrucciones
                    )
                    recetaFavoritaRepository.createOrUpdateRecetaFavorita(receta)
                }
            } catch (e: Exception) {
                _error.value = "Error al guardar la receta favorita: ${e.message}"
            }
        }
    }

    fun deleteRecetaFavorita(idReceta: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    recetaFavoritaRepository.deleteRecetaFavorita(user.uid, idReceta)
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar la receta favorita: ${e.message}"
            }
        }
    }

    fun searchRecetasFavoritas(query: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                recetaFavoritaRepository.searchRecetasFavoritas(user.uid, query).collect {
                    _recetasFavoritas.value = it
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
