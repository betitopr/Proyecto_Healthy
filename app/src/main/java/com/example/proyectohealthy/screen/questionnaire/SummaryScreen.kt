package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        currentPerfil?.let { perfil ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Información Personal
                                SectionTitle("Información Personal")
                                InfoRow("Género", perfil.genero)
                                InfoRow("Edad", "${perfil.edad} años")

                                // Medidas
                                Spacer(modifier = Modifier.height(4.dp))
                                SectionTitle("Medidas y Objetivos")

                                // Altura según preferencias
                                val alturaTexto = when (perfil.unidadesPreferences.sistemaAltura) {
                                    "Imperial (ft/in)" -> {
                                        val totalPulgadas = (perfil.altura / 2.54).toInt()
                                        val pies = totalPulgadas / 12
                                        val pulgadas = totalPulgadas % 12
                                        "$pies' $pulgadas\""
                                    }
                                    else -> "${perfil.altura.toInt()} cm"
                                }
                                InfoRow("Altura", alturaTexto)

                                // Pesos
                                val sistemaPeso = perfil.unidadesPreferences.sistemaPeso
                                val pesoActualTexto = when (sistemaPeso) {
                                    "Imperial (lb)" -> {
                                        val pesoLb = perfil.pesoActual * 2.20462f
                                        "${"%.1f".format(pesoLb)} lb"
                                    }
                                    else -> "${"%.1f".format(perfil.pesoActual)} kg"
                                }
                                val pesoObjetivoTexto = when (sistemaPeso) {
                                    "Imperial (lb)" -> {
                                        val pesoLb = perfil.pesoObjetivo * 2.20462f
                                        "${"%.1f".format(pesoLb)} lb"
                                    }
                                    else -> "${"%.1f".format(perfil.pesoObjetivo)} kg"
                                }

                                InfoRow("Peso Actual", pesoActualTexto)
                                InfoRow("Peso Objetivo", pesoObjetivoTexto)

                                // IMC
                                val alturaMetros = perfil.altura / 100f
                                val imc = perfil.pesoActual / (alturaMetros * alturaMetros)
                                InfoRow("IMC", "${"%.1f".format(imc)}")

                                // Plan de Actividad
                                Spacer(modifier = Modifier.height(4.dp))
                                SectionTitle("Plan de Actividad")
                                InfoRow("Objetivo", perfil.objetivo)
                                InfoRow("Nivel de Actividad", perfil.nivelActividad)
                                InfoRow("Entrenamiento de Fuerza", perfil.entrenamientoFuerza)
                                InfoRow("Método", perfil.comoConseguirlo)
                            }
                        } ?: Text(
                            "No se ha podido cargar la información del perfil.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Espacio adicional al final para evitar que el contenido quede detrás del BottomBar
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = MaterialTheme.typography.titleMedium.fontSize * 0.9
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.9
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.9
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}