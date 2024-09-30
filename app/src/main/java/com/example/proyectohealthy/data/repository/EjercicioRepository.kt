package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.EjercicioDao
import com.example.proyectohealthy.data.local.entity.Ejercicio
import kotlinx.coroutines.flow.Flow
import java.util.Date

class EjercicioRepository(private val ejercicioDao: EjercicioDao) {
    fun getAllEjercicios(): Flow<List<Ejercicio>> = ejercicioDao.getAllEjercicios()

    suspend fun getEjercicioById(id: Int): Ejercicio? = ejercicioDao.getEjercicioById(id)

    suspend fun insertEjercicio(ejercicio: Ejercicio) = ejercicioDao.insertEjercicio(ejercicio)

    suspend fun updateEjercicio(ejercicio: Ejercicio) = ejercicioDao.updateEjercicio(ejercicio)

    suspend fun deleteEjercicio(ejercicio: Ejercicio) = ejercicioDao.deleteEjercicio(ejercicio)

    fun getEjerciciosByTipo(tipo: String): Flow<List<Ejercicio>> = ejercicioDao.getEjerciciosByTipo(tipo)

    fun getEjerciciosByFecha(fecha: Date): Flow<List<Ejercicio>> = ejercicioDao.getEjerciciosByFecha(fecha)

    fun getEjerciciosByMinCalorias(minCalorias: Int): Flow<List<Ejercicio>> =
        ejercicioDao.getEjerciciosByMinCalorias(minCalorias)
}