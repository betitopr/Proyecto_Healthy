package com.example.proyectohealthy.data.repository

import com.example.proyectohealthy.data.remote.Nutriments
import com.example.proyectohealthy.data.remote.OpenFoodFactsApi
import com.example.proyectohealthy.data.remote.OpenFoodFactsResponse
import com.example.proyectohealthy.data.remote.Product
import com.google.firebase.database.FirebaseDatabase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Configuracion de nuestro caso de uso
class AlimentoScannedRepositoryTest{
    @RelaxedMockK
    private lateinit var  openFoodFactsApi: OpenFoodFactsApi
    @RelaxedMockK
    private lateinit var  database: FirebaseDatabase
    //Declaramos nuestra clase de uso
    lateinit var  alimentoscannedRepository: AlimentoScannedRepository

    private val realBarcode = "7622210449283" // Código EAN-13 válido

    //inicializamos la libreria mockk
    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        //Inicializamos nuestra clase de uso
        alimentoscannedRepository = AlimentoScannedRepository(openFoodFactsApi,database)
    }



    @Test
    fun `when API call is successful with real barcode should return Success`() = runBlocking {
        // Given
        val expectedResponse = OpenFoodFactsResponse(
            product = Product(
                product_name = "Oreo Original",
                brands = "Oreo",
                categories = "Snacks, Sweet snacks, Biscuits and cakes, Biscuits, Sandwich biscuits",
                nutriments = Nutriments(
                    energy_100g = 474f,
                    fat_100g = 20f,
                    saturated_fat_100g = 9f,
                    carbohydrates_100g = 69f,
                    sugars_100g = 35f,
                    fiber_100g = 2.4f,
                    proteins_100g = 5f,
                    salt_100g = 0.7f
                )
            )
        )
        coEvery { openFoodFactsApi.getProductInfo(realBarcode) } returns expectedResponse
        // When
        val result = alimentoscannedRepository.getProductInfo(realBarcode)
        // Then
        assertTrue("El resultado debería ser Success", result is ProductResult.Success)
        val successResult = result as ProductResult.Success
        assertEquals("El nombre del producto debería ser 'Oreo Original'",
            "Oreo Original", successResult.data.product.product_name)
        coVerify(exactly = 1) { openFoodFactsApi.getProductInfo(realBarcode) }
    }

    @Test
    fun `when API returns null nutriments should still return Success if product name exists`() = runBlocking {
        // Given
        val response = OpenFoodFactsResponse(
            product = Product(
                product_name = "Test Product",
                brands = "Test Brand",
                categories = "Test Category",
                nutriments = null
            )
        )
        coEvery { openFoodFactsApi.getProductInfo(realBarcode) } returns response

        // When
        val result = alimentoscannedRepository.getProductInfo(realBarcode)

        // Then
        assertTrue("El resultado debería ser Success incluso con nutrientes nulos",
            result is ProductResult.Success)
        val successResult = result as ProductResult.Success
        assertNull("Los nutrientes deberían ser null", successResult.data.product.nutriments)
        assertNotNull("El nombre del producto no debería ser null",
            successResult.data.product.product_name)
    }

    @Test
    fun `when API returns product without name should return Error`() = runBlocking {
        // Given
        val response = OpenFoodFactsResponse(
            product = Product(
                product_name = null,
                brands = "Test Brand",
                categories = null,
                nutriments = null
            )
        )
        coEvery { openFoodFactsApi.getProductInfo(realBarcode) } returns response

        // When
        val result = alimentoscannedRepository.getProductInfo(realBarcode)

        // Then
        assertTrue("El resultado debería ser Error cuando no hay nombre de producto",
            result is ProductResult.Error)
        assertEquals(
            "El mensaje de error debería indicar producto no encontrado",
            "Producto no encontrado",
            (result as ProductResult.Error).exception.message
        )
    }

    @Test
    fun `when API call throws exception should return Error`() = runBlocking {
        // Given
        val expectedException = Exception("Network error")
        coEvery { openFoodFactsApi.getProductInfo(realBarcode) } throws expectedException

        // When
        val result = alimentoscannedRepository.getProductInfo(realBarcode)

        // Then
        assertTrue("El resultado debería ser Error en caso de excepción",
            result is ProductResult.Error)
        assertEquals("La excepción debería ser la misma que se lanzó",
            expectedException, (result as ProductResult.Error).exception)
    }

}