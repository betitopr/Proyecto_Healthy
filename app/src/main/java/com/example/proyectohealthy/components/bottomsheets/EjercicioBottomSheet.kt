package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioBottomSheet(
    ejercicios: List<Ejercicio>,
    onDismiss: () -> Unit,
    onEjercicioSelected: (idEjercicio: String, duracion: Int) -> Unit
) {
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var duracion by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val ejerciciosFiltrados = remember(searchQuery, ejercicios) {
        if (searchQuery.isEmpty()) ejercicios
        else ejercicios.filter { it.nombre.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = {
            selectedEjercicio = null
            duracion = ""
            onDismiss()
        },
        modifier = Modifier.fillMaxHeight(0.9f),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Seleccionar Ejercicio",
                        style = MaterialTheme.typography.titleLarge
                    )
                    // Botón de cancelar en la cabecera
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }

                // Barra de búsqueda
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar ejercicio...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    singleLine = true
                )

                // Panel de selección dividido
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Lista de ejercicios (lado izquierdo)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(ejerciciosFiltrados) { ejercicio ->
                                EjercicioListItem(
                                    ejercicio = ejercicio,
                                    isSelected = selectedEjercicio?.id == ejercicio.id,
                                    onClick = { selectedEjercicio = ejercicio }
                                )
                            }
                        }
                    }

                    // Panel de detalles y duración (lado derecho)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            selectedEjercicio?.let { ejercicio ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (duracion.isNotEmpty()) {
                                                onEjercicioSelected(ejercicio.id, duracion.toInt())
                                                onDismiss()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = duracion.isNotEmpty()
                                    ) {
                                        Text("Agregar Ejercicio")
                                    }
                                    Text(
                                        text = ejercicio.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Calorías por minuto: ${ejercicio.caloriasPorMinuto}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    // Campo de duración
                                    OutlinedTextField(
                                        value = duracion,
                                        onValueChange = { if (it.all { char -> char.isDigit() }) duracion = it },
                                        label = { Text("Duración (minutos)") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Calorías estimadas
                                    if (duracion.isNotEmpty()) {
                                        val totalCalorias = ejercicio.caloriasPorMinuto * duracion.toInt()
                                        Text(
                                            text = "Calorías estimadas: $totalCalorias",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                }
                            } ?: run {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Selecciona un ejercicio",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EjercicioListItem(
    ejercicio: Ejercicio,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ejercicio.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${ejercicio.caloriasPorMinuto} cal/min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
