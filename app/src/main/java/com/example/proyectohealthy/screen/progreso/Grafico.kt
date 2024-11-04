package com.example.proyectohealthy.screen.progreso

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.RegistroDiario
import com.example.proyectohealthy.ui.viewmodel.ProgresoViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate
import java.time.temporal.WeekFields


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoCalorias(registros: List<RegistroDiario>) {
    val datos = remember(registros) {
        registros
            .takeLast(7) // Tomar solo los últimos 7 días
            .sortedBy { it.fecha } // Ordenar por fecha ascendente
            .mapIndexed { index, registro ->
                val etiquetaDia = when {
                    index == 6 -> "Hoy" // Solo el último día será "Hoy"
                    else -> registro.fecha.dayOfMonth.toString()
                }
                etiquetaDia to registro.caloriasNetas.toFloat()
            }
    }

    GraficoMacronutriente(
        datos = datos,
        titulo = "Calorías Netas",
        color = MaterialTheme.colorScheme.primary,
        formatoValor = { "${it.toInt()} kcal" }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoProteinas(registros: List<RegistroDiario>) {
    val datos = remember(registros) {
        registros
            .takeLast(7)
            .sortedBy { it.fecha }
            .mapIndexed { index, registro ->
                val etiquetaDia = when {
                    index == 6 -> "Hoy"
                    else -> registro.fecha.dayOfMonth.toString()
                }
                etiquetaDia to registro.proteinasConsumidas
            }
    }

    GraficoMacronutriente(
        datos = datos,
        titulo = "Proteínas",
        color = MaterialTheme.colorScheme.secondary,
        formatoValor = { "${it.toInt()}g" }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoCarbohidratos(registros: List<RegistroDiario>) {
    val datos = remember(registros) {
        registros
            .takeLast(7)
            .sortedBy { it.fecha }
            .mapIndexed { index, registro ->
                val etiquetaDia = when {
                    index == 6 -> "Hoy"
                    else -> registro.fecha.dayOfMonth.toString()
                }
                etiquetaDia to registro.carbohidratosConsumidos
            }
    }

    GraficoMacronutriente(
        datos = datos,
        titulo = "Carbohidratos",
        color = MaterialTheme.colorScheme.tertiary,
        formatoValor = { "${it.toInt()}g" }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoGrasas(registros: List<RegistroDiario>) {
    val datos = remember(registros) {
        registros
            .takeLast(7)
            .sortedBy { it.fecha }
            .mapIndexed { index, registro ->
                val etiquetaDia = when {
                    index == 6 -> "Hoy"
                    else -> registro.fecha.dayOfMonth.toString()
                }
                etiquetaDia to registro.grasasConsumidas
            }
    }

    GraficoMacronutriente(
        datos = datos,
        titulo = "Grasas",
        color = MaterialTheme.colorScheme.error,
        formatoValor = { "${it.toInt()}g" }
    )
}

@Composable
private fun GraficoMacronutriente(
    datos: List<Pair<String, Float>>,
    titulo: String,
    color: Color,
    formatoValor: (Float) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium
            )

            if (datos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin datos para mostrar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(
                                lineColor = color,
                                lineThickness = 3.dp,
                                pointConnector = DefaultPointConnector(),
                                pointSize = 8.dp
                            )
                        )
                    ),
                    model = entryModelOf(datos.mapIndexed { index, (_, valor) ->
                        entryOf(index.toFloat(), valor)
                    }),
                    startAxis = startAxis(
                        valueFormatter = { value, _ -> formatoValor(value) }
                    ),
                    bottomAxis = bottomAxis(
                        valueFormatter = { value, _ ->
                            datos.getOrNull(value.toInt())?.first ?: ""
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Estadísticas simplificadas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Promedio",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            formatoValor(datos.map { it.second }.average().toFloat()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Máximo",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            formatoValor(datos.maxOf { it.second }),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectorPeriodo(
    periodoSeleccionado: ProgresoViewModel.PeriodoVisualizacion,
    onPeriodoSelected: (ProgresoViewModel.PeriodoVisualizacion) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterChip(
            selected = periodoSeleccionado == ProgresoViewModel.PeriodoVisualizacion.SEMANA,
            onClick = { onPeriodoSelected(ProgresoViewModel.PeriodoVisualizacion.SEMANA) },
            label = { Text("Semana") }
        )

        FilterChip(
            selected = periodoSeleccionado == ProgresoViewModel.PeriodoVisualizacion.MES,
            onClick = { onPeriodoSelected(ProgresoViewModel.PeriodoVisualizacion.MES) },
            label = { Text("Mes") }
        )

        FilterChip(
            selected = periodoSeleccionado == ProgresoViewModel.PeriodoVisualizacion.TRIMESTRE,
            onClick = { onPeriodoSelected(ProgresoViewModel.PeriodoVisualizacion.TRIMESTRE) },
            label = { Text("Trimestre") }
        )
    }
}