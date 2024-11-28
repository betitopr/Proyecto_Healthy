    package com.example.proyectohealthy.util

    import com.example.proyectohealthy.BuildConfig
    import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
    import com.example.proyectohealthy.data.remote.RecetaService
    import okhttp3.OkHttpClient
    import okhttp3.logging.HttpLoggingInterceptor
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitClient {
        private const val BASE_URL = "https://world.openfoodfacts.org/api/v0/"
        private const val NINJA_API_URL = "https://api.api-ninjas.com/"

        val openFoodFactsApi: OpenFoodFactsApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenFoodFactsApi::class.java)
        }

        private val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        private val recetaHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", BuildConfig.NINJA_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()


        val recetaService: RecetaService by lazy {
            Retrofit.Builder()
                .baseUrl(NINJA_API_URL)
                .client(recetaHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecetaService::class.java)
        }
    }