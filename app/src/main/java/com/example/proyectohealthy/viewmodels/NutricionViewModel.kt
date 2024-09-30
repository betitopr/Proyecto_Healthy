package com.example.proyectohealthy.viewmodels

import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.proyectohealthy.BuildConfig
//import com.example.proyectohealthy.UiState
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.type.generationConfig
//import com.squareup.moshi.Moshi
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class NutricionViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
//
//    private val generativeModel by lazy {
//        GenerativeModel(
//            modelName = "gemini-1.5-flash",
//            apiKey = BuildConfig.API_KEY,  // Cambiado de BuildConfig.apiKey a BuildConfig.API_KEY
//            generationConfig = generationConfig {
//                temperature = 0.7f
//                topK = 40
//                topP = 0.95f
//                maxOutputTokens = 1024
//            }
//        )
//    }
//
//    fun obtenerPlanNutricional(userSelectionsViewModel: UserSelectionsViewModel) {
//        _uiState.value = UiState.Loading
//
//        viewModelScope.launch {
//            try {
//                val prompt = construirPrompt(userSelectionsViewModel)
//                println("Prompt enviado a la API: $prompt")
//                val response = withContext(Dispatchers.IO) {
//                    generativeModel.generateContent(prompt)
//                }
//                response.text?.let { outputContent ->
//                    println("Respuesta de la API: $outputContent")
//                    val planNutricional = parsearRespuesta(outputContent)
//                    _uiState.value = UiState.Success.NutritionPlanGenerated(planNutricional)
//                } ?: run {
//                    _uiState.value = UiState.Error("La respuesta del modelo está vacía")
//                }
//            } catch (e: Exception) {
//                println("Error al obtener plan nutricional: ${e.message}")
//                e.printStackTrace() // Esto imprimirá el stack trace completo
//                _uiState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
//            }
//        }
//    }
//
//    fun generateText(prompt: String) {
//        _uiState.value = UiState.Loading
//
//        viewModelScope.launch {
//            try {
//                val response = withContext(Dispatchers.IO) {
//                    generativeModel.generateContent(prompt)
//                }
//                response.text?.let { outputContent ->
//                    _uiState.value = UiState.Success.TextGenerated(outputContent)
//                } ?: run {
//                    _uiState.value = UiState.Error("La respuesta del modelo está vacía")
//                }
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error(e.localizedMessage ?: "Error desconocido")
//            }
//        }
//    }
//
//    private fun construirPrompt(viewModel: UserSelectionsViewModel): String {
//        return """
//    Eres un dietista y nutricionista profesional. Necesito un plan nutricional personalizado basado en los siguientes datos:
//    • Objetivo: ${viewModel.objetivo}
//    • Edad: ${viewModel.edad} años
//    • Sexo: ${viewModel.genero}
//    • Peso actual: ${viewModel.peso.value} kg
//    • Peso objetivo: ${viewModel.pesoObjetivo.value} kg
//    • Altura: ${viewModel.altura.value} cm
//    • Nivel de actividad física: ${if (viewModel.entrenamientoFuerza == "Sí") "Activo" else "Sedentario"}
//    • Entrenamiento de fuerza: ${viewModel.entrenamientoFuerza}
//
//    Por favor, proporciona la siguiente información en formato JSON:
//    1. Calcula mi IMC y TMB.
//    2. Determina mis requerimientos energéticos diarios para ${viewModel.objetivo}.
//    3. Proporciona un desglose detallado de macronutrientes (proteínas, carbohidratos y grasas vegetales).
//    4. Estima el tiempo necesario para alcanzar mi peso objetivo de forma saludable, indicando una fecha aproximada.
//    5. Sugiere un plan nutricional adecuado para lograr mi objetivo.
//
//    IMPORTANTE: Tu respuesta DEBE ser un objeto JSON válido con la siguiente estructura, sin ningún texto adicional antes o después:
//    {
//        "imc": 0.0,
//        "tmb": 0,
//        "requerimientoEnergetico": 0,
//        "proteinas": 0.0,
//        "carbohidratos": 0.0,
//        "grasas": 0.0,
//        "tiempoEstimado": 0,
//        "planDetallado": ""
//    }
//    """
//    }
//
//    private fun parsearRespuesta(respuesta: String): PlanNutricional {
//        println("Respuesta recibida: $respuesta")
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//        val adapter = moshi.adapter(PlanNutricional::class.java)
//
//        return try {
//            // Eliminar los delimitadores de código Markdown si están presentes
//            val jsonString = respuesta.replace("```json", "").replace("```", "").trim()
//
//            // Intentar parsear el JSON
//            adapter.fromJson(jsonString) ?: throw Exception("Error al parsear la respuesta JSON")
//        } catch (e: Exception) {
//            println("Error al parsear JSON: ${e.message}")
//            e.printStackTrace()
//            PlanNutricional(
//                imc = 0f,
//                tmb = 0,
//                requerimientoEnergetico = 0,
//                proteinas = 0f,
//                carbohidratos = 0f,
//                grasas = 0f,
//                tiempoEstimado = 0,
//                planDetallado = "Error al procesar el plan nutricional: ${e.message}"
//            )
//        }
}
//
//data class PlanNutricional(
//    val imc: Float,
//    val tmb: Int,
//    val requerimientoEnergetico: Int,
//    val proteinas: Float,
//    val carbohidratos: Float,
//    val grasas: Float,
//    val tiempoEstimado: Int,
//    val planDetallado: String
//)
//}
