package com.example.proyectohealthy.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
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
    private val alimentoRepository: AlimentoRepository,
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _registrosComidaDiarios = MutableStateFlow<Map<String, List<RegistroComida>>>(emptyMap())
    val registrosComidaDiarios: StateFlow<Map<String, List<RegistroComida>>> = _registrosComidaDiarios.asStateFlow()

    private val _progresoNutricional = MutableStateFlow(ProgresoNutricional())
    val progresoNutricional: StateFlow<ProgresoNutricional> = _progresoNutricional.asStateFlow()

    private val _caloriasQuemadas = MutableStateFlow(0)
    private val caloriasQuemadas: StateFlow<Int> = _caloriasQuemadas.asStateFlow()



    init {
        viewModelScope.launch {
            _fechaSeleccionada.collect { fecha ->
                cargarRegistrosComidaPorFecha(fecha)
            }
        }
    }

    fun setFechaSeleccionada(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun cargarRegistrosComidaPorFecha(fecha: LocalDate) {
        viewModelScope.launch {
            val idPerfil = auth.currentUser?.uid ?: return@launch
            registroComidaRepository.getRegistrosComidaPorFecha(idPerfil, fecha).collect { registros ->
                _registrosComidaDiarios.value = registros.groupBy { it.tipoComida }
                calcularProgresoNutricional(registros)
            }
        }
    }

    fun actualizarCaloriasQuemadas(calorias: Int) {
        _caloriasQuemadas.value = calorias
    }

    private fun calcularProgresoNutricional(registros: List<RegistroComida>) {
        viewModelScope.launch {
            var calorias = 0
            var proteinas = 0f
            var grasas = 0f
            var carbohidratos = 0f

            registros.forEach { registro ->
                registro.alimentos.forEach { (alimentoId, cantidad) ->
                    val alimento = alimentoRepository.getAlimentoById(alimentoId)
                    alimento?.let {
                        calorias += (it.calorias * cantidad).toInt()
                        proteinas += it.proteinas * cantidad
                        grasas += it.grasas * cantidad
                        carbohidratos += it.carbohidratos * cantidad
                    }
                }
                registro.misAlimentos.forEach { (alimentoId, cantidad) ->
                    val miAlimento = misAlimentosRepository.getMiAlimentoById(auth.currentUser?.uid ?: "", alimentoId)
                    miAlimento?.let {
                        calorias += (it.calorias * cantidad).toInt()
                        proteinas += it.proteinas * cantidad
                        grasas += it.grasas * cantidad
                        carbohidratos += it.carbohidratos * cantidad
                    }
                }
            }

            calorias -= caloriasQuemadas.value

            _progresoNutricional.value = ProgresoNutricional(
                calorias = calorias,
                proteinas = proteinas.toInt(),
                grasas = grasas.toInt(),
                carbohidratos = carbohidratos.toInt()
            )
        }
    }

    fun agregarAlimento(alimento: Alimento, cantidad: Float, tipoComida: String) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                val nuevoRegistro = RegistroComida(
                    idPerfil = idPerfil,
                    fecha = _fechaSeleccionada.value,
                    tipoComida = tipoComida,
                    alimentos = mapOf(alimento.id to cantidad),
                    misAlimentos = emptyMap()
                )
                registroComidaRepository.createOrUpdateRegistroComida(nuevoRegistro)
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al agregar alimento: ${e.message}")
            }
        }
    }

    fun agregarMiAlimento(miAlimento: MisAlimentos, cantidad: Float, tipoComida: String) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                val nuevoRegistro = RegistroComida(
                    idPerfil = idPerfil,
                    fecha = _fechaSeleccionada.value,
                    tipoComida = tipoComida,
                    alimentos = emptyMap(),
                    misAlimentos = mapOf(miAlimento.id to cantidad)
                )
                registroComidaRepository.createOrUpdateRegistroComida(nuevoRegistro)
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al agregar mi alimento: ${e.message}")
            }
        }
    }

    fun eliminarRegistroComida(registro: RegistroComida) {
        viewModelScope.launch {
            try {
                registroComidaRepository.deleteRegistroComida(registro.idPerfil, registro.id, registro.fecha)
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al eliminar registro: ${e.message}")
            }
        }
    }
}

data class ProgresoNutricional(
    val calorias: Int = 0,
    val proteinas: Int = 0,
    val grasas: Int = 0,
    val carbohidratos: Int = 0
)