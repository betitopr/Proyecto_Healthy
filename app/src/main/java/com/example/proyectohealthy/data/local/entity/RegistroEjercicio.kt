package com.example.proyectohealthy.data.local.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
data class RegistroEjercicio (
    val id: String = "",
    val idPerfil: String = "",
    val idEjercicio: String = "",
    val duracionMinutos: Int = 0,
    val fecha: LocalDate = LocalDate.now()
)