package com.example.proyectohealthy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserSelectionsViewModel : ViewModel() {
    // Lista para almacenar las selecciones
    private val _selections = mutableStateListOf<String>()
    val selections: List<String> get() = _selections

    // Variables para almacenar los datos del usuario
    var objetivo by mutableStateOf("Perder peso")
    var edad by mutableStateOf(17)
    var entrenamientoFuerza by mutableStateOf("No")
    var genero by mutableStateOf("Femenino")

    // MutableStateFlow para manejar el estado de peso, pesoObjetivo y altura
    private val _peso = MutableStateFlow(70f)
    val peso: StateFlow<Float> get() = _peso

    private val _pesoObjetivo = MutableStateFlow(70f)
    val pesoObjetivo: StateFlow<Float> get() = _pesoObjetivo

    private val _altura = MutableStateFlow(170f)
    val altura: StateFlow<Float> get() = _altura

    // Funciones para actualizar los valores
    fun addSelection(selection: String) {
        _selections.add(selection)
    }

    fun updateAltura(newAltura: Float) {
        _altura.value = newAltura
    }

    fun updatePeso(newPeso: Float) {
        _peso.value = newPeso
    }

    fun updatePesoObjetivo(newPesoObjetivo: Float) {
        _pesoObjetivo.value = newPesoObjetivo
    }

    fun updateEdad(newEdad: Int) {
        edad = newEdad
    }

    fun updateEntrenamientoFuerza(newEntrenamientoFuerza: String) {
        entrenamientoFuerza = newEntrenamientoFuerza
    }

    fun updateObjetivo(newObjetivo: String) {
        objetivo = newObjetivo
    }

    fun updateGenero(newGenero: String) {
        genero = newGenero
    }
}
