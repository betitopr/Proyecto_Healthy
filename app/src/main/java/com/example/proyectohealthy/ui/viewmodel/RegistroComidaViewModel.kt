package com.example.proyectohealthy.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.local.entity.RegistroDiario
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.data.repository.CaloriasQuemadasRepository
import com.example.proyectohealthy.data.repository.EjercicioRepository
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

// RegistroComidaViewModel.kt
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class RegistroComidaViewModel @Inject constructor(
    private val registroComidaRepository: RegistroComidaRepository,
    private val registroDiarioRepository: RegistroDiarioRepository,
    private val alimentoRepository: AlimentoRepository,
    private val misAlimentosRepository: MisAlimentosRepository,

    private val caloriasQuemadasRepository: CaloriasQuemadasRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _registrosComidaDiarios = MutableStateFlow<Map<String, List<RegistroComida>>>(emptyMap())
    val registrosComidaDiarios: StateFlow<Map<String, List<RegistroComida>>> = _registrosComidaDiarios.asStateFlow()

    private val _progresoNutricional = MutableStateFlow(ProgresoNutricional())
    val progresoNutricional: StateFlow<ProgresoNutricional> = _progresoNutricional.asStateFlow()

    init {
        viewModelScope.launch {
            // Observar actualizaciones desde el repositorio
            misAlimentosRepository.alimentoActualizado.collect { alimentoId ->
                // Recargar los registros cuando se actualiza un alimento
                cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
            }
        }

        viewModelScope.launch {
            _fechaSeleccionada.collect { fecha ->
                cargarRegistrosComidaPorFecha(fecha)
                auth.currentUser?.uid?.let { userId ->
                    caloriasQuemadasRepository.getCaloriasQuemadasPorFecha(userId, fecha)
                        .collect { calorias ->
                            actualizarCaloriasQuemadas(calorias)
                        }
                }
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
                actualizarRegistroDiario(fecha)
            }
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
                cargarRegistrosComidaPorFecha(registro.fecha)
            } catch (e: Exception) {
                Log.e("RegistroComidaViewModel", "Error al eliminar registro: ${e.message}")
            }
        }
    }

    private suspend fun actualizarRegistroDiario(fecha: LocalDate) {
        try {
            val userId = auth.currentUser?.uid ?: return
            val registros = registroComidaRepository.getRegistrosComidaPorFecha(userId, fecha).first()
            val caloriasQuemadas = caloriasQuemadasRepository.getCaloriasQuemadasPorFecha(userId, fecha).first()

            // Calcular totales
            var caloriasTotal = 0
            var proteinasTotal = 0f
            var carbohidratosTotal = 0f
            var grasasTotal = 0f

            registros.forEach { registro ->
                registro.alimentos.forEach { (alimentoId, cantidad) ->
                    alimentoRepository.getAlimentoById(alimentoId)?.let { alimento ->
                        caloriasTotal += (alimento.calorias * cantidad).toInt()
                        proteinasTotal += alimento.proteinas * cantidad
                        carbohidratosTotal += alimento.carbohidratos * cantidad
                        grasasTotal += alimento.grasas * cantidad
                    }
                }

                registro.misAlimentos.forEach { (alimentoId, cantidad) ->
                    misAlimentosRepository.getMiAlimentoById(userId, alimentoId)?.let { miAlimento ->
                        caloriasTotal += (miAlimento.calorias * cantidad).toInt()
                        proteinasTotal += miAlimento.proteinas * cantidad
                        carbohidratosTotal += miAlimento.carbohidratos * cantidad
                        grasasTotal += miAlimento.grasas * cantidad
                    }
                }
            }

            val caloriasNetas = caloriasTotal - caloriasQuemadas

            val registroDiario = RegistroDiario(
                idPerfil = userId,
                fecha = fecha,
                caloriasConsumidas = caloriasTotal,
                proteinasConsumidas = proteinasTotal,
                carbohidratosConsumidos = carbohidratosTotal,
                grasasConsumidas = grasasTotal,
                caloriasQuemadas = caloriasQuemadas,
                caloriasNetas = caloriasNetas
            )

            registroDiarioRepository.guardarRegistroDiario(registroDiario)

            _progresoNutricional.value = ProgresoNutricional(
                calorias = caloriasTotal,
                proteinas = proteinasTotal.toInt(),
                carbohidratos = carbohidratosTotal.toInt(),
                grasas = grasasTotal.toInt(),
                caloriasQuemadas = caloriasQuemadas,
                caloriasNetas = caloriasNetas
            )
        } catch (e: Exception) {
            Log.e("RegistroComidaVM", "Error actualizando registro diario", e)
        }
    }

    // Agregar método para actualizar calorías quemadas
    fun actualizarCaloriasQuemadas(caloriasQuemadas: Int) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val registros = registroComidaRepository.getRegistrosComidaPorFecha(userId, _fechaSeleccionada.value).first()

                // Calcular totales de alimentos
                var caloriasTotal = 0
                var proteinasTotal = 0f
                var carbohidratosTotal = 0f
                var grasasTotal = 0f

                registros.forEach { registro ->
                    registro.alimentos.forEach { (alimentoId, cantidad) ->
                        alimentoRepository.getAlimentoById(alimentoId)?.let { alimento ->
                            caloriasTotal += (alimento.calorias * cantidad).toInt()
                            proteinasTotal += alimento.proteinas * cantidad
                            carbohidratosTotal += alimento.carbohidratos * cantidad
                            grasasTotal += alimento.grasas * cantidad
                        }
                    }

                    registro.misAlimentos.forEach { (alimentoId, cantidad) ->
                        misAlimentosRepository.getMiAlimentoById(userId, alimentoId)?.let { miAlimento ->
                            caloriasTotal += (miAlimento.calorias * cantidad).toInt()
                            proteinasTotal += miAlimento.proteinas * cantidad
                            carbohidratosTotal += miAlimento.carbohidratos * cantidad
                            grasasTotal += miAlimento.grasas * cantidad
                        }
                    }
                }

                val caloriasNetas = caloriasTotal - caloriasQuemadas

                // Actualizar progreso nutricional
                _progresoNutricional.value = ProgresoNutricional(
                    calorias = caloriasTotal,
                    proteinas = proteinasTotal.toInt(),
                    carbohidratos = carbohidratosTotal.toInt(),
                    grasas = grasasTotal.toInt(),
                    caloriasQuemadas = caloriasQuemadas,
                    caloriasNetas = caloriasNetas
                )

                // Actualizar registro diario
                val registroDiario = RegistroDiario(
                    idPerfil = userId,
                    fecha = _fechaSeleccionada.value,
                    caloriasConsumidas = caloriasTotal,
                    proteinasConsumidas = proteinasTotal,
                    carbohidratosConsumidos = carbohidratosTotal,
                    grasasConsumidas = grasasTotal,
                    caloriasQuemadas = caloriasQuemadas,
                    caloriasNetas = caloriasNetas
                )

                registroDiarioRepository.guardarRegistroDiario(registroDiario)

            } catch (e: Exception) {
                Log.e("RegistroComidaVM", "Error actualizando calorías quemadas", e)
            }
        }
    }


}

data class ProgresoNutricional(
    val calorias: Int = 0,
    val proteinas: Int = 0,
    val grasas: Int = 0,
    val carbohidratos: Int = 0,
    val caloriasQuemadas: Int = 0,
    val caloriasNetas: Int = 0
)