package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroAlimentoSheet(
    onDismiss: () -> Unit,
    onAlimentoSelected: (Alimento, Float, String) -> Unit,
    onMiAlimentoSelected: (MisAlimentos, Float, String) -> Unit,
    alimentoViewModel: AlimentoViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel,
    tipoComidaSeleccionado: String,
    modifier: Modifier = Modifier
) {
    val searchViewModel: SearchViewModel = hiltViewModel()
    val scannerViewModel: ScannerViewModel = hiltViewModel()
    val searchQuery by searchViewModel.searchQuery.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.85f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        windowInsets = WindowInsets(0, 0, 0, 0)  // Esto es clave
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()  // Padding para los botones de navegación
                .imePadding()            // Padding para el teclado
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)  // Padding extra para asegurar espacio
            ) {
                // Tu contenido actual del BottomSheet
                AlimentoContent(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        searchViewModel.updateSearchQuery(query)
                    },
                    onAlimentoSelected = { alimento, cantidad, tipoComida ->
                        onAlimentoSelected(alimento, cantidad, tipoComida)
                        onDismiss()
                    },
                    onMiAlimentoSelected = { miAlimento, cantidad, tipoComida ->
                        onMiAlimentoSelected(miAlimento, cantidad, tipoComida)
                        onDismiss()
                    },
                    alimentoViewModel = alimentoViewModel,
                    misAlimentosViewModel = misAlimentosViewModel,
                    favoritosViewModel = favoritosViewModel,
                    scannerViewModel = scannerViewModel,
                    tipoComidaSeleccionado = tipoComidaSeleccionado,
                    modifier = Modifier.weight(1f)
                )
            }
        }

    }

    // Efecto para limpiar el estado al cerrar
    DisposableEffect(Unit) {
        onDispose {
            scannerViewModel.resetState()
            searchViewModel.updateSearchQuery("")
        }
    }
}