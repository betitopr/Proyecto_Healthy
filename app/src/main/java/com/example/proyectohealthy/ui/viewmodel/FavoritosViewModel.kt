package com.example.proyectohealthy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.FavoritoInfo
import com.example.proyectohealthy.data.local.entity.MisAlimentos
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
    private val _alimentosFavoritos = MutableStateFlow<Map<String, FavoritoInfo>>(emptyMap())
    val alimentosFavoritos: StateFlow<Map<String, FavoritoInfo>> = _alimentosFavoritos.asStateFlow()

    private val _uiState = MutableStateFlow<FavoritosUiState>(FavoritosUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)  // Cambiado a true inicialmente
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var allItems = listOf<FavoritoItem>()

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

    private fun cargarFavoritos() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                perfilRepository.getFavoritosFlow(userId).collect { favoritosMap ->
                    _uiState.value = FavoritosUiState.Loading
                    val items = mutableListOf<FavoritoItem>()

                    // Cargar todos los items favoritos
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

                    allItems = items
                    _uiState.value = if (items.isEmpty()) {
                        FavoritosUiState.Empty
                    } else {
                        FavoritosUiState.Success(items)
                    }
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

        allItems = items
        _uiState.value = if (items.isEmpty()) {
            FavoritosUiState.Empty
        } else {
            FavoritosUiState.Success(items)
        }
    }

    fun searchFavoritos(query: String) {
        if (query.isEmpty()) {
            // Si no hay búsqueda, mostrar todos los favoritos
            _uiState.value = if (allItems.isEmpty()) {
                FavoritosUiState.Empty
            } else {
                FavoritosUiState.Success(allItems)
            }
            return
        }

        // Filtrar los items según la búsqueda
        val filteredItems = allItems.filter { item ->
            when (item) {
                is FavoritoItem.Alimento ->
                    item.data.nombre.contains(query, ignoreCase = true)
                is FavoritoItem.MiAlimento ->
                    item.data.nombre.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = if (filteredItems.isEmpty()) {
            if (allItems.isEmpty()) FavoritosUiState.Empty else FavoritosUiState.Success(emptyList())
        } else {
            FavoritosUiState.Success(filteredItems)
        }
    }


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

                // Actualizar Firebase primero
                perfilRepository.toggleFavorito(userId, itemId, tipo)

                // El estado se actualizará automáticamente a través del Flow en init
            } catch (e: Exception) {
                _uiState.value = FavoritosUiState.Error("Error al actualizar favorito: ${e.message}")
            }
        }
    }

    fun isFavorito(itemId: String): Boolean {
        return _alimentosFavoritos.value.containsKey(itemId)
    }

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



    // Función modificada para manejar adecuadamente el estado de los favoritos



    fun deleteMiAlimento(alimentoId: String, favoritosViewModel: FavoritosViewModel) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            try {
                // Paso 1: Verificar si el alimento está en favoritos
                val esFavorito = favoritosViewModel.alimentosFavoritos.value.containsKey(alimentoId)

                // Paso 2: Si está en favoritos, eliminarlo de la lista de favoritos
                if (esFavorito) {
                    favoritosViewModel.toggleFavorito(alimentoId, 2) // 2 representa que es un `MiAlimento`
                }

                // Paso 3: Eliminar el alimento del repositorio de alimentos
                misAlimentosRepository.deleteMiAlimento(userId, alimentoId)

            } catch (e: Exception) {
                // Manejar error
                _error.value = "Error al eliminar mi alimento: ${e.message}"
            }
        }
    }
}