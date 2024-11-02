package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
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

    var keyboardVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 50.dp)
        ) {
            // Encabezado fijo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = miAlimento.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (miAlimento.marca.isNotBlank()) {
                    Text(
                        text = miAlimento.marca,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoColumn("Calorías", "${miAlimento.calorias}")
                        InfoColumn("Proteínas", "${miAlimento.proteinas}g")
                        InfoColumn("Carbos", "${miAlimento.carbohidratos}g")
                        InfoColumn("Grasas", "${miAlimento.grasas}g")
                    }
                }
            }

            HorizontalDivider()

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Selector de cantidad
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = {
                        Text(
                            "Cantidad (${miAlimento.nombrePorcion} - ${miAlimento.unidadPorcion})"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Selector de tipo de comida
                ExposedDropdownMenuBox(
                    expanded = showTipoComidaMenu,
                    onExpandedChange = { showTipoComidaMenu = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = tipoComidaSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Comida") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTipoComidaMenu)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
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

                // Resumen nutricional por porción seleccionada
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Total con la cantidad seleccionada:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val cantidadNum = cantidad.toFloatOrNull() ?: 0f
                        InfoRow("Calorías", "${(miAlimento.calorias * cantidadNum).toInt()} kcal")
                        InfoRow("Proteínas", "${String.format("%.1f", miAlimento.proteinas * cantidadNum)}g")
                        InfoRow("Carbohidratos", "${String.format("%.1f", miAlimento.carbohidratos * cantidadNum)}g")
                        InfoRow("Grasas", "${String.format("%.1f", miAlimento.grasas * cantidadNum)}g")

                        // Información nutricional adicional si está disponible
                        if (miAlimento.fibra > 0) {
                            InfoRow("Fibra", "${String.format("%.1f", miAlimento.fibra * cantidadNum)}g")
                        }
                        if (miAlimento.azucares > 0) {
                            InfoRow("Azúcares", "${String.format("%.1f", miAlimento.azucares * cantidadNum)}g")
                        }
                        if (miAlimento.sodio > 0) {
                            InfoRow("Sodio", "${String.format("%.1f", miAlimento.sodio * cantidadNum)}mg")
                        }
                    }
                }

                // Información adicional
                if (miAlimento.categoria.isNotBlank() && miAlimento.categoria != "Sin categoria asignada") {
                    Text(
                        text = "Categoría: ${miAlimento.categoria}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        cantidad.toFloatOrNull()?.let { cantidadNum ->
                            onConfirm(cantidadNum, tipoComidaSeleccionado)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = cantidad.toFloatOrNull() != null
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}