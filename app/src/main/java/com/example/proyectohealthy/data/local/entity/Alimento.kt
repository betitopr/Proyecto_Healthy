package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Alimentos")
data class Alimento(
    @PrimaryKey val id_Alimento: Int,
    val Nombre: String,
    val CaloriasPor100g: Float,
    val ProteinasPor100g: Float,
    val CarbohidratosPor100g: Float,
    val GrasasPor100g: Float,
    val CodigoQR: String?,
    val Porcion: String,
    val unidad_de_medida: String,
    val Fibra: Float,
    val Sodio: Float,
    val Azucares: Float,
    val Grasas_Saturadas: Float,
    val Dia_creado: Date,
    val Clasificacion: String
)