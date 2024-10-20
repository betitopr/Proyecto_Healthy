package com.example.proyectohealthy.data.local.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class RegistroEjercicio @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String = "",
    val idPerfil: String = "",
    val idEjercicio: String = "",
    val duracionMinutos: Int = 0,
    val fecha: LocalDate = LocalDate.now()
)