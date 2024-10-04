package com.example.proyectohealthy

import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel


/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {
    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState

    /**
     * Success states
     */
    sealed interface Success : UiState {
        /**
         * Text has been generated
         */
        data class TextGenerated(val outputText: String) : Success

        /**
         * Nutrition plan has been generated
         */
        data class NutritionPlanGenerated(val data: NutricionViewModel.PlanNutricional) : Success
    }

    /**
     * There was an error
     */
    data class Error(val errorMessage: String) : UiState
}