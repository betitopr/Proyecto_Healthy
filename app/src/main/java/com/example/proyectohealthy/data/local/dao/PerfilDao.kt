//package com.example.proyectohealthy.data.local.dao
//
//import androidx.room.*
//import com.example.proyectohealthy.data.local.entity.Perfil
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface PerfilDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPerfil(perfil: Perfil)
//
//    @Query("SELECT * FROM perfil WHERE uid_firebase = :uidFirebase")
//    suspend fun getPerfilUsuario(uidFirebase: String): Perfil?
//
//    @Query("SELECT * FROM perfil WHERE uid_firebase = :uidFirebase")
//    fun getPerfilUsuarioFlow(uidFirebase: String): Flow<Perfil?>
//
//    @Update
//    suspend fun updatePerfil(perfil: Perfil)
//
//    @Query("UPDATE perfil SET Objetivo = :objetivo WHERE uid_firebase = :uidFirebase")
//    suspend fun updateObjetivo(uidFirebase: String, objetivo: String)
//
//    @Query("UPDATE perfil SET Edad = :edad WHERE uid_firebase = :uidFirebase")
//    suspend fun updateEdad(uidFirebase: String, edad: Int)
//
//    @Query("UPDATE perfil SET Genero = :genero WHERE uid_firebase = :uidFirebase")
//    suspend fun updateGenero(uidFirebase: String, genero: String)
//
//    @Query("UPDATE perfil SET Altura = :altura WHERE uid_firebase = :uidFirebase")
//    suspend fun updateAltura(uidFirebase: String, altura: Float)
//
//    @Query("UPDATE perfil SET Peso_Actual = :pesoActual WHERE uid_firebase = :uidFirebase")
//    suspend fun updatePesoActual(uidFirebase: String, pesoActual: Float)
//
//    @Query("UPDATE perfil SET Peso_Objetivo = :pesoObjetivo WHERE uid_firebase = :uidFirebase")
//    suspend fun updatePesoObjetivo(uidFirebase: String, pesoObjetivo: Float)
//
//    @Query("UPDATE perfil SET Nivel_Actividad = :nivelActividad WHERE uid_firebase = :uidFirebase")
//    suspend fun updateNivelActividad(uidFirebase: String, nivelActividad: String)
//
//    @Query("UPDATE perfil SET Entrenamiento_Fuerza = :entrenamientoFuerza WHERE uid_firebase = :uidFirebase")
//    suspend fun updateEntrenamientoFuerza(uidFirebase: String, entrenamientoFuerza: String)
//
//    @Query("UPDATE perfil SET Como_Conseguirlo = :comoConseguirlo WHERE uid_firebase = :uidFirebase")
//    suspend fun updateComoConseguirlo(uidFirebase: String, comoConseguirlo: String)
//}