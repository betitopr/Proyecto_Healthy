package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.ui.screen.recetas.components.*
import com.example.proyectohealthy.ui.viewmodel.MisRecetasViewModel

@Composable
fun MisRecetasScreen(
    onNavigateToExplorar: () -> Unit,
    viewModel: MisRecetasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDetalleDialog by remember { mutableStateOf<RecetaListItem.GuardadaReceta?>(null) }
    var currentQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BuscadorRecetas(
            query = currentQuery,
            onQueryChange = { newQuery ->
                currentQuery = newQuery
                viewModel.buscarRecetas(newQuery)
            },
            onSearchClick = {
                viewModel.buscarRecetas(currentQuery)
            },
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
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(recetas) { receta ->
                        val item = RecetaListItem.GuardadaReceta(receta)
                        RecetaListItem(
                            item = item,
                            onClick = { showDetalleDialog = item },
                            onDeleteClick = { viewModel.eliminarReceta(receta.id) }
                        )
                    }
                }
            }
            is MisRecetasViewModel.MisRecetasUiState.Error -> {
                EmptyContent(
                    title = "Error",
                    mensaje = (uiState as MisRecetasViewModel.MisRecetasUiState.Error).message,
                    showExplorarButton = false
                )
            }

            MisRecetasViewModel.MisRecetasUiState.AlimentoAgregado -> TODO()
        }
    }

    // Dialog de detalle
    showDetalleDialog?.let { item ->
        RecetaDetalleDialog(
            receta = item.receta,
            onDismiss = { showDetalleDialog = null },
            onAgregarAMisAlimentos = {
                viewModel.agregarAMisAlimentos(item.receta)
                showDetalleDialog = null
            }
        )
    }
}