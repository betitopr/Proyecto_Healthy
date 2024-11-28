package com.example.proyectohealthy.util

import com.example.proyectohealthy.data.local.entity.RecetaGuardada
import com.example.proyectohealthy.data.local.entity.ValoresNutricionales
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.util.Date

object RecetaJsonParser {
    private val gson = Gson()

    fun parseRecetaResponse(jsonString: String): RecetaGuardada? {
        return try {
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject

            val nombre = jsonObject.get("nombre").asString
            val ingredientes = gson.fromJson(jsonObject.get("ingredientes"), Array<String>::class.java).toList()
            val instrucciones = jsonObject.get("instrucciones").asString
            val porciones = jsonObject.get("porciones").asString
            val tiempoPreparacion = jsonObject.get("tiempoPreparacion").asString

            val valoresNutricionalesJson = jsonObject.getAsJsonObject("valoresNutricionales")
            val valoresNutricionales = ValoresNutricionales(
                porcion = valoresNutricionalesJson.get("porcion").asFloat,
                unidadPorcion = valoresNutricionalesJson.get("unidadPorcion").asString,
                calorias = valoresNutricionalesJson.get("calorias").asInt,
                proteinas = valoresNutricionalesJson.get("proteinas").asFloat,
                carbohidratos = valoresNutricionalesJson.get("carbohidratos").asFloat,
                grasas = valoresNutricionalesJson.get("grasas").asFloat,
                fibra = valoresNutricionalesJson.get("fibra").asFloat,
                azucares = valoresNutricionalesJson.get("azucares").asFloat
            )

            RecetaGuardada(
                nombre = nombre,
                ingredientes = ingredientes,
                instrucciones = instrucciones,
                porciones = porciones,
                tiempoPreparacion = tiempoPreparacion,
                valoresNutricionales = valoresNutricionales,
                fechaCreacion = Date()
            )
        } catch (e: Exception) {
            null
        }
    }
}