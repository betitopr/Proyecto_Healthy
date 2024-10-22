package com.example.proyectohealthy.data.local.entity

import com.example.proyectohealthy.util.Constants
import java.util.Date

data class MisAlimentos(
    val id: String = "",
    val idPerfil: String = "",
    val marca: String = "",
    val nombre: String = "",
    val categoria: String = Constants.CATEGORIAS_ALIMENTOS[0],
    val nombrePorcion: String = "",
    val pesoPorcion: Float = 0f,
    val calorias: Int = 0,
    val grasas: Float = 0f,
    val grasasSaturadas: Float = 0f,
    val grasasTrans: Float = 0f,
    val sodio: Float = 0f,
    val carbohidratos: Float = 0f,
    val fibra: Float = 0f,
    val azucares: Float = 0f,
    val proteinas: Float = 0f,
    val codigoQr: String? = null,
    val diaCreado: Date = Date()
)