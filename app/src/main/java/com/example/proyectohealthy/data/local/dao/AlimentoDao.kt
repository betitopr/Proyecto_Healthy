package com.example.proyectohealthy.data.local.dao

import androidx.room.*
import com.example.proyectohealthy.data.local.entity.Alimento
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AlimentoDao {
    @Query("SELECT * FROM Alimentos")
    fun getAllAlimentos(): Flow<List<Alimento>>

    @Query("SELECT * FROM Alimentos WHERE id_Alimento = :id")
    suspend fun getAlimentoById(id: Int): Alimento?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlimento(alimento: Alimento)

    @Update
    suspend fun updateAlimento(alimento: Alimento)

    @Delete
    suspend fun deleteAlimento(alimento: Alimento)

    @Query("SELECT * FROM Alimentos WHERE Nombre LIKE '%' || :nombre || '%'")
    fun searchAlimentosByNombre(nombre: String): Flow<List<Alimento>>

    @Query("SELECT * FROM Alimentos WHERE Clasificacion = :clasificacion")
    fun getAlimentosByClasificacion(clasificacion: String): Flow<List<Alimento>>

    @Query("SELECT * FROM Alimentos WHERE Dia_creado BETWEEN :startDate AND :endDate")
    fun getAlimentosByDateRange(startDate: Date, endDate: Date): Flow<List<Alimento>>
}