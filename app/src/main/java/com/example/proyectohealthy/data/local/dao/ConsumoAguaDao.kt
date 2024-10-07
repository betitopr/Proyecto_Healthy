//package com.example.proyectohealthy.data.local.dao
//
//
//import androidx.room.*
//import com.example.proyectohealthy.data.local.entity.ConsumoAgua
//import kotlinx.coroutines.flow.Flow
//import java.util.Date
//
//@Dao
//interface ConsumoAguaDao {
//    @Query("SELECT * FROM Consumo_Agua")
//    fun getAllConsumosAgua(): Flow<List<ConsumoAgua>>
//
//    @Query("SELECT * FROM Consumo_Agua WHERE id_consumo = :id")
//    suspend fun getConsumoAguaById(id: Int): ConsumoAgua?
//
//    @Query("SELECT * FROM Consumo_Agua WHERE id_Perfil = :userId")
//    fun getConsumosAguaByUserId(userId: Int): Flow<List<ConsumoAgua>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertConsumoAgua(consumoAgua: ConsumoAgua)
//
//    @Update
//    suspend fun updateConsumoAgua(consumoAgua: ConsumoAgua)
//
//    @Delete
//    suspend fun deleteConsumoAgua(consumoAgua: ConsumoAgua)
//
//    @Query("SELECT * FROM Consumo_Agua WHERE Fecha BETWEEN :startDate AND :endDate")
//    fun getConsumosAguaByDateRange(startDate: Date, endDate: Date): Flow<List<ConsumoAgua>>
//
//    @Query("SELECT SUM(Cantidad) FROM Consumo_Agua WHERE id_Perfil = :userId AND Fecha = :fecha")
//    suspend fun getTotalConsumoAguaByFecha(userId: Int, fecha: Date): Float?
//}