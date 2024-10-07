package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.MisAlimentos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class MisAlimentosRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val misAlimentosRef = database.getReference("mis_alimentos")

    suspend fun createOrUpdateMiAlimento(miAlimento: MisAlimentos) {
        val key = miAlimento.id.ifEmpty { misAlimentosRef.push().key ?: return }
        misAlimentosRef.child(miAlimento.idPerfil).child(key).setValue(miAlimento).await()
    }



    fun getMisAlimentosFlow(idPerfil: String): Flow<List<MisAlimentos>> = callbackFlow {
        val listener = misAlimentosRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val misAlimentos = snapshot.children.mapNotNull { it.getValue(MisAlimentos::class.java) }
                trySend(misAlimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { misAlimentosRef.child(idPerfil).removeEventListener(listener) }
    }

    suspend fun deleteMiAlimento(idPerfil: String, idAlimento: String) {
        misAlimentosRef.child(idPerfil).child(idAlimento).removeValue().await()
    }

    fun searchMisAlimentosByNombre(idPerfil: String, nombre: String): Flow<List<MisAlimentos>> = callbackFlow {
        val query = misAlimentosRef.child(idPerfil).orderByChild("nombre").startAt(nombre).endAt(nombre + "\uf8ff")
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val misAlimentos = snapshot.children.mapNotNull { it.getValue(MisAlimentos::class.java) }
                trySend(misAlimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getMisAlimentosByCategoria(idPerfil: String, categoria: String): Flow<List<MisAlimentos>> = callbackFlow {
        val query = misAlimentosRef.child(idPerfil).orderByChild("categoria").equalTo(categoria)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val misAlimentos = snapshot.children.mapNotNull { it.getValue(MisAlimentos::class.java) }
                trySend(misAlimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }
}