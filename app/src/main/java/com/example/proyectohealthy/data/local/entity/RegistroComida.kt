package com.example.proyectohealthy.data.local.entity


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class RegistroComida(
    val id: String = "",
    val idPerfil: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val tipoComida: String = "",
    val alimentos: Map<String, Float> = emptyMap(), // ID del alimento -> cantidad de porciones
    val misAlimentos: Map<String, Float> = emptyMap() // ID de mis alimentos -> cantidad de porciones
)