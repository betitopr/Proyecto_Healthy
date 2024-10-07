package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.RecetaFavorita
import kotlinx.coroutines.flow.Flow
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class RecetaFavoritaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val recetasRef = database.getReference("recetas_favoritas")

    suspend fun createOrUpdateRecetaFavorita(receta: RecetaFavorita) {
        val key = receta.id.ifEmpty { recetasRef.push().key ?: return }
        recetasRef.child(receta.idPerfil).child(key).setValue(receta).await()
    }

    fun getRecetasFavoritasFlow(idPerfil: String): Flow<List<RecetaFavorita>> = callbackFlow {
        val listener = recetasRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recetas = snapshot.children.mapNotNull { it.getValue(RecetaFavorita::class.java) }
                trySend(recetas)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { recetasRef.child(idPerfil).removeEventListener(listener) }
    }

    suspend fun deleteRecetaFavorita(idPerfil: String, idReceta: String) {
        recetasRef.child(idPerfil).child(idReceta).removeValue().await()
    }

    fun searchRecetasFavoritas(idPerfil: String, query: String): Flow<List<RecetaFavorita>> = callbackFlow {
        val listener = recetasRef.child(idPerfil).orderByChild("nombreReceta")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recetas = snapshot.children.mapNotNull { it.getValue(RecetaFavorita::class.java) }
                    trySend(recetas)
                }
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { recetasRef.child(idPerfil).removeEventListener(listener) }
    }
}
