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
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        // Reutilizamos el contenido compartido
        AlimentoContent(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchViewModel.updateSearchQuery(query)
            },
            onAlimentoSelected = onAlimentoSelected,
            onMiAlimentoSelected = onMiAlimentoSelected,
            alimentoViewModel = alimentoViewModel,
            misAlimentosViewModel = misAlimentosViewModel,
            favoritosViewModel = favoritosViewModel,
            scannerViewModel = scannerViewModel,
            tipoComidaSeleccionado = tipoComidaSeleccionado,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }

    // Efecto para limpiar el estado al cerrar
    DisposableEffect(Unit) {
        onDispose {
            scannerViewModel.resetState()
            searchViewModel.updateSearchQuery("")
        }
    }
}