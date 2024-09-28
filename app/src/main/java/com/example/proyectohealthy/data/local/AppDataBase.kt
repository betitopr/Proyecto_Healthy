
package com.example.proyectohealthy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectohealthy.data.local.dao.*
import com.example.proyectohealthy.data.local.entity.*


@Database(
    entities = [
        Alimento::class,
        PerfilUsuario::class,
        PlanNutricional::class,
        Ejercicio::class,
        RecetaFavorita::class,
        RegistroComida::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alimentoDao(): AlimentoDao
    abstract fun perfilUsuarioDao(): PerfilUsuarioDao
    abstract fun planNutricionalDao(): PlanNutricionalDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun recetaFavoritaDao(): RecetaFavoritaDao
    abstract fun registroComidaDao(): RegistroComidaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database1"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}