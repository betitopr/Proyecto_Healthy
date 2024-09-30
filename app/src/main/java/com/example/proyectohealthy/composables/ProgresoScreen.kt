package com.example.proyectohealthy.composables

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectohealthy.UiState
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.proyectohealthy.viewmodels.NutricionViewModel
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel


@Composable
fun ProgresoScreen(
    navController: NavController,
    userSelectionsViewModel: UserSelectionsViewModel,
    nutricionViewModel: NutricionViewModel = viewModel()
) {
    val uiState by nutricionViewModel.uiState.collectAsState()
    val pesoActual by userSelectionsViewModel.peso.collectAsState()
    val pesoObjetivo by userSelectionsViewModel.pesoObjetivo.collectAsState()

    LaunchedEffect(key1 = true) {
        nutricionViewModel.obtenerPlanNutricional(userSelectionsViewModel)
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
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Success.NutritionPlanGenerated -> {
                val planNutricional = state.data
                WeightProgressChart(
                    currentWeight = pesoActual,
                    targetWeight = pesoObjetivo,
                    tiempoEstimado = planNutricional.tiempoEstimado
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("IMC: ${planNutricional.imc}")
                Text("TMB: ${planNutricional.tmb} calorías")
                Text("Tiempo estimado: ${planNutricional.tiempoEstimado} días")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("objetivoNutricional") }) {
                    Text("Ver Plan Nutricional Detallado")
                }
            }
            is UiState.Error -> Text("Error: ${(uiState as UiState.Error).errorMessage}")
            else -> {}
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
@Preview
@Composable
fun PreviewProgresoScreen() {
    val navController = rememberNavController()
    val userSelectionsViewModel = UserSelectionsViewModel()
    ProgresoScreen(navController, userSelectionsViewModel)

}
