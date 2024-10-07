package com.example.proyectohealthy.data.local.entity

import java.util.Date

data class ConsumoAgua(
    val id: String = "",
    val idPerfil: String = "",
    val fecha: Date = Date(),
    val cantidad: Float = 0f
)