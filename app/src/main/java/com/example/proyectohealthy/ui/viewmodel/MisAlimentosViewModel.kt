package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisAlimentosViewModel @Inject constructor(
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _misAlimentos = MutableStateFlow<List<MisAlimentos>>(emptyList())
    val misAlimentos: StateFlow<List<MisAlimentos>> = _misAlimentos.asStateFlow()

    private val _currentMiAlimento = MutableStateFlow<MisAlimentos?>(null)
    val currentMiAlimento: StateFlow<MisAlimentos?> = _currentMiAlimento.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _currentQuery = MutableStateFlow("")


    init {
        loadMisAlimentos()
    }


    fun searchMisAlimentosByNombre(query: String) {
        viewModelScope.launch {
            _currentQuery.value = query
            auth.currentUser?.uid?.let { userId ->
                misAlimentosRepository.getMisAlimentosFlow(userId).collect { allMisAlimentos ->
                    if (query.isEmpty()) {
                        _misAlimentos.value = allMisAlimentos
                    } else {
                        _misAlimentos.value = allMisAlimentos.filter {
                            it.nombre.contains(query, ignoreCase = true)
                        }
                    }
                }
            }
        }
    }

    private fun loadMisAlimentos() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                misAlimentosRepository.getMisAlimentosFlow(idPerfil).collect { alimentos ->
                    _misAlimentos.value = alimentos.filter { it.idPerfil == idPerfil }
                }
            } ?: run {
                _error.value = "Usuario no autenticado"
            }
        }
    }



    fun createOrUpdateMiAlimento(miAlimento: MisAlimentos) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                val updatedMiAlimento = miAlimento.copy(idPerfil = idPerfil)
                misAlimentosRepository.createOrUpdateMiAlimento(updatedMiAlimento)
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar mi alimento: ${e.message}"
            }
        }
    }

    suspend fun getMiAlimentoById(id: String): MisAlimentos? {
        val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        return misAlimentosRepository.getMiAlimentoById(idPerfil, id)
    }

    fun deleteMiAlimento(alimentoId: String, favoritosViewModel: FavoritosViewModel) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                // Primero quitamos de favoritos
                favoritosViewModel.removeFavorito(alimentoId)
                // Luego eliminamos el alimento
                misAlimentosRepository.deleteMiAlimento(userId, alimentoId)
            } catch (e: Exception) {
                _error.value = "Error al eliminar el alimento: ${e.message}"
            }
        }
    }

    fun updateMiAlimento(miAlimento: MisAlimentos) {
        viewModelScope.launch {
            try {
                misAlimentosRepository.createOrUpdateMiAlimento(miAlimento)
            } catch (e: Exception) {
                _error.value = "Error al actualizar el alimento: ${e.message}"
            }
        }
    }

    /*fun searchMisAlimentosByNombre(nombre: String) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                misAlimentosRepository.searchMisAlimentosByNombre(idPerfil, nombre).collect {
                    _misAlimentos.value = it
                }
            } catch (e: Exception) {
                _error.value = "Error al buscar mis alimentos: ${e.message}"
            }
        }
    }*/

    fun clearError() {
        _error.value = null
    }
}