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
    val pesoEntero = (30..200).toList()
    val pesoDecimal = (0..9).toList()
    val currentPesoObjetivo = currentPerfil?.pesoObjetivo ?: 70f
    val listStateEntero = rememberLazyListState(initialFirstVisibleItemIndex = (currentPesoObjetivo.toInt() - 30).coerceIn(0, pesoEntero.size - 1))
    val listStateDecimal = rememberLazyListState(initialFirstVisibleItemIndex = ((currentPesoObjetivo % 1) * 10).toInt())

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

            Row(modifier = Modifier.height(200.dp)) {
                PesoSelector(
                    items = pesoEntero,
                    listState = listStateEntero,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    ".",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                PesoSelector(
                    items = pesoDecimal,
                    listState = listStateDecimal,
                    modifier = Modifier.weight(0.5f)
                )
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

    LaunchedEffect(listStateEntero.firstVisibleItemIndex, listStateDecimal.firstVisibleItemIndex) {
        val entero = pesoEntero[listStateEntero.firstVisibleItemIndex]
        val decimal = pesoDecimal[listStateDecimal.firstVisibleItemIndex]
        perfilViewModel.updatePesoObjetivo(entero + decimal / 10f)
    }
}

@Composable
fun PesoSelector(
    items: List<Int>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
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
        // Overlay para resaltar la selección
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            listState.animateScrollToItem(listState.firstVisibleItemIndex)
        }
    }
}