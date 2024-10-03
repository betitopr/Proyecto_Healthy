package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
            InfoButton(text = "Género: ${currentPerfil?.Genero ?: "No seleccionado"}", onClick = { showGeneroSheet = true })
            InfoButton(text = "Edad: ${currentPerfil?.Edad ?: "No seleccionada"}", onClick = { showEdadSheet = true })
            InfoButton(text = "Altura: ${currentPerfil?.Altura?.toInt() ?: "No seleccionada"} cm", onClick = { showAlturaSheet = true })
            InfoButton(text = "Peso: ${String.format("%.1f", currentPerfil?.Peso_Actual ?: 0f)} kg", onClick = { showPesoSheet = true })

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
    val currentEdad = viewModel.currentPerfil.collectAsState().value?.Edad ?: 18
    val initialIndex = (currentEdad - 18).coerceIn(0, edades.size - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu edad", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(180.dp)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(edades) { index, edad ->
                        val alpha = if (index == listState.firstVisibleItemIndex + 1) 1f else 0.3f
                        Text(
                            text = edad.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .alpha(alpha)
                                .padding(vertical = 8.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val selectedEdad = edades[listState.firstVisibleItemIndex + 1]
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
    val currentAltura = viewModel.currentPerfil.collectAsState().value?.Altura?.toInt() ?: 170
    val initialIndex = (currentAltura - 140).coerceIn(0, alturas.size - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu altura", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(180.dp)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(alturas) { index, altura ->
                        val alpha = if (index == listState.firstVisibleItemIndex + 1) 1f else 0.3f
                        Text(
                            text = "$altura cm",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .alpha(alpha)
                                .padding(vertical = 8.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val selectedAltura = alturas[listState.firstVisibleItemIndex + 1]
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
    val currentPerfil by viewModel.currentPerfil.collectAsState()
    val pesoEntero = (30..200).toList()
    val pesoDecimal = (0..9).toList()
    val currentPeso = currentPerfil?.Peso_Actual ?: 70f
    val listStateEntero = rememberLazyListState(initialFirstVisibleItemIndex = (currentPeso.toInt() - 30).coerceIn(0, pesoEntero.size - 1))
    val listStateDecimal = rememberLazyListState(initialFirstVisibleItemIndex = ((currentPeso % 1) * 10).toInt())
    val coroutineScope = rememberCoroutineScope()

    FullScreenSelector {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Selecciona tu peso", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(180.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        state = listStateEntero,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(pesoEntero) { index, peso ->
                            val alpha = if (index == listStateEntero.firstVisibleItemIndex + 1) 1f else 0.3f
                            Text(
                                text = peso.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier
                                    .alpha(alpha)
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    )
                }
                Text(".", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.align(Alignment.CenterVertically))
                Box(modifier = Modifier.weight(0.5f)) {
                    LazyColumn(
                        state = listStateDecimal,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        itemsIndexed(pesoDecimal) { index, decimal ->
                            val alpha = if (index == listStateDecimal.firstVisibleItemIndex + 1) 1f else 0.3f
                            Text(
                                text = decimal.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier
                                    .alpha(alpha)
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val entero = pesoEntero[listStateEntero.firstVisibleItemIndex + 1]
                    val decimal = pesoDecimal[listStateDecimal.firstVisibleItemIndex + 1]
                    viewModel.updatePesoActual(entero + decimal / 10f)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }
        }
    }
}