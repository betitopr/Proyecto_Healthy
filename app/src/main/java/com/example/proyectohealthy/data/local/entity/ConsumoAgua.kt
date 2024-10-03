package com.example.proyectohealthy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Consumo_Agua",
    foreignKeys = [
        ForeignKey(
            entity = Perfil::class,
            parentColumns = ["id_Perfil"],
            childColumns = ["id_Perfil"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ConsumoAgua(
    @PrimaryKey val id_consumo: Int,
    @ColumnInfo(name = "id_Perfil") val idPerfil: Int,
    val Fecha: Date,
    val Cantidad: Float
)