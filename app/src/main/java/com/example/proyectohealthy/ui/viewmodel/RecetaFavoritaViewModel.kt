package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.RecetaFavorita
import com.example.proyectohealthy.data.repository.RecetaFavoritaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RecetaFavoritaViewModel(private val repository: RecetaFavoritaRepository) : ViewModel() {

    private val _recetasFavoritas = MutableStateFlow<List<RecetaFavorita>>(emptyList())
    val recetasFavoritas: StateFlow<List<RecetaFavorita>> = _recetasFavoritas

    private val _currentReceta = MutableStateFlow<RecetaFavorita?>(null)
    val currentReceta: StateFlow<RecetaFavorita?> = _currentReceta

    init {
        getAllRecetasFavoritas()
    }

    private fun getAllRecetasFavoritas() {
        viewModelScope.launch {
            repository.getAllRecetasFavoritas()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _recetasFavoritas.value = it
                }
        }
    }

    fun getRecetaFavoritaById(id: Int) {
        viewModelScope.launch {
            _currentReceta.value = repository.getRecetaFavoritaById(id)
        }
    }

    fun getRecetasFavoritasByUserId(userId: Int) {
        viewModelScope.launch {
            repository.getRecetasFavoritasByUserId(userId)
                .collect {
                    _recetasFavoritas.value = it
                }
        }
    }

    fun insertRecetaFavorita(recetaFavorita: RecetaFavorita) {
        viewModelScope.launch {
            repository.insertRecetaFavorita(recetaFavorita)
            getAllRecetasFavoritas()
        }
    }

    fun updateRecetaFavorita(recetaFavorita: RecetaFavorita) {
        viewModelScope.launch {
            repository.updateRecetaFavorita(recetaFavorita)
            getAllRecetasFavoritas()
        }
    }

    fun deleteRecetaFavorita(recetaFavorita: RecetaFavorita) {
        viewModelScope.launch {
            repository.deleteRecetaFavorita(recetaFavorita)
            getAllRecetasFavoritas()
        }
    }

    fun searchRecetasFavoritasByNombre(nombre: String) {
        viewModelScope.launch {
            repository.searchRecetasFavoritasByNombre(nombre)
                .collect {
                    _recetasFavoritas.value = it
                }
        }
    }
}