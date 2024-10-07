package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.PlanNutricional
import kotlinx.coroutines.flow.Flow
import java.util.Date
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class PlanNutricionalRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val planesRef = database.getReference("planes_nutricionales")

    suspend fun createOrUpdatePlanNutricional(plan: PlanNutricional) {
        val key = plan.id.ifEmpty { planesRef.push().key ?: return }
        planesRef.child(key).setValue(plan).await()
    }

    fun getPlanNutricionalFlow(id: String): Flow<PlanNutricional?> = callbackFlow {
        val listener = planesRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plan = snapshot.getValue(PlanNutricional::class.java)
                trySend(plan)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { planesRef.child(id).removeEventListener(listener) }
    }

    fun getPlanesNutricionalesForPerfil(idPerfil: String): Flow<List<PlanNutricional>> = callbackFlow {
        val query = planesRef.orderByChild("idPerfil").equalTo(idPerfil)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val planes = snapshot.children.mapNotNull { it.getValue(PlanNutricional::class.java) }
                trySend(planes)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun deletePlanNutricional(id: String) {
        planesRef.child(id).removeValue().await()
    }

    fun getPlanesNutricionalesActivos(): Flow<List<PlanNutricional>> = callbackFlow {
        val currentDate = Date().time
        val query = planesRef.orderByChild("fechaFin").startAt(currentDate.toDouble())
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val planes = snapshot.children.mapNotNull { it.getValue(PlanNutricional::class.java) }
                    .filter { it.fechaInicio.time <= currentDate }
                trySend(planes)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }
}