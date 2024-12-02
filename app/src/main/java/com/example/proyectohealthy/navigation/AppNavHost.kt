package com.example.proyectohealthy.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.screen.AlimentosScreen

import com.example.proyectohealthy.screen.HomeScreen
import com.example.proyectohealthy.screen.auth.LoginScreen
import com.example.proyectohealthy.screen.auth.RegisterScreen
import com.example.proyectohealthy.screen.ProfileScreen
import com.example.proyectohealthy.screen.TeamsScreen//teams
import com.example.proyectohealthy.screen.progreso.ProgresoScreen
import com.example.proyectohealthy.screen.recetas.RecetasScreen
import com.example.proyectohealthy.screen.splash.SplashScreen
import com.example.proyectohealthy.ui.viewmodel.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val perfilViewModel: PerfilViewModel = hiltViewModel()
    val nutricionViewModel: NutricionViewModel = hiltViewModel()
    val registroComidaViewModel: RegistroComidaViewModel = hiltViewModel()
    val alimentoViewModel: AlimentoViewModel = hiltViewModel()
    val misAlimentosViewModel: MisAlimentosViewModel = hiltViewModel()
    val consumoAguaViewModel: ConsumoAguaViewModel = hiltViewModel()
    val ejercicioViewModel: EjercicioViewModel = hiltViewModel()


    val authState by authViewModel.authState.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()


    LaunchedEffect(authState, currentPerfil) {
        perfilViewModel.checkPerfilCompleto()
        if (!hasNavigated) {
            when (authState) {
                is AuthViewModel.AuthState.Authenticated -> {
                    currentPerfil?.let { perfil ->
                        if (perfil.perfilCompleto) {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                            hasNavigated = true
                        } else {
                            navController.navigate("questionnaire") {
                                popUpTo("splash") { inclusive = true }
                            }
                            hasNavigated = true
                        }
                    }
                }
                is AuthViewModel.AuthState.NotAuthenticated -> {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                    hasNavigated = true
                }
                else -> {}
            }
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                // La navegación se maneja en el LaunchedEffect de arriba
            })
        }
        composable("auth") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    if (currentPerfil?.perfilCompleto == true) {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        navController.navigate("questionnaire") {
                            popUpTo("auth") { inclusive = true }
                        }
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
                perfilViewModel = perfilViewModel,
                registroComidaViewModel = registroComidaViewModel,
                alimentoViewModel = alimentoViewModel,
                misAlimentosViewModel = misAlimentosViewModel,
                consumoAguaViewModel = consumoAguaViewModel,
                ejercicioViewModel = ejercicioViewModel
            )
        }
        composable("alimentos") {
            AlimentosScreen(
                navController = navController,
                perfilViewModel = perfilViewModel
                )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                perfilViewModel = perfilViewModel,
                authViewModel = authViewModel
            )
        }
        composable("recetas") {
            RecetasScreen(perfilViewModel = perfilViewModel,
                navController = navController,)
        }

        composable("Teams") {
            val teamsViewModel: TeamsViewModel = hiltViewModel()
            TeamsScreen(perfilViewModel = perfilViewModel,
                navController = navController,
                viewModel = teamsViewModel)
        }

        composable("progreso") {
            ProgresoScreen(
                navController = navController,
                perfilViewModel = perfilViewModel,
            )
        }
    }
}