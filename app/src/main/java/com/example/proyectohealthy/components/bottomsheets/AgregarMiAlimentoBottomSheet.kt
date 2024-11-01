package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMiAlimentoBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (MisAlimentos) -> Unit,
    viewModel: MisAlimentosViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf(Constants.CATEGORIAS_ALIMENTOS[0]) }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcion by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    val windowInsets = WindowInsets.navigationBars

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        windowInsets=windowInsets

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Agregar Nuevo Alimento", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    Constants.CATEGORIAS_ALIMENTOS.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                categoriaSeleccionada = categoria
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = nombrePorcion,
                onValueChange = { nombrePorcion = it },
                label = { Text("Nombre de Porción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pesoPorcion,
                onValueChange = { pesoPorcion = it },
                label = { Text("Peso de Porción (g)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = calorias,
                onValueChange = { calorias = it },
                label = { Text("Calorías") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = proteinas,
                onValueChange = { proteinas = it },
                label = { Text("Proteínas (g)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = grasas,
                onValueChange = { grasas = it },
                label = { Text("Grasas (g)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = carbohidratos,
                onValueChange = { carbohidratos = it },
                label = { Text("Carbohidratos (g)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        val nuevoAlimento = MisAlimentos(
                            nombre = nombre.lowercase(),
                            marca = marca,
                            categoria = categoriaSeleccionada,
                            nombrePorcion = nombrePorcion,
                            pesoPorcion = pesoPorcion.toFloatOrNull() ?: 0f,
                            calorias = calorias.toIntOrNull() ?: 0,
                            proteinas = proteinas.toFloatOrNull() ?: 0f,
                            grasas = grasas.toFloatOrNull() ?: 0f,
                            carbohidratos = carbohidratos.toFloatOrNull() ?: 0f
                        )
                        onConfirm(nuevoAlimento)
                    }
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

