package com.example.proyectohealthy.data.repository


import com.example.proyectohealthy.data.local.entity.RegistroComida
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroComidaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val registrosRef = database.getReference("registros_comida")

    suspend fun createOrUpdateRegistroComida(registro: RegistroComida) {
        val key = registro.id.ifEmpty { registrosRef.push().key ?: return }
        val fechaFormateada = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(registro.fecha)

        // Crear un mapa que represente el registro de comida
        val registroMap = hashMapOf(
            "id" to key,
            "idPerfil" to registro.idPerfil,
            "fecha" to registro.fecha.time,
            "tipoComida" to registro.tipoComida,
            "alimentos" to registro.alimentos.map { (alimentoId, cantidad) ->
                hashMapOf(
                    "id" to alimentoId.replace(".", "_"),
                    "cantidad" to cantidad
                )
            },
            "misAlimentos" to registro.misAlimentos.map { (alimentoId, cantidad) ->
                hashMapOf(
                    "id" to alimentoId.replace(".", "_"),
                    "cantidad" to cantidad
                )
            }
        )

        registrosRef.child(registro.idPerfil).child(fechaFormateada).child(key).setValue(registroMap).await()
    }

    fun getRegistrosComidaFlow(idPerfil: String): Flow<List<RegistroComida>> = callbackFlow {
        val listener = registrosRef.child(idPerfil).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = snapshot.children.mapNotNull { it.getValue(RegistroComida::class.java) }
                trySend(registros)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { registrosRef.child(idPerfil).removeEventListener(listener) }
    }

    fun getRegistrosComidaPorFecha(idPerfil: String, fecha: Date): Flow<List<RegistroComida>> = callbackFlow {
        val startOfDay = fecha.time
        val endOfDay = fecha.time + 24 * 60 * 60 * 1000 // Añade un día en milisegundos
        val query = registrosRef.child(idPerfil)
            .orderByChild("fecha")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = snapshot.children.mapNotNull { it.getValue(RegistroComida::class.java) }
                trySend(registros)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun deleteRegistroComida(idPerfil: String, idRegistro: String) {
        registrosRef.child(idPerfil).child(idRegistro).removeValue().await()
    }
}