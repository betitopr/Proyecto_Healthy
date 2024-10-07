package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.Alimento
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

class AlimentoRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val alimentosRef = database.getReference("alimentos")

    suspend fun createOrUpdateAlimento(alimento: Alimento) {
        val key = alimento.id.ifEmpty { alimentosRef.push().key ?: return }
        alimentosRef.child(key).setValue(alimento).await()
    }

    fun getAlimentoFlow(id: String): Flow<Alimento?> = callbackFlow {
        val listener = alimentosRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alimento = snapshot.getValue(Alimento::class.java)
                trySend(alimento)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { alimentosRef.child(id).removeEventListener(listener) }
    }

    fun getAllAlimentosFlow(): Flow<List<Alimento>> = callbackFlow {
        val listener = alimentosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alimentos = snapshot.children.mapNotNull { it.getValue(Alimento::class.java) }
                trySend(alimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { alimentosRef.removeEventListener(listener) }
    }

    suspend fun deleteAlimento(id: String) {
        alimentosRef.child(id).removeValue().await()
    }

    fun searchAlimentosByNombre(nombre: String): Flow<List<Alimento>> = callbackFlow {
        val query = alimentosRef.orderByChild("nombre").startAt(nombre).endAt(nombre + "\uf8ff")
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alimentos = snapshot.children.mapNotNull { it.getValue(Alimento::class.java) }
                trySend(alimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getAlimentosByCategoria(categoria: String): Flow<List<Alimento>> = callbackFlow {
        val query = alimentosRef.orderByChild("categoria").equalTo(categoria)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alimentos = snapshot.children.mapNotNull { it.getValue(Alimento::class.java) }
                trySend(alimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getAlimentosByDateRange(startDate: Date, endDate: Date): Flow<List<Alimento>> = callbackFlow {
        val query = alimentosRef.orderByChild("diaCreado")
            .startAt(startDate.time.toDouble())
            .endAt(endDate.time.toDouble())
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alimentos = snapshot.children.mapNotNull { it.getValue(Alimento::class.java) }
                trySend(alimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }
}