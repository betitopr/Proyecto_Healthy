package com.example.proyectohealthy.data.repository


import com.example.proyectohealthy.data.local.dao.RegistroComidaDao
import com.example.proyectohealthy.data.local.entity.RegistroComida
import kotlinx.coroutines.flow.Flow
import java.util.Date

class RegistroComidaRepository(private val registroComidaDao: RegistroComidaDao) {
    fun getAllRegistrosComidas(): Flow<List<RegistroComida>> = registroComidaDao.getAllRegistrosComidas()

    suspend fun getRegistroComidaById(id: Int): RegistroComida? = registroComidaDao.getRegistroComidaById(id)

    fun getRegistrosComidasByUserId(userId: Int): Flow<List<RegistroComida>> = registroComidaDao.getRegistrosComidasByUserId(userId)

    suspend fun insertRegistroComida(registroComida: RegistroComida) = registroComidaDao.insertRegistroComida(registroComida)

    suspend fun updateRegistroComida(registroComida: RegistroComida) = registroComidaDao.updateRegistroComida(registroComida)

    suspend fun deleteRegistroComida(registroComida: RegistroComida) = registroComidaDao.deleteRegistroComida(registroComida)

    fun getRegistrosComidasByDateRange(startDate: Date, endDate: Date): Flow<List<RegistroComida>> =
        registroComidaDao.getRegistrosComidasByDateRange(startDate, endDate)

    fun getRegistrosComidasByTipo(tipoComida: String): Flow<List<RegistroComida>> =
        registroComidaDao.getRegistrosComidasByTipo(tipoComida)
}