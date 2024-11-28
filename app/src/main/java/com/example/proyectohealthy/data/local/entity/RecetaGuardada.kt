package com.example.proyectohealthy.data.local.entity

import java.util.Date

data class RecetaGuardada(
    val id: String = "",
    val idPerfil: String = "",
    val nombre: String = "",
    val ingredientes: List<String> = emptyList(),
    val instrucciones: String = "",
    val porciones: String = "",
    val tiempoPreparacion: String = "",
    val valoresNutricionales: ValoresNutricionales = ValoresNutricionales(),
    val origen: OrigenReceta = OrigenReceta.API,
    val fechaCreacion: Date = Date(),
    val imagenUrl: String? = null
)

enum class OrigenReceta {
    API,
    IA
}

// ValoresNutricionales.kt
data class ValoresNutricionales(
    val porcion: Float = 0f,
    val unidadPorcion: String = "g",
    val calorias: Int = 0,
    val proteinas: Float = 0f,
    val carbohidratos: Float = 0f,
    val grasas: Float = 0f,
    val grasasSaturadas: Float = 0f,
    val grasasTrans: Float = 0f,
    val fibra: Float = 0f,
    val azucares: Float = 0f,
    val sodio: Float = 0f
)