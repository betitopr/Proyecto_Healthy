package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectohealthy.BuildConfig
import com.example.proyectohealthy.UiState
import com.example.proyectohealthy.data.local.entity.Perfil
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutricionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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

    fun obtenerPlanNutricional(perfil: Perfil) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val prompt = construirPrompt(perfil)
                println("Prompt enviado a la API: $prompt")
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }
                response.text?.let { outputContent ->
                    println("Respuesta de la API: $outputContent")
                    val planNutricional = parsearRespuesta(outputContent)
                    _uiState.value = UiState.Success.NutritionPlanGenerated(planNutricional)
                } ?: run {
                    _uiState.value = UiState.Error("La respuesta del modelo está vacía")
                }
            } catch (e: Exception) {
                println("Error al obtener plan nutricional: ${e.message}")
                e.printStackTrace()
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun generateText(prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success.TextGenerated(outputContent)
                } ?: run {
                    _uiState.value = UiState.Error("La respuesta del modelo está vacía")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    private fun construirPrompt(perfil: Perfil): String {
        return """
    Eres un dietista y nutricionista profesional. Necesito un plan nutricional personalizado basado en los siguientes datos:
    • Objetivo: ${perfil.Objetivo}
    • Edad: ${perfil.Edad} años
    • Sexo: ${perfil.Genero}
    • Peso actual: ${perfil.Peso_Actual} kg
    • Peso objetivo: ${perfil.Peso_Objetivo} kg
    • Altura: ${perfil.Altura} cm
    • Nivel de actividad física: ${perfil.Nivel_Actividad}
    • Entrenamiento de fuerza: ${perfil.Entrenamiento_Fuerza}

    Por favor, proporciona la siguiente información en formato JSON:
    1. Calcula mi IMC y TMB.
    2. Determina mis requerimientos energéticos diarios para ${perfil.Objetivo}.
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
        println("Respuesta recibida: $respuesta")
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(PlanNutricional::class.java)

        return try {
            val jsonString = respuesta.replace("```json", "").replace("```", "").trim()
            adapter.fromJson(jsonString) ?: throw Exception("Error al parsear la respuesta JSON")
        } catch (e: Exception) {
            println("Error al parsear JSON: ${e.message}")
            e.printStackTrace()
            PlanNutricional(
                imc = 0f,
                tmb = 0,
                requerimientoEnergetico = 0,
                proteinas = 0f,
                carbohidratos = 0f,
                grasas = 0f,
                tiempoEstimado = 0,
                planDetallado = "Error al procesar el plan nutricional: ${e.message}"
            )
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