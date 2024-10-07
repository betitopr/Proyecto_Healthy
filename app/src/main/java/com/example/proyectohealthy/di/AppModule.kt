package com.example.proyectohealthy.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.proyectohealthy.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firebase providers
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Repository providers
    @Provides
    @Singleton
    fun providePerfilRepository(database: FirebaseDatabase): PerfilRepository =
        PerfilRepository(database)

    @Provides
    @Singleton
    fun provideAlimentoRepository(database: FirebaseDatabase): AlimentoRepository =
        AlimentoRepository(database)

    @Provides
    @Singleton
    fun provideMisAlimentosRepository(database: FirebaseDatabase): MisAlimentosRepository =
        MisAlimentosRepository(database)

    @Provides
    @Singleton
    fun providePlanNutricionalRepository(database: FirebaseDatabase): PlanNutricionalRepository =
        PlanNutricionalRepository(database)

    @Provides
    @Singleton
    fun provideEjercicioRepository(database: FirebaseDatabase): EjercicioRepository =
        EjercicioRepository(database)

    @Provides
    @Singleton
    fun provideRecetaFavoritaRepository(database: FirebaseDatabase): RecetaFavoritaRepository =
        RecetaFavoritaRepository(database)

    @Provides
    @Singleton
    fun provideRegistroComidaRepository(database: FirebaseDatabase): RegistroComidaRepository =
        RegistroComidaRepository(database)

    @Provides
    @Singleton
    fun provideConsumoAguaRepository(database: FirebaseDatabase): ConsumoAguaRepository =
        ConsumoAguaRepository(database)
}