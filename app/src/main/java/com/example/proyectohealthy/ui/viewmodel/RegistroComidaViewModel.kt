package com.example.proyectohealthy.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RegistroComidaViewModel @Inject constructor(
    private val registroComidaRepository: RegistroComidaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _registrosComidaDiarios = MutableStateFlow<Map<String, List<RegistroComida>>>(emptyMap())
    val registrosComidaDiarios: StateFlow<Map<String, List<RegistroComida>>> = _registrosComidaDiarios.asStateFlow()

    init {
        viewModelScope.launch {
            _fechaSeleccionada.collect { fecha ->
                cargarRegistrosComidaPorFecha(fecha)
            }
        }
    }

    private fun sanitizeKey(key: String): String {
        return key.replace(Regex("[./#$\\[\\]]"), "_")
    }

    fun setFechaSeleccionada(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun cargarRegistrosComidaPorFecha(fecha: LocalDate) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: return@launch
                registroComidaRepository.getRegistrosComidaPorFecha(idPerfil, fecha).collect { registros ->
                    _registrosComidaDiarios.value = registros.groupBy { it.tipoComida }
                }
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al cargar registros: ${e.message}")
                _registrosComidaDiarios.value = emptyMap()
            }
        }
    }

    fun agregarAlimento(alimento: Alimento, cantidad: Float, tipoComida: String, esMiAlimento: Boolean) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                val sanitizedAlimentoId = sanitizeKey(alimento.id)
                if (sanitizedAlimentoId.isBlank()) {
                    Log.e("RegistroComidaViewModel", "ID de alimento vacío: ${alimento.nombre}")
                    return@launch
                }
                val sanitizedTipoComida = sanitizeKey(tipoComida)

                val alimentosMap = if (!esMiAlimento) mapOf(sanitizedAlimentoId to cantidad) else emptyMap()
                val misAlimentosMap = if (esMiAlimento) mapOf(sanitizedAlimentoId to cantidad) else emptyMap()

                val nuevoRegistro = RegistroComida(
                    idPerfil = sanitizeKey(idPerfil),
                    fecha = _fechaSeleccionada.value,
                    tipoComida = sanitizedTipoComida,
                    alimentos = alimentosMap,
                    misAlimentos = misAlimentosMap
                )

                Log.d("RegistroComidaViewModel", "Intentando agregar: $nuevoRegistro")
                registroComidaRepository.createOrUpdateRegistroComida(nuevoRegistro)
                Log.d("RegistroComidaViewModel", "Alimento agregado exitosamente")
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al agregar alimento: ${e.message}", e)
            }
        }
    }

    fun eliminarRegistroComida(registro: RegistroComida) {
        viewModelScope.launch {
            try {
                registroComidaRepository.deleteRegistroComida(registro.idPerfil, registro.id, registro.fecha)
                Log.d("RegistroComidaViewModel", "Registro eliminado exitosamente")
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al eliminar registro: ${e.message}")
            }
        }
    }
}