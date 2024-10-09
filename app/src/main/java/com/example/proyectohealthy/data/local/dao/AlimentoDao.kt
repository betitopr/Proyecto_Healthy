package com.example.proyectohealthy.data.local.dao

import androidx.room.*
import com.example.proyectohealthy.data.local.entity.Alimento

@Dao
interface AlimentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlimento(alimento: Alimento)

    @Query("SELECT * FROM alimentos")
    suspend fun getAllAlimentos(): List<Alimento>
}