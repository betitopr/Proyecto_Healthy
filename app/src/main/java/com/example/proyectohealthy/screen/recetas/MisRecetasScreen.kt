package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.RecetaGuardada
import com.example.proyectohealthy.ui.viewmodel.MisRecetasViewModel

@Composable
fun MisRecetasScreen(
    onNavigateToExplorar: () -> Unit,
    viewModel: MisRecetasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDetalleDialog by remember { mutableStateOf<RecetaGuardada?>(null) }
    var currentQuery by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BuscadorRecetas(
                query = currentQuery,
                onQueryChange = { newQuery ->
                    currentQuery = newQuery
                    viewModel.buscarRecetas(newQuery)
                },
                onSearchClick = {},
                placeholder = "Buscar en mis recetas..."
            )

            when (uiState) {
                is MisRecetasViewModel.MisRecetasUiState.Loading -> {
                    LoadingContent()
                }
                is MisRecetasViewModel.MisRecetasUiState.Empty -> {
                    EmptyContent(
                        title = "No tienes recetas guardadas",
                        mensaje = "Guarda recetas desde la búsqueda o crea las tuyas con IA",
                        showExplorarButton = true,
                        onExplorarClick = onNavigateToExplorar
                    )
                }
                is MisRecetasViewModel.MisRecetasUiState.Success -> {
                    val recetas = (uiState as MisRecetasViewModel.MisRecetasUiState.Success).recetas
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recetas) { receta ->
                            RecetaListItem(
                                item = RecetaListItem.GuardadaReceta(receta),
                                onClick = { showDetalleDialog = receta },
                                onDeleteClick = { viewModel.eliminarReceta(receta.id) }
                            )
                        }
                    }
                }
                is MisRecetasViewModel.MisRecetasUiState.Error -> {
                    EmptyContent(
                        title = "Error",
                        mensaje = (uiState as MisRecetasViewModel.MisRecetasUiState.Error).message
                    )
                }
                is MisRecetasViewModel.MisRecetasUiState.AlimentoAgregado -> {
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar("Receta agregada a Mis Alimentos")
                        viewModel.cargarRecetas() // Volver a cargar la lista
                    }
                }

                MisRecetasViewModel.MisRecetasUiState.Initial -> TODO()
            }
        }
    }

    // Dialog de detalle
    showDetalleDialog?.let { receta ->
        RecetaDetalleDialog(
            receta = receta,
            onDismiss = { showDetalleDialog = null },
            onAgregarAMisAlimentos = {
                viewModel.agregarAMisAlimentos(receta)
                showDetalleDialog = null
            }
        )
    }
}