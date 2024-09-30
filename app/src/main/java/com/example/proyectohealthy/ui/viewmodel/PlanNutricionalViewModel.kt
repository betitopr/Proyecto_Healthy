package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.PlanNutricional
import com.example.proyectohealthy.data.repository.PlanNutricionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class PlanNutricionalViewModel(private val repository: PlanNutricionalRepository) : ViewModel() {

    private val _planes = MutableStateFlow<List<PlanNutricional>>(emptyList())
    val planes: StateFlow<List<PlanNutricional>> = _planes

    private val _currentPlan = MutableStateFlow<PlanNutricional?>(null)
    val currentPlan: StateFlow<PlanNutricional?> = _currentPlan

    init {
        getAllPlanes()
    }

    private fun getAllPlanes() {
        viewModelScope.launch {
            repository.getAllPlanes()
                .catch { e ->
                    // Manejar errores aquí
                }
                .collect {
                    _planes.value = it
                }
        }
    }

    fun getPlanById(id: Int) {
        viewModelScope.launch {
            _currentPlan.value = repository.getPlanById(id)
        }
    }

    fun getPlanesByUserId(userId: Int) {
        viewModelScope.launch {
            repository.getPlanesByUserId(userId)
                .collect {
                    _planes.value = it
                }
        }
    }

    fun insertPlan(plan: PlanNutricional) {
        viewModelScope.launch {
            repository.insertPlan(plan)
            getAllPlanes()
        }
    }

    fun updatePlan(plan: PlanNutricional) {
        viewModelScope.launch {
            repository.updatePlan(plan)
            getAllPlanes()
        }
    }

    fun deletePlan(plan: PlanNutricional) {
        viewModelScope.launch {
            repository.deletePlan(plan)
            getAllPlanes()
        }
    }

    fun getPlanesActivos(fecha: Date) {
        viewModelScope.launch {
            repository.getPlanesActivos(fecha)
                .collect {
                    _planes.value = it
                }
        }
    }

    fun getPlanesByMaxCalorias(maxCalorias: Int) {
        viewModelScope.launch {
            repository.getPlanesByMaxCalorias(maxCalorias)
                .collect {
                    _planes.value = it
                }
        }
    }
}