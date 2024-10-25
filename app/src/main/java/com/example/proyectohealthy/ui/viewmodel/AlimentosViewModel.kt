package com.example.proyectohealthy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.OrderType
import com.example.proyectohealthy.data.repository.AlimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.comparisons.compareBy
import javax.inject.Inject

@HiltViewModel
class AlimentoViewModel @Inject constructor(
    private val alimentoRepository: AlimentoRepository
) : ViewModel() {
    // Estados existentes
    private val _alimentos = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentos = _alimentos.asStateFlow()

    private val _currentAlimento = MutableStateFlow<Alimento?>(null)
    val currentAlimento: StateFlow<Alimento?> = _currentAlimento.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentQuery = MutableStateFlow("")

    // Nuevos estados para filtros
    private val _filtros = MutableStateFlow(AlimentoFiltros())
    val filtros = _filtros.asStateFlow()

    private val _categoriasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val categoriasDisponibles = _categoriasDisponibles.asStateFlow()

    private var alimentosSinFiltrar = listOf<Alimento>()

    init {
        viewModelScope.launch {
            alimentoRepository.getAllAlimentosFlow().collect { alimentos ->
                alimentosSinFiltrar = alimentos
                _categoriasDisponibles.value = alimentos.map { it.categoria }.distinct().sorted()
                aplicarFiltros()
            }
        }
    }

    // Nueva función para aplicar filtros
    private fun aplicarFiltros() {
        val query = _currentQuery.value
        val filtrosActuales = _filtros.value
        var resultados = alimentosSinFiltrar

        // Aplicar búsqueda por texto
        if (query.isNotBlank()) {
            resultados = resultados.filter {
                it.nombre.contains(query, ignoreCase = true)
            }
        }

        // Aplicar filtros
        resultados = resultados
            .aplicarFiltrosCategoria(filtrosActuales)
            .aplicarFiltrosNutrientes(filtrosActuales)
            .aplicarOrdenamiento(filtrosActuales)

        _alimentos.value = resultados
    }

    // Funciones auxiliares para filtrado
    private fun List<Alimento>.aplicarFiltrosCategoria(filtros: AlimentoFiltros): List<Alimento> {
        return if (filtros.categories.isEmpty()) this
        else filter { it.categoria in filtros.categories }
    }

    private fun List<Alimento>.aplicarFiltrosNutrientes(filtros: AlimentoFiltros): List<Alimento> {
        var resultado = this

        with(filtros) {
            if (caloriesRange.isEnabled) {
                resultado = resultado.filter {
                    it.calorias.toFloat() in caloriesRange.min..caloriesRange.max
                }
            }
            if (proteinsRange.isEnabled) {
                resultado = resultado.filter {
                    it.proteinas in proteinsRange.min..proteinsRange.max
                }
            }
            if (carbsRange.isEnabled) {
                resultado = resultado.filter {
                    it.carbohidratos in carbsRange.min..carbsRange.max
                }
            }
            if (fatsRange.isEnabled) {
                resultado = resultado.filter {
                    it.grasas in fatsRange.min..fatsRange.max
                }
            }
        }

        return resultado
    }

    private fun List<Alimento>.aplicarOrdenamiento(filtros: AlimentoFiltros): List<Alimento> {
        val comparator = when (filtros.orderType) {
            is OrderType.Name -> compareBy<Alimento> { it.nombre }
            is OrderType.Calories -> compareBy<Alimento> { it.calorias }
            is OrderType.Proteins -> compareBy<Alimento> { it.proteinas }
            is OrderType.Carbs -> compareBy<Alimento> { it.carbohidratos }
            is OrderType.Fats -> compareBy<Alimento> { it.grasas }
        }

        return if (filtros.isAscending) {
            sortedWith(comparator)
        } else {
            sortedWith(comparator.reversed())
        }
    }

    // Función para actualizar filtros
    fun updateFiltros(nuevosFiltros: AlimentoFiltros) {
        viewModelScope.launch {
            _filtros.value = nuevosFiltros
            aplicarFiltros()
        }
    }

    // Funciones existentes modificadas para trabajar con filtros
    fun searchAlimentosByNombre(nombre: String) {
        _currentQuery.value = nombre
        aplicarFiltros()
    }

    /* Funciones existentes mantenidas sin cambios */
    fun createOrUpdateAlimento(alimento: Alimento) {
        viewModelScope.launch {
            try {
                alimentoRepository.createOrUpdateAlimento(alimento)
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar el alimento: ${e.message}"
            }
        }
    }

    suspend fun getAlimentoById(id: String): Alimento? {
        return alimentoRepository.getAlimentoById(id)
    }

    fun createAlimento(alimento: Alimento) {
        viewModelScope.launch {
            try {
                val newId = alimentoRepository.createOrUpdateAlimento(alimento)
            } catch (e: Exception) {
                Log.e("AlimentoViewModel", "Error al crear alimento: ${e.message}")
            }
        }
    }

    fun deleteAlimento(id: String) {
        viewModelScope.launch {
            try {
                alimentoRepository.deleteAlimento(id)
            } catch (e: Exception) {
                _error.value = "Error al eliminar el alimento: ${e.message}"
            }
        }
    }

    // Estas funciones se mantienen pero ahora usan el sistema de filtros
    // fun getAlimentosByCategoria(categoria: String) {
    //     viewModelScope.launch {
    //         alimentoRepository.getAlimentosByCategoria(categoria).collect {
    //             _alimentos.value = it
    //         }
    //     }
    // }

    // fun getAlimentosByDateRange(startDate: Date, endDate: Date) {
    //     viewModelScope.launch {
    //         alimentoRepository.getAlimentosByDateRange(startDate, endDate).collect {
    //             _alimentos.value = it
    //         }
    //     }
    // }

    fun clearError() {
        _error.value = null
    }
}