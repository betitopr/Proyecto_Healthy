package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.repository.AlimentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class AlimentosViewModel(private val repository: AlimentoRepository) : ViewModel() {

    private val _alimentos = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentos: StateFlow<List<Alimento>> = _alimentos

    init {
        getAllAlimentos()
    }

    class Factory(private val repository: AlimentoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlimentosViewModel::class.java)) {
                return AlimentosViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private fun getAllAlimentos() {
        viewModelScope.launch {
            repository.getAllAlimentos()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _alimentos.value = it
                }
        }
    }

    fun insertAlimento(alimento: Alimento) {
        viewModelScope.launch {
            repository.insertAlimento(alimento)
        }
    }

    fun updateAlimento(alimento: Alimento) {
        viewModelScope.launch {
            repository.updateAlimento(alimento)
        }
    }

    fun deleteAlimento(alimento: Alimento) {
        viewModelScope.launch {
            repository.deleteAlimento(alimento)
        }
    }

    fun searchAlimentosByNombre(nombre: String) {
        viewModelScope.launch {
            repository.searchAlimentosByNombre(nombre)
                .collect {
                    _alimentos.value = it
                }
        }
    }

    fun getAlimentosByClasificacion(clasificacion: String) {
        viewModelScope.launch {
            repository.getAlimentosByClasificacion(clasificacion)
                .collect {
                    _alimentos.value = it
                }
        }
    }

    fun getAlimentosByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getAlimentosByDateRange(startDate, endDate)
                .collect {
                    _alimentos.value = it
                }
        }
    }
}