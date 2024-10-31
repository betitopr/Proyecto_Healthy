package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesoObjetivoScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Convertir altura de cm a metros para cálculos
    val alturaMetros = (currentPerfil?.altura ?: 170f) / 100f

    // Calcular rangos de peso saludable basados en IMC (18.5 - 24.9)
    val pesoMinKg = (18.5f * alturaMetros * alturaMetros).round(1)
    val pesoMaxKg = (24.9f * alturaMetros * alturaMetros).round(1)

    // Convertir rangos a libras si es necesario
    val (pesoMinMostrado, pesoMaxMostrado) = if (sistemaPeso == "Imperial (lb)") {
        Pair(
            (pesoMinKg * 2.20462f).round(1),
            (pesoMaxKg * 2.20462f).round(1)
        )
    } else {
        Pair(pesoMinKg, pesoMaxKg)
    }

    // Definir rangos para los selectores según el sistema
    val pesoEntero = if (sistemaPeso == "Imperial (lb)") {
        // Rango en libras (convertido desde kg)
        ((pesoMinKg * 2.20462f).toInt() - 20..((pesoMaxKg * 2.20462f).toInt() + 20)).toList()
    } else {
        // Rango en kg
        (pesoMinKg.toInt() - 10..pesoMaxKg.toInt() + 10).toList()
    }
    val pesoDecimal = (0..9).toList()

    // Convertir peso objetivo actual al sistema seleccionado
    var pesoSeleccionadoMostrado by remember { mutableStateOf(
        if (sistemaPeso == "Imperial (lb)") {
            currentPerfil?.pesoObjetivo?.times(2.20462f) ?: (70f * 2.20462f)
        } else {
            currentPerfil?.pesoObjetivo ?: 70f
        }
    )}

    // Función para validar el peso seleccionado
    fun validarPesoSeleccionado(): Boolean {
        val pesoKg = if (sistemaPeso == "Imperial (lb)") {
            pesoSeleccionadoMostrado / 2.20462f
        } else {
            pesoSeleccionadoMostrado
        }
        return pesoKg in pesoMinKg..pesoMaxKg
    }

    // Calcular IMC actual
    val imcActual = currentPerfil?.let { perfil ->
        (perfil.pesoActual / (alturaMetros * alturaMetros)).round(1)
    } ?: 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peso Objetivo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "¿Cuál es tu peso objetivo?",
                style = MaterialTheme.typography.headlineSmall
            )

            // Información de IMC y rangos saludables
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tu IMC actual: $imcActual",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "El IMC (Índice de Masa Corporal) es una medida que relaciona tu peso y altura. " +
                                "Un IMC saludable está entre 18.5 y 24.9.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rango de peso saludable para tu altura:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (sistemaPeso == "Imperial (lb)") {
                            "${"%.1f".format(pesoMinMostrado)} - ${"%.1f".format(pesoMaxMostrado)} lb"
                        } else {
                            "${"%.1f".format(pesoMinMostrado)} - ${"%.1f".format(pesoMaxMostrado)} kg"
                        }
                    )
                }
            }

            // Selectores de peso
            Row(
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    NumberSelector(
                        items = pesoEntero,
                        initialValue = pesoSeleccionadoMostrado.toInt(),
                        onValueSelected = { entero ->
                            val decimal = (pesoSeleccionadoMostrado % 1 * 10).toInt()
                            pesoSeleccionadoMostrado = entero + decimal / 10f
                        }
                    )
                }
                Text(
                    ".",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Box(modifier = Modifier.weight(0.5f)) {
                    NumberSelector(
                        items = pesoDecimal,
                        initialValue = ((pesoSeleccionadoMostrado % 1) * 10).toInt(),
                        onValueSelected = { decimal ->
                            val entero = pesoSeleccionadoMostrado.toInt()
                            pesoSeleccionadoMostrado = entero + decimal / 10f
                        }
                    )
                }
            }

            // Mostrar unidad actual y equivalencia
            Text(
                text = if (sistemaPeso == "Imperial (lb)") {
                    val pesoKg = pesoSeleccionadoMostrado / 2.20462f
                    "≈ ${"%.1f".format(pesoKg)} kg"
                } else {
                    val pesoLb = pesoSeleccionadoMostrado * 2.20462f
                    "≈ ${"%.1f".format(pesoLb)} lb"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPreviousClick) {
                    Text("Anterior")
                }
                Button(
                    onClick = {
                        val pesoObjetivoKg = if (sistemaPeso == "Imperial (lb)") {
                            pesoSeleccionadoMostrado / 2.20462f
                        } else {
                            pesoSeleccionadoMostrado
                        }

                        if (pesoObjetivoKg <= pesoMinKg  || pesoObjetivoKg >= pesoMaxKg ) {
                            showConfirmDialog = true
                        } else {
                            perfilViewModel.updatePesoObjetivo(pesoObjetivoKg)
                            onNextClick()
                        }
                    }
                ) {
                    Text("Siguiente")
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Advertencia") },
            text = {
                Text(
                    "El peso objetivo seleccionado está fuera del rango recomendado para tu altura. " +
                            "¿Estás seguro de que deseas continuar?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pesoObjetivoKg = if (sistemaPeso == "Imperial (lb)") {
                            pesoSeleccionadoMostrado / 2.20462f
                        } else {
                            pesoSeleccionadoMostrado
                        }
                        perfilViewModel.updatePesoObjetivo(pesoObjetivoKg)
                        showConfirmDialog = false
                        onNextClick()
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Ajustar peso")
                }
            }
        )
    }
}

@Composable
private fun NumberSelector(
    items: List<Int>,
    initialValue: Int,
    onValueSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.indexOf(initialValue).coerceAtLeast(0)
    )

    Box {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 80.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(items.size) { index ->
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index].toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (listState.firstVisibleItemIndex == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
        )
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val index = listState.firstVisibleItemIndex.coerceIn(0, items.size - 1)
            onValueSelected(items[index])
        }
    }
}

// Función de extensión para redondear Float a un número específico de decimales
private fun Float.round(decimals: Int): Float {
    var multiplier = 1f
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}