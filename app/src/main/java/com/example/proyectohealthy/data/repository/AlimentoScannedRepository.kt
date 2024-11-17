package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.example.proyectohealthy.data.remote.OpenFoodFactsResponse
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

sealed class ProductResult {
    data class Success(val data: OpenFoodFactsResponse) : ProductResult()
    data class Error(val exception: Exception) : ProductResult()
}

class AlimentoScannedRepository @Inject constructor(
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val database: FirebaseDatabase
) {

    suspend fun getProductInfo(barcode: String): ProductResult {
        return try {
            val response = openFoodFactsApi.getProductInfo(barcode)
            if (response.product.product_name == null) {
                ProductResult.Error(Exception("Producto no encontrado"))
            } else {
                ProductResult.Success(response)
            }
        } catch (e: Exception) {
            ProductResult.Error(e)
        }
    }
    fun saveMiAlimento(miAlimento: MisAlimentos) {
        val misAlimentosRef = database.getReference("mis_alimentos")
        val key = misAlimentosRef.push().key ?: throw IllegalStateException("Couldn't get push key for posts")
        val miAlimentoWithId = miAlimento.copy(id = key)
        misAlimentosRef.child(miAlimentoWithId.idPerfil).child(key).setValue(miAlimentoWithId)
    }
}

