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
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel


@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val perfilViewModel: PerfilViewModel = hiltViewModel()
    val nutricionViewModel: NutricionViewModel = hiltViewModel()

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                //No hacemos nada aquí, ya que la navegación a "questionnaire" o "home"
                //se maneja en otros lugares
            }
            is AuthViewModel.AuthState.NotAuthenticated -> {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {}
        }
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
                perfilViewModel = perfilViewModel,
                authViewModel = authViewModel
            )
        }
    }
}


