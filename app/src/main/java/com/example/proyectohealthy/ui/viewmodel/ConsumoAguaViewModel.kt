package com.example.proyectohealthy.ui.viewmodel



import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import com.example.proyectohealthy.data.repository.ConsumoAguaRepository
import com.example.proyectohealthy.data.repository.PerfilRepository
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
import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ConsumoAguaViewModel @Inject constructor(
    private val consumoAguaRepository: ConsumoAguaRepository,
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _consumoAgua = MutableStateFlow<ConsumoAgua?>(null)
    val consumoAgua: StateFlow<ConsumoAgua?> = _consumoAgua.asStateFlow()

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    private val _pesoUsuario = MutableStateFlow<Float?>(null)
    val pesoUsuario: StateFlow<Float?> = _pesoUsuario.asStateFlow()

    init {
        cargarDatosUsuario()
        cargarConsumoAguaPorFecha(_fechaSeleccionada.value)
    }

    private fun cargarDatosUsuario() {
        viewModelScope.launch {
            try {
                auth.currentUser?.uid?.let { idPerfil ->
                    perfilRepository.getPerfil(idPerfil)?.let { perfil ->
                        val peso = perfil.pesoActual
                        if (peso != null && peso > 0) {
                            _pesoUsuario.value = peso
                        } else {
                            _pesoUsuario.value = null // o algún valor por defecto
                        }
                        // Aquí puedes restablecer el consumo de agua al cambiar de usuario
                        _consumoAgua.value = null // Limpiar el consumo anterior
                        cargarConsumoAguaPorFecha(_fechaSeleccionada.value) // Cargar consumo para el nuevo usuario
                    }
                }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun setFechaSeleccionada(fecha: LocalDate) {
        _fechaSeleccionada.value = fecha
        cargarConsumoAguaPorFecha(fecha)
    }

    private fun cargarConsumoAguaPorFecha(fecha: LocalDate) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                consumoAguaRepository.getConsumoAguaPorFecha(idPerfil, fecha).collect { consumo ->
                    _consumoAgua.value = consumo ?: ConsumoAgua.fromLocalDate("", idPerfil, fecha, 0)
                }
            }
        }
    }

    fun actualizarConsumoAgua(nuevaCantidad: Int) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                val nuevoConsumo = ConsumoAgua.fromLocalDate(
                    id = _consumoAgua.value?.id ?: "",
                    idPerfil = idPerfil,
                    fecha = _fechaSeleccionada.value,
                    cantidad = nuevaCantidad
                )
                consumoAguaRepository.createOrUpdateConsumoAgua(nuevoConsumo)
                _consumoAgua.value = nuevoConsumo
            }
        }
    }

    fun calcularVasosRecomendados(): Int {
        val peso = _pesoUsuario.value ?: return 8 // Valor por defecto si no hay peso
        val mlRecomendados = peso * 0.033 * 1000 // Litros a mililitros en un solo paso
        return ceil(mlRecomendados / 300).toInt()
    }

    fun obtenerVasosMostrados(): Int {
        return calcularVasosRecomendados() + 2 // Añadimos 2 vasos adicionales solo para la UI
    }
}