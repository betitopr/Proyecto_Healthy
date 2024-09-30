package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.AlimentoDao
import com.example.proyectohealthy.data.local.entity.Alimento
import kotlinx.coroutines.flow.Flow
import java.util.Date

class AlimentoRepository(private val alimentoDao: AlimentoDao) {
    fun getAllAlimentos(): Flow<List<Alimento>> = alimentoDao.getAllAlimentos()

    suspend fun getAlimentoById(id: Int): Alimento? = alimentoDao.getAlimentoById(id)

    suspend fun insertAlimento(alimento: Alimento) = alimentoDao.insertAlimento(alimento)

    suspend fun updateAlimento(alimento: Alimento) = alimentoDao.updateAlimento(alimento)

    suspend fun deleteAlimento(alimento: Alimento) = alimentoDao.deleteAlimento(alimento)

    fun searchAlimentosByNombre(nombre: String): Flow<List<Alimento>> = alimentoDao.searchAlimentosByNombre(nombre)

    fun getAlimentosByClasificacion(clasificacion: String): Flow<List<Alimento>> = alimentoDao.getAlimentosByClasificacion(clasificacion)

    fun getAlimentosByDateRange(startDate: Date, endDate: Date): Flow<List<Alimento>> = alimentoDao.getAlimentosByDateRange(startDate, endDate)
}