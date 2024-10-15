    package com.example.proyectohealthy.util

    import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitClient {
        private const val BASE_URL = "https://world.openfoodfacts.org/api/v0/"

        val openFoodFactsApi: OpenFoodFactsApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenFoodFactsApi::class.java)
        }
    }