package com.example.proyectohealthy.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectohealthy.AppLifecycleHandler
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.proyectohealthy.data.repository.*
import com.example.proyectohealthy.util.RetrofitClient
import com.example.proyectohealthy.widget.WidgetUpdateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Providers para Scanner
    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(): OpenFoodFactsApi {
        return RetrofitClient.openFoodFactsApi
    }
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

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
    fun providePerfilRepository(
        database: FirebaseDatabase,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): PerfilRepository = PerfilRepository(database, storage, auth)

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

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideRegistroComidaRepository(database: FirebaseDatabase): RegistroComidaRepository =
        RegistroComidaRepository(database)

    @Provides
    @Singleton
    fun provideConsumoAguaRepository(database: FirebaseDatabase): ConsumoAguaRepository =
        ConsumoAguaRepository(database)

    //teams
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideWidgetUpdateManager(
        @ApplicationContext context: Context,
        registroDiarioRepository: RegistroDiarioRepository,
        perfilRepository: PerfilRepository,
        auth: FirebaseAuth
    ): WidgetUpdateManager {
        return WidgetUpdateManager(context, registroDiarioRepository, perfilRepository, auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {
        @Provides
        @Singleton
        fun provideAppLifecycleHandler(@ApplicationContext context: Context): AppLifecycleHandler {
            return AppLifecycleHandler(context)
        }
    }
}