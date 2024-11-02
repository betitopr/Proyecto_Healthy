package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel

@Composable
fun BusquedaAlimentoTab(
    viewModel: AlimentoViewModel,
    favoritosViewModel: FavoritosViewModel,
    onAlimentoSelected: (Alimento) -> Unit,
    currentQuery: String
) {
    val alimentos by viewModel.alimentos.collectAsState()
    val alimentosFavoritos by favoritosViewModel.alimentosFavoritos.collectAsState()

    LaunchedEffect(currentQuery) {
        viewModel.searchAlimentosByNombre(currentQuery)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (alimentos.isEmpty()) {
            if (currentQuery.isNotEmpty()) {
                EmptySearchResult()
            } else {
                LoadingIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alimentos) { alimento ->
                    AlimentoItem(
                        alimento = alimento,
                        isFavorito = alimentosFavoritos.containsKey(alimento.id),
                        onFavoritoClick = {
                            favoritosViewModel.toggleFavorito(alimento.id, 1)
                        },
                        onClick = { onAlimentoSelected(alimento) }
                    )
                }
            }
        }
    }
}
@Composable
fun AlimentoItem(
    alimento: Alimento,
    isFavorito: Boolean,
    onFavoritoClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alimento.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${alimento.calorias} kcal por ${alimento.nombrePorcion}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onFavoritoClick) {
                AnimatedContent(
                    targetState = isFavorito,
                    transitionSpec = {
                        scaleIn() togetherWith scaleOut()
                    }
                ) { esFavorito ->
                    Icon(
                        imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (esFavorito) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.scale(if (esFavorito) 1.2f else 1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAlimentoBottomSheet(
    alimento: Alimento,
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
                    text = alimento.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

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
                        InfoColumn("Calorías", "${alimento.calorias}")
                        InfoColumn("Proteínas", "${alimento.proteinas}g")
                        InfoColumn("Carbos", "${alimento.carbohidratos}g")
                        InfoColumn("Grasas", "${alimento.grasas}g")
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
                    label = { Text("Cantidad (${alimento.nombrePorcion})") },
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
                        InfoRow("Calorías", "${(alimento.calorias * cantidadNum).toInt()} kcal")
                        InfoRow("Proteínas", "${String.format("%.1f", alimento.proteinas * cantidadNum)}g")
                        InfoRow("Carbohidratos", "${String.format("%.1f", alimento.carbohidratos * cantidadNum)}g")
                        InfoRow("Grasas", "${String.format("%.1f", alimento.grasas * cantidadNum)}g")
                    }
                }
            }

            // Botones de acción siempre visibles al final
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