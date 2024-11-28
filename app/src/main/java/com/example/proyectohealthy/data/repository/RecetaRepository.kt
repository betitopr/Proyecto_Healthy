package com.example.proyectohealthy.data.repository

import android.util.Log
import com.example.proyectohealthy.BuildConfig
import com.example.proyectohealthy.data.local.entity.OrigenReceta
import com.example.proyectohealthy.data.local.entity.RecetaApi
import com.example.proyectohealthy.data.local.entity.RecetaGuardada
import com.example.proyectohealthy.data.local.entity.ValoresNutricionales
import com.example.proyectohealthy.data.remote.RecetaService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecetaRepository @Inject constructor(
    private val api: RecetaService,
    private val database: FirebaseDatabase,
) {
    private val recetasRef = database.getReference("recetas")

    private val _recetaActualizada = MutableStateFlow<String>("")
    val recetaActualizada: StateFlow<String> = _recetaActualizada.asStateFlow()

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

    // Buscar recetas en la API
    suspend fun buscarRecetas(query: String): List<RecetaApi> {
        return try {
            val response = api.buscarRecetas(query)
            if (response.isSuccessful) {
                response.body()?.map { recetaResponse ->
                    RecetaApi(
                        title = recetaResponse.title,
                        ingredients = recetaResponse.ingredients.split("|"),
                        servings = recetaResponse.servings,
                        instructions = recetaResponse.instructions
                    )
                } ?: emptyList()
            } else {
                Log.e("RecetaRepository", "Error ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error API", e)
            emptyList()
        }
    }

    // Procesar receta con IA para traducción y valores nutricionales
    suspend fun procesarRecetaConIA(receta: RecetaApi): RecetaGuardada? {
        try {
            val prompt = """
                Traduce la siguiente receta del inglés al español y calcula sus valores nutricionales aproximados por porción.
                Receta:
                
                Título: ${receta.title}
                Porciones: ${receta.servings}
                Ingredientes:
                ${receta.ingredients.joinToString("\n")}
                
                Instrucciones:
                ${receta.instructions}
                
                Proporciona la respuesta en formato JSON con esta estructura exacta:
                {
                    "nombre": "nombre traducido",
                    "ingredientes": ["ingrediente1", "ingrediente2"],
                    "instrucciones": "instrucciones traducidas",
                    "porciones": "número de porciones",
                    "tiempoPreparacion": "tiempo estimado",
                    "valoresNutricionales": {
                        "porcion": 100,
                        "unidadPorcion": "g",
                        "calorias": 0,
                        "proteinas": 0,
                        "carbohidratos": 0,
                        "grasas": 0,
                        "fibra": 0,
                        "azucares": 0
                    }
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonResponse = response.text?.trim() ?: throw Exception("Respuesta vacía de IA")

            // TODO: Implementar parseador de JSON a RecetaGuardada
            return null // Placeholder
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error procesando receta con IA: ${e.message}")
            return null
        }
    }

    // Generar nueva receta con IA
    suspend fun generarRecetaConIA(descripcion: String, tipoComida: String, restricciones: String): RecetaGuardada? {
        try {
            val prompt = """
                Genera una receta detallada basada en los siguientes parámetros:
                
                Descripción: $descripcion
                Tipo de comida: $tipoComida
                Restricciones: $restricciones
                
                Proporciona la receta en formato JSON con esta estructura exacta:
                {
                    "nombre": "",
                    "ingredientes": [""],
                    "instrucciones": "",
                    "porciones": "",
                    "tiempoPreparacion": "",
                    "valoresNutricionales": {
                        "porcion": 100,
                        "unidadPorcion": "g",
                        "calorias": 0,
                        "proteinas": 0,
                        "carbohidratos": 0,
                        "grasas": 0,
                        "fibra": 0,
                        "azucares": 0
                    }
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonResponse = response.text?.trim() ?: throw Exception("Respuesta vacía de IA")

            // TODO: Implementar parseador de JSON a RecetaGuardada
            return null // Placeholder
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error generando receta con IA: ${e.message}")
            return null
        }
    }

    // Guardar nueva receta
    suspend fun createOrUpdateReceta(receta: RecetaGuardada): String {
        return try {
            val key = if (receta.id.isBlank()) {
                recetasRef.child(receta.idPerfil).push().key ?: throw IllegalStateException("No se pudo generar key")
            } else {
                receta.id
            }

            val updatedReceta = receta.copy(id = key)
            recetasRef.child(receta.idPerfil).child(key).setValue(updatedReceta).await()
            _recetaActualizada.value = key
            key
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error guardando receta: ${e.message}")
            throw e
        }
    }

    suspend fun procesarYGuardarRecetaAPI(receta: RecetaApi, idPerfil: String): String {
        try {
            // Prompt para traducir y obtener valores nutricionales
            val prompt = """
                Traduce esta receta del inglés al español y calcula sus valores nutricionales aproximados.
                Dame solo los datos solicitados en el siguiente formato, sin explicaciones adicionales:

                Título original: ${receta.title}
                Porciones: ${receta.servings}
                Ingredientes originales:
                ${receta.ingredients.joinToString("\n")}
                Instrucciones originales:
                ${receta.instructions}

                Necesito:
                1. NOMBRE:
                2. INGREDIENTES: (uno por línea)
                3. INSTRUCCIONES:
                4. CALORIAS_POR_PORCION:
                5. PROTEINAS_GRAMOS:
                6. CARBOHIDRATOS_GRAMOS:
                7. GRASAS_GRAMOS:
                8. TIEMPO_PREPARACION:
            """.trimIndent()

            // Obtener respuesta de la IA
            val response = generativeModel.generateContent(prompt)
            val respuesta = response.text?.trim() ?: throw Exception("No se pudo traducir la receta")

            // Procesar respuesta línea por línea
            val lineas = respuesta.lines()
            var nombre = ""
            val ingredientes = mutableListOf<String>()
            var instrucciones = ""
            var calorias = 0
            var proteinas = 0f
            var carbohidratos = 0f
            var grasas = 0f
            var tiempoPreparacion = ""

            var seccionActual = ""
            for (linea in lineas) {
                when {
                    linea.startsWith("1. NOMBRE:") -> {
                        seccionActual = "nombre"
                        nombre = linea.substringAfter(":").trim()
                    }
                    linea.startsWith("2. INGREDIENTES:") -> {
                        seccionActual = "ingredientes"
                    }
                    linea.startsWith("3. INSTRUCCIONES:") -> {
                        seccionActual = "instrucciones"
                        instrucciones = linea.substringAfter(":").trim()
                    }
                    linea.startsWith("4. CALORIAS_POR_PORCION:") -> {
                        calorias = linea.substringAfter(":").trim().toIntOrNull() ?: 0
                    }
                    linea.startsWith("5. PROTEINAS_GRAMOS:") -> {
                        proteinas = linea.substringAfter(":").trim().toFloatOrNull() ?: 0f
                    }
                    linea.startsWith("6. CARBOHIDRATOS_GRAMOS:") -> {
                        carbohidratos = linea.substringAfter(":").trim().toFloatOrNull() ?: 0f
                    }
                    linea.startsWith("7. GRASAS_GRAMOS:") -> {
                        grasas = linea.substringAfter(":").trim().toFloatOrNull() ?: 0f
                    }
                    linea.startsWith("8. TIEMPO_PREPARACION:") -> {
                        tiempoPreparacion = linea.substringAfter(":").trim()
                    }
                    else -> {
                        when (seccionActual) {
                            "ingredientes" -> if (linea.isNotBlank()) ingredientes.add(linea.trim())
                            "instrucciones" -> if (linea.isNotBlank()) instrucciones += "\n" + linea.trim()
                        }
                    }
                }
            }

            // Crear y guardar la receta procesada
            val recetaGuardada = RecetaGuardada(
                idPerfil = idPerfil,
                nombre = nombre.ifEmpty { receta.title },
                ingredientes = ingredientes.ifEmpty { receta.ingredients },
                instrucciones = instrucciones.ifEmpty { receta.instructions },
                porciones = receta.servings,
                tiempoPreparacion = tiempoPreparacion.ifEmpty { "No especificado" },
                valoresNutricionales = ValoresNutricionales(
                    calorias = calorias,
                    proteinas = proteinas,
                    carbohidratos = carbohidratos,
                    grasas = grasas
                ),
                origen = OrigenReceta.API,
                fechaCreacion = Date()
            )

            val key = recetasRef.child(idPerfil).push().key ?:
            throw IllegalStateException("Error al generar ID")

            val recetaConId = recetaGuardada.copy(id = key)
            recetasRef.child(idPerfil).child(key).setValue(recetaConId).await()

            return key

        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error procesando receta", e)
            throw e
        }
    }

    // Obtener recetas del usuario
    fun getRecetasFlow(idPerfil: String): Flow<List<RecetaGuardada>> = callbackFlow {
        val listener = recetasRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recetas = snapshot.children.mapNotNull {
                    it.getValue(RecetaGuardada::class.java)?.copy(id = it.key ?: "")
                }.sortedByDescending { it.fechaCreacion }
                trySend(recetas)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecetaRepository", "Error obteniendo recetas: ${error.message}")
                close(error.toException())
            }
        })
        awaitClose { recetasRef.child(idPerfil).removeEventListener(listener) }
    }

    // Buscar en recetas guardadas
    fun searchRecetasFlow(idPerfil: String, query: String): Flow<List<RecetaGuardada>> = callbackFlow {
        val listener = recetasRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recetas = snapshot.children.mapNotNull {
                    it.getValue(RecetaGuardada::class.java)?.copy(id = it.key ?: "")
                }.filter { receta ->
                    receta.nombre.contains(query, ignoreCase = true) ||
                            receta.ingredientes.any { it.contains(query, ignoreCase = true) }
                }.sortedByDescending { it.fechaCreacion }
                trySend(recetas)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecetaRepository", "Error buscando recetas: ${error.message}")
                close(error.toException())
            }
        })
        awaitClose { recetasRef.child(idPerfil).removeEventListener(listener) }
    }

    // Obtener una receta específica
    suspend fun getRecetaById(idPerfil: String, idReceta: String): RecetaGuardada? {
        return try {
            val snapshot = recetasRef.child(idPerfil).child(idReceta).get().await()
            snapshot.getValue(RecetaGuardada::class.java)?.copy(id = snapshot.key ?: "")
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error obteniendo receta: ${e.message}")
            null
        }
    }

    // Obtener receta por flow
    fun getRecetaFlow(idPerfil: String, idReceta: String): Flow<RecetaGuardada?> = callbackFlow {
        val listener = recetasRef.child(idPerfil).child(idReceta)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val receta = snapshot.getValue(RecetaGuardada::class.java)?.copy(id = snapshot.key ?: "")
                    trySend(receta)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RecetaRepository", "Error obteniendo receta: ${error.message}")
                    close(error.toException())
                }
            })
        awaitClose { recetasRef.child(idPerfil).child(idReceta).removeEventListener(listener) }
    }

    // Eliminar receta
    suspend fun deleteReceta(idPerfil: String, idReceta: String) {
        try {
            recetasRef.child(idPerfil).child(idReceta).removeValue().await()
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error eliminando receta: ${e.message}")
            throw e
        }
    }
}