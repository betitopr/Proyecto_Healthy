package com.example.proyectohealthy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class RegistroComidaViewModel(private val repository: RegistroComidaRepository) : ViewModel() {

    private val _registrosComidas = MutableStateFlow<List<RegistroComida>>(emptyList())
    val registrosComidas: StateFlow<List<RegistroComida>> = _registrosComidas

    private val _currentRegistro = MutableStateFlow<RegistroComida?>(null)
    val currentRegistro: StateFlow<RegistroComida?> = _currentRegistro

    init {
        getAllRegistrosComidas()
    }

    private fun getAllRegistrosComidas() {
        viewModelScope.launch {
            repository.getAllRegistrosComidas()
                .catch { e ->
                    // Manejar errores
                }
                .collect {
                    _registrosComidas.value = it
                }
        }
    }

    fun getRegistroComidaById(id: Int) {
        viewModelScope.launch {
            _currentRegistro.value = repository.getRegistroComidaById(id)
        }
    }

    fun getRegistrosComidasByUserId(userId: Int) {
        viewModelScope.launch {
            repository.getRegistrosComidasByUserId(userId)
                .collect {
                    _registrosComidas.value = it
                }
        }
    }

    fun insertRegistroComida(registroComida: RegistroComida) {
        viewModelScope.launch {
            repository.insertRegistroComida(registroComida)
            getAllRegistrosComidas()
        }
    }

    fun updateRegistroComida(registroComida: RegistroComida) {
        viewModelScope.launch {
            repository.updateRegistroComida(registroComida)
            getAllRegistrosComidas()
        }
    }

    fun deleteRegistroComida(registroComida: RegistroComida) {
        viewModelScope.launch {
            repository.deleteRegistroComida(registroComida)
            getAllRegistrosComidas()
        }
    }

    fun getRegistrosComidasByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getRegistrosComidasByDateRange(startDate, endDate)
                .collect {
                    _registrosComidas.value = it
                }
        }
    }

    fun getRegistrosComidasByTipo(tipoComida: String) {
        viewModelScope.launch {
            repository.getRegistrosComidasByTipo(tipoComida)
                .collect {
                    _registrosComidas.value = it
                }
        }
    }
}