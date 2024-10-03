package com.example.proyectohealthy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "plan_nutricional",
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
data class PlanNutricional(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_Perfil") val idPerfil: Int,
    val Fecha_inicio: Date,
    val Fecha_fin: Date,
    val Objetivos_Calorias: Int,
    val Objetivos_Proteinas: Float,
    val Objetivos_Carbohidratos: Float,
    val Objetivos_Grasas: Float
)