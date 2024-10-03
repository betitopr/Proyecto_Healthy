package com.example.proyectohealthy.di

import android.content.Context
import com.example.proyectohealthy.data.local.AppDatabase
import com.example.proyectohealthy.data.local.dao.*
import com.example.proyectohealthy.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providePerfilDao(database: AppDatabase): PerfilDao = database.perfilDao()

    @Provides
    @Singleton
    fun provideAlimentoDao(database: AppDatabase): AlimentoDao = database.alimentoDao()

    @Provides
    @Singleton
    fun providePlanNutricionalDao(database: AppDatabase): PlanNutricionalDao = database.planNutricionalDao()

    @Provides
    @Singleton
    fun provideEjercicioDao(database: AppDatabase): EjercicioDao = database.ejercicioDao()

    @Provides
    @Singleton
    fun provideRecetaFavoritaDao(database: AppDatabase): RecetaFavoritaDao = database.recetaFavoritaDao()

    @Provides
    @Singleton
    fun provideRegistroComidaDao(database: AppDatabase): RegistroComidaDao = database.registroComidaDao()

    @Provides
    @Singleton
    fun provideUserRepository(perfilDao: PerfilDao): PerfilRepository = PerfilRepository(perfilDao)

    @Provides
    @Singleton
    fun provideAlimentoRepository(alimentoDao: AlimentoDao): AlimentoRepository = AlimentoRepository(alimentoDao)

    @Provides
    @Singleton
    fun providePlanNutricionalRepository(planNutricionalDao: PlanNutricionalDao): PlanNutricionalRepository = PlanNutricionalRepository(planNutricionalDao)

    @Provides
    @Singleton
    fun provideEjercicioRepository(ejercicioDao: EjercicioDao): EjercicioRepository = EjercicioRepository(ejercicioDao)

    @Provides
    @Singleton
    fun provideRecetaFavoritaRepository(recetaFavoritaDao: RecetaFavoritaDao): RecetaFavoritaRepository = RecetaFavoritaRepository(recetaFavoritaDao)

    @Provides
    @Singleton
    fun provideRegistroComidaRepository(registroComidaDao: RegistroComidaDao): RegistroComidaRepository = RegistroComidaRepository(registroComidaDao)
}