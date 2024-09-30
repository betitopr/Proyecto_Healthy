package com.example.proyectohealthy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.repository.EjercicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class EjercicioViewModel(private val repository: EjercicioRepository) : ViewModel() {

    private val _ejercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val ejercicios: StateFlow<List<Ejercicio>> = _ejercicios

    private val _currentEjercicio = MutableStateFlow<Ejercicio?>(null)
    val currentEjercicio: StateFlow<Ejercicio?> = _currentEjercicio

    init {
        getAllEjercicios()
    }

    private fun getAllEjercicios() {
        viewModelScope.launch {
            repository.getAllEjercicios()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _ejercicios.value = it
                }
        }
    }

    fun getEjercicioById(id: Int) {
        viewModelScope.launch {
            _currentEjercicio.value = repository.getEjercicioById(id)
        }
    }

    fun insertEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            repository.insertEjercicio(ejercicio)
            getAllEjercicios()
        }
    }

    fun updateEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            repository.updateEjercicio(ejercicio)
            getAllEjercicios()
        }
    }

    fun deleteEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            repository.deleteEjercicio(ejercicio)
            getAllEjercicios()
        }
    }

    fun getEjerciciosByTipo(tipo: String) {
        viewModelScope.launch {
            repository.getEjerciciosByTipo(tipo)
                .collect {
                    _ejercicios.value = it
                }
        }
    }

    fun getEjerciciosByFecha(fecha: Date) {
        viewModelScope.launch {
            repository.getEjerciciosByFecha(fecha)
                .collect {
                    _ejercicios.value = it
                }
        }
    }

    fun getEjerciciosByMinCalorias(minCalorias: Int) {
        viewModelScope.launch {
            repository.getEjerciciosByMinCalorias(minCalorias)
                .collect {
                    _ejercicios.value = it
                }
        }
    }
}