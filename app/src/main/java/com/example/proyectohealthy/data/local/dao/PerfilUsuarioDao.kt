package com.example.proyectohealthy.data.local.dao

import com.example.proyectohealthy.data.local.entity.PerfilUsuario


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilUsuarioDao {
    @Query("SELECT * FROM perfil_usuario")
    fun getAllPerfiles(): Flow<List<PerfilUsuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerfil(perfil: PerfilUsuario)

    @Update
    suspend fun updatePerfil(perfil: PerfilUsuario)

    @Delete
    suspend fun deletePerfil(perfil: PerfilUsuario)

    @Query("SELECT * FROM perfil_usuario WHERE Nivel_Actividad = :nivelActividad")
    fun getPerfilesByNivelActividad(nivelActividad: String): Flow<List<PerfilUsuario>>

    @Query("SELECT * FROM perfil_usuario WHERE Objetivo_Salud = :objetivo")
    fun getPerfilesByObjetivoSalud(objetivo: String): Flow<List<PerfilUsuario>>

    @Query("SELECT * FROM perfil_usuario WHERE Peso_Actual BETWEEN :pesoMin AND :pesoMax")
    fun getPerfilesByRangoPeso(pesoMin: Float, pesoMax: Float): Flow<List<PerfilUsuario>>

    // Si realmente necesitas este método, considéra renombrarlo
    @Query("SELECT * FROM perfil_usuario WHERE id_Perfil = :id")
    suspend fun getPerfilById(id: Int): PerfilUsuario?
}