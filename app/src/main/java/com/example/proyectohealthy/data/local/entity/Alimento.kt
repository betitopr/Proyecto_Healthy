package com.example.proyectohealthy.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Entity(tableName = "alimentos")
data class Alimento(
    @PrimaryKey val id: String = "",
    val marca: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val nombrePorcion: String = "",
    val pesoPorcion: Float = 0f,
    val calorias: Float = 0f,
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