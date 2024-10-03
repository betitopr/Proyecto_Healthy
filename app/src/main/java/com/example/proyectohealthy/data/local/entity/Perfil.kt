package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "perfil",
    indices = [Index(value = ["uid_firebase"], unique = true)]
)
data class Perfil(
    @PrimaryKey(autoGenerate = true) val id_Perfil: Int = 0,
    val uid_firebase: String, // Mantenemos el UID de Firebase
    val Nombre: String="",
    val Apellido: String="",
    val Genero: String = "",
    val Altura: Float = 0f,
    val Edad: Int = 0,
    val Peso_Actual: Float = 0f,
    val Peso_Objetivo: Float = 0f,
    val Nivel_Actividad: String = "",
    val Objetivo: String = "",
    val Como_Conseguirlo: String = "",
    val Entrenamiento_Fuerza: String = "",
    val Perfil_Imagen: String? = null,
    val Biografia: String? = null
)