package com.example.proyectohealthy.data.local.entity



data class Perfil(
    val uid: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val genero: String = "",
    val altura: Float = 0f,
    val edad: Int = 0,
    val pesoActual: Float = 0f,
    val pesoObjetivo: Float = 0f,
    val nivelActividad: String = "",
    val objetivo: String = "",
    val comoConseguirlo: String = "",
    val entrenamientoFuerza: String = "",
    val perfilImagen: String? = null,
    val biografia: String? = null,
    val alimentosFavoritos: Map<String, Boolean> = emptyMap(),
    val alimentosRecientes: Map<String, Boolean> = emptyMap(),
    val premium: Boolean = false
)