package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionPersonalScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    var showGeneroSheet by remember { mutableStateOf(false) }
    var showEdadSheet by remember { mutableStateOf(false) }
    var showAlturaSheet by remember { mutableStateOf(false) }
    var showPesoSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información Personal") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoButton(
                text = "Género: ${currentPerfil?.genero ?: "No seleccionado"}",
                onClick = { showGeneroSheet = true }
            )

            InfoButton(
                text = "Edad: ${currentPerfil?.edad ?: "No seleccionada"}",
                onClick = { showEdadSheet = true }
            )

            // Altura: Mostrar en el formato preferido
            val alturaText = currentPerfil?.let { perfil ->
                if (perfil.unidadesPreferences.sistemaAltura == "Imperial (ft/in)") {
                    val totalPulgadas = (perfil.altura / 2.54).toInt()
                    val pies = totalPulgadas / 12
                    val pulgadas = totalPulgadas % 12
                    "Altura: $pies' $pulgadas\""
                } else {
                    "Altura: ${perfil.altura.toInt()} cm"
                }
            } ?: "Altura: No seleccionada"
            InfoButton(text = alturaText, onClick = { showAlturaSheet = true })

            // Peso: Mostrar en el formato preferido
            val pesoText = currentPerfil?.let { perfil ->
                if (perfil.unidadesPreferences.sistemaPeso == "Imperial (lb)") {
                    val pesoLb = perfil.pesoActual * 2.20462f
                    "Peso: ${"%.1f".format(pesoLb)} lb"
                } else {
                    "Peso: ${"%.1f".format(perfil.pesoActual)} kg"
                }
            } ?: "Peso: No seleccionado"
            InfoButton(text = pesoText, onClick = { showPesoSheet = true })

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

    if (showGeneroSheet) {
        GeneroSelector(perfilViewModel) { showGeneroSheet = false }
    }
    if (showEdadSheet) {
        EdadSelector(perfilViewModel) { showEdadSheet = false }
    }
    if (showAlturaSheet) {
        AlturaSelector(perfilViewModel) { showAlturaSheet = false }
    }
    if (showPesoSheet) {
        PesoSelector(perfilViewModel) { showPesoSheet = false }
    }
}

@Composable
fun InfoButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun FullScreenSelector(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            content()
        }
    }
}

@Composable
fun GeneroSelector(viewModel: PerfilViewModel, onDismiss: () -> Unit) {
    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu género", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.updateGenero("Masculino")
                onDismiss()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Masculino")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                viewModel.updateGenero("Femenino")
                onDismiss()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Femenino")
            }
        }
    }
}

@Composable
fun EdadSelector(viewModel: PerfilViewModel, onDismiss: () -> Unit) {
    val edades = (18..100).toList()
    val currentEdad = viewModel.currentPerfil.collectAsState().value?.edad ?: 18
    val initialIndex = (currentEdad - 18).coerceIn(0, edades.size - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var selectedEdad by remember { mutableStateOf(currentEdad) }

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu edad", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(200.dp)) {
                NumberSelector(
                    items = edades,
                    listState = listState,
                    onValueSelected = { selectedEdad = it }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateEdad(selectedEdad)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        }
    }
}

@Composable
fun AlturaSelector(viewModel: PerfilViewModel, onDismiss: () -> Unit) {
    val alturas = (140..220).toList()
    val currentAltura = viewModel.currentPerfil.collectAsState().value?.altura?.toInt() ?: 170
    val initialIndex = (currentAltura - 140).coerceIn(0, alturas.size - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var selectedAltura by remember { mutableStateOf(currentAltura) }
    val currentPerfil by viewModel.currentPerfil.collectAsState()
    val sistemaAltura = currentPerfil?.unidadesPreferences?.sistemaAltura ?: "Métrico (cm)"

    // Convertir la altura actual (cm) a pies y pulgadas si es necesario
    val alturaActualCm = currentPerfil?.altura?.toInt() ?: 170
    val (pies, pulgadas) = if (sistemaAltura == "Imperial (ft/in)") {
        val totalPulgadas = (alturaActualCm / 2.54).toInt()
        Pair(totalPulgadas / 12, totalPulgadas % 12)
    } else {
        Pair(0, 0)
    }

    // Estados para los selectores
    val alturasCm = (140..220).toList()
    val piesList = (4..7).toList()
    val pulgadasList = (0..11).toList()

    // Estados para las listas
    val listStateCm = rememberLazyListState(
        initialFirstVisibleItemIndex = (alturaActualCm - 140).coerceIn(0, alturasCm.size - 1)
    )
    val listStatePies = rememberLazyListState(
        initialFirstVisibleItemIndex = piesList.indexOf(pies).coerceIn(0, piesList.size - 1)
    )
    val listStatePulgadas = rememberLazyListState(
        initialFirstVisibleItemIndex = pulgadas
    )

    // Estado para el valor seleccionado
    var selectedAlturaCm by remember { mutableStateOf(alturaActualCm) }

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Selecciona tu altura",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (sistemaAltura == "Métrico (cm)") {
                // Selector único para centímetros
                Box(modifier = Modifier.height(200.dp)) {
                    NumberSelector(
                        items = alturasCm,
                        listState = listStateCm,
                        onValueSelected = { selectedAlturaCm = it },
                        itemToString = { "$it cm" }
                    )
                }
            } else {
                // Selectores duales para pies y pulgadas
                Row(modifier = Modifier.height(200.dp)) {
                    // Selector de pies
                    Box(modifier = Modifier.weight(1f)) {
                        NumberSelector(
                            items = piesList,
                            listState = listStatePies,
                            onValueSelected = { pies ->
                                val pulgadasActuales = listStatePulgadas.firstVisibleItemIndex
                                selectedAlturaCm = ((pies * 12 + pulgadasActuales) * 2.54).toInt()
                            },
                            itemToString = { "$it ft" }
                        )
                    }

                    // Selector de pulgadas
                    Box(modifier = Modifier.weight(1f)) {
                        NumberSelector(
                            items = pulgadasList,
                            listState = listStatePulgadas,
                            onValueSelected = { pulgadas ->
                                val piesActuales = piesList[listStatePies.firstVisibleItemIndex]
                                selectedAlturaCm = ((piesActuales * 12 + pulgadas) * 2.54).toInt()
                            },
                            itemToString = { "$it in" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar equivalencia
            if (sistemaAltura == "Imperial (ft/in)") {
                Text(
                    text = "≈ ${selectedAlturaCm} cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val totalPulgadas = (selectedAlturaCm / 2.54).toInt()
                val piesEquivalentes = totalPulgadas / 12
                val pulgadasEquivalentes = totalPulgadas % 12
                Text(
                    text = "≈ $piesEquivalentes ft $pulgadasEquivalentes in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateAltura(selectedAlturaCm.toFloat())
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        }
    }
}

@Composable
fun PesoSelector(viewModel: PerfilViewModel, onDismiss: () -> Unit) {
    val currentPerfil by viewModel.currentPerfil.collectAsState()
    val sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"

    // Convertir el peso actual a la unidad seleccionada
    val pesoActualKg = currentPerfil?.pesoActual ?: 70f
    val pesoMostrado = if (sistemaPeso == "Imperial (lb)") {
        pesoActualKg * 2.20462f // Convertir a libras
    } else {
        pesoActualKg
    }

    // Rangos según el sistema
    val pesoEntero = if (sistemaPeso == "Imperial (lb)") {
        (66..440).toList() // Rango en libras
    } else {
        (30..200).toList() // Rango en kg
    }
    val pesoDecimal = (0..9).toList()

    // Calcular posiciones iniciales
    val initialEnteroIndex = (pesoMostrado.toInt() - pesoEntero.first()).coerceIn(0, pesoEntero.size - 1)
    val initialDecimalIndex = ((pesoMostrado % 1) * 10).toInt().coerceIn(0, 9)

    val listStateEntero = rememberLazyListState(initialFirstVisibleItemIndex = initialEnteroIndex)
    val listStateDecimal = rememberLazyListState(initialFirstVisibleItemIndex = initialDecimalIndex)

    var selectedPeso by remember { mutableStateOf(pesoMostrado) }

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (sistemaPeso == "Imperial (lb)") "Selecciona tu peso (lb)" else "Selecciona tu peso (kg)",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.height(200.dp)) {
                // Selector entero
                Box(modifier = Modifier.weight(1f)) {
                    NumberSelector(
                        items = pesoEntero,
                        listState = listStateEntero,
                        onValueSelected = { entero ->
                            val decimal = pesoDecimal[listStateDecimal.firstVisibleItemIndex]
                            selectedPeso = entero + decimal / 10f
                        }
                    )
                }

                Text(
                    ".",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // Selector decimal
                Box(modifier = Modifier.weight(0.5f)) {
                    NumberSelector(
                        items = pesoDecimal,
                        listState = listStateDecimal,
                        onValueSelected = { decimal ->
                            val entero = pesoEntero[listStateEntero.firstVisibleItemIndex]
                            selectedPeso = entero + decimal / 10f
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar equivalencia
            val pesoEquivalente = if (sistemaPeso == "Imperial (lb)") {
                selectedPeso / 2.20462f // Convertir de libras a kg
            } else {
                selectedPeso * 2.20462f // Convertir de kg a libras
            }

            Text(
                text = if (sistemaPeso == "Imperial (lb)") {
                    "≈ ${"%.1f".format(pesoEquivalente)} kg"
                } else {
                    "≈ ${"%.1f".format(pesoEquivalente)} lb"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val pesoGuardadoKg = if (sistemaPeso == "Imperial (lb)") {
                        selectedPeso / 2.20462f // Guardar en kg
                    } else {
                        selectedPeso
                    }
                    viewModel.updatePesoActual(pesoGuardadoKg)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        }
    }
}

@Composable
fun NumberSelector(
    items: List<Int>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onValueSelected: (Int) -> Unit,
    itemToString: (Int) -> String = { it.toString() }
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 80.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemToString(item),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (listState.firstVisibleItemIndex == items.indexOf(item))
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