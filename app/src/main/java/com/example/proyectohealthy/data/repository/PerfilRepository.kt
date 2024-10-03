package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.PerfilDao
import com.example.proyectohealthy.data.local.entity.Perfil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PerfilRepository @Inject constructor(private val perfilDao: PerfilDao) {
    suspend fun createOrUpdatePerfil(perfil: Perfil) {
        perfilDao.insertPerfil(perfil)
    }

    suspend fun getPerfil(uidFirebase: String): Perfil? {
        return perfilDao.getPerfilUsuario(uidFirebase)
    }

    fun getPerfilFlow(uidFirebase: String): Flow<Perfil?> {
        return perfilDao.getPerfilUsuarioFlow(uidFirebase)
    }

    suspend fun updateObjetivo(uidFirebase: String, objetivo: String) {
        perfilDao.updateObjetivo(uidFirebase, objetivo)
    }

    suspend fun updateEdad(uidFirebase: String, edad: Int) {
        perfilDao.updateEdad(uidFirebase, edad)
    }

    suspend fun updateGenero(uidFirebase: String, genero: String) {
        perfilDao.updateGenero(uidFirebase, genero)
    }

    suspend fun updateAltura(uidFirebase: String, altura: Float) {
        perfilDao.updateAltura(uidFirebase, altura)
    }

    suspend fun updatePesoActual(uidFirebase: String, pesoActual: Float) {
        perfilDao.updatePesoActual(uidFirebase, pesoActual)
    }

    suspend fun updatePesoObjetivo(uidFirebase: String, pesoObjetivo: Float) {
        perfilDao.updatePesoObjetivo(uidFirebase, pesoObjetivo)
    }

    suspend fun updateNivelActividad(uidFirebase: String, nivelActividad: String) {
        perfilDao.updateNivelActividad(uidFirebase, nivelActividad)
    }

    suspend fun updateEntrenamientoFuerza(uidFirebase: String, entrenamientoFuerza: String) {
        perfilDao.updateEntrenamientoFuerza(uidFirebase, entrenamientoFuerza)
    }

    suspend fun updateComoConseguirlo(uidFirebase: String, comoConseguirlo: String) {
        perfilDao.updateComoConseguirlo(uidFirebase, comoConseguirlo)
    }
}