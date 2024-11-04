package com.example.proyectohealthy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaloriasQuemadasRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val caloriasRef = database.getReference("calorias_quemadas")

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCaloriasQuemadasPorFecha(userId: String, fecha: LocalDate): Flow<Int> = callbackFlow {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val listener = caloriasRef.child(userId).child(fechaStr)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val calorias = snapshot.getValue(Int::class.java) ?: 0
                    trySend(calorias)
                }
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { caloriasRef.child(userId).child(fechaStr).removeEventListener(listener) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun actualizarCaloriasQuemadas(userId: String, fecha: LocalDate, calorias: Int) {
        val fechaStr = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        caloriasRef.child(userId).child(fechaStr).setValue(calorias).await()
    }
}

