//package com.example.proyectohealthy.data.local.dao
//
//import androidx.room.*
//import com.example.proyectohealthy.data.local.entity.Ejercicio
//import kotlinx.coroutines.flow.Flow
//import java.util.Date
//
//@Dao
//interface EjercicioDao {
//    @Query("SELECT * FROM Ejercicios")
//    fun getAllEjercicios(): Flow<List<Ejercicio>>
//
//    @Query("SELECT * FROM Ejercicios WHERE id_Actividad = :id")
//    suspend fun getEjercicioById(id: Int): Ejercicio?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertEjercicio(ejercicio: Ejercicio)
//
//    @Update
//    suspend fun updateEjercicio(ejercicio: Ejercicio)
//
//    @Delete
//    suspend fun deleteEjercicio(ejercicio: Ejercicio)
//
//    @Query("SELECT * FROM Ejercicios WHERE Tipo_Actividad = :tipo")
//    fun getEjerciciosByTipo(tipo: String): Flow<List<Ejercicio>>
//
//    @Query("SELECT * FROM Ejercicios WHERE fecha_creacion = :fecha")
//    fun getEjerciciosByFecha(fecha: Date): Flow<List<Ejercicio>>
//
//    @Query("SELECT * FROM Ejercicios WHERE calorias_burned_per_minute >= :minCalorias")
//    fun getEjerciciosByMinCalorias(minCalorias: Int): Flow<List<Ejercicio>>
//}