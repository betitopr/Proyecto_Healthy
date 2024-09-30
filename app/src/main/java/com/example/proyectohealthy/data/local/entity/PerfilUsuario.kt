package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "perfil_usuario")
data class PerfilUsuario(
    @PrimaryKey val id_Perfil: Int,
    val Nombre: String,
    val Apellido: String,
    val Genero: String,
    val Altura: Float,
    val Peso_Actual: Float,
    val Peso_Objetivo: Float,
    val Nivel_Actividad: String,
    val Objetivo_Salud: String,
    val Perfil_Imagen: String?,
    val Biografia: String?
)