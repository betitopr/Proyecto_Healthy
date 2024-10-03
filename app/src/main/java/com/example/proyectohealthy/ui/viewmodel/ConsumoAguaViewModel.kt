package com.example.proyectohealthy.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import com.example.proyectohealthy.data.repository.ConsumoAguaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class ConsumoAguaViewModel(private val repository: ConsumoAguaRepository) : ViewModel() {

    private val _consumosAgua = MutableStateFlow<List<ConsumoAgua>>(emptyList())
    val consumosAgua: StateFlow<List<ConsumoAgua>> = _consumosAgua

    private val _currentConsumo = MutableStateFlow<ConsumoAgua?>(null)
    val currentConsumo: StateFlow<ConsumoAgua?> = _currentConsumo

    private val _totalConsumoHoy = MutableStateFlow<Float>(0f)
    val totalConsumoHoy: StateFlow<Float> = _totalConsumoHoy

    init {
        getAllConsumosAgua()
    }

    private fun getAllConsumosAgua() {
        viewModelScope.launch {
            repository.getAllConsumosAgua()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _consumosAgua.value = it
                }
        }
    }

    fun getConsumoAguaById(id: Int) {
        viewModelScope.launch {
            _currentConsumo.value = repository.getConsumoAguaById(id)
        }
    }

    fun getConsumosAguaByUserId(userId: Int) {
        viewModelScope.launch {
            repository.getConsumosAguaByUserId(userId)
                .collect {
                    _consumosAgua.value = it
                }
        }
    }

    fun insertConsumoAgua(consumoAgua: ConsumoAgua) {
        viewModelScope.launch {
            repository.insertConsumoAgua(consumoAgua)
            getAllConsumosAgua()
            updateTotalConsumoHoy(consumoAgua.idPerfil)
        }
    }

    fun updateConsumoAgua(consumoAgua: ConsumoAgua) {
        viewModelScope.launch {
            repository.updateConsumoAgua(consumoAgua)
            getAllConsumosAgua()
            updateTotalConsumoHoy(consumoAgua.idPerfil)
        }
    }

    fun deleteConsumoAgua(consumoAgua: ConsumoAgua) {
        viewModelScope.launch {
            repository.deleteConsumoAgua(consumoAgua)
            getAllConsumosAgua()
            updateTotalConsumoHoy(consumoAgua.idPerfil)
        }
    }

    fun getConsumosAguaByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            repository.getConsumosAguaByDateRange(startDate, endDate)
                .collect {
                    _consumosAgua.value = it
                }
        }
    }

    fun updateTotalConsumoHoy(userId: Int) {
        viewModelScope.launch {
            val hoy = Date() // Asumiendo que quieres el consumo de hoy
            val total = repository.getTotalConsumoAguaByFecha(userId, hoy) ?: 0f
            _totalConsumoHoy.value = total
        }
    }
}