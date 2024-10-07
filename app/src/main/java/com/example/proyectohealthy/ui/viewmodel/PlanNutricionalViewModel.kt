package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.data.local.entity.PlanNutricional
import com.example.proyectohealthy.data.repository.PlanNutricionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlanNutricionalViewModel @Inject constructor(
    private val planNutricionalRepository: PlanNutricionalRepository
) : ViewModel() {
    private val _planes = MutableStateFlow<List<PlanNutricional>>(emptyList())
    val planes: StateFlow<List<PlanNutricional>> = _planes.asStateFlow()

    private val _currentPlan = MutableStateFlow<PlanNutricional?>(null)
    val currentPlan: StateFlow<PlanNutricional?> = _currentPlan.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createOrUpdatePlanNutricional(plan: PlanNutricional) {
        viewModelScope.launch {
            try {
                planNutricionalRepository.createOrUpdatePlanNutricional(plan)
            } catch (e: Exception) {
                _error.value = "Error al crear o actualizar el plan nutricional: ${e.message}"
            }
        }
    }

    fun getPlanNutricional(id: String) {
        viewModelScope.launch {
            planNutricionalRepository.getPlanNutricionalFlow(id).collect {
                _currentPlan.value = it
            }
        }
    }

    fun getPlanesNutricionalesForPerfil(idPerfil: String) {
        viewModelScope.launch {
            planNutricionalRepository.getPlanesNutricionalesForPerfil(idPerfil).collect {
                _planes.value = it
            }
        }
    }

    fun deletePlanNutricional(id: String) {
        viewModelScope.launch {
            try {
                planNutricionalRepository.deletePlanNutricional(id)
            } catch (e: Exception) {
                _error.value = "Error al eliminar el plan nutricional: ${e.message}"
            }
        }
    }

    fun getPlanesNutricionalesActivos() {
        viewModelScope.launch {
            planNutricionalRepository.getPlanesNutricionalesActivos().collect {
                _planes.value = it
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}