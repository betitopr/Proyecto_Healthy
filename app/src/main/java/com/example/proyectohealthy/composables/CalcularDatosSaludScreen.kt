/*package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun CalcularDatosSaludScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit
) {
    // Aquí podrías realizar los cálculos necesarios basados en los datos del ViewModel
    val altura by userSelectionsViewModel.altura.collectAsState()
    val peso by userSelectionsViewModel.peso.collectAsState()
    val edad by remember { mutableStateOf(userSelectionsViewModel.edad) }
    val objetivo by remember { mutableStateOf(userSelectionsViewModel.objetivo) }

    // Ejemplo de cálculo (puedes personalizar según tus necesidades)
    val imc = if (peso > 0f && altura > 0f) peso / ((altura / 100f) * (altura / 100f)) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cálculo de Datos de Salud",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "IMC: ${"%.2f".format(imc)}")
        Text(text = "Edad: $edad años")
        Text(text = "Objetivo: $objetivo")

        // Aquí puedes agregar más cálculos y mostrar los resultados correspondientes

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* Acción para volver a la pantalla anterior o realizar otra acción */ }) {
            Text(text = "Volver")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onContinueClick) {
            Text(text = "Continuar a Progreso")
        }
    }
}
*/