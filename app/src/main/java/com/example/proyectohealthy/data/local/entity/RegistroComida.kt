package com.example.proyectohealthy.data.local.entity


import java.util.Date

data class RegistroComida(
    val id: String = "",
    val idPerfil: String = "", // Cambiado a String para Firebase
    val fecha: Date = Date(),
    val tipoComida: String = "",
    val alimentos: Map<String, Float> = emptyMap(), // ID del alimento -> cantidad de porciones
    val misAlimentos: Map<String, Float> = emptyMap() // ID de mis alimentos -> cantidad de porciones
)