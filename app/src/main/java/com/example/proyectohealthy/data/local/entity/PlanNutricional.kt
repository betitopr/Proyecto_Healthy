package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Planes_Nutricionales",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuario::class,
            parentColumns = ["id_Perfil"],
            childColumns = ["id_Perfil"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlanNutricional(
    @PrimaryKey val id_Plan: Int,
    val id_Perfil: Int,
    val Fecha_inicio: Date,
    val Fecha_fin: Date,
    val Objetivos_Calorias: Int,
    val Objetivos_Proteinas: Float,
    val Objetivos_Carbohidratos: Float,
    val Objetivos_Grasas: Float
)