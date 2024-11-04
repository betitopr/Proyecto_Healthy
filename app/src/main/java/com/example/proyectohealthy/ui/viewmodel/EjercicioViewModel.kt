package com.example.proyectohealthy.ui.viewmodel


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import com.example.proyectohealthy.data.repository.CaloriasQuemadasRepository
import com.example.proyectohealthy.data.repository.EjercicioRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EjercicioViewModel @Inject constructor(
    private val ejercicioRepository: EjercicioRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _ejercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val ejercicios: StateFlow<List<Ejercicio>> = _ejercicios.asStateFlow()

    private val _registrosEjercicio = MutableStateFlow<List<RegistroEjercicio>>(emptyList())
    val registrosEjercicio: StateFlow<List<RegistroEjercicio>> = _registrosEjercicio.asStateFlow()

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _caloriasQuemadas = MutableStateFlow(0)
    val caloriasQuemadas: StateFlow<Int> = _caloriasQuemadas.asStateFlow()

    init {
        viewModelScope.launch {
            cargarEjercicios()
            _fechaSeleccionada.collect { fecha ->
                cargarRegistrosEjercicioPorFecha(fecha)
            }
        }
    }

    private suspend fun cargarEjercicios() {
        ejercicioRepository.getEjercicios().collect {
            _ejercicios.value = it
            calcularCaloriasQuemadas()
        }
    }

    fun setFechaSeleccionada(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun cargarRegistrosEjercicioPorFecha(fecha: LocalDate) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: return@launch
                ejercicioRepository.getRegistrosEjercicioPorFecha(idPerfil, fecha).collect { registros ->
                    _registrosEjercicio.value = registros
                    calcularCaloriasQuemadas()
                }
            } catch (e: Exception) {
                Log.e("EjercicioViewModel", "Error cargando registros", e)
            }
        }
    }

    private fun calcularCaloriasQuemadas() {
        viewModelScope.launch {
            val totalCalorias = registrosEjercicio.value.sumOf { registro ->
                val ejercicio = ejercicios.value.find { it.id == registro.idEjercicio }
                ejercicio?.let { it.caloriasPorMinuto * registro.duracionMinutos } ?: 0
            }
            _caloriasQuemadas.value = totalCalorias

            // Actualizar en el repositorio
            auth.currentUser?.uid?.let { userId ->
                ejercicioRepository.actualizarCaloriasQuemadas(
                    userId,
                    _fechaSeleccionada.value,
                    totalCalorias
                )
            }
        }
    }

    fun agregarRegistroEjercicio(idEjercicio: String, duracionMinutos: Int) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val nuevoRegistro = RegistroEjercicio(
                    idPerfil = userId,
                    idEjercicio = idEjercicio,
                    duracionMinutos = duracionMinutos,
                    fecha = _fechaSeleccionada.value
                )

                // Calcular calorías actuales más las del nuevo ejercicio
                val ejercicio = ejercicios.value.find { it.id == idEjercicio }
                val caloriasNuevoEjercicio = ejercicio?.let { it.caloriasPorMinuto * duracionMinutos } ?: 0
                val totalCalorias = _caloriasQuemadas.value + caloriasNuevoEjercicio

                // Guardar registro y actualizar calorías en una sola operación
                ejercicioRepository.createOrUpdateRegistroEjercicio(nuevoRegistro, totalCalorias)
                cargarRegistrosEjercicioPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("EjercicioViewModel", "Error al agregar ejercicio", e)
            }
        }
    }

    fun eliminarRegistroEjercicio(registro: RegistroEjercicio) {
        viewModelScope.launch {
            try {
                val ejercicio = ejercicios.value.find { it.id == registro.idEjercicio }
                val caloriasEliminadas = ejercicio?.let { it.caloriasPorMinuto * registro.duracionMinutos } ?: 0
                val totalCalorias = (_caloriasQuemadas.value - caloriasEliminadas).coerceAtLeast(0)

                ejercicioRepository.deleteRegistroEjercicio(
                    registro.idPerfil,
                    registro.id,
                    registro.fecha,
                    totalCalorias
                )
                cargarRegistrosEjercicioPorFecha(_fechaSeleccionada.value)
            } catch (e: Exception) {
                Log.e("EjercicioViewModel", "Error al eliminar registro", e)
            }
        }
    }

    fun crearEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            try {
                ejercicioRepository.createOrUpdateEjercicios(ejercicio)
                cargarEjercicios()
            } catch (e: Exception) {
                Log.e("EjercicioViewModel", "Error al crear ejercicio", e)
            }
        }
    }

    suspend fun getEjercicioById(id: String): Ejercicio? {
        return ejercicioRepository.getEjercicioById(id)
    }
}