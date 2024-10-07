package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.BuildConfig
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NutricionViewModel @Inject constructor(
    private val perfilRepository: PerfilRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow<NutricionUiState>(NutricionUiState.Initial)
    val uiState: StateFlow<NutricionUiState> = _uiState.asStateFlow()

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )
    }

    fun obtenerPlanNutricional() {
        viewModelScope.launch {
            _uiState.value = NutricionUiState.Loading
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
                val perfil = perfilRepository.getPerfil(userId) ?: throw Exception("Perfil no encontrado")
                val prompt = construirPrompt(perfil)
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }
                response.text?.let { outputContent ->
                    val planNutricional = parsearRespuesta(outputContent)
                    _uiState.value = NutricionUiState.Success(planNutricional)
                } ?: throw Exception("La respuesta del modelo está vacía")
            } catch (e: Exception) {
                _uiState.value = NutricionUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun construirPrompt(perfil: Perfil): String {
        return """
        Eres un dietista y nutricionista profesional. Necesito un plan nutricional personalizado basado en los siguientes datos:
        • Objetivo: ${perfil.objetivo}
        • Edad: ${perfil.edad} años
        • Sexo: ${perfil.genero}
        • Peso actual: ${perfil.pesoActual} kg
        • Peso objetivo: ${perfil.pesoObjetivo} kg
        • Altura: ${perfil.altura} cm
        • Nivel de actividad física: ${perfil.nivelActividad}
        • Entrenamiento de fuerza: ${perfil.entrenamientoFuerza}

        Por favor, proporciona la siguiente información en formato JSON:
        1. Calcula mi IMC y TMB.
        2. Determina mis requerimientos energéticos diarios para ${perfil.objetivo}.
        3. Proporciona un desglose detallado de macronutrientes (proteínas, carbohidratos y grasas vegetales).
        4. Estima el tiempo necesario para alcanzar mi peso objetivo de forma saludable, indicando una fecha aproximada.
        5. Sugiere un plan nutricional adecuado para lograr mi objetivo.

        IMPORTANTE: Tu respuesta DEBE ser un objeto JSON válido con la siguiente estructura, sin ningún texto adicional antes o después:
        {
            "imc": 0.0,
            "tmb": 0,
            "requerimientoEnergetico": 0,
            "proteinas": 0.0,
            "carbohidratos": 0.0,
            "grasas": 0.0,
            "tiempoEstimado": 0,
            "planDetallado": ""
        }
        """
    }

    private fun parsearRespuesta(respuesta: String): PlanNutricional {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(PlanNutricional::class.java)

        return try {
            val jsonString = respuesta.replace("```json", "").replace("```", "").trim()
            adapter.fromJson(jsonString) ?: throw Exception("Error al parsear la respuesta JSON")
        } catch (e: Exception) {
            throw Exception("Error al procesar el plan nutricional: ${e.message}")
        }
    }

    data class PlanNutricional(
        val imc: Float,
        val tmb: Int,
        val requerimientoEnergetico: Int,
        val proteinas: Float,
        val carbohidratos: Float,
        val grasas: Float,
        val tiempoEstimado: Int,
        val planDetallado: String
    )
}

sealed class NutricionUiState {
    object Initial : NutricionUiState()
    object Loading : NutricionUiState()
    data class Success(val planNutricional: NutricionViewModel.PlanNutricional) : NutricionUiState()
    data class Error(val message: String) : NutricionUiState()
}