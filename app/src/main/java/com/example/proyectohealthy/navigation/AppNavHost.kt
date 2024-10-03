package com.example.proyectohealthy.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.proyectohealthy.screen.HomeScreen
import com.example.proyectohealthy.screen.auth.LoginScreen
import com.example.proyectohealthy.screen.auth.RegisterScreen
import com.example.proyectohealthy.screen.splash.ProfileScreen
import com.example.proyectohealthy.screen.splash.SplashScreen
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.viewmodels.NutricionViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val perfilViewModel: PerfilViewModel = hiltViewModel()
    val nutricionViewModel: NutricionViewModel = hiltViewModel()

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                when (authState) {
                    is AuthViewModel.AuthState.Authenticated -> {
                        navController.navigate("questionnaire") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate("auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            })
        }
        composable("auth") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("questionnaire") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("auth") },
                onNavigateToHome = {
                    navController.navigate("questionnaire") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("questionnaire") {
            QuestionnaireHost(
                perfilViewModel = perfilViewModel,
                nutricionViewModel = nutricionViewModel,
                onFinishQuestionnaire = {
                    navController.navigate("home") {
                        popUpTo("questionnaire") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                perfilViewModel = perfilViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = perfilViewModel
            )
        }
    }
}

/*
@Composable
fun QuestionnaireNavHost(
    navController: NavHostController = rememberNavController(),
    userSelectionsViewModel: UserSelectionsViewModel,
    nutricionViewModel: NutricionViewModel,
    onFinishQuestionnaire: () -> Unit // Añadido este parámetro
) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            InicioScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("objetivo") }
            )
        }
        composable("objetivo") {
            ObjetivoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("edad") }
            )
        }
        composable("edad") {
            EdadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("genero") }
            )
        }
        composable("genero") {
            GeneroScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("altura") }
            )
        }
        composable("altura") {
            AlturaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("peso") }
            )
        }
        composable("peso") {
            PesoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("pesoObjetivo") }
            )
        }
        composable("pesoObjetivo") {
            PesoObjetivoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("nivelActividad") }
            )
        }
        composable("nivelActividad") {
            NivelActividadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("entrenamientoFuerza") }
            )
        }
        composable("entrenamientoFuerza") {
            EntrenamientoFuerzaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("summary") }
            )
        }
        composable("objetivoNutricional") {
            ObjetivoNutricionalScreen(
                nutricionViewModel = nutricionViewModel
            )
        }
        composable("comoConseguirlo") {
            ComoConseguirloScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("progreso") }
            )
        }
        composable("calcularDatosSalud") {
            CalcularDatosSaludScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("objetivoNutricional") }
            )
        }
        composable("summary") {
            SummaryScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                navController = navController,
                onEditClick = { /* Navegar a la pantalla que se quiere editar / },
                onCalcularDatosClick = { navController.navigate("calcularDatosSalud") }
            )
        }
        composable("progreso") {
            ProgresoScreen(
                navController = navController,
                userSelectionsViewModel = userSelectionsViewModel,
                nutricionViewModel = nutricionViewModel,
                //onFinishQuestionnaire = onFinishQuestionnaire // Pasamos el parámetro aquí
            )
        }
    }
}/



4 / 4

Basándome en tu código y los requisitos que mencionas, aquí tienes una versión actualizada y optimizada de tu AppNavigation que incluye el manejo de autenticación y el flujo del cuestionario:

kotlin

Copy
package com.example.proyectohealthy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectohealthy.screen.HomeScreen
import com.example.proyectohealthy.screen.auth.LoginScreen
import com.example.proyectohealthy.screen.auth.RegisterScreen
import com.example.proyectohealthy.screen.splash.ProfileScreen
import com.example.proyectohealthy.screen.splash.SplashScreen
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.viewmodels.NutricionViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val perfilViewModel: PerfilViewModel = hiltViewModel()
    val nutricionViewModel: NutricionViewModel = hiltViewModel()

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                when (authState) {
                    is AuthViewModel.AuthState.Authenticated -> {
                        // Verificar si el perfil está completo
                        if (perfilViewModel.isProfileComplete()) {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            navController.navigate("questionnaire") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                    else -> {
                        navController.navigate("auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            })
        }
        composable("auth") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("questionnaire") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("auth") },
                onNavigateToHome = {
                    navController.navigate("questionnaire") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        composable("questionnaire") {
            QuestionnaireHost(
                perfilViewModel = perfilViewModel,
                nutricionViewModel = nutricionViewModel,
                onFinishQuestionnaire = {
                    navController.navigate("home") {
                        popUpTo("questionnaire") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                perfilViewModel = perfilViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = perfilViewModel
            )
        }
    }
}





/*
@Composable
fun QuestionnaireNavHost(
    navController: NavHostController = rememberNavController(),
    userSelectionsViewModel: UserSelectionsViewModel,
    nutricionViewModel: NutricionViewModel,
    onFinishQuestionnaire: () -> Unit // Añadido este parámetro
) {
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            InicioScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("objetivo") }
            )
        }
        composable("objetivo") {
            ObjetivoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("edad") }
            )
        }
        composable("edad") {
            EdadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("genero") }
            )
        }
        composable("genero") {
            GeneroScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("altura") }
            )
        }
        composable("altura") {
            AlturaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("peso") }
            )
        }
        composable("peso") {
            PesoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("pesoObjetivo") }
            )
        }
        composable("pesoObjetivo") {
            PesoObjetivoScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("nivelActividad") }
            )
        }
        composable("nivelActividad") {
            NivelActividadScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("entrenamientoFuerza") }
            )
        }
        composable("entrenamientoFuerza") {
            EntrenamientoFuerzaScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("summary") }
            )
        }
        composable("objetivoNutricional") {
            ObjetivoNutricionalScreen(
                nutricionViewModel = nutricionViewModel
            )
        }
        composable("comoConseguirlo") {
            ComoConseguirloScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("progreso") }
            )
        }
        composable("calcularDatosSalud") {
            CalcularDatosSaludScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                onContinueClick = { navController.navigate("objetivoNutricional") }
            )
        }
        composable("summary") {
            SummaryScreen(
                userSelectionsViewModel = userSelectionsViewModel,
                navController = navController,
                onEditClick = { /* Navegar a la pantalla que se quiere editar */ },
                onCalcularDatosClick = { navController.navigate("calcularDatosSalud") }
            )
        }
        composable("progreso") {
            ProgresoScreen(
                navController = navController,
                userSelectionsViewModel = userSelectionsViewModel,
                nutricionViewModel = nutricionViewModel,
                //onFinishQuestionnaire = onFinishQuestionnaire // Pasamos el parámetro aquí
            )
        }
    }
}*/