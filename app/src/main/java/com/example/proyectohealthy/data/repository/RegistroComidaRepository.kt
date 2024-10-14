package com.example.proyectohealthy.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.google.firebase.database.getValue
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class RegistroComidaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val registrosRef = database.getReference("registros_comida")

    private fun sanitizeKey(key: String): String {
        return key.replace(Regex("[./#$\\[\\]]"), "_")
    }

    suspend fun createOrUpdateRegistroComida(registro: RegistroComida) {
        val fechaFormatted = registro.fecha.format(DateTimeFormatter.BASIC_ISO_DATE)

        val sanitizedIdPerfil = sanitizeKey(registro.idPerfil)
        val key = if (registro.id.isBlank()) {
            registrosRef.child(sanitizedIdPerfil).child(fechaFormatted).push().key ?: return
        } else {
            sanitizeKey(registro.id)
        }

        val sanitizedAlimentos = registro.alimentos
            .filterKeys { it.isNotBlank() }
            .mapKeys { sanitizeKey(it.key) }
        val sanitizedMisAlimentos = registro.misAlimentos
            .filterKeys { it.isNotBlank() }
            .mapKeys { sanitizeKey(it.key) }

        val registroMap = mutableMapOf<String, Any>(
            "id" to key,
            "idPerfil" to sanitizedIdPerfil,
            "fecha" to fechaFormatted,
            "tipoComida" to sanitizeKey(registro.tipoComida)
        )

        if (sanitizedAlimentos.isNotEmpty()) {
            registroMap["alimentos"] = sanitizedAlimentos
        }
        if (sanitizedMisAlimentos.isNotEmpty()) {
            registroMap["misAlimentos"] = sanitizedMisAlimentos
        }

        try {
            Log.d("RegistroComidaRepository", "Intentando guardar: $registroMap")
            registrosRef.child(sanitizedIdPerfil)
                .child(fechaFormatted)
                .child(key)
                .setValue(registroMap)
                .await()
            Log.d("RegistroComidaRepository", "Registro guardado exitosamente")
        } catch (e: Exception) {
            Log.e("RegistroComidaRepository", "Error al guardar registro: ${e.message}")
            throw e
        }
    }

    fun getRegistrosComidaPorFecha(idPerfil: String, fecha: LocalDate): Flow<List<RegistroComida>> = callbackFlow {
        val fechaFormatted = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
        val query = registrosRef.child(idPerfil).child(fechaFormatted)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registros = snapshot.children.mapNotNull { it.toRegistroComida() }
                trySend(registros)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun deleteRegistroComida(idPerfil: String, idRegistro: String, fecha: LocalDate) {
        try {
            val fechaFormatted = fecha.format(DateTimeFormatter.BASIC_ISO_DATE)
            registrosRef.child(idPerfil).child(fechaFormatted).child(idRegistro).removeValue().await()
            Log.d("RegistroComidaRepository", "Registro eliminado exitosamente")
        } catch (e: Exception) {
            Log.e("RegistroComidaRepository", "Error al eliminar registro: ${e.message}")
            throw e
        }
    }

    private fun DataSnapshot.toRegistroComida(): RegistroComida? {
        return try {
            val id = this.child("id").getValue(String::class.java) ?: ""
            val idPerfil = this.child("idPerfil").getValue(String::class.java) ?: ""
            val fechaString = this.child("fecha").getValue(String::class.java) ?: return null
            val fecha = LocalDate.parse(fechaString, DateTimeFormatter.BASIC_ISO_DATE)
            val tipoComida = this.child("tipoComida").getValue(String::class.java) ?: ""
            val alimentos = this.child("alimentos").getValue<Map<String, Float>>() ?: emptyMap()
            val misAlimentos = this.child("misAlimentos").getValue<Map<String, Float>>() ?: emptyMap()

            RegistroComida(id, idPerfil, fecha, tipoComida, alimentos, misAlimentos)
        } catch (e: Exception) {
            Log.e("RegistroComidaRepository", "Error al convertir DataSnapshot a RegistroComida: ${e.message}")
            null
        }
    }
}