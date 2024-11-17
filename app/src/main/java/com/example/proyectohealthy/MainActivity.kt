package com.example.proyectohealthy


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.navigation.AppNavigation
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appLifecycleHandler: AppLifecycleHandler

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Manejar excepciones no capturadas
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("MainActivity", "Excepción no capturada: ${e.message}", e)
            mostrarToastError("Error inesperado: ${e.message}")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        lifecycle.addObserver(appLifecycleHandler)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppNavigationWrapper() {
        val navController = rememberNavController()

        // Manejar la navegación desde el widget de forma inmediata
        LaunchedEffect(Unit) {
            if (intent?.getStringExtra("destination") == "alimentos") {
                // Navegar directamente a alimentos sin pasar por home
                navController.navigate("alimentos") {
                    // Esto evita que pase por home
                    launchSingleTop = true
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        AppNavigation(navController = navController)
    }
}
