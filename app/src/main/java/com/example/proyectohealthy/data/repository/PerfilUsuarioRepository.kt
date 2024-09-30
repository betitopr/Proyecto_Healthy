package com.example.proyectohealthy.data.repository


import com.example.proyectohealthy.data.local.dao.PerfilUsuarioDao
import com.example.proyectohealthy.data.local.entity.PerfilUsuario
import kotlinx.coroutines.flow.Flow

class PerfilUsuarioRepository(private val perfilUsuarioDao: PerfilUsuarioDao) {
    fun getAllPerfiles(): Flow<List<PerfilUsuario>> = perfilUsuarioDao.getAllPerfiles()

    suspend fun getPerfilById(id: Int): PerfilUsuario? = perfilUsuarioDao.getPerfilById(id)

    suspend fun insertPerfil(perfil: PerfilUsuario) = perfilUsuarioDao.insertPerfil(perfil)

    suspend fun updatePerfil(perfil: PerfilUsuario) = perfilUsuarioDao.updatePerfil(perfil)

    suspend fun deletePerfil(perfil: PerfilUsuario) = perfilUsuarioDao.deletePerfil(perfil)

    fun getPerfilesByNivelActividad(nivelActividad: String): Flow<List<PerfilUsuario>> =
        perfilUsuarioDao.getPerfilesByNivelActividad(nivelActividad)

    fun getPerfilesByObjetivoSalud(objetivo: String): Flow<List<PerfilUsuario>> =
        perfilUsuarioDao.getPerfilesByObjetivoSalud(objetivo)

    fun getPerfilesByRangoPeso(pesoMin: Float, pesoMax: Float): Flow<List<PerfilUsuario>> =
        perfilUsuarioDao.getPerfilesByRangoPeso(pesoMin, pesoMax)
}