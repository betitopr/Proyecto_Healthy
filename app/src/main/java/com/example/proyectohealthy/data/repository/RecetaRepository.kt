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
                response.body()?.map { it.toRecetaApi() } ?: emptyList()
            } else {
                Log.e("RecetaRepository", "Error API ${response.code()}: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error buscando recetas: ${e.message}")
            emptyList()
        }
    }

    // Procesar receta con IA para traducción y valores nutricionales
    suspend fun procesarRecetaConIA(receta: RecetaApi): RecetaGuardada? {
        try {
            val prompt = """
                Corrige como debe ser la receta y TRADUCELA del ingles al ESPAÑOL (ligeramente esta mal escrita pero suficiente para que la corrijas) y traduce al español la siguiente receta y proporciona valores nutricionales aproximados.
                
                Título original: ${receta.title}
                Porciones: ${receta.servings}
                
                Ingredientes:
                ${receta.ingredients.joinToString("\n")}
                
                Instrucciones:
                ${receta.instructions}
                DAME VALORES FIJOS, NO QUIOER RANGOS, EN DONDE TE PIDEN NUMEROS DAME UN NUMERO NO QUIOER RANGOS NI ALGO COMO ESO 350/400.... MEJOR DAME 375.
                Por favor, proporciona en el formato:
                1. Título en español:
                2. Ingredientes traducidos (uno por línea):
                3. Instrucciones en español:
                4. Peso aproximado por plato estandar o tazon(depedera si es sopa,segundo,postre) en gramos(no des 2 valores, solo un valor o un valor promedio pero no me des 2 valores):
                5. Valores nutricionales por plato o tipo de plato o envase aproximado(lo mas realista posible)(no des 2 valores, solo un valor o un valor promedio pero no me des 2 valores de ninguno):
                   - Calorías:
                   - Proteínas (g):
                   - Carbohidratos (g):
                   - Grasas (g):
                   - Fibra (g):
                   - Azúcares (g):
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val respuesta = response.text?.trim() ?: throw Exception("Respuesta vacía de IA")

            Log.d("RecetaRepository", "Respuesta de IA: $respuesta")

            // Variables para almacenar los datos
            var nombre = ""
            val ingredientes = mutableListOf<String>()
            var instrucciones = ""
            var porcion = 0f
            var calorias = 0
            var proteinas = 0f
            var carbohidratos = 0f
            var grasas = 0f
            var fibra = 0f
            var azucares = 0f

            // Parsear la respuesta línea por línea
            val lineas = respuesta.lines()
            var seccionActual = ""

            for (linea in lineas) {
                val lineaTrimmed = linea.trim()
                when {
                    lineaTrimmed.startsWith("1.") && lineaTrimmed.contains("español:") -> {
                        nombre = lineaTrimmed.substringAfter(":").trim()
                        seccionActual = "nombre"
                    }
                    lineaTrimmed.startsWith("2.") && lineaTrimmed.contains("Ingredientes") -> {
                        seccionActual = "ingredientes"
                    }
                    lineaTrimmed.startsWith("3.") && lineaTrimmed.contains("Instrucciones") -> {
                        seccionActual = "instrucciones"
                        instrucciones = lineaTrimmed.substringAfter(":").trim()
                    }
                    lineaTrimmed.startsWith("4.") && lineaTrimmed.contains("Peso") -> {
                        val match = "\\d+".toRegex().find(lineaTrimmed)
                        porcion = match?.value?.toFloatOrNull() ?: 100f
                        seccionActual = "peso"
                    }
                    lineaTrimmed.contains("Calorías:") -> {
                        val match = "\\d+".toRegex().find(lineaTrimmed)
                        calorias = match?.value?.toIntOrNull() ?: 0
                    }
                    lineaTrimmed.contains("Proteínas") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        proteinas = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Carbohidratos") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        carbohidratos = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Grasas") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        grasas = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Fibra") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        fibra = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Azúcares") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        azucares = match?.value?.toFloatOrNull() ?: 0f
                    }
                    else -> {
                        when (seccionActual) {
                            "ingredientes" -> {
                                if (lineaTrimmed.startsWith("-") || lineaTrimmed.startsWith("*")) {
                                    ingredientes.add(lineaTrimmed.substringAfter("-").substringAfter("*").trim())
                                }
                            }
                            "instrucciones" -> {
                                if (lineaTrimmed.isNotBlank() && !lineaTrimmed.startsWith("4.")) {
                                    instrucciones += "\n$lineaTrimmed"
                                }
                            }
                        }
                    }
                }
            }

            Log.d("RecetaRepository", "Nombre traducido: $nombre")
            Log.d("RecetaRepository", "Ingredientes traducidos: ${ingredientes.joinToString(", ")}")
            Log.d("RecetaRepository", "Instrucciones traducidas: $instrucciones")

            // Verificar que tenemos los datos mínimos necesarios
            if (nombre.isBlank()) {
                Log.e("RecetaRepository", "Nombre está vacío")
                throw Exception("Nombre de receta vacío")
            }

            if (ingredientes.isEmpty()) {
                Log.e("RecetaRepository", "No hay ingredientes")
                throw Exception("Lista de ingredientes vacía")
            }

            if (instrucciones.isBlank()) {
                Log.e("RecetaRepository", "Instrucciones están vacías")
                throw Exception("Instrucciones vacías")
            }

            return RecetaGuardada(
                nombre = nombre,
                ingredientes = ingredientes,
                instrucciones = instrucciones,
                porciones = receta.servings,
                tiempoPreparacion = "No especificado",
                valoresNutricionales = ValoresNutricionales(
                    porcion = porcion,
                    unidadPorcion = "g",
                    calorias = calorias,
                    proteinas = proteinas,
                    carbohidratos = carbohidratos,
                    grasas = grasas,
                    fibra = fibra,
                    azucares = azucares
                ),
                origen = OrigenReceta.API,
                fechaCreacion = Date()
            )
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error procesando receta con IA: ${e.message}")
            return null
        }
    }


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

    // Generar nueva receta con IA
    suspend fun generarRecetaConIA(descripcion: String): RecetaGuardada? {
        try {
            val prompt = """
            Genera una receta completa en español basada en esta descripción: $descripcion

            Proporciona la receta en el siguiente formato:
            1. NOMBRE DE LA RECETA:
            2. PORCIONES:
            3. TIEMPO DE PREPARACIÓN:
            4. INGREDIENTES (con cantidades específicas):
            5. INSTRUCCIONES (paso a paso):
            6. VALORES NUTRICIONALES POR PORCIÓN:
               - Peso por porción (g):
               - Calorías:
               - Proteínas (g):
               - Carbohidratos (g):
               - Grasas (g):
               - Fibra (g):
               - Azúcares (g):

            IMPORTANTE: 
            - Da cantidades exactas en los ingredientes
            - Instrucciones detalladas paso a paso
            - Valores nutricionales realistas y aproximados
        """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val respuesta = response.text?.trim() ?: throw Exception("Respuesta vacía de IA")

            Log.d("RecetaRepository", "Respuesta IA generación: $respuesta")

            // Variables para almacenar datos
            var nombre = ""
            var porciones = ""
            var tiempoPreparacion = ""
            val ingredientes = mutableListOf<String>()
            var instrucciones = ""
            var porcion = 0f
            var calorias = 0
            var proteinas = 0f
            var carbohidratos = 0f
            var grasas = 0f
            var fibra = 0f
            var azucares = 0f

            // Parsear la respuesta línea por línea
            val lineas = respuesta.lines()
            var seccionActual = ""

            for (linea in lineas) {
                val lineaTrimmed = linea.trim()
                when {
                    lineaTrimmed.startsWith("1.") && lineaTrimmed.contains("NOMBRE") -> {
                        nombre = lineaTrimmed.substringAfter(":").trim()
                        Log.d("RecetaRepository", "Nombre encontrado: $nombre")
                    }
                    lineaTrimmed.startsWith("2.") && lineaTrimmed.contains("PORCIONES") -> {
                        porciones = lineaTrimmed.substringAfter(":").trim()
                        Log.d("RecetaRepository", "Porciones encontradas: $porciones")
                    }
                    lineaTrimmed.startsWith("3.") && lineaTrimmed.contains("TIEMPO") -> {
                        tiempoPreparacion = lineaTrimmed.substringAfter(":").trim()
                    }
                    lineaTrimmed.startsWith("4.") && lineaTrimmed.contains("INGREDIENTES") -> {
                        seccionActual = "ingredientes"
                    }
                    lineaTrimmed.startsWith("5.") && lineaTrimmed.contains("INSTRUCCIONES") -> {
                        seccionActual = "instrucciones"
                    }
                    lineaTrimmed.startsWith("6.") && lineaTrimmed.contains("VALORES") -> {
                        seccionActual = "nutricional"
                    }
                    lineaTrimmed.contains("Peso por porción") -> {
                        val match = "\\d+".toRegex().find(lineaTrimmed)
                        porcion = match?.value?.toFloatOrNull() ?: 100f
                    }
                    lineaTrimmed.contains("Calorías") -> {
                        val match = "\\d+".toRegex().find(lineaTrimmed)
                        calorias = match?.value?.toIntOrNull() ?: 0
                    }
                    lineaTrimmed.contains("Proteínas") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        proteinas = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Carbohidratos") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        carbohidratos = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Grasas") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        grasas = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Fibra") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        fibra = match?.value?.toFloatOrNull() ?: 0f
                    }
                    lineaTrimmed.contains("Azúcares") -> {
                        val match = "\\d+(\\.\\d+)?".toRegex().find(lineaTrimmed)
                        azucares = match?.value?.toFloatOrNull() ?: 0f
                    }
                    else -> {
                        when (seccionActual) {
                            "ingredientes" -> {
                                if (lineaTrimmed.isNotBlank() && !lineaTrimmed.startsWith("5.")) {
                                    if (lineaTrimmed.startsWith("-") || lineaTrimmed.startsWith("•")) {
                                        ingredientes.add(lineaTrimmed.substringAfter("-").substringAfter("•").trim())
                                    } else if (lineaTrimmed.length > 5) { // Para asegurarnos que es un ingrediente
                                        ingredientes.add(lineaTrimmed)
                                    }
                                }
                            }
                            "instrucciones" -> {
                                if (lineaTrimmed.isNotBlank() && !lineaTrimmed.startsWith("6.")) {
                                    if (instrucciones.isBlank()) {
                                        instrucciones = lineaTrimmed
                                    } else {
                                        instrucciones += "\n$lineaTrimmed"
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Log.d("RecetaRepository", """
            Datos parseados:
            Nombre: $nombre
            Ingredientes: ${ingredientes.size} encontrados
            Instrucciones: ${instrucciones.take(50)}...
            Valores nutricionales: $calorias kcal
        """.trimIndent())

            // Verificar datos mínimos
            if (nombre.isBlank()) {
                Log.e("RecetaRepository", "Nombre está vacío en generación")
                throw Exception("No se pudo generar un nombre para la receta")
            }
            if (ingredientes.isEmpty()) {
                Log.e("RecetaRepository", "No hay ingredientes en generación")
                throw Exception("No se generaron ingredientes")
            }
            if (instrucciones.isBlank()) {
                Log.e("RecetaRepository", "Instrucciones vacías en generación")
                throw Exception("No se generaron instrucciones")
            }

            return RecetaGuardada(
                nombre = nombre,
                ingredientes = ingredientes,
                instrucciones = instrucciones,
                porciones = porciones.ifBlank { "1 porción" },
                tiempoPreparacion = tiempoPreparacion.ifBlank { "No especificado" },
                valoresNutricionales = ValoresNutricionales(
                    porcion = porcion,
                    unidadPorcion = "g",
                    calorias = calorias,
                    proteinas = proteinas,
                    carbohidratos = carbohidratos,
                    grasas = grasas,
                    fibra = fibra,
                    azucares = azucares
                ),
                origen = OrigenReceta.IA,
                fechaCreacion = Date()
            )
        } catch (e: Exception) {
            Log.e("RecetaRepository", "Error generando receta: ${e.message}")
            return null
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