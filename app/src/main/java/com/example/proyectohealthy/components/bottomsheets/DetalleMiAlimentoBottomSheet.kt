package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.MisAlimentos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleMiAlimentoBottomSheet(
    miAlimento: MisAlimentos,
    tipoComidaInicial: String,
    onDismiss: () -> Unit,
    onConfirm: (Float, String) -> Unit
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
            Text(miAlimento.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Calorías: ${miAlimento.calorias}")
            Text("Proteínas: ${miAlimento.proteinas}g")
            Text("Carbohidratos: ${miAlimento.carbohidratos}g")
            Text("Grasas: ${miAlimento.grasas}g")
            Text("Sodio: ${miAlimento.sodio}mg")
            Text("Fibra: ${miAlimento.fibra}g")
            Text("Azúcares: ${miAlimento.azucares}g")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad (${miAlimento.nombrePorcion})") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Separar los botones en dos, uno para agregar y otro para seleccionar el tipo de comida
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }

                // Botón para "Agregar"
                Button(
                    onClick = {
                        cantidad.toFloatOrNull()?.let {
                            onConfirm(it, tipoComidaSeleccionado)
                        }
                    }
                ) {
                    Text("Agregar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón desplegable para seleccionar tipo de comida
                Box {
                    Button(
                        onClick = { showTipoComidaMenu = true }
                    ) {
                        Row {
                            Text(tipoComidaSeleccionado)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar tipo de comida"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTipoComidaMenu,
                        onDismissRequest = { showTipoComidaMenu = false }
                    ) {
                        tiposComida.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoComidaSeleccionado = tipo
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