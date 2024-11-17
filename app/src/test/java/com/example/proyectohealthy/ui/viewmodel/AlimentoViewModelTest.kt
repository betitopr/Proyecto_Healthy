package com.example.proyectohealthy.ui.viewmodel

import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.OrderType
import com.example.proyectohealthy.data.repository.AlimentoRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlimentoViewModelTest {
    @RelaxedMockK
    private lateinit var alimentoRepository: AlimentoRepository
    //Instanciar el caso de uso
    lateinit var alimentoViewModel: AlimentoViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var alimentosFlow: MutableStateFlow<List<Alimento>>

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        // Inicializar el Flow antes de crear el ViewModel
        alimentosFlow = MutableStateFlow(emptyList())
        coEvery { alimentoRepository.getAllAlimentosFlow() } returns alimentosFlow

        alimentoViewModel = AlimentoViewModel(alimentoRepository)
        // Avanzar el dispatcher para permitir que se complete la inicialización
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createAlimento should successfully create new alimento`() = runTest {
        // Given
        val newAlimento = Alimento(
            id = "",
            nombre = "Manzana",
            categoria = "Frutas",
            calorias = 52,
            proteinas = 0.3f,
            carbohidratos = 14f,
            grasas = 0.2f
        )
        val createdId = "abc123"
        coEvery { alimentoRepository.createOrUpdateAlimento(any()) } returns createdId

        // When
        alimentoViewModel.createAlimento(newAlimento)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { alimentoRepository.createOrUpdateAlimento(newAlimento) }
        assertNull(alimentoViewModel.error.value)
    }

    @Test
    fun `getAlimentoById should return alimento when exists`() = runTest {
        // Given
        val alimentoId = "abc123"
        val expectedAlimento = Alimento(
            id = alimentoId,
            nombre = "Manzana",
            categoria = "Frutas"
        )
        coEvery { alimentoRepository.getAlimentoById(alimentoId) } returns expectedAlimento

        // When
        val result = alimentoViewModel.getAlimentoById(alimentoId)

        // Then
        assertEquals(expectedAlimento, result)
    }

    @Test
    fun `deleteAlimento should successfully delete existing alimento`() = runTest {
        // Given
        val alimentoId = "abc123"
        coEvery { alimentoRepository.deleteAlimento(alimentoId) } returns Unit

        // When
        alimentoViewModel.deleteAlimento(alimentoId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { alimentoRepository.deleteAlimento(alimentoId) }
        assertNull(alimentoViewModel.error.value)
    }

    @Test
    fun `searchAlimentosByNombre should filter alimentos by name`() = runTest {
        // Given
        val alimentos = listOf(
            Alimento(id = "1", nombre = "Manzana"),
            Alimento(id = "2", nombre = "Pera"),
            Alimento(id = "3", nombre = "Mandarina")
        )
        // Actualizar el Flow existente
        alimentosFlow.value = alimentos
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        alimentoViewModel.searchAlimentosByNombre("Man")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val filteredAlimentos = alimentoViewModel.alimentos.value
        assertEquals(2, filteredAlimentos.size)
        assert(filteredAlimentos.all { it.nombre.contains("Man", ignoreCase = true) })
        assertEquals("Man", alimentoViewModel.currentQuery.value)
    }

    @Test
    fun `updateFiltros should apply filters correctly`() = runTest {
        // Given
        val alimentos = listOf(
            Alimento(id = "1", nombre = "Manzana", categoria = "Frutas", calorias = 52),
            Alimento(id = "2", nombre = "Pollo", categoria = "Carnes", calorias = 165),
            Alimento(id = "3", nombre = "Arroz", categoria = "Cereales", calorias = 130)
        )
        // Actualizar el Flow existente
        alimentosFlow.value = alimentos
        testDispatcher.scheduler.advanceUntilIdle()

        val filtros = AlimentoFiltros(
            categories = setOf("Frutas", "Cereales"),
            orderType = OrderType.Calories,
            isAscending = true
        )

        // When
        alimentoViewModel.updateFiltros(filtros)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val filteredAlimentos = alimentoViewModel.alimentos.value
        assertEquals(2, filteredAlimentos.size)
        assert(filteredAlimentos.all { it.categoria in setOf("Frutas", "Cereales") })
        assert(filteredAlimentos[0].calorias <= filteredAlimentos[1].calorias)
        assertEquals(filtros, alimentoViewModel.filtros.value)
    }

    @Test
    fun `empty search query should show all alimentos`() = runTest {
        // Given
        val alimentos = listOf(
            Alimento(id = "1", nombre = "Manzana"),
            Alimento(id = "2", nombre = "Pera"),
            Alimento(id = "3", nombre = "Mandarina")
        )
        // Actualizar el Flow existente
        alimentosFlow.value = alimentos
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        alimentoViewModel.searchAlimentosByNombre("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("", alimentoViewModel.currentQuery.value)
        assertEquals(alimentos.size, alimentoViewModel.alimentos.value.size)
    }


    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        alimentoViewModel.createAlimento(Alimento()) // Trigger an error state
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        alimentoViewModel.clearError()

        // Then
        assertNull(alimentoViewModel.error.value)
    }

}