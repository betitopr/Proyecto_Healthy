package com.example.proyectohealthy.screen.questionnaire


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.viewmodels.NutricionViewModel

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

    LaunchedEffect(currentPerfil) {
        currentPerfil?.let { perfil ->
            nutricionViewModel.obtenerPlanNutricional(perfil)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF0F4F8))
    ) {
        Text(
            text = "Plan Nutricional",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF2C3E50)
            ),
            modifier = Modifier.padding(bottom = 16.dp, top = 50.dp)
        )

        when (val state = uiState) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success.NutritionPlanGenerated -> {
                val planNutricional = state.data
                DisplayNutritionPlan(planNutricional)
            }
            is UiState.Success.TextGenerated -> {
                Text("Texto generado: ${state.outputText}")
            }
            is UiState.Error -> Text("Error: ${state.errorMessage}")
            is UiState.Initial -> Text("Esperando datos...")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
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

@Composable
fun DisplayNutritionPlan(planNutricional: NutricionViewModel.PlanNutricional) {
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