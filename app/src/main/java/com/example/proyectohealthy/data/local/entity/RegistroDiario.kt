package com.example.proyectohealthy.data.local.entity

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
data class RegistroDiario(
    val id: String = "",
    val idPerfil: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val caloriasConsumidas: Int = 0,
    val proteinasConsumidas: Float = 0f,
    val carbohidratosConsumidos: Float = 0f,
    val grasasConsumidas: Float = 0f,
    val caloriasQuemadas: Int = 0,
    val aguaConsumida: Int = 0,
    val pesoRegistrado: Float? = null,
    val caloriasNetas: Int = caloriasConsumidas - caloriasQuemadas
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "idPerfil" to idPerfil,
            "fecha" to fecha.toString(),
            "fechaStr" to fecha.format(DateTimeFormatter.BASIC_ISO_DATE),
            "caloriasConsumidas" to caloriasConsumidas,
            "proteinasConsumidas" to proteinasConsumidas,
            "carbohidratosConsumidos" to carbohidratosConsumidos,
            "grasasConsumidas" to grasasConsumidas,
            "caloriasQuemadas" to caloriasQuemadas,
            "aguaConsumida" to aguaConsumida,
            "pesoRegistrado" to (pesoRegistrado ?: 0f)
        )
    }

    companion object {
        fun fromMap(map: Map<*, *>): RegistroDiario? {
            return try {
                RegistroDiario(
                    id = map["id"] as? String ?: "",
                    idPerfil = map["idPerfil"] as? String ?: "",
                    fecha = LocalDate.parse(map["fecha"] as? String ?: return null),
                    caloriasConsumidas = (map["caloriasConsumidas"] as? Number)?.toInt() ?: 0,
                    proteinasConsumidas = (map["proteinasConsumidas"] as? Number)?.toFloat() ?: 0f,
                    carbohidratosConsumidos = (map["carbohidratosConsumidos"] as? Number)?.toFloat() ?: 0f,
                    grasasConsumidas = (map["grasasConsumidas"] as? Number)?.toFloat() ?: 0f,
                    caloriasQuemadas = (map["caloriasQuemadas"] as? Number)?.toInt() ?: 0,
                    aguaConsumida = (map["aguaConsumida"] as? Number)?.toInt() ?: 0,
                    pesoRegistrado = (map["pesoRegistrado"] as? Number)?.toFloat()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}