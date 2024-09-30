package com.example.proyectohealthy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.composables.MainScreen // Asegúrate de que la ruta sea correcta
import com.example.proyectohealthy.ui.theme.ProyectoHealthyTheme // Asegúrate de que la ruta sea correcta

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
