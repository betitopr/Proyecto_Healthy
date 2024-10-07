package com.example.proyectohealthy


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.navigation.AppNavigation
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manejar excepciones no capturadas
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("MainActivity", "Excepción no capturada: ${e.message}", e)
            mostrarToastError("Error inesperado: ${e.message}")
        }


        try {
            setContent {
                ProyectoHealthyTheme {
                    AppNavigationWrapper()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al iniciar la app: ${e.message}", e)
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
