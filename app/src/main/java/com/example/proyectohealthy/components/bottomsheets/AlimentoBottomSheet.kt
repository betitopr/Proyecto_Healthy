package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentoBottomSheet(
    onDismiss: () -> Unit,
    onAlimentoSelected: (Alimento, Float) -> Unit,
    onMiAlimentoSelected: (MisAlimentos, Float) -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showDetalleBottomSheet by remember { mutableStateOf(false) }
    var showAgregarMiAlimentoBottomSheet by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf<Any?>(null) }

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
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
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
                2 -> Text("Escaneo QR (No implementado)")
            }
        }
    }

    if (showDetalleBottomSheet && alimentoSeleccionado != null) {
        when (alimentoSeleccionado) {
            is Alimento -> DetalleAlimentoBottomSheet(
                alimento = alimentoSeleccionado as Alimento,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad ->
                    onAlimentoSelected(alimentoSeleccionado as Alimento, cantidad)
                    showDetalleBottomSheet = false
                    onDismiss()
                }
            )
            is MisAlimentos -> DetalleMiAlimentoBottomSheet(
                miAlimento = alimentoSeleccionado as MisAlimentos,
                onDismiss = { showDetalleBottomSheet = false },
                onConfirm = { cantidad ->
                    onMiAlimentoSelected(alimentoSeleccionado as MisAlimentos, cantidad)
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