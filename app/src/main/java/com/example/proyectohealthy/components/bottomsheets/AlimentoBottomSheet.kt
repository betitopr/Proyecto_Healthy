package com.example.proyectohealthy.components.bottomsheets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.example.proyectohealthy.ui.viewmodel.SearchViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentoBottomSheet(
    onDismiss: () -> Unit,
    onAlimentoSelected: (Alimento, Float, String) -> Unit,
    onMiAlimentoSelected: (MisAlimentos, Float, String) -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(), // Añadimos el SearchViewModel
    tipoComidaSeleccionado: String
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val searchQuery by searchViewModel.searchQuery.collectAsState() // Usamos el estado compartido
    var showDetalleBottomSheet by remember { mutableStateOf(false) }
    var showAgregarMiAlimentoBottomSheet by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf<Any?>(null) }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                scannerViewModel.getProductInfo(barcode)
            }
        }
    )

    // Efecto para aplicar la búsqueda al cambiar de pestaña
    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> alimentoViewModel.searchAlimentosByNombre(searchQuery)
            1 -> misAlimentosViewModel.searchMisAlimentosByNombre(searchQuery)
            2 -> favoritosViewModel.searchFavoritos(searchQuery)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.85f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra de búsqueda y scanner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchViewModel.updateSearchQuery(query) // Actualizamos el estado compartido
                        // Realizamos la búsqueda en la pestaña actual
                        when (selectedTabIndex) {
                            0 -> alimentoViewModel.searchAlimentosByNombre(query)
                            1 -> misAlimentosViewModel.searchMisAlimentosByNombre(query)
                            2 -> favoritosViewModel.searchFavoritos(query)
                        }
                    },
                    placeholder = { Text("Buscar alimentos") },
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        val options = ScanOptions().apply {
                            setOrientationLocked(false)
                            setPrompt("Escanea el código de barras")
                            setBeepEnabled(true)
                            setBarcodeImageEnabled(true)
                        }
                        scanLauncher.launch(options)
                    }
                ) {
                    Icon(Icons.Default.QrCodeScanner, "Escanear")
                }
            }

            // Pestañas
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                ) {
                    Text("Alimentos")
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                ) {
                    Text("Mis Alimentos")
                }
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 }
                ) {
                    Text("Favoritos")
                }
            }

            // Contenido según la pestaña seleccionada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> BusquedaAlimentoTab(
                        viewModel = alimentoViewModel,
                        favoritosViewModel = favoritosViewModel,
                        onAlimentoSelected = { alimento ->
                            alimentoSeleccionado = alimento
                            showDetalleBottomSheet = true
                        },
                        currentQuery = searchQuery
                    )
                    1 -> MisAlimentosTab(
                        viewModel = misAlimentosViewModel,
                        favoritosViewModel = favoritosViewModel,
                        onMiAlimentoSelected = { miAlimento ->
                            alimentoSeleccionado = miAlimento
                            showDetalleBottomSheet = true
                        },
                        onAddMiAlimentoClick = {
                            showAgregarMiAlimentoBottomSheet = true
                        },
                        currentQuery = searchQuery
                    )
                    2 -> FavoritosTab(
                        viewModel = favoritosViewModel,
                        onAlimentoSelected = { alimento ->
                            alimentoSeleccionado = alimento
                            showDetalleBottomSheet = true
                        },
                        onMiAlimentoSelected = { miAlimento ->
                            alimentoSeleccionado = miAlimento
                            showDetalleBottomSheet = true
                        },
                        currentQuery = searchQuery
                    )
                }
            }
        }
    }

    // El resto del código (BottomSheets) se mantiene igual...
    if (showDetalleBottomSheet && alimentoSeleccionado != null) {
        when (alimentoSeleccionado) {
            is Alimento -> DetalleAlimentoBottomSheet(
                alimento = alimentoSeleccionado as Alimento,
                tipoComidaInicial = tipoComidaSeleccionado,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad, tipoComida ->
                    onAlimentoSelected(alimentoSeleccionado as Alimento, cantidad, tipoComida)
                    showDetalleBottomSheet = false
                    onDismiss()
                }
            )
            is MisAlimentos -> DetalleMiAlimentoBottomSheet(
                miAlimento = alimentoSeleccionado as MisAlimentos,
                tipoComidaInicial = tipoComidaSeleccionado,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad, tipoComida ->
                    onMiAlimentoSelected(alimentoSeleccionado as MisAlimentos, cantidad, tipoComida)
                    showDetalleBottomSheet = false
                    onDismiss()
                }
            )
        }
    }

    if (showAgregarMiAlimentoBottomSheet) {
        AgregarMiAlimentoBottomSheet(
            onDismiss = { showAgregarMiAlimentoBottomSheet = false },
            onConfirm = { nuevoMiAlimento ->
                misAlimentosViewModel.createOrUpdateMiAlimento(nuevoMiAlimento)
                showAgregarMiAlimentoBottomSheet = false
            },
            viewModel = misAlimentosViewModel
        )
    }
}