package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.ConsumoAguaDao
import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ConsumoAguaRepository(private val consumoAguaDao: ConsumoAguaDao) {
    fun getAllConsumosAgua(): Flow<List<ConsumoAgua>> = consumoAguaDao.getAllConsumosAgua()

    suspend fun getConsumoAguaById(id: Int): ConsumoAgua? = consumoAguaDao.getConsumoAguaById(id)

    fun getConsumosAguaByUserId(userId: Int): Flow<List<ConsumoAgua>> = consumoAguaDao.getConsumosAguaByUserId(userId)

    suspend fun insertConsumoAgua(consumoAgua: ConsumoAgua) = consumoAguaDao.insertConsumoAgua(consumoAgua)

    suspend fun updateConsumoAgua(consumoAgua: ConsumoAgua) = consumoAguaDao.updateConsumoAgua(consumoAgua)

    suspend fun deleteConsumoAgua(consumoAgua: ConsumoAgua) = consumoAguaDao.deleteConsumoAgua(consumoAgua)

    fun getConsumosAguaByDateRange(startDate: Date, endDate: Date): Flow<List<ConsumoAgua>> =
        consumoAguaDao.getConsumosAguaByDateRange(startDate, endDate)

    suspend fun getTotalConsumoAguaByFecha(userId: Int, fecha: Date): Float? =
        consumoAguaDao.getTotalConsumoAguaByFecha(userId, fecha)
}