package com.example.proyectohealthy.data.local.entity



data class Perfil(
    val uid: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val username: String = "",
    val email: String = "",
    val authType: AuthType = AuthType.APP,
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
    val premium: Boolean = false,
    var perfilCompleto: Boolean = false,
    val unidadesPreferences: UnidadesPreferences = UnidadesPreferences(),
    val fechaCreacion: Long = System.currentTimeMillis(),    // Nuevo campo
    val ultimaActualizacion: Long = System.currentTimeMillis(), // Nuevo campo
    val grupoActualId: String? = null,
)
data class FavoritoInfo(
    val id: String = "",
    val tipo: Int = 0, // 1 para Alimento, 2 para MisAlimentos
)

data class UnidadesPreferences(
    val sistemaPeso: String = "Métrico (kg)",
    val sistemaAltura: String = "Métrico (cm)",
    val sistemaVolumen: String = "Métrico (ml)"
)

enum class AuthType {
    GMAIL,  // Usuario registrado con Gmail
    APP     // Usuario registrado directamente en la app
}