package com.example.proyectohealthy.data.local.entity

import java.util.Date

data class Ejercicio(
    val id: String = "",
    val idPerfil: String = "", // Añadido para asociar el ejercicio a un perfil específico
    val fechaCreacion: Date = Date(),
    val tipoActividad: String = "",
    val caloriasBurnedPerMinute: Int = 0
)