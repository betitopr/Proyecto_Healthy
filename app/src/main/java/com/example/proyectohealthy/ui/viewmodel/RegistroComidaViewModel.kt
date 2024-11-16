package com.example.proyectohealthy.ui.viewmodel

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
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
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroComidaRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.example.proyectohealthy.widget.WidgetUpdateManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
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
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val perfilRepository: PerfilRepository,
    private val widgetUpdateManager: WidgetUpdateManager

) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _registrosComidaDiarios = MutableStateFlow<Map<String, List<RegistroComida>>>(emptyMap())
    val registrosComidaDiarios: StateFlow<Map<String, List<RegistroComida>>> = _registrosComidaDiarios.asStateFlow()

    private val _caloriasQuemadas = MutableStateFlow(0)
    private val _progresoNutricional = MutableStateFlow(ProgresoNutricional())
    val progresoNutricional: StateFlow<ProgresoNutricional> = _progresoNutricional.asStateFlow()


    private val updateJob = SupervisorJob()

    init {
        viewModelScope.launch {
            // Combinar cambios de fecha y calorías quemadas
            combine(
                _fechaSeleccionada,
                caloriasQuemadasRepository.getCaloriasQuemadasPorFecha(
                    auth.currentUser?.uid ?: return@launch,
                    _fechaSeleccionada.value
                )
            ) { fecha, calorias ->
                Pair(fecha, calorias)
            }.collect { (fecha, calorias) ->
                _caloriasQuemadas.value = calorias
                cargarDatosCompletos(fecha, calorias)
            }
        }

        // Observar actualizaciones de alimentos
        viewModelScope.launch {
            misAlimentosRepository.alimentoActualizado.collect { alimentoId ->
                if (alimentoId.isNotBlank()) {
                    cargarDatosCompletos(
                        _fechaSeleccionada.value,
                        _caloriasQuemadas.value
                    )
                }
            }
        }
    }

    suspend fun cargarDatosCompletos(fecha: LocalDate, caloriasQuemadas: Int) {
        try {
            val userId = auth.currentUser?.uid ?: return

            // Cargar registros de comida
            registroComidaRepository.getRegistrosComidaPorFecha(userId, fecha).collect { registros ->
                // Agrupar registros por tipo de comida
                _registrosComidaDiarios.value = registros.groupBy { it.tipoComida }

                // Calcular totales
                var caloriasTotal = 0
                var proteinasTotal = 0f
                var carbohidratosTotal = 0f
                var grasasTotal = 0f

                // Procesar todos los registros
                registros.forEach { registro ->
                    // Procesar alimentos regulares
                    registro.alimentos.forEach { (alimentoId, cantidad) ->
                        alimentoRepository.getAlimentoById(alimentoId)?.let { alimento ->
                            caloriasTotal += (alimento.calorias * cantidad).toInt()
                            proteinasTotal += alimento.proteinas * cantidad
                            carbohidratosTotal += alimento.carbohidratos * cantidad
                            grasasTotal += alimento.grasas * cantidad
                        }
                    }

                    // Procesar alimentos personalizados
                    registro.misAlimentos.forEach { (alimentoId, cantidad) ->
                        misAlimentosRepository.getMiAlimentoById(userId, alimentoId)?.let { miAlimento ->
                            caloriasTotal += (miAlimento.calorias * cantidad).toInt()
                            proteinasTotal += miAlimento.proteinas * cantidad
                            carbohidratosTotal += miAlimento.carbohidratos * cantidad
                            grasasTotal += miAlimento.grasas * cantidad
                        }
                    }
                }

                // Calcular calorías netas
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

                // Guardar registro diario actualizado
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
                widgetUpdateManager.forceImmediateUpdate()
            }
        } catch (e: Exception) {
            Log.e("RegistroComidaVM", "Error cargando datos completos: ${e.message}")
        }
    }

    private fun actualizarWidget() {
        viewModelScope.launch {
            try {
                // Forzar actualización del widget
                widgetUpdateManager.forceImmediateUpdate()

                // Forzar actualización del sistema
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                context.sendBroadcast(intent)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error actualizando widget", e)
            }
        }
    }




    fun setFechaSeleccionada(fecha: LocalDate) {
        viewModelScope.launch {
            _fechaSeleccionada.value = fecha
        }
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
                actualizarRegistroDiario(_fechaSeleccionada.value)

                actualizarWidget()


                //cargarRegistrosComidaPorFecha(_fechaSeleccionada.value)
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
                actualizarWidget()




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
                actualizarWidget()

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
            actualizarWidget()

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
}



data class ProgresoNutricional(
    val calorias: Int = 0,
    val proteinas: Int = 0,
    val grasas: Int = 0,
    val carbohidratos: Int = 0,
    val caloriasQuemadas: Int = 0,
    val caloriasNetas: Int = 0
)