package com.example.proyectohealthy.data.local.dao

import androidx.room.*
import com.example.proyectohealthy.data.local.entity.PlanNutricional
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface PlanNutricionalDao {
    @Query("SELECT * FROM Planes_Nutricionales")
    fun getAllPlanes(): Flow<List<PlanNutricional>>

    @Query("SELECT * FROM Planes_Nutricionales WHERE id_Plan = :id")
    suspend fun getPlanById(id: Int): PlanNutricional?

    @Query("SELECT * FROM Planes_Nutricionales WHERE id_Perfil = :userId")
    fun getPlanesByUserId(userId: Int): Flow<List<PlanNutricional>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanNutricional)

    @Update
    suspend fun updatePlan(plan: PlanNutricional)

    @Delete
    suspend fun deletePlan(plan: PlanNutricional)

    @Query("SELECT * FROM Planes_Nutricionales WHERE Fecha_inicio <= :fecha AND Fecha_fin >= :fecha")
    fun getPlanesActivos(fecha: Date): Flow<List<PlanNutricional>>

    @Query("SELECT * FROM Planes_Nutricionales WHERE Objetivos_Calorias <= :maxCalorias")
    fun getPlanesByMaxCalorias(maxCalorias: Int): Flow<List<PlanNutricional>>
}