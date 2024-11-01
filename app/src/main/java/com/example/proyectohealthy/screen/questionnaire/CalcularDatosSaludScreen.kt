/*package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@Composable
fun CalcularDatosSaludScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val imc = currentPerfil?.let { perfil ->
        if (perfil.pesoActual > 0f && perfil.altura > 0f) {
            perfil.pesoActual / ((perfil.altura / 100f) * (perfil.altura / 100f))
        } else {
            0f
        }
    } ?: 0f

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
        currentPerfil?.let { perfil ->
            Text(text = "Edad: ${perfil.edad} años")
            Text(text = "Objetivo: ${perfil.objetivo}")
        }

        // Aquí puedes agregar más cálculos y mostrar los resultados correspondientes

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onPreviousClick) {
                Text("Anterior")
            }
            Button(onClick = onNextClick) {
                Text("Siguiente")
            }
        }
    }
}*/