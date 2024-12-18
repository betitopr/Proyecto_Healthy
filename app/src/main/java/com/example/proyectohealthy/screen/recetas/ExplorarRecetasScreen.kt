package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.RecetaApi
import com.example.proyectohealthy.ui.screen.recetas.components.*
import com.example.proyectohealthy.ui.viewmodel.ExplorarRecetasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorarRecetasScreen(
    onNavigateToMisRecetas: () -> Unit,
    viewModel: ExplorarRecetasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedReceta by remember { mutableStateOf<RecetaApi?>(null) }
    var showGeneradorIA by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra de búsqueda solo se muestra cuando no estamos en modo IA
        if (!showGeneradorIA) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BuscadorRecetas(
                    query = searchQuery,
                    onQueryChange = { query ->
                        viewModel.updateSearchQuery(query)
                    },
                    onSearchClick = {},
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { viewModel.buscarRecetas() },
                    enabled = searchQuery.length >= 3
                ) {
                    Text("Buscar")
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (showGeneradorIA) {
                // Sección del Generador IA
                GeneradorIASection(
                    onGenerar = { descripcion ->
                        viewModel.generarReceta(descripcion)
                    },
                    isLoading = uiState is ExplorarRecetasViewModel.ExplorarRecetasUiState.Loading
                )
            } else {
                // Sección de búsqueda de recetas
                when (uiState) {
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.Loading -> {
                        LoadingContent()
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.Success -> {
                        val recetas = (uiState as ExplorarRecetasViewModel.ExplorarRecetasUiState.Success).recetas
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(recetas) { receta ->
                                RecetaApiItem(
                                    receta = receta,
                                    onClick = { selectedReceta = receta }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.Empty -> {
                        EmptyContent(
                            title = "No se encontraron resultados",
                            mensaje = "Intenta con otros términos\no genera una receta con IA"
                        )
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.Error -> {
                        EmptyContent(
                            title = "Error",
                            mensaje = (uiState as ExplorarRecetasViewModel.ExplorarRecetasUiState.Error).message
                        )
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.Initial -> {
                        EmptyContent(
                            title = "Encuentra tu próxima receta",
                            mensaje = "Busca por nombre o ingrediente\no genera una receta con IA"
                        )
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.RecetaGuardada -> {
                        LaunchedEffect(Unit) {
                            onNavigateToMisRecetas()
                        }
                    }
                    is ExplorarRecetasViewModel.ExplorarRecetasUiState.GeneracionExitosa -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    text = (uiState as ExplorarRecetasViewModel.ExplorarRecetasUiState.GeneracionExitosa).mensaje,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            // Botón flotante para alternar entre búsqueda y generador IA
            ExtendedFloatingActionButton(
                onClick = {
                    showGeneradorIA = !showGeneradorIA
                    // Limpiar estados al cambiar de modo
                    if (!showGeneradorIA) {
                        viewModel.updateSearchQuery("")
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(if (showGeneradorIA) "Buscar Recetas" else "Generar con IA")
            }
        }
    }

    // BottomSheet para detalles de receta
    selectedReceta?.let { receta ->
        RecetaDetalleSheet(
            receta = receta,
            isLoading = uiState is ExplorarRecetasViewModel.ExplorarRecetasUiState.Loading,
            onDismiss = { selectedReceta = null },
            onGuardar = {
                viewModel.guardarReceta(receta)
            }
        )
    }
}