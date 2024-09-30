<<<<<<< HEAD
package com.example.proyectohealthy.composables//package com.example.proyectohealthy.composables
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.patrykandpatrick.vico.core.entry.entryModelOf // Para el modelo de entradas del gráfico
//import com.patrykandpatrick.vico.compose.chart.Chart // Para el componente del gráfico
//
//// ViewModel y estados
//import androidx.compose.runtime.collectAsState
//import com.example.proyectohealthy.UiState
//import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
//import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
//import com.patrykandpatrick.vico.compose.chart.column.columnChart
//import kotlinx.coroutines.launch
//import viewmodels.NutricionViewModel
//
//@Composable
//fun ObjetivoNutricionalScreen(
//    nutricionViewModel: NutricionViewModel = viewModel()
//) {
//    val uiState by nutricionViewModel.uiState.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .background(Color(0xFFF0F4F8))
//    ) {
//        Text(
//            text = "Plan Nutricional",
//            style = MaterialTheme.typography.headlineMedium.copy(
//                fontFamily = FontFamily.Serif,
//                fontWeight = FontWeight.Bold,
//                fontSize = 28.sp,
//                color = Color(0xFF2C3E50)
//            ),
//            modifier = Modifier.padding(bottom = 16.dp, top = 50.dp)
//        )
//
//        when (val state = uiState) {
//            is UiState.Loading -> CircularProgressIndicator()
//            is UiState.Success.NutritionPlanGenerated -> {
//                val planNutricional = state.data
//                DisplayNutritionPlan(planNutricional)
//            }
//            is UiState.Success.TextGenerated -> {
//                Text("Texto generado: ${state.outputText}")
//            }
//            is UiState.Error -> Text("Error: ${state.errorMessage}")
//            is UiState.Initial -> Text("Esperando datos...")
//        }
//    }
//}
//
//@Composable
//fun DisplayNutritionPlan(planNutricional: NutricionViewModel.PlanNutricional) {
//    Text("Requerimiento Energético: ${planNutricional.requerimientoEnergetico} calorías")
//
//    Spacer(modifier = Modifier.height(16.dp))
//
//    MacronutrientesChart(
//        proteinas = planNutricional.proteinas,
//        carbohidratos = planNutricional.carbohidratos,
//        grasas = planNutricional.grasas
//    )
//
//    Spacer(modifier = Modifier.height(16.dp))
//
//    Text("Plan Detallado:", style = MaterialTheme.typography.titleMedium)
//    Text(planNutricional.planDetallado)
//}
//
//@Composable
//fun MacronutrientesChart(proteinas: Float, carbohidratos: Float, grasas: Float) {
//    val chartEntryModel = entryModelOf(
//        0 to proteinas,
//        1 to carbohidratos,
//        2 to grasas
//    )
//
//    Chart(
//        chart = columnChart(),
//        model = chartEntryModel,
//        startAxis = startAxis(),
//        bottomAxis = bottomAxis(
//            valueFormatter = { value, _ ->
//                when (value.toInt()) {
//                    0 -> "Proteínas"
//                    1 -> "Carbohidratos"
//                    2 -> "Grasas"
//                    else -> ""
//                }
//            }
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//    )
//}
=======
package com.example.proyectohealthy.composables
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.core.entry.entryModelOf // Para el modelo de entradas del gráfico
import com.patrykandpatrick.vico.compose.chart.Chart // Para el componente del gráfico

// ViewModel y estados
import androidx.compose.runtime.collectAsState
import com.example.proyectohealthy.UiState
import com.example.proyectohealthy.viewmodels.NutricionViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import kotlinx.coroutines.launch

@Composable
fun ObjetivoNutricionalScreen(
    nutricionViewModel: NutricionViewModel = viewModel()
) {
    val uiState by nutricionViewModel.uiState.collectAsState()

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
>>>>>>> 680887606a2737c1ac5a80d8424abe3aadbe9428
