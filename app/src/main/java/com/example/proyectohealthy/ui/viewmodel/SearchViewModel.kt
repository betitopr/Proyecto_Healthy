package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Añadimos un delay corto para evitar demasiadas llamadas
    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        searchJob?.cancel()
        _searchQuery.value = query
    }
}