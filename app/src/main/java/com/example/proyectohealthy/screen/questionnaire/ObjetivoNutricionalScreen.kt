package com.example.proyectohealthy.screen.questionnaire


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.ui.viewmodel.NutricionUiState
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel
import kotlinx.coroutines.delay

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Plan Nutricional",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (uiState) {
                is NutricionUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator()
                                Text("Calculando tu plan personalizado...")
                            }
                        }
                    }
                }

                is NutricionUiState.Success -> {
                    val planNutricional = (uiState as NutricionUiState.Success).planNutricional
                    currentPerfil?.let { perfil ->
                        // Card de Macronutrientes
                        item {
                            MacronutrientesCard(planNutricional, perfil.objetivo)
                        }

                        // Gráfico de distribución
                        item {
                            DistribucionCard(planNutricional)
                        }

                        // Card de Desglose Diario
                        item {
                            DesgloseDiarioCard(planNutricional, perfil)
                        }

                        // Card de Recomendaciones
                        item {
                            RecomendacionesCard(planNutricional, perfil)
                        }
                    }
                }

                is NutricionUiState.Error -> {
                    item {
                        ErrorCard((uiState as NutricionUiState.Error).message)
                    }
                }

                else -> { /* Estado inicial */ }
            }
        }
    }
}

@Composable
private fun MacronutrientesCard(
    planNutricional: NutricionViewModel.PlanNutricional,
    objetivo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Distribución de Macronutrientes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Mostrar cada macronutriente con su barra de progreso
            MacronutrienteProgress(
                "0->Proteínas",
                planNutricional.proteinas,
                planNutricional.requerimientoEnergetico * 0.3f,
                MaterialTheme.colorScheme.primary
            )
            MacronutrienteProgress(
                "1->Carbohidratos",
                planNutricional.carbohidratos,
                planNutricional.requerimientoEnergetico * 0.4f,
                MaterialTheme.colorScheme.secondary
            )
            MacronutrienteProgress(
                "2->Grasas",
                planNutricional.grasas,
                planNutricional.requerimientoEnergetico * 0.3f,
                MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun DistribucionCard(planNutricional: NutricionViewModel.PlanNutricional) {
    var isLoading by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Distribución Calórica",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                    LaunchedEffect(Unit) {
                        delay(1000)
                        isLoading = false
                    }
                } else {
                    val chartEntryModel = entryModelOf(
                        0 to planNutricional.proteinas,
                        1 to planNutricional.carbohidratos,
                        2 to planNutricional.grasas
                    )

                    Chart(
                        chart = columnChart(),
                        model = chartEntryModel,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DesgloseDiarioCard(
    planNutricional: NutricionViewModel.PlanNutricional,
    perfil: Perfil
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Tu Plan Diario",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "Calorías Diarias: ${planNutricional.requerimientoEnergetico} kcal",
                style = MaterialTheme.typography.bodyLarge
            )

            // Mostrar objetivos en las unidades seleccionadas
            val sistemaPeso = perfil.unidadesPreferences.sistemaPeso
            val pesoObjetivoTexto = if (sistemaPeso == "Imperial (lb)") {
                "${"%.1f".format(perfil.pesoObjetivo * 2.20462f)} lb"
            } else {
                "${"%.1f".format(perfil.pesoObjetivo)} kg"
            }

            Text(
                "Peso objetivo: $pesoObjetivoTexto",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Tiempo estimado: ${planNutricional.tiempoEstimado} semanas",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun RecomendacionesCard(
    planNutricional: NutricionViewModel.PlanNutricional,
    perfil: Perfil
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Recomendaciones Personalizadas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val recomendaciones = when (perfil.objetivo) {
                "Perder peso" -> listOf(
                    "Mantén un déficit calórico controlado",
                    "Prioriza proteínas en cada comida",
                    "Realiza ejercicio regular",
                    "Mantén un registro de tu progreso"
                )
                "Ganar peso" -> listOf(
                    "Consume calorías extra de forma saludable",
                    "Aumenta el consumo de proteínas",
                    "Incluye snacks nutritivos",
                    "Combina con entrenamiento de fuerza"
                )
                else -> listOf(
                    "Mantén un balance calórico",
                    "Conserva hábitos saludables",
                    "Realiza actividad física regular",
                    "Monitorea tu peso semanalmente"
                )
            }

            recomendaciones.forEach { recomendacion ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(recomendacion)
                }
            }
        }
    }
}

@Composable
private fun MacronutrienteProgress(
    nombre: String,
    valor: Float,
    objetivo: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre)
            Text("${valor.toInt()}g / ${objetivo.toInt()}g")
        }
        LinearProgressIndicator(
            progress = { (valor / objetivo).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
        )
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}