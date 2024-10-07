//package com.example.proyectohealthy.data.local.dao
//
//import androidx.room.*
//import com.example.proyectohealthy.data.local.entity.RecetaFavorita
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface RecetaFavoritaDao {
//    @Query("SELECT * FROM Recetas_Favoritas")
//    fun getAllRecetasFavoritas(): Flow<List<RecetaFavorita>>
//
//    @Query("SELECT * FROM Recetas_Favoritas WHERE id_Receta = :id")
//    suspend fun getRecetaFavoritaById(id: Int): RecetaFavorita?
//
//    @Query("SELECT * FROM Recetas_Favoritas WHERE id_Perfil = :userId")
//    fun getRecetasFavoritasByUserId(userId: Int): Flow<List<RecetaFavorita>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertRecetaFavorita(recetaFavorita: RecetaFavorita)
//
//    @Update
//    suspend fun updateRecetaFavorita(recetaFavorita: RecetaFavorita)
//
//    @Delete
//    suspend fun deleteRecetaFavorita(recetaFavorita: RecetaFavorita)
//
//    @Query("SELECT * FROM Recetas_Favoritas WHERE Nombre_Receta LIKE '%' || :nombre || '%'")
//    fun searchRecetasFavoritasByNombre(nombre: String): Flow<List<RecetaFavorita>>
//}