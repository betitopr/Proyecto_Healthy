package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel

@Composable
fun BusquedaAlimentoTab(
    viewModel: AlimentoViewModel,
    onAlimentoSelected: (Alimento) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val alimentos by viewModel.alimentos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchAlimentosByNombre(it)
            },
            label = { Text("Buscar comida") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(alimentos) { alimento ->
                AlimentoItem(
                    alimento = alimento,
                    onClick = { onAlimentoSelected(alimento) }
                )
            }
        }
    }
}

@Composable
fun AlimentoItem(
    alimento: Alimento,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = alimento.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${alimento.calorias} kcal por ${alimento.nombrePorcion}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAlimentoBottomSheet(
    alimento: Alimento,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var cantidad by remember { mutableStateOf("1") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.78f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding() // Ajusta el contenido cuando aparece el teclado
                .navigationBarsPadding() // Agrega padding para la barra de navegación
                .verticalScroll(rememberScrollState()) // Permite desplazamiento si el contenido no cabe
        ) {
            Text(alimento.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Calorías: ${alimento.calorias}")
            Text("Proteínas: ${alimento.proteinas}g")
            Text("Carbohidratos: ${alimento.carbohidratos}g")
            Text("Grasas: ${alimento.grasas}g")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad (${alimento.nombrePorcion})") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        cantidad.toFloatOrNull()?.let { onConfirm(it) }
                    }
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}