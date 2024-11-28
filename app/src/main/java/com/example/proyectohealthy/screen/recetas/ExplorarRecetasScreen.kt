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
        // Barra de búsqueda con botón
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
                onClick = {
                    viewModel.buscarRecetas() // Aquí ya no pasamos el parámetro
                },
                enabled = searchQuery.length >= 3
            ) {
                Text("Buscar")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is ExplorarRecetasViewModel.ExplorarRecetasUiState.Initial -> {
                    if (!showGeneradorIA) {
                        EmptyContent(
                            title = "Encuentra tu próxima receta",
                            mensaje = "Busca por nombre o ingrediente\no genera una receta con IA"
                        )
                    }
                }

                is ExplorarRecetasViewModel.ExplorarRecetasUiState.Loading -> {
                    LoadingContent()
                }

                is ExplorarRecetasViewModel.ExplorarRecetasUiState.Success -> {
                    val recetas = (uiState as ExplorarRecetasViewModel.ExplorarRecetasUiState.Success).recetas
                    if (showGeneradorIA) {
                        GeneradorIASection(
                            onGenerar = { descripcion, tipo, restricciones ->
                                viewModel.generarReceta(descripcion, tipo, restricciones)
                            },
                            isLoading = false
                        )
                    } else {
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

                is ExplorarRecetasViewModel.ExplorarRecetasUiState.RecetaGuardada -> {
                    LaunchedEffect(Unit) {
                        onNavigateToMisRecetas()
                    }
                }

                is ExplorarRecetasViewModel.ExplorarRecetasUiState.GeneracionExitosa -> TODO()
            }

            // Botón flotante para alternar entre búsqueda y generador IA
            ExtendedFloatingActionButton(
                onClick = { showGeneradorIA = !showGeneradorIA },
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