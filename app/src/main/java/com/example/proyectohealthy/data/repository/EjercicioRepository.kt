package com.example.proyectohealthy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EjercicioRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val ejerciciosRef = database.getReference("ejercicios")
    private val registroEjerciciosRef = database.getReference("registro_ejercicios")
    private val caloriasQuemadasRef = database.getReference("calorias_quemadas")


    fun getEjercicios(): Flow<List<Ejercicio>> = callbackFlow {
        val listener = ejerciciosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicios = snapshot.children.mapNotNull {
                    try {
                        Ejercicio(
                            id = it.key ?: "",
                            nombre = it.child("nombre").getValue(String::class.java) ?: "",
                            caloriasPorMinuto = it.child("caloriasPorMinuto").getValue(Int::class.java) ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(ejercicios)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ejerciciosRef.removeEventListener(listener) }
    }

    suspend fun getEjercicioById(id: String): Ejercicio? {
        return ejerciciosRef.child(id).get().await().toEjercicio()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createOrUpdateRegistroEjercicio(registro: RegistroEjercicio, caloriasQuemadas: Int) {
        val fechaStr = registro.fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val key = if (registro.id.isBlank()) {
            registroEjerciciosRef.child(registro.idPerfil).child(fechaStr).push().key
                ?: throw IllegalStateException("No se pudo generar key")
        } else {
            registro.id
        }

        val registroMap = mapOf(
            "idEjercicio" to registro.idEjercicio,
            "duracionMinutos" to registro.duracionMinutos
        )

        // Usar una transacción para actualizar tanto el registro como las calorías
        database.reference.updateChildren(
            mapOf(
                "registro_ejercicios/${registro.idPerfil}/$fechaStr/$key" to registroMap,
                "calorias_quemadas/${registro.idPerfil}/$fechaStr" to caloriasQuemadas
            )
        ).await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteRegistroEjercicio(idPerfil: String, idRegistro: String, fecha: LocalDate, caloriasQuemadas: Int) {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)

        // Usar una transacción para eliminar el registro y actualizar las calorías
        database.reference.updateChildren(
            mapOf(
                "registro_ejercicios/$idPerfil/$fechaStr/$idRegistro" to null,
                "calorias_quemadas/$idPerfil/$fechaStr" to caloriasQuemadas
            )
        ).await()
    }


    suspend fun createOrUpdateEjercicios(ejercicio: Ejercicio) {
        val key = if (ejercicio.id.isBlank()) {
            ejerciciosRef.push().key ?: throw IllegalStateException("No se pudo generar key")
        } else {
            ejercicio.id
        }

        val ejercicioMap = mapOf(
            "nombre" to ejercicio.nombre,
            "caloriasPorMinuto" to ejercicio.caloriasPorMinuto
        )

        ejerciciosRef.child(key).setValue(ejercicioMap).await()
    }

    private fun Ejercicio.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "nombre" to nombre,
            "caloriasPorMinuto" to caloriasPorMinuto
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRegistrosEjercicioPorFecha(idPerfil: String, fecha: LocalDate): Flow<List<RegistroEjercicio>> = callbackFlow {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val query = registroEjerciciosRef.child(idPerfil).child(fechaStr)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = snapshot.children.mapNotNull {
                    try {
                        RegistroEjercicio(
                            id = it.key ?: "",
                            idPerfil = idPerfil,
                            idEjercicio = it.child("idEjercicio").getValue(String::class.java) ?: "",
                            duracionMinutos = it.child("duracionMinutos").getValue(Int::class.java) ?: 0,
                            fecha = fecha
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(registros)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCaloriasQuemadasPorFecha(idPerfil: String, fecha: LocalDate): Flow<Int> = callbackFlow {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val listener = caloriasQuemadasRef.child(idPerfil).child(fechaStr)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val calorias = snapshot.getValue(Int::class.java) ?: 0
                    trySend(calorias)
                }
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { caloriasQuemadasRef.child(idPerfil).child(fechaStr).removeEventListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun actualizarCaloriasQuemadas(idPerfil: String, fecha: LocalDate, calorias: Int) {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        caloriasQuemadasRef.child(idPerfil).child(fechaStr).setValue(calorias).await()
    }

    suspend fun deleteRegistroEjercicio(idPerfil: String, idRegistro: String) {
        registroEjerciciosRef.child(idPerfil).child(idRegistro).removeValue().await()
    }

    private fun DataSnapshot.toEjercicio(): Ejercicio? {
        return try {
            val id = this.child("id").getValue(String::class.java) ?: ""
            val nombre = this.child("nombre").getValue(String::class.java) ?: ""
            val caloriasPorMinuto = this.child("caloriasPorMinuto").getValue(Int::class.java) ?: 0

            Ejercicio(id, nombre, caloriasPorMinuto)
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DataSnapshot.toRegistroEjercicio(): RegistroEjercicio? {
        return try {
            val id = this.child("id").getValue(String::class.java) ?: ""
            val idPerfil = this.child("idPerfil").getValue(String::class.java) ?: ""
            val idEjercicio = this.child("idEjercicio").getValue(String::class.java) ?: ""
            val duracionMinutos = this.child("duracionMinutos").getValue(Int::class.java) ?: 0
            val fechaString = this.child("fecha").getValue(String::class.java) ?: return null
            val fecha = LocalDate.parse(fechaString)

            RegistroEjercicio(id, idPerfil, idEjercicio, duracionMinutos, fecha)
        } catch (e: Exception) {
            null
        }
    }

    private fun RegistroEjercicio.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "idPerfil" to idPerfil,
            "idEjercicio" to idEjercicio,
            "duracionMinutos" to duracionMinutos,
            "fecha" to fecha.toString()
        )
    }
}