package com.example.proyectohealthy.data.repository

import android.util.Log
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MisAlimentosRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val misAlimentosRef = database.getReference("mis_alimentos")

    suspend fun createOrUpdateMiAlimento(miAlimento: MisAlimentos): String {
        val key = if (miAlimento.id.isBlank()) {
            misAlimentosRef.child(miAlimento.idPerfil).push().key ?: throw IllegalStateException("No se pudo generar una nueva clave")
        } else {
            miAlimento.id
        }
        val updatedMiAlimento = miAlimento.copy(id = key)
        misAlimentosRef.child(miAlimento.idPerfil).child(key).setValue(updatedMiAlimento).await()
        return key
    }



    suspend fun getMiAlimentoById(idPerfil: String, id: String): MisAlimentos? {
        return try {
            val snapshot = misAlimentosRef.child(idPerfil).child(id).get().await()
            snapshot.getValue(MisAlimentos::class.java)?.copy(id = id)
        } catch (e: Exception) {
            Log.e("MisAlimentosRepository", "Error al obtener mi alimento: ${e.message}")
            null
        }
    }

    fun getMisAlimentosFlow(idPerfil: String): Flow<List<MisAlimentos>> = callbackFlow {
        val listener = misAlimentosRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val misAlimentos = snapshot.children.mapNotNull {
                    it.getValue(MisAlimentos::class.java)?.copy(id = it.key ?: "")
                }
                trySend(misAlimentos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { misAlimentosRef.child(idPerfil).removeEventListener(listener) }
    }



    suspend fun deleteMiAlimento(idPerfil: String, id: String) {
        misAlimentosRef.child(idPerfil).child(id).removeValue().await()
    }

    fun searchMisAlimentosByNombre(idPerfil: String, nombre: String): Flow<List<MisAlimentos>> = callbackFlow {
        if (nombre.isBlank()) {
            // Si está vacío, obtener todos los alimentos
            val queryTodos = misAlimentosRef.child(idPerfil).orderByChild("nombre")
            val listener = queryTodos.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val misAlimentos = snapshot.children.mapNotNull {
                        it.getValue(MisAlimentos::class.java)?.copy(id = it.key ?: "")
                    }
                    trySend(misAlimentos)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose { queryTodos.removeEventListener(listener) }
        } else {
            // Si hay texto, realizar la búsqueda
            val nombreBusqueda = nombre.trim().lowercase()
            val query = misAlimentosRef
                .child(idPerfil)
                .orderByChild("nombre")
                .startAt(nombreBusqueda)
                .endAt(nombreBusqueda + "\uf8ff")

            val listener = query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val misAlimentos = snapshot.children.mapNotNull {
                        it.getValue(MisAlimentos::class.java)?.copy(id = it.key ?: "")
                    }
                    trySend(misAlimentos)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose { query.removeEventListener(listener) }
        }
    }
}