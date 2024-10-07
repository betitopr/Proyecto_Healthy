package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RegistroComidaViewModel @Inject constructor(
    private val registroComidaRepository: RegistroComidaRepository,
    private val alimentoRepository: AlimentoRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _registrosComida = MutableStateFlow<List<RegistroComida>>(emptyList())
    val registrosComida: StateFlow<List<RegistroComida>> = _registrosComida.asStateFlow()

    private val _alimentosBuscados = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentosBuscados: StateFlow<List<Alimento>> = _alimentosBuscados.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _registrosComidaDiarios = MutableStateFlow<Map<String, List<RegistroComida>>>(emptyMap())
    val registrosComidaDiarios: StateFlow<Map<String, List<RegistroComida>>> = _registrosComidaDiarios.asStateFlow()

    init {
        cargarRegistrosComida()
        cargarRegistrosComidaDiarios()
    }

    private fun cargarRegistrosComida() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                registroComidaRepository.getRegistrosComidaFlow(user.uid).collect {
                    _registrosComida.value = it
                }
            }
        }
    }

    private fun cargarRegistrosComidaDiarios() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                val fechaHoy = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                registroComidaRepository.getRegistrosComidaPorFecha(user.uid, fechaHoy).collect { registros ->
                    _registrosComidaDiarios.value = registros.groupBy { it.tipoComida }
                }
            }
        }
    }




    fun buscarAlimentos(query: String) {
        viewModelScope.launch {
            alimentoRepository.searchAlimentosByNombre(query).collect {
                _alimentosBuscados.value = it
            }
        }
    }

    fun eliminarRegistroComida(registro: RegistroComida) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                registroComidaRepository.deleteRegistroComida(user.uid, registro.id)
                cargarRegistrosComidaDiarios() // Recargar los registros después de eliminar
            }
        }
    }

    fun agregarAlimento(alimento: Alimento, cantidad: Float, tipoComida: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                val nuevoRegistro = RegistroComida(
                    idPerfil = user.uid,
                    fecha = Date(),
                    tipoComida = tipoComida,
                    alimentos = mapOf(alimento.id to cantidad)
                )
                registroComidaRepository.createOrUpdateRegistroComida(nuevoRegistro)
            }
        }
    }


    fun clearError() {
        _error.value = null
    }
}