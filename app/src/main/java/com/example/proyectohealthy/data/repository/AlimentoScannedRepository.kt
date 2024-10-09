package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.dao.AlimentoDao
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.example.proyectohealthy.data.remote.OpenFoodFactsResponse
import javax.inject.Inject

class AlimentoScannedRepository @Inject constructor(
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val alimentoDao: AlimentoDao
) {
    suspend fun getProductInfo(barcode: String): OpenFoodFactsResponse {
        return openFoodFactsApi.getProductInfo(barcode)
    }

    suspend fun saveAlimento(alimento: Alimento) {
        alimentoDao.insertAlimento(alimento)
    }
}