package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.components.ScannerComponent
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel

@Composable
fun SearchAndFilterRow(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onScannerClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onClearFilters: () -> Unit,
    selectedTabIndex: Int,
    alimentoFiltros: AlimentoFiltros,
    misAlimentosFiltros: AlimentoFiltros,
    favoritosFiltros: AlimentoFiltros,
    scannerViewModel: ScannerViewModel,
    onScannerResult: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar alimentos") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Botón Scanner
        ScannerComponent(
            scannerViewModel = scannerViewModel,
            setAlimentoSeleccionado = onScannerResult,
            setShowDetalleBottomSheet = { show ->
                if (!show) onScannerResult(null)
            },
        )
/*
        ScannerComponent(
            scannerViewModel = scannerViewModel,
            setAlimentoSeleccionado = { alimento ->
                alimentoSeleccionado = alimento
                showDetalleBottomSheet = true
            },
            setShowDetalleBottomSheet = { show ->
                if (!show) {
                    alimentoSeleccionado = null
                }
                showDetalleBottomSheet = show
            }
        )*/

        // Botón Filtros
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp)
        ) {
            IconButton(onClick = onFilterClicked) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtros",
                    modifier = Modifier.size(20.dp),
                    tint = if (when (selectedTabIndex) {
                            0 -> alimentoFiltros.hasActiveFilters()
                            1 -> misAlimentosFiltros.hasActiveFilters()
                            2 -> favoritosFiltros.hasActiveFilters()
                            else -> false
                        }) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Badge con contador de filtros
            val activeFilters = when (selectedTabIndex) {
                0 -> alimentoFiltros.countActiveFilters()
                1 -> misAlimentosFiltros.countActiveFilters()
                2 -> favoritosFiltros.countActiveFilters()
                else -> 0
            }
            if (activeFilters > 0) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                ) {
                    Text(
                        text = activeFilters.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Botón limpiar filtros
        AnimatedVisibility(
            visible = when (selectedTabIndex) {
                0 -> alimentoFiltros.hasActiveFilters()
                1 -> misAlimentosFiltros.hasActiveFilters()
                2 -> favoritosFiltros.hasActiveFilters()
                else -> false
            }
        ) {
            IconButton(
                onClick = onClearFilters,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Limpiar filtros",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}