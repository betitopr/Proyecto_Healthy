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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MisAlimentosViewModel @Inject constructor(
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _misAlimentos = MutableStateFlow<List<MisAlimentos>>(emptyList())
    val misAlimentos: StateFlow<List<MisAlimentos>> = _misAlimentos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                misAlimentosRepository.getMisAlimentosFlow(user.uid).collect {
                    _misAlimentos.value = it
                }
            }
        }
    }

    fun createOrUpdateMiAlimento(miAlimento: MisAlimentos) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val alimentoConIdPerfil = miAlimento.copy(idPerfil = user.uid)
                    misAlimentosRepository.createOrUpdateMiAlimento(alimentoConIdPerfil)
                }
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar el alimento: ${e.message}"
            }
        }
    }

    fun deleteMiAlimento(idAlimento: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    misAlimentosRepository.deleteMiAlimento(user.uid, idAlimento)
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar el alimento: ${e.message}"
            }
        }
    }

    fun searchMisAlimentosByNombre(nombre: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                misAlimentosRepository.searchMisAlimentosByNombre(user.uid, nombre).collect {
                    _misAlimentos.value = it
                }
            }
        }
    }

    fun getMisAlimentosByCategoria(categoria: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                misAlimentosRepository.getMisAlimentosByCategoria(user.uid, categoria).collect {
                    _misAlimentos.value = it
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}