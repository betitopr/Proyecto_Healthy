package com.example.proyectohealthy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.FavoritoInfo
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.OrderType
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.data.repository.MisAlimentosRepository
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val alimentoRepository: AlimentoRepository,
    private val misAlimentosRepository: MisAlimentosRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    // Estados existentes
    private val _alimentosFavoritos = MutableStateFlow<Map<String, FavoritoInfo>>(emptyMap())
    val alimentosFavoritos: StateFlow<Map<String, FavoritoInfo>> = _alimentosFavoritos.asStateFlow()

    private val _uiState = MutableStateFlow<FavoritosUiState>(FavoritosUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Nuevos estados para filtros
    private val _filtros = MutableStateFlow(AlimentoFiltros())
    val filtros = _filtros.asStateFlow()

    private val _categoriasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val categoriasDisponibles = _categoriasDisponibles.asStateFlow()

    private var allItems = listOf<FavoritoItem>()
    private var itemsSinFiltrar = listOf<FavoritoItem>()

    init {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                perfilRepository.getFavoritosFlow(userId).collect { favoritosMap ->
                    _alimentosFavoritos.value = favoritosMap
                    actualizarListaFavoritos(favoritosMap)
                }
            } catch (e: Exception) {
                _uiState.value = FavoritosUiState.Error("Error al cargar favoritos: ${e.message}")
            }
        }
    }

    private suspend fun actualizarListaFavoritos(favoritosMap: Map<String, FavoritoInfo>) {
        val userId = auth.currentUser?.uid ?: return
        val items = mutableListOf<FavoritoItem>()

        favoritosMap.forEach { (id, info) ->
            when (info.tipo) {
                1 -> {
                    alimentoRepository.getAlimentoById(id)?.let { alimento ->
                        items.add(FavoritoItem.Alimento(alimento))
                    }
                }
                2 -> {
                    misAlimentosRepository.getMiAlimentoById(userId, id)?.let { miAlimento ->
                        items.add(FavoritoItem.MiAlimento(miAlimento))
                    }
                }
            }
        }

        itemsSinFiltrar = items
        _categoriasDisponibles.value = items.map {
            when(it) {
                is FavoritoItem.Alimento -> it.data.categoria
                is FavoritoItem.MiAlimento -> it.data.categoria
            }
        }.distinct().sorted()

        aplicarFiltros(items)
    }

    private fun aplicarFiltros(items: List<FavoritoItem> = itemsSinFiltrar) {
        val filtrosActuales = _filtros.value
        var resultados = items

        // Aplicar filtros de categoría
        if (filtrosActuales.categories.isNotEmpty()) {
            resultados = resultados.filter { item ->
                when (item) {
                    is FavoritoItem.Alimento -> item.data.categoria in filtrosActuales.categories
                    is FavoritoItem.MiAlimento -> item.data.categoria in filtrosActuales.categories
                }
            }
        }

        // Aplicar filtros nutricionales
        resultados = resultados.filter { item ->
            val cumpleFiltros = when (item) {
                is FavoritoItem.Alimento -> {
                    cumpleFiltrosNutricionales(
                        calorias = item.data.calorias.toFloat(),
                        proteinas = item.data.proteinas,
                        carbohidratos = item.data.carbohidratos,
                        grasas = item.data.grasas,
                        filtros = filtrosActuales
                    )
                }
                is FavoritoItem.MiAlimento -> {
                    cumpleFiltrosNutricionales(
                        calorias = item.data.calorias.toFloat(),
                        proteinas = item.data.proteinas,
                        carbohidratos = item.data.carbohidratos,
                        grasas = item.data.grasas,
                        filtros = filtrosActuales
                    )
                }
            }
            cumpleFiltros
        }

        // Aplicar ordenamiento
        resultados = when (filtrosActuales.orderType) {
            is OrderType.Name -> resultados.sortedBy { it.nombre }
            is OrderType.Calories -> resultados.sortedBy {
                when (it) {
                    is FavoritoItem.Alimento -> it.data.calorias
                    is FavoritoItem.MiAlimento -> it.data.calorias
                }
            }
            is OrderType.Proteins -> resultados.sortedBy {
                when (it) {
                    is FavoritoItem.Alimento -> it.data.proteinas
                    is FavoritoItem.MiAlimento -> it.data.proteinas
                }
            }
            is OrderType.Carbs -> resultados.sortedBy {
                when (it) {
                    is FavoritoItem.Alimento -> it.data.carbohidratos
                    is FavoritoItem.MiAlimento -> it.data.carbohidratos
                }
            }
            is OrderType.Fats -> resultados.sortedBy {
                when (it) {
                    is FavoritoItem.Alimento -> it.data.grasas
                    is FavoritoItem.MiAlimento -> it.data.grasas
                }
            }
        }

        if (!filtrosActuales.isAscending) {
            resultados = resultados.reversed()
        }

        allItems = resultados
        _uiState.value = if (resultados.isEmpty()) {
            FavoritosUiState.Empty
        } else {
            FavoritosUiState.Success(resultados)
        }
    }

    private fun cumpleFiltrosNutricionales(
        calorias: Float,
        proteinas: Float,
        carbohidratos: Float,
        grasas: Float,
        filtros: AlimentoFiltros
    ): Boolean {
        return (!filtros.caloriesRange.isEnabled || calorias in filtros.caloriesRange.min..filtros.caloriesRange.max) &&
                (!filtros.proteinsRange.isEnabled || proteinas in filtros.proteinsRange.min..filtros.proteinsRange.max) &&
                (!filtros.carbsRange.isEnabled || carbohidratos in filtros.carbsRange.min..filtros.carbsRange.max) &&
                (!filtros.fatsRange.isEnabled || grasas in filtros.fatsRange.min..filtros.fatsRange.max)
    }

    fun updateFiltros(nuevosFiltros: AlimentoFiltros) {
        viewModelScope.launch {
            _filtros.value = nuevosFiltros
            aplicarFiltros()
        }
    }

    fun searchFavoritos(query: String) {
        if (query.isEmpty()) {
            aplicarFiltros()
            return
        }

        val itemsFiltrados = itemsSinFiltrar.filter { item ->
            when (item) {
                is FavoritoItem.Alimento ->
                    item.data.nombre.contains(query, ignoreCase = true)
                is FavoritoItem.MiAlimento ->
                    item.data.nombre.contains(query, ignoreCase = true)
            }
        }
        aplicarFiltros(itemsFiltrados)
    }

    // Mantener el resto de las funciones existentes
    fun removeFavorito(itemId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                perfilRepository.quitarFavorito(userId, itemId)
            } catch (e: Exception) {
                _error.value = "Error al quitar de favoritos: ${e.message}"
            }
        }
    }

    fun toggleFavorito(itemId: String, tipo: Int) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                perfilRepository.toggleFavorito(userId, itemId, tipo)
            } catch (e: Exception) {
                _uiState.value = FavoritosUiState.Error("Error al actualizar favorito: ${e.message}")
            }
        }
    }

    fun isFavorito(itemId: String): Boolean {
        return _alimentosFavoritos.value.containsKey(itemId)
    }

    // Clases existentes
    sealed class FavoritosUiState {
        object Loading : FavoritosUiState()
        object Empty : FavoritosUiState()
        data class Success(val items: List<FavoritoItem>) : FavoritosUiState()
        data class Error(val message: String) : FavoritosUiState()
    }

    sealed class FavoritoItem {
        abstract val id: String
        abstract val nombre: String

        data class Alimento(val data: com.example.proyectohealthy.data.local.entity.Alimento) : FavoritoItem() {
            override val id: String = data.id
            override val nombre: String = data.nombre
        }

        data class MiAlimento(val data: com.example.proyectohealthy.data.local.entity.MisAlimentos) : FavoritoItem() {
            override val id: String = data.id
            override val nombre: String = data.nombre
        }
    }
}