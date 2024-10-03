package com.example.proyectohealthy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Registro_Comidas",
    foreignKeys = [
        ForeignKey(
            entity = Perfil::class,
            parentColumns = ["id_Perfil"],
            childColumns = ["id_Perfil"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Alimento::class,
            parentColumns = ["id_Alimento"],
            childColumns = ["id_Alimento"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id_Perfil"),
        Index("id_Alimento")
    ]
)
data class RegistroComida(
    @PrimaryKey val id_Registro_Comida: Int,
    @ColumnInfo(name = "id_Perfil") val idPerfil: Int,
    val Fecha: Date,
    val Tipo_Comida: String,
    val id_Alimento: Int,
    val Cantidad: Float
)