package com.example.proyectohealthy


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
<<<<<<< HEAD
import com.example.proyectohealthy.composables.MainScreen // Asegúrate de que la ruta sea correcta
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme // Asegúrate de que la ruta sea correcta
=======
import com.example.proyectohealthy.composables.AlturaScreen
import com.example.proyectohealthy.composables.CalcularDatosSaludScreen
import com.example.proyectohealthy.composables.ComoConseguirloScreen
import com.example.proyectohealthy.composables.EdadScreen
import com.example.proyectohealthy.composables.EntrenamientoFuerzaScreen
import com.example.proyectohealthy.composables.GeneroScreen
import com.example.proyectohealthy.composables.InicioScreen
import com.example.proyectohealthy.composables.NivelActividadScreen
import com.example.proyectohealthy.composables.ObjetivoNutricionalScreen
import com.example.proyectohealthy.composables.ObjetivoScreen
import com.example.proyectohealthy.composables.PesoObjetivoScreen
import com.example.proyectohealthy.composables.PesoScreen
import com.example.proyectohealthy.composables.ProgresoScreen
import com.example.proyectohealthy.composables.SummaryScreen
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme
import com.example.proyectohealthy.viewmodels.NutricionViewModel
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel
>>>>>>> 680887606a2737c1ac5a80d8424abe3aadbe9428

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoHealthyTheme {
                // Crea un NavController para la navegación
                val navController = rememberNavController()

                // Superficie que usará el color de fondo según el tema
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(navController) // Llama a MainScreen y pasa el navController
                }
            }
        }
    }
}
<<<<<<< HEAD
=======
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
        composable("progreso") {
            ProgresoScreen(
                navController = navController,
                userSelectionsViewModel = userSelectionsViewModel,
                nutricionViewModel = nutricionViewModel
            )
        }
        composable("objetivoNutricional") {
            ObjetivoNutricionalScreen(
                nutricionViewModel = nutricionViewModel
            )
        }
    }
}


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
*/






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
}
>>>>>>> 680887606a2737c1ac5a80d8424abe3aadbe9428
