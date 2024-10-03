package com.example.proyectohealthy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Recetas_Favoritas",
    foreignKeys = [
        ForeignKey(
            entity = Perfil::class,
            parentColumns = ["id_Perfil"],
            childColumns = ["id_Perfil"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_Perfil")]
)
data class RecetaFavorita(
    @PrimaryKey val id_Receta: Int,
    @ColumnInfo(name = "id_Perfil") val idPerfil: Int,
    val Nombre_Receta: String,
    val Ingredientes: String,
    val Instrucciones: String
)