package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel

@Composable
fun AlimentoContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAlimentoSelected: (Alimento, Float, String) -> Unit,
    onMiAlimentoSelected: (MisAlimentos, Float, String) -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel,
    scannerViewModel: ScannerViewModel,
    tipoComidaSeleccionado: String,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAgregarMiAlimentoBottomSheet by remember { mutableStateOf(false) }
    var showDetalleBottomSheet by rememberSaveable { mutableStateOf(false) }
    var alimentoSeleccionado by rememberSaveable { mutableStateOf<Any?>(null) }
    var showFiltrosSheet by remember { mutableStateOf(false) }
    val alimentoFiltros by alimentoViewModel.filtros.collectAsState()
    val misAlimentosFiltros by misAlimentosViewModel.filtros.collectAsState()
    val favoritosFiltros by favoritosViewModel.filtros.collectAsState()

// Efectos
    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> alimentoViewModel.searchAlimentosByNombre(searchQuery)
            1 -> misAlimentosViewModel.searchMisAlimentosByNombre(searchQuery)
            2 -> favoritosViewModel.searchFavoritos(searchQuery)
        }
    }

    LaunchedEffect(showDetalleBottomSheet) {
        if (!showDetalleBottomSheet) {
            alimentoSeleccionado = null
        }
    }

    Column(modifier = modifier) {
        // Barra de búsqueda y filtros
        SearchAndFilterRow(
            searchQuery = searchQuery,
            onQueryChange = onSearchQueryChange,
            onScannerClicked = {
                scannerViewModel.resetState()
            },
            onFilterClicked = { showFiltrosSheet = true },
            onClearFilters = {
                when (selectedTabIndex) {
                    0 -> alimentoViewModel.updateFiltros(AlimentoFiltros())
                    1 -> misAlimentosViewModel.updateFiltros(AlimentoFiltros())
                    2 -> favoritosViewModel.updateFiltros(AlimentoFiltros())
                }
            },
            selectedTabIndex = selectedTabIndex,
            alimentoFiltros = alimentoFiltros,
            misAlimentosFiltros = misAlimentosFiltros,
            favoritosFiltros = favoritosFiltros,
            scannerViewModel = scannerViewModel,
            onScannerResult = { alimento ->
                alimentoSeleccionado = alimento
                showDetalleBottomSheet = true
            }
        )

        // Pestañas y contenido
        AlimentoTabContent(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            onAlimentoSelected = { alimento ->
                alimentoSeleccionado = alimento
                showDetalleBottomSheet = true
            },
            onMiAlimentoSelected = { miAlimento ->
                alimentoSeleccionado = miAlimento
                showDetalleBottomSheet = true
            },
            onAddMiAlimento = { showAgregarMiAlimentoBottomSheet = true },
            alimentoViewModel = alimentoViewModel,
            misAlimentosViewModel = misAlimentosViewModel,
            favoritosViewModel = favoritosViewModel,
            searchQuery = searchQuery
        )

        // BottomSheets
        if (showDetalleBottomSheet && alimentoSeleccionado != null) {
            when (alimentoSeleccionado) {
                is Alimento -> DetalleAlimentoBottomSheet(
                    alimento = alimentoSeleccionado as Alimento,
                    tipoComidaInicial = tipoComidaSeleccionado,
                    onDismiss = {
                        showDetalleBottomSheet = false
                        alimentoSeleccionado = null
                    },
                    onConfirm = { cantidad, tipoComida ->
                        onAlimentoSelected(alimentoSeleccionado as Alimento, cantidad, tipoComida)
                        showDetalleBottomSheet = false
                        alimentoSeleccionado = null
                    }
                )
                is MisAlimentos -> DetalleMiAlimentoBottomSheet(
                    miAlimento = alimentoSeleccionado as MisAlimentos,
                    tipoComidaInicial = tipoComidaSeleccionado,
                    onDismiss = {
                        showDetalleBottomSheet = false
                        alimentoSeleccionado = null
                        scannerViewModel.resetState()
                    },
                    onConfirm = { cantidad, tipoComida ->
                        onMiAlimentoSelected(alimentoSeleccionado as MisAlimentos, cantidad, tipoComida)
                        showDetalleBottomSheet = false
                        alimentoSeleccionado = null
                        scannerViewModel.resetState()
                    }
                )
            }
        }

        if (showFiltrosSheet) {
            FiltrosBottomSheet(
                mostrar = true,
                filtrosActuales = when (selectedTabIndex) {
                    0 -> alimentoFiltros
                    1 -> misAlimentosFiltros
                    2 -> favoritosFiltros
                    else -> AlimentoFiltros()
                },
                categoriasDisponibles = when (selectedTabIndex) {
                    0 -> alimentoViewModel.categoriasDisponibles.collectAsState().value
                    1 -> misAlimentosViewModel.categoriasDisponibles.collectAsState().value
                    2 -> favoritosViewModel.categoriasDisponibles.collectAsState().value
                    else -> emptyList()
                },
                onDismiss = { showFiltrosSheet = false },
                onFiltrosChanged = { newFiltros ->
                    when (selectedTabIndex) {
                        0 -> alimentoViewModel.updateFiltros(newFiltros)
                        1 -> misAlimentosViewModel.updateFiltros(newFiltros)
                        2 -> favoritosViewModel.updateFiltros(newFiltros)
                    }
                    showFiltrosSheet = false
                }
            )
        }

        if (showAgregarMiAlimentoBottomSheet) {
            AgregarMiAlimentoBottomSheet(
                onDismiss = {
                    showAgregarMiAlimentoBottomSheet = false
                    alimentoSeleccionado = null
                },
                onConfirm = { nuevoMiAlimento ->
                    misAlimentosViewModel.createOrUpdateMiAlimento(nuevoMiAlimento)
                    showAgregarMiAlimentoBottomSheet = false
                    alimentoSeleccionado = null
                },
                viewModel = misAlimentosViewModel
            )
        }
    }
}