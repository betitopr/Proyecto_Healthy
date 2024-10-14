package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentoBottomSheet(
    onDismiss: () -> Unit,
    onAlimentoSelected: (Alimento, Float) -> Unit,
    viewModel: AlimentoViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showDetalleBottomSheet by remember { mutableStateOf(false) }
    var alimentoSeleccionado by remember { mutableStateOf<Alimento?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column {
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
                    viewModel = viewModel,
                    onAlimentoSelected = { alimento ->
                        alimentoSeleccionado = alimento
                        showDetalleBottomSheet = true
                    }
                )
                1 -> Text("Ingreso Manual (No implementado)")
                2 -> Text("Escaneo QR (No implementado)")
            }
        }
    }

    if (showDetalleBottomSheet && alimentoSeleccionado != null) {
        DetalleAlimentoBottomSheet(
            alimento = alimentoSeleccionado!!,
            onDismiss = { showDetalleBottomSheet = false },
            onConfirm = { cantidad ->
                onAlimentoSelected(alimentoSeleccionado!!, cantidad)
                showDetalleBottomSheet = false
                onDismiss()
            }
        )
    }
}
