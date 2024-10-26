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

                // Botón para agregar el alimento
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

                // Botón para seleccionar el tipo de comida (con menú desplegable)
                Box {
                    Button(
                        onClick = { showTipoComidaMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(tipoComidaSeleccionado)
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar tipo de comida"
                            )
                        }
                    }

                    // Menú desplegable para cambiar el tipo de comida
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