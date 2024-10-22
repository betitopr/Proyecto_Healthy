package com.example.proyectohealthy.screen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar

import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.MisAlimentos

import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.proyectohealthy.components.bottomsheets.BusquedaAlimentoTab
import com.example.proyectohealthy.components.bottomsheets.DetalleAlimentoBottomSheet
import com.example.proyectohealthy.components.bottomsheets.DetalleMiAlimentoBottomSheet
import com.example.proyectohealthy.components.bottomsheets.MisAlimentosTab
import com.example.proyectohealthy.components.bottomsheets.QrScannerTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDatosScreen(
    navController: NavController,
    alimentoViewModel: AlimentoViewModel = hiltViewModel(),
    misAlimentosViewModel: MisAlimentosViewModel = hiltViewModel(),
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    onAlimentoSelected: (Alimento, Float, String) -> Unit,
    onMiAlimentoSelected: (MisAlimentos, Float, String) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showQrScanner by remember { mutableStateOf(false) }
    var showDetalleBottomSheet by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf<Any?>(null) }

    val tipoComidaDefecto = "Desayuno" // Valor por defecto fijo

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Base de Datos",
                userPhotoUrl = null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            // Reutilizamos las mismas tabs que ya teníamos
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                ) {
                    Text("Búsqueda")
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
                    Text("QR")
                }
            }

            // Reutilizamos el mismo contenido de las tabs
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
                        // Manejar agregar nuevo alimento
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
                                selectedTabIndex = 1
                            }
                        )
                    }
                }
            }
        }
    }

    // Reutilizamos los mismos BottomSheets para detalles
    if (showDetalleBottomSheet && alimentoSeleccionado != null) {
        when (alimentoSeleccionado) {
            is Alimento -> DetalleAlimentoBottomSheet(
                alimento = alimentoSeleccionado as Alimento,
                tipoComidaInicial = tipoComidaDefecto,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad, _ ->
                    onAlimentoSelected(alimentoSeleccionado as Alimento, cantidad, tipoComidaDefecto)
                    showDetalleBottomSheet = false
                }
            )
            is MisAlimentos -> DetalleMiAlimentoBottomSheet(
                miAlimento = alimentoSeleccionado as MisAlimentos,
                tipoComidaInicial = tipoComidaDefecto,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad, _ ->
                    onMiAlimentoSelected(alimentoSeleccionado as MisAlimentos, cantidad, tipoComidaDefecto)
                    showDetalleBottomSheet = false
                }
            )
        }
    }
}