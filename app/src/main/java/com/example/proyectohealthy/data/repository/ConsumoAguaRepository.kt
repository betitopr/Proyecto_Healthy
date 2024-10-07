package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ConsumoAguaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val consumoAguaRef = database.getReference("consumo_agua")

    suspend fun createOrUpdateConsumoAgua(consumoAgua: ConsumoAgua) {
        val key = consumoAgua.id.ifEmpty { consumoAguaRef.push().key ?: return }
        consumoAguaRef.child(consumoAgua.idPerfil).child(key).setValue(consumoAgua).await()
    }

    fun getConsumoAguaFlow(idPerfil: String): Flow<List<ConsumoAgua>> = callbackFlow {
        val listener = consumoAguaRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val consumos = snapshot.children.mapNotNull { it.getValue(ConsumoAgua::class.java) }
                trySend(consumos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { consumoAguaRef.child(idPerfil).removeEventListener(listener) }
    }

    fun getConsumoAguaPorFecha(idPerfil: String, fecha: Date): Flow<List<ConsumoAgua>> = callbackFlow {
        val startOfDay = fecha.time
        val endOfDay = fecha.time + 24 * 60 * 60 * 1000 // Añade un día en milisegundos
        val query = consumoAguaRef.child(idPerfil)
            .orderByChild("fecha")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val consumos = snapshot.children.mapNotNull { it.getValue(ConsumoAgua::class.java) }
                trySend(consumos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun deleteConsumoAgua(idPerfil: String, idConsumo: String) {
        consumoAguaRef.child(idPerfil).child(idConsumo).removeValue().await()
    }
}