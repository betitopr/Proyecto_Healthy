package com.example.proyectohealthy.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import com.example.proyectohealthy.data.repository.ConsumoAguaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ConsumoAguaViewModel @Inject constructor(
    private val consumoAguaRepository: ConsumoAguaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _consumosAgua = MutableStateFlow<List<ConsumoAgua>>(emptyList())
    val consumosAgua: StateFlow<List<ConsumoAgua>> = _consumosAgua.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                consumoAguaRepository.getConsumoAguaFlow(user.uid).collect {
                    _consumosAgua.value = it
                }
            }
        }
    }

    fun createOrUpdateConsumoAgua(cantidad: Float) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val consumoAgua = ConsumoAgua(
                        idPerfil = user.uid,
                        fecha = Date(),
                        cantidad = cantidad
                    )
                    consumoAguaRepository.createOrUpdateConsumoAgua(consumoAgua)
                }
            } catch (e: Exception) {
                _error.value = "Error al registrar el consumo de agua: ${e.message}"
            }
        }
    }

    fun getConsumoAguaPorFecha(fecha: Date) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                consumoAguaRepository.getConsumoAguaPorFecha(user.uid, fecha).collect {
                    _consumosAgua.value = it
                }
            }
        }
    }

    fun deleteConsumoAgua(idConsumo: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    consumoAguaRepository.deleteConsumoAgua(user.uid, idConsumo)
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar el registro de consumo de agua: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}