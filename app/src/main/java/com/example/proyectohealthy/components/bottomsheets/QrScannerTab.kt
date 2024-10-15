package com.example.proyectohealthy.components.bottomsheets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.util.Date

@Composable
fun QrScannerTab(
    scannerViewModel: ScannerViewModel,
    misAlimentosViewModel: MisAlimentosViewModel,
    onMiAlimentoCreated: (MisAlimentos) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by scannerViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ScannerViewModel.UiState.Loading -> CircularProgressIndicator()
        is ScannerViewModel.UiState.Error -> Text((uiState as ScannerViewModel.UiState.Error).message, color = Color.Red)
        is ScannerViewModel.UiState.Success -> {

            val product = state.product
            LaunchedEffect(product) {
                val miAlimento = MisAlimentos(
                    nombre = product.product_name ?: "Desconocido",
                    marca = product.brands ?: "Desconocida",
                    categoria = product.categories?.split(',')?.firstOrNull() ?: "Sin categoría",
                    nombrePorcion = "100g",
                    pesoPorcion = 100f,
                    calorias = product.nutriments?.energy_100g?.toInt() ?: 0,
                    proteinas = product.nutriments?.proteins_100g ?: 0f,
                    carbohidratos = product.nutriments?.carbohydrates_100g ?: 0f,
                    grasas = product.nutriments?.fat_100g ?: 0f,
                    grasasSaturadas = product.nutriments?.saturated_fat_100g ?: 0f,
                    grasasTrans = 0f,
                    sodio = (product.nutriments?.salt_100g ?: 0f) * 400f,
                    fibra = product.nutriments?.fiber_100g ?: 0f,
                    azucares = product.nutriments?.sugars_100g ?: 0f,
                    codigoQr = "",
                    diaCreado = Date()
                )
                misAlimentosViewModel.createOrUpdateMiAlimento(miAlimento)
                onMiAlimentoCreated(miAlimento)
            }
        }
        is ScannerViewModel.UiState.Error -> {
            Text(state.message, color = Color.Red)
            Button(onClick = onDismiss) {
                Text("Volver")
            }
        }
        else -> {
            Text("Escaneando...")
        }
    }
}