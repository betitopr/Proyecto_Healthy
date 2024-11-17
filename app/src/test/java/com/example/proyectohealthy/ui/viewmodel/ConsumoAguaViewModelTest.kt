package com.example.proyectohealthy.ui.viewmodel

import com.example.proyectohealthy.data.local.entity.ConsumoAgua
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.ConsumoAguaRepository
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    private val testDispatcher = StandardTestDispatcher()

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ConsumoAguaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var consumoAguaRepository: ConsumoAguaRepository
    @RelaxedMockK
    private lateinit var perfilRepository: PerfilRepository
    @RelaxedMockK
    private lateinit var auth: FirebaseAuth
    @RelaxedMockK
    private lateinit var currentUser: FirebaseUser

    private lateinit var consumoAguaViewModel: ConsumoAguaViewModel

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        every { auth.currentUser } returns currentUser
        every { currentUser.uid } returns "test-uid"
    }

    @Test
    fun `cuando se inicializa el ViewModel, carga el perfil del usuario correctamente`() = runTest {
        // Given
        val perfil = Perfil(uid = "test-uid", pesoActual = 70f)
        coEvery { perfilRepository.getPerfil("test-uid") } returns perfil
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha(any(), any())
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)

        // Then
        advanceUntilIdle() // Avanzamos el tiempo virtual hasta que se completen todas las corrutinas
        coVerify { perfilRepository.getPerfil("test-uid") }
        assertEquals(70f, consumoAguaViewModel.pesoUsuario.value)
    }

    @Test
    fun `cuando se calcula vasos recomendados con peso válido, retorna el cálculo correcto`() = runTest {
        // Given
        val perfil = Perfil(uid = "test-uid", pesoActual = 70f)
        coEvery { perfilRepository.getPerfil("test-uid") } returns perfil
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha(any(), any())
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle() // Importante: esperar a que se complete la inicialización
        val vasosRecomendados = consumoAguaViewModel.calcularVasosRecomendados()

        // Then
        assertEquals(8, vasosRecomendados)
    }

    @Test
    fun `cuando se actualiza el consumo de agua, se guarda correctamente en el repositorio`() = runTest {
        // Given
        val nuevaCantidad = 5
        coEvery {
            consumoAguaRepository.createOrUpdateConsumoAgua(any())
        } returns Unit
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha(any(), any())
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()
        consumoAguaViewModel.actualizarConsumoAgua(nuevaCantidad)
        advanceUntilIdle()

        // Then
        coVerify {
            consumoAguaRepository.createOrUpdateConsumoAgua(match {
                it.cantidad == nuevaCantidad
            })
        }
    }
    @Test
    fun `cuando se cambia la fecha seleccionada, se carga el consumo correspondiente`() = runTest {
        // Given
        val nuevaFecha = LocalDate.now().plusDays(1)
        val consumoEsperado = ConsumoAgua("1", "test-uid", nuevaFecha.toString(), 3)

        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha("test-uid", nuevaFecha)
        } returns flowOf(consumoEsperado)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        consumoAguaViewModel.setFechaSeleccionada(nuevaFecha)

        // Then
        advanceUntilIdle()
        assertEquals(consumoEsperado, consumoAguaViewModel.consumoAgua.value)
    }

    @Test
    fun `cuando no hay peso del usuario, vasos recomendados retorna valor por defecto`() = runTest {
        // Given
        coEvery { perfilRepository.getPerfil("test-uid") } returns null
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha(any(), any())
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()
        val vasosRecomendados = consumoAguaViewModel.calcularVasosRecomendados()

        // Then
        assertEquals(8, vasosRecomendados)
    }
    @Test
    fun `cuando se obtienen vasos mostrados, retorna vasos recomendados más dos`() = runTest {
        // Given
        val perfil = Perfil(uid = "test-uid", pesoActual = 60f)
        coEvery { perfilRepository.getPerfil("test-uid") } returns perfil
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha(any(), any())
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()
        val vasosMostrados = consumoAguaViewModel.obtenerVasosMostrados()

        // Then
        val vasosRecomendados = consumoAguaViewModel.calcularVasosRecomendados()
        assertEquals(vasosRecomendados + 2, vasosMostrados)
    }
    @Test
    fun `cuando no existe consumo para la fecha seleccionada, se crea uno nuevo con cantidad cero`() = runTest {
        // Given
        val fecha = LocalDate.now()
        coEvery {
            consumoAguaRepository.getConsumoAguaPorFecha("test-uid", fecha)
        } returns flowOf(null)

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        consumoAguaViewModel.setFechaSeleccionada(fecha)

        // Then
        advanceUntilIdle()
        assertEquals(0, consumoAguaViewModel.consumoAgua.value?.cantidad)
    }

    @Test
    fun `cuando el peso es cero o negativo en el perfil, el peso usuario es null`() = runTest {
        // Given
        val perfil = Perfil(uid = "test-uid", pesoActual = -5f)
        coEvery { perfilRepository.getPerfil("test-uid") } returns perfil

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()

        // Then
        assertNull(consumoAguaViewModel.pesoUsuario.value)
    }

    @Test
    fun `cuando se actualiza el consumo múltiples veces, se mantiene el último valor`() = runTest {
        // Given
        coEvery {
            consumoAguaRepository.createOrUpdateConsumoAgua(any())
        } returns Unit

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()
        consumoAguaViewModel.actualizarConsumoAgua(3)
        advanceUntilIdle()
        consumoAguaViewModel.actualizarConsumoAgua(5)
        advanceUntilIdle()

        // Then
        coVerify {
            consumoAguaRepository.createOrUpdateConsumoAgua(match {
                it.cantidad == 5
            })
        }
        assertEquals(5, consumoAguaViewModel.consumoAgua.value?.cantidad)
    }

    @Test
    fun `cuando se inicializa el ViewModel sin usuario autenticado, no carga datos`() = runTest {
        // Given
        every { auth.currentUser } returns null

        // When
        consumoAguaViewModel = ConsumoAguaViewModel(consumoAguaRepository, perfilRepository, auth)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { perfilRepository.getPerfil(any()) }
        assertNull(consumoAguaViewModel.pesoUsuario.value)
    }
}