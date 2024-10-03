package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
    val pesoEntero = (30..200).toList()
    val pesoDecimal = (0..9).toList()
    val currentPesoObjetivo = currentPerfil?.Peso_Objetivo ?: 70f
    val listStateEntero = rememberLazyListState(initialFirstVisibleItemIndex = (currentPesoObjetivo.toInt() - 30).coerceIn(0, pesoEntero.size - 1))
    val listStateDecimal = rememberLazyListState(initialFirstVisibleItemIndex = ((currentPesoObjetivo % 1) * 10).toInt())
    val coroutineScope = rememberCoroutineScope()

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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Cuál es tu peso objetivo?",
                style = MaterialTheme.typography.headlineSmall
            )

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

            Button(
                onClick = {
                    val entero = pesoEntero[listStateEntero.firstVisibleItemIndex + 1]
                    val decimal = pesoDecimal[listStateDecimal.firstVisibleItemIndex + 1]
                    perfilViewModel.updatePesoObjetivo(entero + decimal / 10f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar")
            }

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
}