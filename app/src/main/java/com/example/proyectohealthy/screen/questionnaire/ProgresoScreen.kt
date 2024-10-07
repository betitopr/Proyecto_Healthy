package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionUiState
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun ProgresoScreen(
    perfilViewModel: PerfilViewModel,
    nutricionViewModel: NutricionViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val uiState by nutricionViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = currentPerfil) {
        nutricionViewModel.obtenerPlanNutricional()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF0F4F8))
    ) {
        Text(
            text = "Progreso de Peso",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF2C3E50)
            ),
            modifier = Modifier.padding(bottom = 16.dp, top = 50.dp)
        )

        when (val state = uiState) {
            is NutricionUiState.Loading -> CircularProgressIndicator()
            is NutricionUiState.Success -> {
                val planNutricional = state.planNutricional
                currentPerfil?.let { perfil ->
                    WeightProgressChart(
                        currentWeight = perfil.pesoActual,
                        targetWeight = perfil.pesoObjetivo,
                        tiempoEstimado = planNutricional.tiempoEstimado
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("IMC: ${planNutricional.imc}")
                    Text("TMB: ${planNutricional.tmb} calorías")
                    Text("Tiempo estimado: ${planNutricional.tiempoEstimado} días")

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is NutricionUiState.Error -> Text("Error: ${state.message}")
            NutricionUiState.Initial -> Text("Esperando datos...")
        }
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

@Composable
fun WeightProgressChart(currentWeight: Float, targetWeight: Float, tiempoEstimado: Int) {
    val chartEntryModel = entryModelOf(
        0f to currentWeight,
        tiempoEstimado.toFloat() to targetWeight
    )

    Chart(
        chart = lineChart(),
        model = chartEntryModel,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}