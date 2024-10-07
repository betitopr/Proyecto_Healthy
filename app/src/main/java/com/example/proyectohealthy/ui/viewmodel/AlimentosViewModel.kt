package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.repository.AlimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AlimentoViewModel @Inject constructor(
    private val alimentoRepository: AlimentoRepository
) : ViewModel() {
    private val _alimentos = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentos: StateFlow<List<Alimento>> = _alimentos.asStateFlow()

    private val _currentAlimento = MutableStateFlow<Alimento?>(null)
    val currentAlimento: StateFlow<Alimento?> = _currentAlimento.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            alimentoRepository.getAllAlimentosFlow().collect {
                _alimentos.value = it
            }
        }
    }

    fun createOrUpdateAlimento(alimento: Alimento) {
        viewModelScope.launch {
            try {
                alimentoRepository.createOrUpdateAlimento(alimento)
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar el alimento: ${e.message}"
            }
        }
    }

    fun getAlimento(id: String) {
        viewModelScope.launch {
            alimentoRepository.getAlimentoFlow(id).collect {
                _currentAlimento.value = it
            }
        }
    }

    fun deleteAlimento(id: String) {
        viewModelScope.launch {
            try {
                alimentoRepository.deleteAlimento(id)
            } catch (e: Exception) {
                _error.value = "Error al eliminar el alimento: ${e.message}"
            }
        }
    }

    fun searchAlimentosByNombre(nombre: String) {
        viewModelScope.launch {
            alimentoRepository.searchAlimentosByNombre(nombre).collect {
                _alimentos.value = it
            }
        }
    }

    fun getAlimentosByCategoria(categoria: String) {
        viewModelScope.launch {
            alimentoRepository.getAlimentosByCategoria(categoria).collect {
                _alimentos.value = it
            }
        }
    }

    fun getAlimentosByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            alimentoRepository.getAlimentosByDateRange(startDate, endDate).collect {
                _alimentos.value = it
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}