package com.example.proyectohealthy.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.proyectohealthy.data.local.entity.RegistroDiario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RegistroDiarioRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val registrosRef = database.getReference("registros_diarios")

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun guardarRegistroDiario(registro: RegistroDiario) {
        try {
            val fechaStr = registro.fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
            Log.d("RegistroDiarioRepo", "Guardando registro para fecha: $fechaStr")

            registrosRef
                .child(registro.idPerfil)
                .child(fechaStr)
                .setValue(registro.toMap())
                .await()
        } catch (e: Exception) {
            Log.e("RegistroDiarioRepo", "Error guardando registro: ${e.message}")
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistrosPorRango(
        idPerfil: String,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): Flow<List<RegistroDiario>> = callbackFlow {
        val fechaInicioStr = fechaInicio.format(DateTimeFormatter.BASIC_ISO_DATE)
        val fechaFinStr = fechaFin.format(DateTimeFormatter.BASIC_ISO_DATE)

        Log.d("RegistroDiarioRepo", "Consultando registros desde $fechaInicioStr hasta $fechaFinStr")

        val query = registrosRef
            .child(idPerfil)
            .orderByChild("fechaStr")
            .startAt(fechaInicioStr)
            .endAt(fechaFinStr)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = mutableListOf<RegistroDiario>()
                var fechaActual = fechaInicio

                // Iteramos por cada día en el rango
                while (!fechaActual.isAfter(fechaFin)) {
                    val fechaStr = fechaActual.format(DateTimeFormatter.BASIC_ISO_DATE)
                    val registroDia = snapshot.child(fechaStr).getValue(object : GenericTypeIndicator<Map<String, Any>>() {})

                    if (registroDia != null) {
                        // Si existe registro para este día
                        RegistroDiario.fromMap(registroDia)?.let {
                            registros.add(it)
                        }
                    } else {
                        // Si no hay registro, agregamos uno con valores en 0
                        registros.add(
                            RegistroDiario(
                                idPerfil = idPerfil,
                                fecha = fechaActual
                            )
                        )
                    }
                    fechaActual = fechaActual.plusDays(1)
                }

                Log.d("RegistroDiarioRepo", "Total registros procesados: ${registros.size}")
                trySend(registros.sortedBy { it.fecha })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RegistroDiarioRepo", "Error en consulta: ${error.message}")
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerRegistroDia(
        idPerfil: String,
        fecha: LocalDate
    ): Flow<RegistroDiario?> = callbackFlow {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val registroRef = registrosRef.child(idPerfil).child(fechaStr)

        val listener = registroRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registro = if (snapshot.exists()) {
                    snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})?.let {
                        RegistroDiario.fromMap(it)
                    }
                } else null

                trySend(registro)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { registroRef.removeEventListener(listener) }
    }
}