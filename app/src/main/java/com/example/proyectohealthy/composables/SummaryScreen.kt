package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import viewmodels.UserSelectionsViewModel

@Composable
fun SummaryScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    navController: NavController, // Asegúrate de pasar el NavController
    onEditClick: () -> Unit,
    onCalcularDatosClick: () -> Unit // Añadido para la acción del botón
) {
    val altura by userSelectionsViewModel.altura.collectAsState()
    val peso by userSelectionsViewModel.peso.collectAsState()
    val pesoObjetivo by userSelectionsViewModel.pesoObjetivo.collectAsState()
    val objetivo by remember { mutableStateOf(userSelectionsViewModel.objetivo) }
    val edad by remember { mutableStateOf(userSelectionsViewModel.edad) }
    val entrenamientoFuerza by remember { mutableStateOf(userSelectionsViewModel.entrenamientoFuerza) }
    val genero by remember { mutableStateOf(userSelectionsViewModel.genero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resumen de Selecciones",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Altura: ${altura.toInt()} cm")
        Text(text = "Peso: ${peso.toInt()} kg")
        Text(text = "Peso Objetivo: ${pesoObjetivo.toInt()} kg") // Mostrar el peso objetivo
        Text(text = "Objetivo: $objetivo")
        Text(text = "Edad: $edad años")
        Text(text = "Entrenamiento de Fuerza: $entrenamientoFuerza")
        Text(text = "Género: $genero")

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            // Botón para retroceder
            Button(onClick = { onEditClick() }) {
                Text(text = "Editar Selecciones")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Botón para calcular datos de salud
            Button(onClick = { onCalcularDatosClick() }) {
                Text(text = "Calcular Datos de Salud")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    val mockViewModel = UserSelectionsViewModel().apply {
        // Establece valores iniciales para la vista previa
        updateAltura(170f)
        updatePeso(70f)
        updatePesoObjetivo(65f) // Establece un peso objetivo para la vista previa
        updateObjetivo("Perder peso")
        updateEdad(25)
        updateEntrenamientoFuerza("Sí")
        updateGenero("Masculino")
    }
    // Para la vista previa, los callbacks pueden ser funciones vacías
    SummaryScreen(
        userSelectionsViewModel = mockViewModel,
        navController = rememberNavController(), // Utilizado para mostrar el preview
        onEditClick = { /* Acción para editar selecciones */ },
        onCalcularDatosClick = { /* Acción para calcular datos de salud */ }
    )
}
