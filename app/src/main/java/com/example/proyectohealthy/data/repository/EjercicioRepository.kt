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

class EjercicioRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val ejerciciosRef = database.getReference("ejercicios")
    private val registroEjerciciosRef = database.getReference("registro_ejercicios")

    fun getEjercicios(): Flow<List<Ejercicio>> = callbackFlow {
        val listener = ejerciciosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicios = snapshot.children.mapNotNull { it.toEjercicio() }
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

    suspend fun createOrUpdateRegistroEjercicio(registro: RegistroEjercicio) {
        val key = registro.id.ifEmpty { registroEjerciciosRef.push().key ?: return }
        val registroMap = registro.toMap()
        registroEjerciciosRef.child(registro.idPerfil).child(key).setValue(registroMap).await()
    }

    suspend fun createOrUpdateEjercicio(ejercicio: Ejercicio) {
        val key = ejercicio.id.ifEmpty { ejerciciosRef.push().key ?: return }
        val ejercicioMap = ejercicio.toMap()
        ejerciciosRef.child(key).setValue(ejercicioMap).await()
    }

    private fun Ejercicio.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "nombre" to nombre,
            "caloriasPorMinuto" to caloriasPorMinuto
        )
    }

    fun getRegistroEjerciciosPorFecha(idPerfil: String, fecha: LocalDate): Flow<List<RegistroEjercicio>> = callbackFlow {
        val fechaString = fecha.toString()
        val query = registroEjerciciosRef.child(idPerfil).orderByChild("fecha").equalTo(fechaString)

        val listener = query.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = snapshot.children.mapNotNull { it.toRegistroEjercicio() }
                trySend(registros)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
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