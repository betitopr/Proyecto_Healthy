package com.example.proyectohealthy.data.local.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
// ConsumoAgua.kt
data class ConsumoAgua(
    val id: String = "",
    val idPerfil: String = "",
    val fecha: String = "", // Cambiamos a String para evitar problemas de serialización
    val cantidad: Int = 0
) {
    companion object {
        fun fromLocalDate(id: String, idPerfil: String, fecha: LocalDate, cantidad: Int): ConsumoAgua {
            return ConsumoAgua(id, idPerfil, fecha.toString(), cantidad)
        }
    }

    fun toLocalDate(): LocalDate {
        return LocalDate.parse(fecha)
    }
}