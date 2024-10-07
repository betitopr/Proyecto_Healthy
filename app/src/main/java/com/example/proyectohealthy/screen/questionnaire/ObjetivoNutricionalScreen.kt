package com.example.proyectohealthy.screen.questionnaire


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.example.proyectohealthy.UiState
import com.example.proyectohealthy.ui.viewmodel.NutricionUiState
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel

@Composable
fun ObjetivoNutricionalScreen(
    perfilViewModel: PerfilViewModel,
    nutricionViewModel: NutricionViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onFinishQuestionnaire: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val uiState by nutricionViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = currentPerfil) {
        nutricionViewModel.obtenerPlanNutricional()
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onPreviousClick) {
                        Text("Anterior")
                    }
                    Button(onClick = onFinishQuestionnaire) {
                        Text("Finalizar")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Plan Nutricional",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF2C3E50)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
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
        }
    }
}

@Composable
fun DisplayNutritionPlan(planNutricional: NutricionViewModel.PlanNutricional) {
    Column {
        Text("Requerimiento Energético: ${planNutricional.requerimientoEnergetico} calorías")
        Spacer(modifier = Modifier.height(16.dp))
        MacronutrientesChart(
            proteinas = planNutricional.proteinas,
            carbohidratos = planNutricional.carbohidratos,
            grasas = planNutricional.grasas
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Plan Detallado:", style = MaterialTheme.typography.titleMedium)
        Text(planNutricional.planDetallado)
    }
}

@Composable
fun MacronutrientesChart(proteinas: Float, carbohidratos: Float, grasas: Float) {
    val chartEntryModel = entryModelOf(
        0 to proteinas,
        1 to carbohidratos,
        2 to grasas
    )

    Chart(
        chart = columnChart(),
        model = chartEntryModel,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(
            valueFormatter = { value, _ ->
                when (value.toInt()) {
                    0 -> "Proteínas"
                    1 -> "Carbohidratos"
                    2 -> "Grasas"
                    else -> ""
                }
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}