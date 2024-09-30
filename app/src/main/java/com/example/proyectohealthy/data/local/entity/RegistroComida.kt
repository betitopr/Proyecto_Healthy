package com.example.proyectohealthy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "Registro_Comidas",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuario::class,
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
    ]
)
data class RegistroComida(
    @PrimaryKey val id_Registro_Comida: Int,
    val id_Perfil: Int,  // Cambiado de id_Usuario a id_Perfil
    val Fecha: Date,
    val Tipo_Comida: String,
    val id_Alimento: Int,
    val Cantidad: Float
)