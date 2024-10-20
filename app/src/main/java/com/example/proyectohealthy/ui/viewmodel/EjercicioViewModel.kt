package com.example.proyectohealthy.ui.viewmodel


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import com.example.proyectohealthy.data.repository.EjercicioRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    @RequiresApi(Build.VERSION_CODES.O)
    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

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
        }
    }

    fun setFechaSeleccionada(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
    }

    fun cargarRegistrosEjercicioPorFecha(fecha: LocalDate) {
        viewModelScope.launch {
            val idPerfil = auth.currentUser?.uid ?: return@launch
            ejercicioRepository.getRegistroEjerciciosPorFecha(idPerfil, fecha).collect { registros ->
                _registrosEjercicio.value = registros
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarRegistroEjercicio(idEjercicio: String, duracionMinutos: Int) {
        viewModelScope.launch {
            val idPerfil = auth.currentUser?.uid ?: return@launch
            val nuevoRegistro = RegistroEjercicio(
                idPerfil = idPerfil,
                idEjercicio = idEjercicio,
                duracionMinutos = duracionMinutos,
                fecha = _fechaSeleccionada.value
            )
            ejercicioRepository.createOrUpdateRegistroEjercicio(nuevoRegistro)
            cargarRegistrosEjercicioPorFecha(_fechaSeleccionada.value)
        }
    }

    fun eliminarRegistroEjercicio(registroEjercicio: RegistroEjercicio) {
        viewModelScope.launch {
            ejercicioRepository.deleteRegistroEjercicio(registroEjercicio.idPerfil, registroEjercicio.id)
            cargarRegistrosEjercicioPorFecha(_fechaSeleccionada.value)
        }
    }

    fun crearEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            try {
                ejercicioRepository.createOrUpdateEjercicio(ejercicio)
                // Recarga la lista de ejercicios después de crear uno nuevo
                cargarEjercicios()
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    suspend fun getEjercicioById(id: String): Ejercicio? {
        return ejercicioRepository.getEjercicioById(id)
    }
}