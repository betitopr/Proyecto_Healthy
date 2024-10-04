package com.example.proyectohealthy.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.screen.questionnaire.CalcularDatosSaludScreen
import com.example.proyectohealthy.screen.questionnaire.ComoConseguirloScreen
import com.example.proyectohealthy.screen.questionnaire.EntrenamientoFuerzaScreen
import com.example.proyectohealthy.screen.questionnaire.InformacionPersonalScreen
import com.example.proyectohealthy.screen.questionnaire.NivelActividadScreen
import com.example.proyectohealthy.screen.questionnaire.ObjetivoNutricionalScreen
import com.example.proyectohealthy.screen.questionnaire.ObjetivoScreen
import com.example.proyectohealthy.screen.questionnaire.PesoObjetivoScreen
import com.example.proyectohealthy.screen.questionnaire.ProgresoScreen
import com.example.proyectohealthy.screen.questionnaire.SummaryScreen
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel

@Composable
fun QuestionnaireHost(
    perfilViewModel: PerfilViewModel,
    nutricionViewModel: NutricionViewModel,
    onFinishQuestionnaire: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "objetivo") {
        composable("objetivo") {
            ObjetivoScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("informacion_personal") },
                onPreviousClick = {  }
            )
        }
        composable("informacion_personal") {
            InformacionPersonalScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("nivel_actividad") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("nivel_actividad") {
            NivelActividadScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("como_conseguirlo") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("como_conseguirlo") {
            ComoConseguirloScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("peso_objetivo") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("peso_objetivo") {
            PesoObjetivoScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("entrenamiento_fuerza") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("entrenamiento_fuerza") {
            EntrenamientoFuerzaScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("summary") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("summary") {
            SummaryScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("calcular_datos_salud") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("calcular_datos_salud") {
            CalcularDatosSaludScreen(
                perfilViewModel = perfilViewModel,
                onNextClick = { navController.navigate("progreso") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("progreso") {
            ProgresoScreen(
                perfilViewModel = perfilViewModel,
                nutricionViewModel = nutricionViewModel,
                onNextClick = { navController.navigate("objetivo_nutricional") },
                onPreviousClick = { navController.popBackStack() }
            )
        }
        composable("objetivo_nutricional") {
            ObjetivoNutricionalScreen(
                perfilViewModel = perfilViewModel,
                nutricionViewModel = nutricionViewModel,
                onNextClick = { navController.navigate("home") },
                onPreviousClick = { navController.popBackStack() },
                onFinishQuestionnaire = onFinishQuestionnaire
            )
        }
    }
}

@Composable
fun QuestionnaireNavigation(
    navController: NavHostController,
    onFinishQuestionnaire: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentRoute != "objetivo") {
            Button(onClick = { navController.popBackStack() }) {
                Text("Anterior")
            }
        }

        if (currentRoute != "objetivo_nutricional") {
            Button(onClick = {
                when (currentRoute) {
                    "objetivo" -> navController.navigate("informacion_personal")
                    "informacion_personal" -> navController.navigate("nivel_actividad")
                    "nivel_actividad" -> navController.navigate("como_conseguirlo")
                    "como_conseguirlo" -> navController.navigate("peso_objetivo")
                    "peso_objetivo" -> navController.navigate("entrenamiento_fuerza")
                    "entrenamiento_fuerza" -> navController.navigate("summary")
                    "summary" -> navController.navigate("calcular_datos_salud")
                    "calcular_datos_salud" -> navController.navigate("progreso")
                    "progreso" -> navController.navigate("objetivo_nutricional")
                }
            }) {
                Text("Siguiente")
            }
        } else {
            Button(onClick = onFinishQuestionnaire) {
                Text("Finalizar")
            }
        }
    }
}