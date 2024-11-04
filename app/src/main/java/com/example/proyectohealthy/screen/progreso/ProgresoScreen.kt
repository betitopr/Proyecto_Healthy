package com.example.proyectohealthy.screen.progreso

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.ui.viewmodel.ProgresoViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProgresoScreen(
    navController: NavController,
    viewModel: ProgresoViewModel = hiltViewModel(),
    perfilViewModel: PerfilViewModel
) {
    val registrosDiarios by viewModel.registrosDiarios.collectAsState()
    val progresoHaciaObjetivo by viewModel.progresoHaciaObjetivo.collectAsState()
    val periodoSeleccionado by viewModel.periodoSeleccionado.collectAsState()
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Mi Progreso",
                userPhotoUrl = currentPerfil?.perfilImagen
            )
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                BarraProgresoObjetivo(
                    progreso = progresoHaciaObjetivo,
                    pesoInicial = currentPerfil?.pesoActual ?: 0f,
                    pesoObjetivo = currentPerfil?.pesoObjetivo ?: 0f,
                    objetivo = currentPerfil?.objetivo ?: "",
                    sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"
                )
            }

            item {
                SelectorPeriodo(
                    periodoSeleccionado = periodoSeleccionado,
                    onPeriodoSelected = { viewModel.cambiarPeriodo(it) }
                )
            }

            item { GraficoCalorias(registrosDiarios) }
            item { GraficoProteinas(registrosDiarios) }
            item { GraficoCarbohidratos(registrosDiarios) }
            item { GraficoGrasas(registrosDiarios) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarraProgresoObjetivo(
    progreso: Float,
    objetivo: String,
    pesoInicial: Float,
    pesoObjetivo: Float,
    sistemaPeso: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Progreso hacia tu objetivo",
                style = MaterialTheme.typography.titleMedium
            )

            LinearProgressIndicator(
                progress = { progreso / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = when {
                    progreso >= 100f -> MaterialTheme.colorScheme.primary
                    progreso >= 50f -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.tertiary
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formatoPeso = if (sistemaPeso.contains("Imperial")) {
                    { peso: Float -> "${(peso * 2.20462f).format(1)} lb" }
                } else {
                    { peso: Float -> "${peso.format(1)} kg" }
                }

                Text(
                    text = "Inicial: ${formatoPeso(pesoInicial)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Objetivo: ${formatoPeso(pesoObjetivo)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${progreso.format(1)}% completado",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            // Mensaje motivacional basado en el progreso
            val mensaje = when {
                progreso >= 100f -> "¡Felicitaciones! Has alcanzado tu objetivo"
                progreso >= 75f -> "¡Excelente progreso! Ya casi llegas"
                progreso >= 50f -> "¡Vas por buen camino! Sigue así"
                progreso >= 25f -> "Buen comienzo, mantén el impulso"
                else -> "El primer paso es el más importante"
            }

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)