package com.example.proyectohealthy.data.repository

import android.util.Log
import com.example.proyectohealthy.data.local.entity.FavoritoInfo
import com.example.proyectohealthy.data.local.entity.Perfil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class PerfilRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val perfilesRef = database.getReference("perfiles")

    suspend fun createOrUpdatePerfil(perfil: Perfil) {
        perfilesRef.child(perfil.uid).setValue(perfil).await()
    }



    suspend fun getPerfil(uid: String): Perfil? {
        return try {
            val snapshot = perfilesRef.child(uid).get().await()
            snapshot.getValue(Perfil::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getPerfilFlow(uid: String): Flow<Perfil?> = callbackFlow {
        val listener = perfilesRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val perfil = snapshot.getValue(Perfil::class.java)
                trySend(perfil)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { perfilesRef.child(uid).removeEventListener(listener) }
    }

    suspend fun agregarFavorito(userId: String, alimentoId: String, tipo: Int) {
        val favoritosRef = perfilesRef.child(userId).child("alimentosFavoritos")
        favoritosRef.child(alimentoId).setValue(tipo).await()
    }

    suspend fun quitarFavorito(userId: String, alimentoId: String) {
        val favoritosRef = perfilesRef.child(userId).child("alimentosFavoritos")
        favoritosRef.child(alimentoId).removeValue().await()
    }

    fun getFavoritosFlow(userId: String): Flow<Map<String, FavoritoInfo>> = callbackFlow {
        val favoritosRef = perfilesRef.child(userId).child("favoritos")
        val listener = favoritosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoritos = mutableMapOf<String, FavoritoInfo>()
                snapshot.children.forEach { child ->
                    child.getValue(FavoritoInfo::class.java)?.let { info ->
                        favoritos[child.key ?: return@forEach] = info
                    }
                }
                trySend(favoritos)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { favoritosRef.removeEventListener(listener) }
    }

    suspend fun toggleFavorito(userId: String, itemId: String, tipo: Int) {
        try {
            val favoritosRef = perfilesRef.child(userId).child("favoritos")
            val snapshot = favoritosRef.child(itemId).get().await()

            if (snapshot.exists()) {
                // Si existe, lo removemos
                favoritosRef.child(itemId).removeValue().await()
            } else {
                // Si no existe, lo agregamos
                val favoritoInfo = FavoritoInfo(
                    id = itemId,
                    tipo = tipo
                )
                favoritosRef.child(itemId).setValue(favoritoInfo).await()
            }
        } catch (e: Exception) {
            Log.e("PerfilRepository", "Error al toggle favorito", e)
            throw e
        }
    }

    suspend fun updateObjetivo(uid: String, objetivo: String) {
        perfilesRef.child(uid).child("objetivo").setValue(objetivo).await()
    }

    suspend fun updateEdad(uid: String, edad: Int) {
        perfilesRef.child(uid).child("edad").setValue(edad).await()
    }

    suspend fun updateGenero(uid: String, genero: String) {
        perfilesRef.child(uid).child("genero").setValue(genero).await()
    }

    suspend fun updateAltura(uid: String, altura: Float) {
        perfilesRef.child(uid).child("altura").setValue(altura).await()
    }

    suspend fun updatePesoActual(uid: String, pesoActual: Float) {
        perfilesRef.child(uid).child("pesoActual").setValue(pesoActual).await()
    }

    suspend fun updatePesoObjetivo(uid: String, pesoObjetivo: Float) {
        perfilesRef.child(uid).child("pesoObjetivo").setValue(pesoObjetivo).await()
    }

    suspend fun updateNivelActividad(uid: String, nivelActividad: String) {
        perfilesRef.child(uid).child("nivelActividad").setValue(nivelActividad).await()
    }

    suspend fun updateEntrenamientoFuerza(uid: String, entrenamientoFuerza: String) {
        perfilesRef.child(uid).child("entrenamientoFuerza").setValue(entrenamientoFuerza).await()
    }

    suspend fun updateComoConseguirlo(uid: String, comoConseguirlo: String) {
        perfilesRef.child(uid).child("comoConseguirlo").setValue(comoConseguirlo).await()
    }

    suspend fun updatePremium(uid: String, premium: Boolean) {
        perfilesRef.child(uid).child("premium").setValue(premium).await()
    }

    suspend fun addAlimentoFavorito(uid: String, alimentoId: String) {
        perfilesRef.child(uid).child("alimentosFavoritos").child(alimentoId).setValue(true).await()
    }

    suspend fun removeAlimentoFavorito(uid: String, alimentoId: String) {
        perfilesRef.child(uid).child("alimentosFavoritos").child(alimentoId).removeValue().await()
    }

    suspend fun addAlimentoReciente(uid: String, alimentoId: String) {
        perfilesRef.child(uid).child("alimentosRecientes").child(alimentoId).setValue(true).await()
    }


}