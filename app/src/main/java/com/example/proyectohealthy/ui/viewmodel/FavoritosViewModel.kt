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

    private val _favoritos = MutableStateFlow<List<Any>>(emptyList())
    val favoritos = _favoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)  // Cambiado a true inicialmente
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _favoritosMap = MutableStateFlow<Map<String, FavoritoInfo>>(emptyMap())

    private val _currentQuery = MutableStateFlow("")

    init {
        cargarFavoritos()
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")
                perfilRepository.getFavoritosFlow(userId).collect { favoritosMap ->
                    _alimentosFavoritos.value = favoritosMap
                    loadFavoritosDetails(favoritosMap)
                }
            } catch (e: Exception) {
                _uiState.value = FavoritosUiState.Error("Error al cargar favoritos: ${e.message}")
            }
        }
    }

    private suspend fun loadFavoritosDetails(favoritosMap: Map<String, FavoritoInfo>) {
        try {
            val favoritosList = mutableListOf<FavoritoItem>()

            favoritosMap.forEach { (itemId, info) ->
                when (info.tipo) {
                    1 -> { // Alimento
                        alimentoRepository.getAlimentoById(itemId)?.let { alimento ->
                            favoritosList.add(FavoritoItem.Alimento(alimento))
                        }
                    }
                    2 -> { // MiAlimento
                        val userId = auth.currentUser?.uid ?: return@forEach
                        misAlimentosRepository.getMiAlimentoById(userId, itemId)?.let { miAlimento ->
                            favoritosList.add(FavoritoItem.MiAlimento(miAlimento))
                        }
                    }
                }
            }

            _uiState.value = if (favoritosList.isEmpty()) {
                FavoritosUiState.Empty
            } else {
                FavoritosUiState.Success(favoritosList)
            }
        } catch (e: Exception) {
            _uiState.value = FavoritosUiState.Error("Error al cargar detalles: ${e.message}")
        }
    }

    fun searchFavoritos(query: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is FavoritosUiState.Success) return@launch

            val filteredItems = if (query.isBlank()) {
                currentState.items
            } else {
                currentState.items.filter { item ->
                    when (item) {
                        is FavoritoItem.Alimento ->
                            item.data.nombre.contains(query, ignoreCase = true) ||
                                    item.data.marca.contains(query, ignoreCase = true)
                        is FavoritoItem.MiAlimento ->
                            item.data.nombre.contains(query, ignoreCase = true) ||
                                    item.data.marca.contains(query, ignoreCase = true)
                    }
                }
            }

            _uiState.value = if (filteredItems.isEmpty()) {
                FavoritosUiState.Empty
            } else {
                FavoritosUiState.Success(filteredItems)
            }
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
                perfilRepository.toggleFavorito(userId, itemId, tipo)
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



    private suspend fun actualizarListaFavoritos(favoritosMap: Map<String, Int>) {
        val favoritosList = mutableListOf<Any>()

        favoritosMap.forEach { (id, tipo) ->
            try {
                when (tipo) {
                    1 -> { // Alimento
                        val alimento = alimentoRepository.getAlimentoById(id)
                        if (alimento != null) {
                            favoritosList.add(alimento)
                        }
                    }
                    2 -> { // MiAlimento
                        auth.currentUser?.uid?.let { userId ->
                            val miAlimento = misAlimentosRepository.getMiAlimentoById(userId, id)
                            if (miAlimento != null) {
                                favoritosList.add(miAlimento)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FavoritosViewModel", "Error cargando favorito $id: ${e.message}")
            }
        }
        _favoritos.value = favoritosList
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