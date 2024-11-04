package com.example.proyectohealthy.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.example.proyectohealthy.data.local.entity.RegistroDiario
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.data.repository.RegistroDiarioRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ProgresoViewModel @Inject constructor(
    private val registroDiarioRepository: RegistroDiarioRepository,
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _registrosDiarios = MutableStateFlow<List<RegistroDiario>>(emptyList())
    val registrosDiarios = _registrosDiarios.asStateFlow()

    private val _progresoHaciaObjetivo = MutableStateFlow(0f)
    val progresoHaciaObjetivo = _progresoHaciaObjetivo.asStateFlow()

    private val _periodoSeleccionado = MutableStateFlow(PeriodoVisualizacion.SEMANA)
    val periodoSeleccionado = _periodoSeleccionado.asStateFlow()

    private val _datosAgregados = MutableStateFlow<List<DatoAgregado>>(emptyList())
    val datosAgregados = _datosAgregados.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarDatos()
    }

    fun cambiarPeriodo(periodo: PeriodoVisualizacion) {
        viewModelScope.launch {
            _periodoSeleccionado.value = periodo
            cargarDatos()
        }
    }



    private fun calcularProgresoHaciaObjetivo(registros: List<RegistroDiario>, perfil: Perfil) {
        val pesoInicial = perfil.pesoActual
        val pesoObjetivo = perfil.pesoObjetivo
        val objetivo = perfil.objetivo

        val ultimoPesoRegistrado = registros
            .filter { it.pesoRegistrado != null }
            .maxByOrNull { it.fecha }
            ?.pesoRegistrado ?: pesoInicial

        val progreso = when(objetivo) {
            "Perder peso" -> {
                ((pesoInicial - ultimoPesoRegistrado) / (pesoInicial - pesoObjetivo)) * 100
            }
            "Ganar peso" -> {
                ((ultimoPesoRegistrado - pesoInicial) / (pesoObjetivo - pesoInicial)) * 100
            }
            else -> {
                val desviacionPermitida = 0.02f * pesoObjetivo // 2% de desviación permitida
                if (abs(ultimoPesoRegistrado - pesoObjetivo) <= desviacionPermitida) 100f else 0f
            }
        }.coerceIn(0f, 100f)

        _progresoHaciaObjetivo.value = progreso
    }

    private fun calcularDatosAgregados(registros: List<RegistroDiario>) {
        val datosAgregados = when(_periodoSeleccionado.value) {
            PeriodoVisualizacion.SEMANA -> calcularDatosDiarios(registros)
            PeriodoVisualizacion.MES -> calcularPromediosSemanales(registros)
            PeriodoVisualizacion.TRIMESTRE -> calcularPromediosMensuales(registros)
        }
        _datosAgregados.value = datosAgregados
    }

    private fun calcularDatosDiarios(registros: List<RegistroDiario>): List<DatoAgregado> {
        return registros.map { registro ->
            DatoAgregado(
                fecha = registro.fecha,
                calorias = registro.caloriasConsumidas.toFloat(),
                proteinas = registro.proteinasConsumidas,
                carbohidratos = registro.carbohidratosConsumidos,
                grasas = registro.grasasConsumidas,
                caloriasQuemadas = registro.caloriasQuemadas.toFloat()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularPromediosSemanales(registros: List<RegistroDiario>): List<DatoAgregado> {
        val hoy = LocalDate.now()
        val semanasAtras = hoy.minusWeeks(8)

        return registros
            .filter { it.fecha.isAfter(semanasAtras) && !it.fecha.isAfter(hoy) }
            .groupBy { it.fecha.get(WeekFields.ISO.weekOfWeekBasedYear()) }
            .map { (semana, registrosSemana) ->
                val primerDiaSemana = registrosSemana.minOf { it.fecha }
                DatoAgregado(
                    fecha = primerDiaSemana,
                    calorias = registrosSemana.averageOf { it.caloriasConsumidas },
                    proteinas = registrosSemana.averageOf { it.proteinasConsumidas },
                    carbohidratos = registrosSemana.averageOf { it.carbohidratosConsumidos },
                    grasas = registrosSemana.averageOf { it.grasasConsumidas },
                    caloriasQuemadas = registrosSemana.averageOf { it.caloriasQuemadas }
                )
            }
            .sortedBy { it.fecha }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularPromediosMensuales(registros: List<RegistroDiario>): List<DatoAgregado> {
        val hoy = LocalDate.now()
        val mesesAtras = hoy.minusMonths(6)

        return registros
            .filter { it.fecha.isAfter(mesesAtras) && !it.fecha.isAfter(hoy) }
            .groupBy { it.fecha.month }
            .map { (mes, registrosMes) ->
                val primerDiaMes = registrosMes.minOf { it.fecha }
                DatoAgregado(
                    fecha = primerDiaMes,
                    calorias = registrosMes.averageOf { it.caloriasConsumidas },
                    proteinas = registrosMes.averageOf { it.proteinasConsumidas },
                    carbohidratos = registrosMes.averageOf { it.carbohidratosConsumidos },
                    grasas = registrosMes.averageOf { it.grasasConsumidas },
                    caloriasQuemadas = registrosMes.averageOf { it.caloriasQuemadas }
                )
            }
            .sortedBy { it.fecha }
    }

    fun cargarDatos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: return@launch
                val perfil = perfilRepository.getPerfil(userId) ?: return@launch

                val fechaFin = LocalDate.now()
                val fechaInicio = when(_periodoSeleccionado.value) {
                    PeriodoVisualizacion.SEMANA -> fechaFin.minusDays(6) // Exactamente 7 días
                    PeriodoVisualizacion.MES -> fechaFin.minusWeeks(7) // 8 semanas
                    PeriodoVisualizacion.TRIMESTRE -> fechaFin.minusMonths(5) // 6 meses
                }

                registroDiarioRepository.obtenerRegistrosPorRango(userId, fechaInicio, fechaFin)
                    .collect { registros ->
                        val registrosProcesados = when(_periodoSeleccionado.value) {
                            PeriodoVisualizacion.SEMANA -> procesarRegistrosDiarios(
                                completarRegistrosFaltantes(registros, fechaInicio, fechaFin)
                            )
                            PeriodoVisualizacion.MES -> procesarRegistrosSemanales(registros)
                            PeriodoVisualizacion.TRIMESTRE -> procesarRegistrosMensuales(registros)
                        }

                        _registrosDiarios.value = registrosProcesados
                        calcularProgresoHaciaObjetivo(registrosProcesados, perfil)
                        calcularDatosAgregados(registrosProcesados)
                    }
            } catch (e: Exception) {
                Log.e("ProgresoViewModel", "Error cargando datos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun completarRegistrosFaltantes(
        registros: List<RegistroDiario>,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<RegistroDiario> {
        val registrosPorFecha = registros.associateBy { it.fecha }

        return generateSequence(fechaInicio) { it.plusDays(1) }
            .takeWhile { !it.isAfter(fechaFin) }
            .map { fecha ->
                registrosPorFecha[fecha] ?: RegistroDiario(
                    idPerfil = auth.currentUser?.uid ?: "",
                    fecha = fecha,
                    caloriasConsumidas = 0,
                    caloriasQuemadas = 0,
                    caloriasNetas = 0,
                    proteinasConsumidas = 0f,
                    carbohidratosConsumidos = 0f,
                    grasasConsumidas = 0f
                )
            }
            .toList()
            .takeLast(7) // Asegurar que solo tomamos los últimos 7 días
            .sortedBy { it.fecha }
    }

    private fun procesarRegistrosDiarios(registros: List<RegistroDiario>): List<RegistroDiario> {
        return registros
            .sortedBy { it.fecha }
            .takeLast(7) // Solo los últimos 7 días
    }

    private fun procesarRegistrosSemanales(registros: List<RegistroDiario>): List<RegistroDiario> {
        return registros
            .groupBy { it.fecha.get(WeekFields.ISO.weekOfWeekBasedYear()) }
            .map { (_, registrosSemana) ->
                val promedios = calcularPromedios(registrosSemana)
                val fechaUltimoDia = registrosSemana.maxOf { it.fecha }
                RegistroDiario(
                    fecha = fechaUltimoDia,
                    caloriasConsumidas = promedios.caloriasPromedio,
                    caloriasQuemadas = promedios.caloriasQuemadasPromedio,
                    caloriasNetas = promedios.caloriasNetasPromedio,
                    proteinasConsumidas = promedios.proteinasPromedio,
                    carbohidratosConsumidos = promedios.carbohidratosPromedio,
                    grasasConsumidas = promedios.grasasPromedio
                )
            }
            .takeLast(8) // Solo últimas 8 semanas
            .sortedBy { it.fecha }
    }

    private fun procesarRegistrosMensuales(registros: List<RegistroDiario>): List<RegistroDiario> {
        return registros
            .groupBy { it.fecha.month }
            .map { (_, registrosMes) ->
                val promedios = calcularPromedios(registrosMes)
                val fechaUltimoDia = registrosMes.maxOf { it.fecha }
                RegistroDiario(
                    fecha = fechaUltimoDia,
                    caloriasConsumidas = promedios.caloriasPromedio,
                    caloriasQuemadas = promedios.caloriasQuemadasPromedio,
                    caloriasNetas = promedios.caloriasNetasPromedio,
                    proteinasConsumidas = promedios.proteinasPromedio,
                    carbohidratosConsumidos = promedios.carbohidratosPromedio,
                    grasasConsumidas = promedios.grasasPromedio
                )
            }
            .takeLast(6) // Solo últimos 6 meses
            .sortedBy { it.fecha }
    }

    private fun <T> List<RegistroDiario>.averageOf(selector: (RegistroDiario) -> T): Float where T : Number {
        return if (isEmpty()) 0f else map { selector(it).toFloat() }.average().toFloat()
    }

    private data class PromediosRegistro(
        val caloriasPromedio: Int,
        val caloriasQuemadasPromedio: Int,
        val caloriasNetasPromedio: Int,
        val proteinasPromedio: Float,
        val carbohidratosPromedio: Float,
        val grasasPromedio: Float
    )

    private fun calcularPromedios(registros: List<RegistroDiario>): PromediosRegistro {
        return PromediosRegistro(
            caloriasPromedio = registros.map { it.caloriasConsumidas }.average().toInt(),
            caloriasQuemadasPromedio = registros.map { it.caloriasQuemadas }.average().toInt(),
            caloriasNetasPromedio = registros.map { it.caloriasNetas }.average().toInt(),
            proteinasPromedio = registros.map { it.proteinasConsumidas }.average().toFloat(),
            carbohidratosPromedio = registros.map { it.carbohidratosConsumidos }.average().toFloat(),
            grasasPromedio = registros.map { it.grasasConsumidas }.average().toFloat()
        )
    }

    data class DatoAgregado(
        val fecha: LocalDate,
        val calorias: Float,
        val proteinas: Float,
        val carbohidratos: Float,
        val grasas: Float,
        val caloriasQuemadas: Float
    )

    enum class PeriodoVisualizacion {
        SEMANA, MES, TRIMESTRE
    }
}