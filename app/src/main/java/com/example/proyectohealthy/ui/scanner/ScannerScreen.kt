package com.example.proyectohealthy.ui.scanner

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.remote.Product
import com.example.proyectohealthy.ui.viewmodel.ScannerViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.util.UUID

@Composable
fun ScannerScreen(viewModel: ScannerViewModel = hiltViewModel(), navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    var resultadoEscaneo by remember { mutableStateOf("") }
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { barcode ->
                viewModel.getProductInfo(barcode)
            }
        }
    )

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(Unit) {
            val options = ScanOptions()
            options.setOrientationLocked(false)
            options.setPrompt("Escanea el código de barras")
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(true)
            scanLauncher.launch(options)
        }

        when (uiState) {
            is ScannerViewModel.UiState.Initial -> CircularProgressIndicator()
            is ScannerViewModel.UiState.Loading -> CircularProgressIndicator()
            is ScannerViewModel.UiState.Success -> ProductInfo(
                (uiState as ScannerViewModel.UiState.Success).product,
                viewModel,
                navController
            )
            is ScannerViewModel.UiState.Error -> Text(
                (uiState as ScannerViewModel.UiState.Error).message,
                color = Color.Red
            )
        }
    }
}

@Composable
fun ProductInfo(product: Product, viewModel: ScannerViewModel, navController: NavController) {
    var cantidad by remember { mutableStateOf("5") }
    var porcion by remember { mutableStateOf("5.3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = product.product_name ?: "Nombre desconocido", style = MaterialTheme.typography.bodyLarge)
        Text(text = product.brands ?: "Marca desconocida", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Datos por 100g", style = MaterialTheme.typography.bodySmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NutritionBox("Calorías", product.nutriments?.energy_100g?.toString() ?: "N/A")
            NutritionBox("Proteínas", product.nutriments?.proteins_100g?.toString() ?: "N/A")
            NutritionBox("Carbs", product.nutriments?.carbohydrates_100g?.toString() ?: "N/A")
            NutritionBox("Grasas", product.nutriments?.fat_100g?.toString() ?: "N/A")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = porcion,
                onValueChange = { porcion = it },
                label = { Text("Porción") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val alimento = Alimento(
                    id = UUID.randomUUID().toString(), // Genera un ID único
                    nombre = product.product_name ?: "Desconocido",
                    marca = product.brands ?: "Desconocida",
                    calorias = product.nutriments?.energy_100g ?: 0f,
                    proteinas = product.nutriments?.proteins_100g ?: 0f,
                    carbohidratos = product.nutriments?.carbohydrates_100g ?: 0f,
                    grasas = product.nutriments?.fat_100g ?: 0f,
                    grasasSaturadas = product.nutriments?.saturated_fat_100g ?: 0f,
                    azucares = product.nutriments?.sugars_100g ?: 0f,
                    fibra = product.nutriments?.fiber_100g ?: 0f,
                    sodio = (product.nutriments?.salt_100g ?: 0f) * 400f, // Convertir sal a sodio (aproximado)
                    pesoPorcion = cantidad.toFloatOrNull() ?: 0f,
                    nombrePorcion = porcion
                )
                viewModel.saveAlimento(alimento)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar a Desayuno")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Información Nutricional", style = MaterialTheme.typography.bodyMedium)
        Text("Datos por 100g", style = MaterialTheme.typography.bodyMedium)

        NutritionInfoItem("Calorías", product.nutriments?.energy_100g?.toString() + " kcal")
        NutritionInfoItem("Grasas", product.nutriments?.fat_100g?.toString() + " g")
        NutritionInfoItem("Grasas Saturadas", product.nutriments?.saturated_fat_100g?.toString() + " g")
        NutritionInfoItem("Carbohidratos", product.nutriments?.carbohydrates_100g?.toString() + " g")
        NutritionInfoItem("Azúcares", product.nutriments?.sugars_100g?.toString() + " g")
        NutritionInfoItem("Fibra", product.nutriments?.fiber_100g?.toString() + " g")
        NutritionInfoItem("Proteínas", product.nutriments?.proteins_100g?.toString() + " g")
        NutritionInfoItem("Sal", product.nutriments?.salt_100g?.toString() + " g")
    }
}

@Composable
fun NutritionBox(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        Text(text = label)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NutritionInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}