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
    val favoritos: Map<String, FavoritoInfo> = emptyMap(),
    val alimentosRecientes: Map<String, Boolean> = emptyMap(),
    val premium: Boolean = false
)
data class FavoritoInfo(
    val id: String = "",
    val tipo: Int = 0, // 1 para Alimento, 2 para MisAlimentos
)