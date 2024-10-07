package com.example.proyectohealthy.data.local.entity


import java.util.Date



data class PlanNutricional(
    val id: String = "", // Cambiado a String para Firebase
    val idPerfil: String = "", // Cambiado a String para Firebase
    val fechaInicio: Date = Date(),
    val fechaFin: Date = Date(),
    val objetivosCalorias: Int = 0,
    val objetivosProteinas: Float = 0f,
    val objetivosCarbohidratos: Float = 0f,
    val objetivosGrasas: Float = 0f
)