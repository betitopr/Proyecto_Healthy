package com.example.proyectohealthy


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.example.proyectohealthy.composables.AlturaScreen
//import com.example.proyectohealthy.composables.CalcularDatosSaludScreen
//import com.example.proyectohealthy.composables.ComoConseguirloScreen
//import com.example.proyectohealthy.composables.EdadScreen
//import com.example.proyectohealthy.composables.EntrenamientoFuerzaScreen
//import com.example.proyectohealthy.composables.GeneroScreen
//import com.example.proyectohealthy.composables.InicioScreen
//import com.example.proyectohealthy.composables.NivelActividadScreen
//import com.example.proyectohealthy.composables.ObjetivoNutricionalScreen
//import com.example.proyectohealthy.composables.ObjetivoScreen
//import com.example.proyectohealthy.composables.PesoObjetivoScreen
//import com.example.proyectohealthy.composables.PesoScreen
import com.example.proyectohealthy.data.local.AppDatabase
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.navigation.AppNavigation
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.viewmodels.NutricionViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val perfilViewModel: PerfilViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userSelectionsViewModel = UserSelectionsViewModel()
        try {
            setContent {
                ProyectoHealthyTheme {
                    val navController = rememberNavController()
                    val userSelectionsViewModel = remember { UserSelectionsViewModel() }
                    val nutricionViewModel = remember { NutricionViewModel() }
                    AppNavigation(
                        authViewModel = authViewModel,
                        perfilViewModel = perfilViewModel
                    )
                }
            }
        }catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
        }


//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    AppNavGraph(
//                        navController = navController,
//                        userSelectionsViewModel = userSelectionsViewModel,
//                        nutricionViewModel = nutricionViewModel,
//                        modifier = Modifier.padding(innerPadding)
//                    )

//                }


    }
}
@Composable
fun AppNavGraph(
    navController: NavHostController,
    userSelectionsViewModel: UserSelectionsViewModel,
    nutricionViewModel: NutricionViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            InicioScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("objetivo")
                }
            )
        }
        composable("objetivo") {
            ObjetivoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("comoConseguirlo")
                }
            )
        }
        composable("comoConseguirlo") {
            ComoConseguirloScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("edad")
                }
            )
        }
        composable("edad") {
            EdadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("genero")
                }
            )
        }
        composable("genero") {
            GeneroScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("peso")
                }
            )
        }
        composable("peso") {
            PesoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("pesoObjetivo")
                }
            )
        }
        composable("pesoObjetivo") {
            PesoObjetivoScreen(userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("altura")
                })
        }
        composable("altura") {
            AlturaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("nivelActividad")
                }
            )
        }
        composable("nivelActividad") {
            NivelActividadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("entrenamientoFuerza")
                }
            )
        }
        composable("entrenamientoFuerza") {
            EntrenamientoFuerzaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("summary")
                }
            )
        }
        composable("summary") {
            SummaryScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                navController = navController, // Asegúrate de pasar el navController
                onEditClick = {
                    navController.popBackStack() // Opcional, vuelve a la pantalla anterior
                },
                onCalcularDatosClick = {
                    navController.navigate("calcularDatosSalud") // Reemplaza con la ruta correcta
                }
            )
        }
        composable("calcularDatosSalud") {
            CalcularDatosSaludScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = {
                    navController.navigate("progreso")
                }
            )
        }

        composable("objetivoNutricional") {
            ObjetivoNutricionalScreen(
                nutricionViewModel = nutricionViewModel
            )
        }
        composable("progreso") {
            ProgresoScreen(
                navController = navController,
                userSelectionsViewModel = userSelectionsViewModel,
                nutricionViewModel = nutricionViewModel,
//                onFinishQuestionnaire = {
//                    navController.navigate("home") {
//                        popUpTo("questionnaire") { inclusive = true }
//                    }
//                }
            )
        }
    }
}

*/
/*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.proyectohealthy.data.local.AppDatabase
import com.example.proyectohealthy.data.repository.AlimentoRepository
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("UncaughtException", "Unhandled exception", e)
            Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = AlimentoRepository(database.alimentoDao())

            setContent {
                ProyectoHealthyTheme {
                    AlimentosScreen(repository = repository)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}







@Preview(showSystemUi = true)
@Composable
fun appPreview(){
    val navController = rememberNavController()
    val userSelectionsViewModel = remember { UserSelectionsViewModel() }
    val nutricionViewModel = remember { NutricionViewModel() }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppNavGraph(
            navController = navController,
            userSelectionsViewModel = userSelectionsViewModel,
            nutricionViewModel = nutricionViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}*/

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manejo global de excepciones no capturadas en el hilo principal
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("UncaughtException", "Excepción no capturada", e)
            mostrarToastError("Error inesperado: ${e.message}")
        }

        try {
            Firebase.initialize(this)
            Log.d("Firebase", "Firebase inicializado correctamente")
        } catch (e: Exception) {
            Log.e("Firebase", "Error al inicializar Firebase: ${e.message}")
        }

        try {
            setContent {
                ProyectoHealthyTheme {
                    AppNavigationWrapper()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al iniciar la app", e)
            mostrarToastError("Error al iniciar: ${e.message}")
        }
    }

    private fun mostrarToastError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    @Composable
    fun AppNavigationWrapper() {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
    }
}