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

    init {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                misAlimentosRepository.getMisAlimentosFlow(idPerfil).collect {
                    _misAlimentos.value = it
                }
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

//    fun getMiAlimentoById(id: String) {
//        viewModelScope.launch {
//            try {
//                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
//                val miAlimento = misAlimentosRepository.getMiAlimentoById(idPerfil, id)
//                _currentMiAlimento.value = miAlimento
//            } catch (e: Exception) {
//                _error.value = "Error al obtener mi alimento: ${e.message}"
//            }
//        }
//    }

    fun deleteMiAlimento(id: String) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                misAlimentosRepository.deleteMiAlimento(idPerfil, id)
            } catch (e: Exception) {
                _error.value = "Error al eliminar mi alimento: ${e.message}"
            }
        }
    }

    fun searchMisAlimentosByNombre(nombre: String) {
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
    }

    fun clearError() {
        _error.value = null
    }
}