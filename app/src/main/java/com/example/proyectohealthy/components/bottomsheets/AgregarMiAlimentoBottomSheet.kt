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
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMiAlimentoBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (MisAlimentos) -> Unit,
    viewModel: MisAlimentosViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcion by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
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
                            nombre = nombre,
                            marca = marca,
                            categoria = categoria,
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

