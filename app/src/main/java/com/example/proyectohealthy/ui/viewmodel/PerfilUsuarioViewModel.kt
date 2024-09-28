package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.PerfilUsuario
import com.example.proyectohealthy.data.repository.PerfilUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PerfilUsuarioViewModel(private val repository: PerfilUsuarioRepository) : ViewModel() {

    private val _perfiles = MutableStateFlow<List<PerfilUsuario>>(emptyList())
    val perfiles: StateFlow<List<PerfilUsuario>> = _perfiles

    private val _currentPerfil = MutableStateFlow<PerfilUsuario?>(null)
    val currentPerfil: StateFlow<PerfilUsuario?> = _currentPerfil

    init {
        getAllPerfiles()
    }

    private fun getAllPerfiles() {
        viewModelScope.launch {
            repository.getAllPerfiles()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _perfiles.value = it
                }
        }
    }

    fun insertPerfil(perfil: PerfilUsuario) {
        viewModelScope.launch {
            repository.insertPerfil(perfil)
            getAllPerfiles()
        }
    }

    fun updatePerfil(perfil: PerfilUsuario) {
        viewModelScope.launch {
            repository.updatePerfil(perfil)
            getAllPerfiles()
        }
    }

    fun deletePerfil(perfil: PerfilUsuario) {
        viewModelScope.launch {
            repository.deletePerfil(perfil)
            getAllPerfiles()
        }
    }

    fun getPerfilesByNivelActividad(nivelActividad: String) {
        viewModelScope.launch {
            repository.getPerfilesByNivelActividad(nivelActividad)
                .collect {
                    _perfiles.value = it
                }
        }
    }

    fun getPerfilesByObjetivoSalud(objetivo: String) {
        viewModelScope.launch {
            repository.getPerfilesByObjetivoSalud(objetivo)
                .collect {
                    _perfiles.value = it
                }
        }
    }

    fun getPerfilesByRangoPeso(pesoMin: Float, pesoMax: Float) {
        viewModelScope.launch {
            repository.getPerfilesByRangoPeso(pesoMin, pesoMax)
                .collect {
                    _perfiles.value = it
                }
        }
    }
}