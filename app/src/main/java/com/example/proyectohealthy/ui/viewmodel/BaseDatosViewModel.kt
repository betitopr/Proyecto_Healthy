package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.repository.AlimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseDatosViewModel @Inject constructor(
    private val alimentoRepository: AlimentoRepository
) : ViewModel() {
    private val _alimentos = MutableStateFlow<List<Alimento>>(emptyList())
    val alimentos = _alimentos.asStateFlow()

    private val _selectedCategoria = MutableStateFlow<String?>(null)
    val selectedCategoria = _selectedCategoria.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadAlimentos()
    }

    private fun loadAlimentos() {
        viewModelScope.launch {
            alimentoRepository.getAllAlimentosFlow().collect { alimentos ->
                _alimentos.value = filtrarYOrdenarAlimentos(alimentos)
            }
        }
    }

    fun searchAlimentos(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadAlimentos()
            } else {
                alimentoRepository.searchAlimentosByNombre(query).collect { alimentos ->
                    _alimentos.value = filtrarYOrdenarAlimentos(alimentos)
                }
            }
        }
    }

    fun setSelectedCategoria(categoria: String?) {
        _selectedCategoria.value = categoria
        loadAlimentos()
    }

    private fun filtrarYOrdenarAlimentos(alimentos: List<Alimento>): List<Alimento> {
        return alimentos
            .filter { alimento ->
                (_selectedCategoria.value == null || alimento.categoria == _selectedCategoria.value) &&
                        (_searchQuery.value.isEmpty() || alimento.nombre.contains(_searchQuery.value, ignoreCase = true))
            }
            .sortedBy { it.nombre }
    }
}