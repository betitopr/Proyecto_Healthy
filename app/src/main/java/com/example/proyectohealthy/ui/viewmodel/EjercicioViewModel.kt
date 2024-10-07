package com.example.proyectohealthy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.repository.EjercicioRepository
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
class EjercicioViewModel @Inject constructor(
    private val ejercicioRepository: EjercicioRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _ejercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val ejercicios: StateFlow<List<Ejercicio>> = _ejercicios.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                ejercicioRepository.getEjerciciosFlow(user.uid).collect {
                    _ejercicios.value = it
                }
            }
        }
    }

    fun createOrUpdateEjercicio(tipoActividad: String, caloriasBurnedPerMinute: Int) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val ejercicio = Ejercicio(
                        idPerfil = user.uid,
                        fechaCreacion = Date(),
                        tipoActividad = tipoActividad,
                        caloriasBurnedPerMinute = caloriasBurnedPerMinute
                    )
                    ejercicioRepository.createOrUpdateEjercicio(ejercicio)
                }
            } catch (e: Exception) {
                _error.value = "Error al registrar el ejercicio: ${e.message}"
            }
        }
    }

    fun getEjerciciosPorFecha(fecha: Date) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                ejercicioRepository.getEjerciciosPorFecha(user.uid, fecha).collect {
                    _ejercicios.value = it
                }
            }
        }
    }

    fun deleteEjercicio(idEjercicio: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    ejercicioRepository.deleteEjercicio(user.uid, idEjercicio)
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar el ejercicio: ${e.message}"
            }
        }
    }

    fun getEjerciciosPorTipo(tipoActividad: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                ejercicioRepository.getEjerciciosPorTipo(user.uid, tipoActividad).collect {
                    _ejercicios.value = it
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}