package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionViewModel
import com.example.proyectohealthy.ui.viewmodel.NutricionUiState
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Plan Personalizado") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar {
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Sección de Resumen
                ResumenCard(currentPerfil, uiState)
            }

            item {
                // Sección de Progreso Proyectado
                ProgresoProyectadoCard(currentPerfil, uiState)
            }

            item {
                // Sección de Consejos
                ConsejosCard(currentPerfil)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ResumenCard(perfil: Perfil?, uiState: NutricionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tu Objetivo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            perfil?.let {
                val mensaje = when (it.objetivo) {
                    "Perder peso" -> "Estás en camino a un peso más saludable"
                    "Ganar peso" -> "Construyendo un cuerpo más fuerte"
                    else -> "Manteniendo un estilo de vida saludable"
                }
                Text(mensaje, style = MaterialTheme.typography.bodyLarge)

                if (uiState is NutricionUiState.Success) {
                    Text(
                        "Tiempo estimado: ${uiState.planNutricional.tiempoEstimado} semanas",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgresoProyectadoCard(perfil: Perfil?, uiState: NutricionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Progreso Proyectado",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Aquí va el gráfico de progreso
            if (uiState is NutricionUiState.Success) {
                ProgresoChart(
                    pesoInicial = perfil?.pesoActual ?: 0f,
                    pesoObjetivo = perfil?.pesoObjetivo ?: 0f,
                    semanas = uiState.planNutricional.tiempoEstimado,
                    sistemaPeso = perfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"
                )
            }
        }
    }
}
/*
@Composable
private fun PlanNutricionalCard(uiState: NutricionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Tu Plan Nutricional",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (uiState is NutricionUiState.Success) {
                val plan = uiState.planNutricional
                InfoRow("Calorías diarias", "${plan.requerimientoEnergetico} kcal")
                InfoRow("Proteínas", "${plan.proteinas}g")
                InfoRow("Carbohidratos", "${plan.carbohidratos}g")
                InfoRow("Grasas", "${plan.grasas}g")
            }
        }
    }
}*/

@Composable
private fun ConsejosCard(perfil: Perfil?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Consejos Personalizados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            perfil?.let {
                val consejos = when (it.objetivo) {
                    "Perder peso" -> listOf(
                        "Mantén un diario de alimentación",
                        "Prioriza alimentos ricos en proteínas",
                        "Mantente hidratado",
                        "Incluye ejercicio cardiovascular"
                    )
                    "Ganar peso" -> listOf(
                        "Come con más frecuencia",
                        "Aumenta el tamaño de las porciones",
                        "Incluye snacks nutritivos",
                        "Enfócate en el entrenamiento de fuerza"
                    )
                    else -> listOf(
                        "Mantén horarios regulares de comida",
                        "Escucha las señales de tu cuerpo",
                        "Equilibra actividad y descanso",
                        "Mantén la variedad en tu dieta"
                    )
                }

                consejos.forEach { consejo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(consejo)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgresoChart(
    pesoInicial: Float,
    pesoObjetivo: Float,
    semanas: Int,
    sistemaPeso: String
) {
    // Convertir pesos según el sistema seleccionado
    val (pesoInicialMostrado, pesoObjetivoMostrado) = if (sistemaPeso == "Imperial (lb)") {
        Pair(
            pesoInicial * 2.20462f,
            pesoObjetivo * 2.20462f
        )
    } else {
        Pair(pesoInicial, pesoObjetivo)
    }

    val chartEntryModel = entryModelOf(
        0f to pesoInicialMostrado,
        semanas.toFloat() to pesoObjetivoMostrado
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Mostrar las unidades actuales
        Text(
            text = "Peso en ${if (sistemaPeso == "Imperial (lb)") "libras" else "kilogramos"}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
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

        // Leyenda del gráfico
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Inicio: ${"%.1f".format(pesoInicialMostrado)} ${if (sistemaPeso == "Imperial (lb)") "lb" else "kg"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Semana $semanas: ${"%.1f".format(pesoObjetivoMostrado)} ${if (sistemaPeso == "Imperial (lb)") "lb" else "kg"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun WeightProgressChart(
    currentWeight: Float,
    targetWeight: Float,
    tiempoEstimado: Int
) {
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