package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.Ejercicio
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class EjercicioRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val ejerciciosRef = database.getReference("ejercicios")

    suspend fun createOrUpdateEjercicio(ejercicio: Ejercicio) {
        val key = ejercicio.id.ifEmpty { ejerciciosRef.push().key ?: return }
        ejerciciosRef.child(ejercicio.idPerfil).child(key).setValue(ejercicio).await()
    }

    fun getEjerciciosFlow(idPerfil: String): Flow<List<Ejercicio>> = callbackFlow {
        val listener = ejerciciosRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicios = snapshot.children.mapNotNull { it.getValue(Ejercicio::class.java) }
                trySend(ejercicios)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ejerciciosRef.child(idPerfil).removeEventListener(listener) }
    }

    fun getEjerciciosPorFecha(idPerfil: String, fecha: Date): Flow<List<Ejercicio>> = callbackFlow {
        val startOfDay = fecha.time
        val endOfDay = fecha.time + 24 * 60 * 60 * 1000 // Añade un día en milisegundos
        val query = ejerciciosRef.child(idPerfil)
            .orderByChild("fechaCreacion")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicios = snapshot.children.mapNotNull { it.getValue(Ejercicio::class.java) }
                trySend(ejercicios)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun deleteEjercicio(idPerfil: String, idEjercicio: String) {
        ejerciciosRef.child(idPerfil).child(idEjercicio).removeValue().await()
    }

    fun getEjerciciosPorTipo(idPerfil: String, tipoActividad: String): Flow<List<Ejercicio>> = callbackFlow {
        val query = ejerciciosRef.child(idPerfil).orderByChild("tipoActividad").equalTo(tipoActividad)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ejercicios = snapshot.children.mapNotNull { it.getValue(Ejercicio::class.java) }
                trySend(ejercicios)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }
}