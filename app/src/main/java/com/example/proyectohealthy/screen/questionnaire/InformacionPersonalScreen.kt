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
            InfoButton(text = "Género: ${currentPerfil?.genero ?: "No seleccionado"}", onClick = { showGeneroSheet = true })
            InfoButton(text = "Edad: ${currentPerfil?.edad ?: "No seleccionada"}", onClick = { showEdadSheet = true })
            InfoButton(text = "Altura: ${currentPerfil?.altura?.toInt() ?: "No seleccionada"} cm", onClick = { showAlturaSheet = true })
            InfoButton(text = "Peso: ${String.format("%.1f", currentPerfil?.pesoActual ?: 0f)} kg", onClick = { showPesoSheet = true })

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
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondaryContainer)
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

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu altura", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(200.dp)) {
                NumberSelector(
                    items = alturas,
                    listState = listState,
                    onValueSelected = { selectedAltura = it },
                    itemToString = { "$it cm" }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateAltura(selectedAltura.toFloat())
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
    val pesoEntero = (30..200).toList()
    val pesoDecimal = (0..9).toList()
    val currentPeso = viewModel.currentPerfil.collectAsState().value?.pesoActual ?: 70f
    val initialEnteroIndex = (currentPeso.toInt() - 30).coerceIn(0, pesoEntero.size - 1)
    val initialDecimalIndex = ((currentPeso % 1) * 10).toInt().coerceIn(0, pesoDecimal.size - 1)
    val listStateEntero = rememberLazyListState(initialFirstVisibleItemIndex = initialEnteroIndex)
    val listStateDecimal = rememberLazyListState(initialFirstVisibleItemIndex = initialDecimalIndex)
    var selectedPeso by remember { mutableStateOf(currentPeso) }

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu peso", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(200.dp)) {
                NumberSelector(
                    items = pesoEntero,
                    listState = listStateEntero,
                    modifier = Modifier.weight(1f),
                    onValueSelected = { entero ->
                        val decimal = pesoDecimal[listStateDecimal.firstVisibleItemIndex]
                        selectedPeso = entero + decimal / 10f
                    }
                )
                Text(
                    ".",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                NumberSelector(
                    items = pesoDecimal,
                    listState = listStateDecimal,
                    modifier = Modifier.weight(0.5f),
                    onValueSelected = { decimal ->
                        val entero = pesoEntero[listStateEntero.firstVisibleItemIndex]
                        selectedPeso = entero + decimal / 10f
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updatePesoActual(selectedPeso)
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