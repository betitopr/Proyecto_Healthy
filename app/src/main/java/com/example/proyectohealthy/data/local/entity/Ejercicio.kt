package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Ejercicios")
data class Ejercicio(
    @PrimaryKey val id_Actividad: Int,
    val fecha_creacion: Date,
    val Tipo_Actividad: String,
    val calorias_burned_per_minute: Int
)