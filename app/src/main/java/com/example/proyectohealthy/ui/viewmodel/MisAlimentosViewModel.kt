package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.OrderType
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisAlimentosViewModel @Inject constructor(
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _misAlimentos = MutableStateFlow<List<MisAlimentos>>(emptyList())
    val misAlimentos: StateFlow<List<MisAlimentos>> = _misAlimentos.asStateFlow()

    private val _currentMiAlimento = MutableStateFlow<MisAlimentos?>(null)
    val currentMiAlimento: StateFlow<MisAlimentos?> = _currentMiAlimento.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentQuery = MutableStateFlow("")

    // Nuevos estados para filtros
    private val _filtros = MutableStateFlow(AlimentoFiltros())
    val filtros = _filtros.asStateFlow()

    private val _categoriasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val categoriasDisponibles = _categoriasDisponibles.asStateFlow()

    private var alimentosSinFiltrar = listOf<MisAlimentos>()

    init {
        loadMisAlimentos()
    }

    private fun loadMisAlimentos() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                misAlimentosRepository.getMisAlimentosFlow(idPerfil).collect { alimentos ->
                    alimentosSinFiltrar = alimentos
                    _categoriasDisponibles.value = alimentos.map { it.categoria }.distinct().sorted()
                    aplicarFiltros()
                }
            } ?: run {
                _error.value = "Usuario no autenticado"
            }
        }
    }

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

        _misAlimentos.value = resultados
    }

    private fun List<MisAlimentos>.aplicarFiltrosCategoria(filtros: AlimentoFiltros): List<MisAlimentos> {
        return if (filtros.categories.isEmpty()) this
        else filter { it.categoria in filtros.categories }
    }

    private fun List<MisAlimentos>.aplicarFiltrosNutrientes(filtros: AlimentoFiltros): List<MisAlimentos> {
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

    private fun List<MisAlimentos>.aplicarOrdenamiento(filtros: AlimentoFiltros): List<MisAlimentos> {
        val comparator = when (filtros.orderType) {
            is OrderType.Name -> compareBy<MisAlimentos> { it.nombre }
            is OrderType.Calories -> compareBy { it.calorias }
            is OrderType.Proteins -> compareBy { it.proteinas }
            is OrderType.Carbs -> compareBy { it.carbohidratos }
            is OrderType.Fats -> compareBy { it.grasas }
        }

        return if (filtros.isAscending) {
            sortedWith(comparator)
        } else {
            sortedWith(comparator.reversed())
        }
    }

    fun updateFiltros(nuevosFiltros: AlimentoFiltros) {
        viewModelScope.launch {
            _filtros.value = nuevosFiltros
            aplicarFiltros()
        }
    }

    fun searchMisAlimentosByNombre(query: String) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { idPerfil ->
                _currentQuery.value = query
                aplicarFiltros()
            }
        }
    }

    // Mantener las funciones existentes
    fun createOrUpdateMiAlimento(miAlimento: MisAlimentos) {
        viewModelScope.launch {
            try {
                val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                val updatedMiAlimento = miAlimento.copy(idPerfil = idPerfil)
                misAlimentosRepository.createOrUpdateMiAlimento(updatedMiAlimento)
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar mi alimento: ${e.message}"
            }
        }
    }

    suspend fun getMiAlimentoById(id: String): MisAlimentos? {
        val idPerfil = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
        return misAlimentosRepository.getMiAlimentoById(idPerfil, id)
    }

    fun deleteMiAlimento(alimentoId: String, favoritosViewModel: FavoritosViewModel) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                favoritosViewModel.removeFavorito(alimentoId)
                misAlimentosRepository.deleteMiAlimento(userId, alimentoId)
            } catch (e: Exception) {
                _error.value = "Error al eliminar el alimento: ${e.message}"
            }
        }
    }

    fun updateMiAlimento(miAlimento: MisAlimentos) {
        viewModelScope.launch {
            try {
                misAlimentosRepository.createOrUpdateMiAlimento(miAlimento)
            } catch (e: Exception) {
                _error.value = "Error al actualizar el alimento: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}