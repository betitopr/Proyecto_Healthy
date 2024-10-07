package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Selecciones") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            currentPerfil?.let { perfil ->
                InfoRow("Género", perfil.genero)
                InfoRow("Edad", "${perfil.edad} años")
                InfoRow("Altura", "${perfil.altura.toInt()} cm")
                InfoRow("Peso Actual", "${perfil.pesoActual.toInt()} kg")
                InfoRow("Peso Objetivo", "${perfil.pesoObjetivo.toInt()} kg")
                InfoRow("Objetivo", perfil.objetivo)
                InfoRow("Nivel de Actividad", perfil.nivelActividad)
                InfoRow("Entrenamiento de Fuerza", perfil.entrenamientoFuerza)
                InfoRow("Cómo Conseguirlo", perfil.comoConseguirlo)
            } ?: Text("No se ha podido cargar la información del perfil.", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.weight(1f))

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
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}