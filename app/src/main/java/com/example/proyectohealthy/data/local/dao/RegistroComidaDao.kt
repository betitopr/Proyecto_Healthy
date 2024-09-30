package com.example.proyectohealthy.data.local.dao


import androidx.room.*
import com.example.proyectohealthy.data.local.entity.RegistroComida
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RegistroComidaDao {
    @Query("SELECT * FROM Registro_Comidas")
    fun getAllRegistrosComidas(): Flow<List<RegistroComida>>

    @Query("SELECT * FROM Registro_Comidas WHERE id_Registro_Comida = :id")
    suspend fun getRegistroComidaById(id: Int): RegistroComida?

    @Query("SELECT * FROM Registro_Comidas WHERE id_Perfil = :userId")
    fun getRegistrosComidasByUserId(userId: Int): Flow<List<RegistroComida>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistroComida(registroComida: RegistroComida)

    @Update
    suspend fun updateRegistroComida(registroComida: RegistroComida)

    @Delete
    suspend fun deleteRegistroComida(registroComida: RegistroComida)

    @Query("SELECT * FROM Registro_Comidas WHERE Fecha BETWEEN :startDate AND :endDate")
    fun getRegistrosComidasByDateRange(startDate: Date, endDate: Date): Flow<List<RegistroComida>>

    @Query("SELECT * FROM Registro_Comidas WHERE Tipo_Comida = :tipoComida")
    fun getRegistrosComidasByTipo(tipoComida: String): Flow<List<RegistroComida>>
}