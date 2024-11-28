package com.example.proyectohealthy.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RecetaService {
    companion object {
        const val BASE_URL = "https://api.calorieninjas.com/"
    }

    //@Headers("X-Api-Key: \${BuildConfig.NINJA_API_KEY}")
    @GET("v1/recipe")
    suspend fun buscarRecetas(
        @Query("query") query: String
    ): Response<List<RecetaResponse>>
}


