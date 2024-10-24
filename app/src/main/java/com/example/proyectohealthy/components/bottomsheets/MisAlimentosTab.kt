package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel

@Composable
fun MisAlimentosTab(
    viewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel = hiltViewModel(), // Agregamos ViewModel de favoritos
    onMiAlimentoSelected: (MisAlimentos) -> Unit,
    onAddMiAlimentoClick: () -> Unit,
    currentQuery: String
) {
    val misAlimentos by viewModel.misAlimentos.collectAsState()
    val favoritos by favoritosViewModel.alimentosFavoritos.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<MisAlimentos?>(null) }
    var showEditDialog by remember { mutableStateOf<MisAlimentos?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddMiAlimentoClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar Nuevo Alimento")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(misAlimentos) { miAlimento ->
                MiAlimentoItem(
                    miAlimento = miAlimento,
                    isFavorito = favoritos.containsKey(miAlimento.id),
                    onFavoritoClick = {
                        favoritosViewModel.toggleFavorito(miAlimento.id, 2)
                    },
                    onEditClick = {
                        showEditDialog = miAlimento
                    },
                    onDeleteClick = {
                        showDeleteDialog = miAlimento
                    },
                    onClick = { onMiAlimentoSelected(miAlimento) }
                )
            }
        }
    }

    showEditDialog?.let { miAlimento ->
        EditarMiAlimentoDialog(
            miAlimento = miAlimento,
            onDismiss = { showEditDialog = null },
            onConfirm = { alimentoEditado ->
                viewModel.updateMiAlimento(alimentoEditado)
                showEditDialog = null
            }
        )
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { miAlimento ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar eliminación") },
            text = {
                Text("¿Estás seguro de que quieres eliminar '${miAlimento.nombre}'?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMiAlimento(miAlimento.id,favoritosViewModel)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EditarMiAlimentoDialog(
    miAlimento: MisAlimentos,
    onDismiss: () -> Unit,
    onConfirm: (MisAlimentos) -> Unit
) {
    var nombre by remember { mutableStateOf(miAlimento.nombre) }
    var marca by remember { mutableStateOf(miAlimento.marca) }
    var calorias by remember { mutableStateOf(miAlimento.calorias.toString()) }
    var proteinas by remember { mutableStateOf(miAlimento.proteinas.toString()) }
    var carbohidratos by remember { mutableStateOf(miAlimento.carbohidratos.toString()) }
    var grasas by remember { mutableStateOf(miAlimento.grasas.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar alimento") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = calorias,
                    onValueChange = { calorias = it },
                    label = { Text("Calorías") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                // Agrega más campos según necesites
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val alimentoEditado = miAlimento.copy(
                        nombre = nombre,
                        marca = marca,
                        calorias = calorias.toIntOrNull() ?: 0,
                        proteinas = proteinas.toFloatOrNull() ?: 0f,
                        carbohidratos = carbohidratos.toFloatOrNull() ?: 0f,
                        grasas = grasas.toFloatOrNull() ?: 0f
                    )
                    onConfirm(alimentoEditado)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun MiAlimentoItem(
    miAlimento: MisAlimentos,
    isFavorito: Boolean,
    onFavoritoClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = miAlimento.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${miAlimento.calorias} kcal por ${miAlimento.nombrePorcion}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Botones de acción
                Row {
                    IconButton(onClick = onFavoritoClick) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorito) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    IconButton(
                        onClick = { showConfirmDelete = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                }
            }
        }
    }
// Diálogo de confirmación para eliminar
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar este alimento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showConfirmDelete = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDelete = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}