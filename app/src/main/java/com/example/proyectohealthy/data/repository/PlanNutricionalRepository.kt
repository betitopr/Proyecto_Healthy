package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.PlanNutricionalDao
import com.example.proyectohealthy.data.local.entity.PlanNutricional
import kotlinx.coroutines.flow.Flow
import java.util.Date

class PlanNutricionalRepository(private val planNutricionalDao: PlanNutricionalDao) {
    fun getAllPlanes(): Flow<List<PlanNutricional>> = planNutricionalDao.getAllPlanes()

    suspend fun getPlanById(id: Int): PlanNutricional? = planNutricionalDao.getPlanById(id)

    fun getPlanesByUserId(userId: Int): Flow<List<PlanNutricional>> = planNutricionalDao.getPlanesByUserId(userId)

    suspend fun insertPlan(plan: PlanNutricional) = planNutricionalDao.insertPlan(plan)

    suspend fun updatePlan(plan: PlanNutricional) = planNutricionalDao.updatePlan(plan)

    suspend fun deletePlan(plan: PlanNutricional) = planNutricionalDao.deletePlan(plan)

    fun getPlanesActivos(fecha: Date): Flow<List<PlanNutricional>> = planNutricionalDao.getPlanesActivos(fecha)

    fun getPlanesByMaxCalorias(maxCalorias: Int): Flow<List<PlanNutricional>> =
        planNutricionalDao.getPlanesByMaxCalorias(maxCalorias)
}