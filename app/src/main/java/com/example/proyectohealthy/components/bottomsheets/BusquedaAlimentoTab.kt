package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    tipoComidaInicial: String, // Nuevo parámetro
    onDismiss: () -> Unit,
    onConfirm: (Float, String) -> Unit // Modificado para incluir el tipo de comida
) {
    var cantidad by remember { mutableStateOf("1") }
    var tipoComidaSeleccionado by remember { mutableStateOf(tipoComidaInicial) }
    var showTipoComidaMenu by remember { mutableStateOf(false) }

    val tiposComida = listOf("Desayuno", "Almuerzo", "Cena", "Snacks")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.78f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }

                Box {
                    // Botón principal con menú desplegable
                    Button(
                        onClick = {
                            // Si la cantidad es válida, mostrar el menú
                            if (cantidad.toFloatOrNull() != null) {
                                showTipoComidaMenu = true
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Agregar $tipoComidaSeleccionado")
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar tipo de comida"
                            )
                        }
                    }

                    // Menú desplegable para tipos de comida
                    DropdownMenu(
                        expanded = showTipoComidaMenu,
                        onDismissRequest = { showTipoComidaMenu = false }
                    ) {
                        tiposComida.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text("Agregar a $tipo") },
                                onClick = {
                                    cantidad.toFloatOrNull()?.let { cantidadFloat ->
                                        onConfirm(cantidadFloat, tipo)
                                    }
                                    showTipoComidaMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}