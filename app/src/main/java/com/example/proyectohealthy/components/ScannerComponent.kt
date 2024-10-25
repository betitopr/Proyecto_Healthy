package com.example.proyectohealthy.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.util.Date

@Composable
fun ScannerComponent(
    scannerViewModel: ScannerViewModel,
    setAlimentoSeleccionado: (Any?) -> Unit,
    setShowDetalleBottomSheet: (Boolean) -> Unit,

) {
    val uiState by scannerViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasShownBottomSheet by remember { mutableStateOf(false) }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                scannerViewModel.getProductInfo(barcode)
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            scannerViewModel.resetState()
            hasShownBottomSheet = false
            setShowDetalleBottomSheet(false)
            setAlimentoSeleccionado(null)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ScannerViewModel.UiState.Success -> {
                if (!hasShownBottomSheet) {
                    val miAlimento = (uiState as ScannerViewModel.UiState.Success).miAlimento
                    setAlimentoSeleccionado(miAlimento)
                    setShowDetalleBottomSheet(true)
                    hasShownBottomSheet = true
                }
            }
            is ScannerViewModel.UiState.Error -> {
                Toast.makeText(context, (uiState as ScannerViewModel.UiState.Error).message, Toast.LENGTH_LONG).show()
                setShowDetalleBottomSheet(false)
                hasShownBottomSheet = false
                scannerViewModel.resetState()
            }
            is ScannerViewModel.UiState.Initial -> {
                hasShownBottomSheet = false
            }
            else -> {}
        }
    }

    IconButton(
        onClick = {
            val options = ScanOptions().apply {
                setOrientationLocked(false)
                setPrompt("Escanea el código de barras")
                setBeepEnabled(true)
                setBarcodeImageEnabled(true)
            }
            scanLauncher.launch(options)
        },
        modifier = Modifier.size(36.dp)
    ) {
        Icon(Icons.Default.QrCodeScanner, "Escanear")
    }

    if (uiState is ScannerViewModel.UiState.Loading) {
        CircularProgressIndicator()
    }
}