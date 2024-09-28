package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Recetas_Favoritas",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuario::class,
            parentColumns = ["id_Perfil"],
            childColumns = ["id_Perfil"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecetaFavorita(
    @PrimaryKey val id_Receta: Int,
    val id_Perfil: Int,
    val Nombre_Receta: String,
    val Ingredientes: String,
    val Instrucciones: String
)