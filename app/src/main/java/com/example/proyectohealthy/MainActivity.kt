package com.example.proyectohealthy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import viewmodels.NutricionViewModel
import viewmodels.UserSelectionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userSelectionsViewModel = UserSelectionsViewModel()
        setContent {
            ProyectoHealthyTheme {
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
        }
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

