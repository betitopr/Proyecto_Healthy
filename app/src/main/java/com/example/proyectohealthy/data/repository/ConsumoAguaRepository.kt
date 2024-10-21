package com.example.proyectohealthy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ConsumoAguaRepository.kt
class ConsumoAguaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val consumoAguaRef = database.getReference("consumo_agua")

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createOrUpdateConsumoAgua(consumoAgua: ConsumoAgua) {
        consumoAguaRef.child(consumoAgua.idPerfil).child(consumoAgua.fecha).setValue(consumoAgua).await()
    }

    fun getConsumoAguaFlow(idPerfil: String): Flow<List<ConsumoAgua>> = callbackFlow {
        val listener = consumoAguaRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val consumos = snapshot.children.mapNotNull {
                    it.getValue(ConsumoAgua::class.java)?.copy(fecha = LocalDate.parse(it.key).toString())
                }
                trySend(consumos)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { consumoAguaRef.child(idPerfil).removeEventListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getConsumoAguaPorFecha(idPerfil: String, fecha: LocalDate): Flow<ConsumoAgua?> = callbackFlow {
        val fechaString = fecha.toString()
        val listener = consumoAguaRef.child(idPerfil).child(fechaString).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val consumo = snapshot.getValue(ConsumoAgua::class.java)
                trySend(consumo)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { consumoAguaRef.child(idPerfil).child(fechaString).removeEventListener(listener) }
    }

    suspend fun deleteConsumoAgua(idPerfil: String, fecha: LocalDate) {
        val fechaFormatted = fecha.toString()
        consumoAguaRef.child(idPerfil).child(fechaFormatted).removeValue().await()
    }
}
