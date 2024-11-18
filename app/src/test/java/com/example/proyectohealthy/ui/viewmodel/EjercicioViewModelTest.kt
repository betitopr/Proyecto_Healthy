package com.example.proyectohealthy.ui.viewmodel

import android.content.Context
import android.util.Log
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio
import com.example.proyectohealthy.data.repository.EjercicioRepository
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate


@OptIn(ExperimentalCoroutinesApi::class)
class EjercicioViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var auth: FirebaseAuth

    @RelaxedMockK
    private lateinit var ejercicioRepository: EjercicioRepository

    @RelaxedMockK
    private lateinit var currentUser: FirebaseUser

    private lateinit var ejercicioViewModel: EjercicioViewModel

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        every { auth.currentUser } returns currentUser
        every { currentUser.uid } returns "test-uid"
        coEvery {
            ejercicioRepository.getEjercicios()
        } returns flowOf(emptyList())
        coEvery {
            ejercicioRepository.getRegistrosEjercicioPorFecha(any(), any())
        } returns flowOf(emptyList())
    }

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

    @Test
    fun `cuando se inicializa el ViewModel, carga ejercicios y registros correctamente`() = runTest {
        // Given
        val ejercicios = listOf(
            Ejercicio("1", "Correr", 10),
            Ejercicio("2", "Nadar", 8)
        )
        coEvery { ejercicioRepository.getEjercicios() } returns flowOf(ejercicios)

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()

        // Then
        assertEquals(ejercicios, ejercicioViewModel.ejercicios.value)
    }

    @Test
    fun `cuando se agrega un registro de ejercicio, se actualiza la lista y las calorías`() = runTest {
        // Given
        val ejercicios = listOf(Ejercicio("1", "Correr", 10))
        coEvery { ejercicioRepository.getEjercicios() } returns flowOf(ejercicios)
        coEvery {
            ejercicioRepository.createOrUpdateRegistroEjercicio(any(), any())
        } returns Unit

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()
        ejercicioViewModel.agregarRegistroEjercicio("1", 30)
        advanceUntilIdle()

        // Then
        coVerify {
            ejercicioRepository.createOrUpdateRegistroEjercicio(any(), 300) // 10 calorías * 30 minutos
        }
    }

    @Test
    fun `cuando se cambia la fecha seleccionada, se cargan los registros correspondientes`() = runTest {
        // Given
        val fecha = LocalDate.now().plusDays(1)
        val registros = listOf(
            RegistroEjercicio("1", "test-uid", "1", 30, fecha)
        )
        coEvery {
            ejercicioRepository.getRegistrosEjercicioPorFecha("test-uid", fecha)
        } returns flowOf(registros)

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        ejercicioViewModel.setFechaSeleccionada(fecha)
        advanceUntilIdle()

        // Then
        assertEquals(registros, ejercicioViewModel.registrosEjercicio.value)
    }

    @Test
    fun `cuando se elimina un registro de ejercicio, se actualiza el total de calorías`() = runTest {
        // Given
        val registro = RegistroEjercicio("1", "test-uid", "1", 30, LocalDate.now())
        val ejercicios = listOf(Ejercicio("1", "Correr", 10))

        coEvery { ejercicioRepository.getEjercicios() } returns flowOf(ejercicios)
        coEvery {
            ejercicioRepository.deleteRegistroEjercicio(any(), any(), any(), any())
        } returns Unit

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()
        ejercicioViewModel.eliminarRegistroEjercicio(registro)
        advanceUntilIdle()

        // Then
        coVerify {
            ejercicioRepository.deleteRegistroEjercicio("test-uid", "1", any(), 0)
        }
    }

    @Test
    fun `cuando se crea un nuevo ejercicio, se actualiza la lista de ejercicios`() = runTest {
        // Given
        val nuevoEjercicio = Ejercicio("3", "Ciclismo", 12)
        coEvery {
            ejercicioRepository.createOrUpdateEjercicios(nuevoEjercicio)
        } returns Unit

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        ejercicioViewModel.crearEjercicio(nuevoEjercicio)
        advanceUntilIdle()

        // Then
        coVerify { ejercicioRepository.createOrUpdateEjercicios(nuevoEjercicio) }
    }

    @Test
    fun `cuando no hay usuario autenticado, no se cargan datos`() = runTest {
        // Given
        every { auth.currentUser } returns null

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()

        // Then
        assertEquals(emptyList<RegistroEjercicio>(), ejercicioViewModel.registrosEjercicio.value)
    }

    @Test
    fun `cálculo correcto de calorías quemadas con múltiples registros`() = runTest {
        // Given
        val ejercicios = listOf(
            Ejercicio("1", "Correr", 10),
            Ejercicio("2", "Nadar", 8)
        )
        val registros = listOf(
            RegistroEjercicio("1", "test-uid", "1", 30, LocalDate.now()),
            RegistroEjercicio("2", "test-uid", "2", 45, LocalDate.now())
        )
        coEvery { ejercicioRepository.getEjercicios() } returns flowOf(ejercicios)
        coEvery {
            ejercicioRepository.getRegistrosEjercicioPorFecha(any(), any())
        } returns flowOf(registros)

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()

        // Then
        // Calorías esperadas: (10 cal * 30 min) + (8 cal * 45 min) = 300 + 360 = 660
        assertEquals(660, ejercicioViewModel.caloriasQuemadas.value)
    }

    @Test
    fun `obtener ejercicio por ID retorna el ejercicio correcto`() = runTest {
        // Given
        val ejercicio = Ejercicio("1", "Correr", 10)
        coEvery {
            ejercicioRepository.getEjercicioById("1")
        } returns ejercicio

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        val resultado = ejercicioViewModel.getEjercicioById("1")

        // Then
        assertEquals(ejercicio, resultado)
    }

    @Test
    fun `cuando falla la carga de registros, mantiene la lista vacía`() = runTest {
        // Given
        coEvery {
            ejercicioRepository.getRegistrosEjercicioPorFecha(any(), any())
        } throws Exception("Error de red")

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()

        // Then
        assertEquals(emptyList<RegistroEjercicio>(), ejercicioViewModel.registrosEjercicio.value)
    }

    @Test
    fun `actualización de calorías quemadas actualiza el estado y el repositorio`() = runTest {
        // Given
        val ejercicios = listOf(Ejercicio("1", "Correr", 10))
        val registros = listOf(
            RegistroEjercicio("1", "test-uid", "1", 30, LocalDate.now())
        )
        coEvery { ejercicioRepository.getEjercicios() } returns flowOf(ejercicios)
        coEvery {
            ejercicioRepository.getRegistrosEjercicioPorFecha(any(), any())
        } returns flowOf(registros)
        coEvery {
            ejercicioRepository.actualizarCaloriasQuemadas(any(), any(), any())
        } returns Unit

        // When
        ejercicioViewModel = EjercicioViewModel(ejercicioRepository, auth)
        advanceUntilIdle()

        // Then
        assertEquals(300, ejercicioViewModel.caloriasQuemadas.value) // 10 cal * 30 min
        coVerify {
            ejercicioRepository.actualizarCaloriasQuemadas("test-uid", any(), 300)
        }
    }
}