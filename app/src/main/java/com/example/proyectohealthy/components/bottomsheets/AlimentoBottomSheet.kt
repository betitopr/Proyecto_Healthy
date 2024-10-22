package com.example.proyectohealthy.components.bottomsheets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentoBottomSheet(
    onDismiss: () -> Unit,
    onAlimentoSelected: (Alimento, Float, String) -> Unit, // Añadido parámetro String para tipo de comida
    onMiAlimentoSelected: (MisAlimentos, Float, String) -> Unit, // Añadido parámetro String para tipo de comida
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    tipoComidaSeleccionado: String
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showDetalleBottomSheet by remember { mutableStateOf(false) }
    var showAgregarMiAlimentoBottomSheet by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf<Any?>(null) }
    var showQrScanner by remember { mutableStateOf(false) }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                scannerViewModel.getProductInfo(barcode)
                showQrScanner = true
            }
        }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.85f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                    Text("Búsqueda")
                }
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                    Text("Mis Alimentos")
                }
                Tab(selected = selectedTabIndex == 2, onClick = {
                    selectedTabIndex = 2
                    val options = ScanOptions()
                    options.setOrientationLocked(false)
                    options.setPrompt("Escanea el código de barras")
                    options.setBeepEnabled(true)
                    options.setBarcodeImageEnabled(true)
                    scanLauncher.launch(options)
                }) {
                    Text("QR")
                }
            }

            when (selectedTabIndex) {
                0 -> BusquedaAlimentoTab(
                    viewModel = alimentoViewModel,
                    onAlimentoSelected = { alimento ->
                        alimentoSeleccionado = alimento
                        showDetalleBottomSheet = true
                    }
                )
                1 -> MisAlimentosTab(
                    viewModel = misAlimentosViewModel,
                    onMiAlimentoSelected = { miAlimento ->
                        alimentoSeleccionado = miAlimento
                        showDetalleBottomSheet = true
                    },
                    onAddMiAlimentoClick = {
                        showAgregarMiAlimentoBottomSheet = true
                    }
                )
                2 -> {
                    if (showQrScanner) {
                        QrScannerTab(
                            scannerViewModel = scannerViewModel,
                            misAlimentosViewModel = misAlimentosViewModel,
                            onMiAlimentoCreated = { miAlimento ->
                                showQrScanner = false
                                alimentoSeleccionado = miAlimento
                                showDetalleBottomSheet = true
                            },
                            onDismiss = {
                                showQrScanner = false
                                selectedTabIndex = 1  // Vuelve a la pestaña de Mis Alimentos
                            }
                        )
                    } else {
                        Text("Presiona la pestaña QR para escanear un código")
                    }
                }
            }
        }
    }

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