package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.FavoritosViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.util.Constants

@Composable
fun MisAlimentosTab(
    viewModel: MisAlimentosViewModel,
    favoritosViewModel: FavoritosViewModel = hiltViewModel(),
    onMiAlimentoSelected: (MisAlimentos) -> Unit,
    onAddMiAlimentoClick: () -> Unit,
    currentQuery: String
) {
    val misAlimentos by viewModel.misAlimentos.collectAsState()
    val favoritos by favoritosViewModel.alimentosFavoritos.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<MisAlimentos?>(null) }
    var showEditDialog by remember { mutableStateOf<MisAlimentos?>(null) }

    LaunchedEffect(currentQuery) {
        viewModel.searchMisAlimentosByNombre(currentQuery)
    }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                currentQuery.isNotEmpty() && misAlimentos.isEmpty() -> {
                    EmptySearchResult()
                }
                misAlimentos.isEmpty() -> {
                    EmptyMisAlimentosContent()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }

    // Diálogo de edición
    showEditDialog?.let { miAlimento ->
        EditarMiAlimentoBottomSheet(
            miAlimento = miAlimento,
            onDismiss = { showEditDialog = null },
            onConfirm = { alimentoEditado ->
                viewModel.updateMiAlimento(alimentoEditado)
                showEditDialog = null
            },
            viewModel = viewModel
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
                        viewModel.deleteMiAlimento(miAlimento.id, favoritosViewModel)
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
fun EmptyMisAlimentosContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Text(
            text = "No tienes alimentos personalizados",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Agrega tus propios alimentos para un seguimiento más personalizado",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Presiona el botón '+' para agregar tu primer alimento",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptySearchResult() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Text(
            text = "No se encontraron resultados",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Intenta con una búsqueda diferente",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarMiAlimentoBottomSheet(
    miAlimento: MisAlimentos,
    onDismiss: () -> Unit,
    onConfirm: (MisAlimentos) -> Unit,
    viewModel: MisAlimentosViewModel,
    perfilViewModel: PerfilViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf(miAlimento.nombre) }
    var marca by remember { mutableStateOf(miAlimento.marca) }
    var categoriaSeleccionada by remember { mutableStateOf(miAlimento.categoria) }
    var nombrePorcion by remember { mutableStateOf(miAlimento.nombrePorcion) }
    var pesoPorcionOriginal by remember { mutableStateOf(miAlimento.pesoPorcion) }
    var pesoPorcionMostrado by remember { mutableStateOf("") }
    var unidadPorcion by remember { mutableStateOf(miAlimento.unidadPorcion) }
    var calorias by remember { mutableStateOf(miAlimento.calorias.toString()) }
    var proteinas by remember { mutableStateOf(miAlimento.proteinas.toString()) }
    var grasas by remember { mutableStateOf(miAlimento.grasas.toString()) }
    var carbohidratos by remember { mutableStateOf(miAlimento.carbohidratos.toString()) }
    var expandedUnidades by remember { mutableStateOf(false) }
    var showCategoriasSheet by remember { mutableStateOf(false) }

    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"

    // Unidades de peso disponibles según el sistema
    val unidadesPeso = if (sistemaPeso.contains("Imperial")) {
        listOf("oz", "fl oz", "lb")
    } else {
        listOf("g", "kg", "ml", "L")
    }

    // Funciones de conversión
    fun convertirAGramos(valor: Float, unidad: String): Float {
        return when (unidad) {
            "kg" -> valor * 1000
            "oz" -> valor * 28.3495f
            "fl oz" -> valor * 29.5735f
            "lb" -> valor * 453.592f
            "ml" -> valor
            "L" -> valor * 1000
            else -> valor // gramos
        }
    }

    fun convertirDesdeGramos(valorEnGramos: Float, unidadDestino: String): Float {
        return when (unidadDestino) {
            "kg" -> valorEnGramos / 1000
            "oz" -> valorEnGramos / 28.3495f
            "fl oz" -> valorEnGramos / 29.5735f
            "lb" -> valorEnGramos / 453.592f
            "ml" -> valorEnGramos
            "L" -> valorEnGramos / 1000
            else -> valorEnGramos
        }
    }

    // Inicializar valores según el sistema
    LaunchedEffect(sistemaPeso) {
        unidadPorcion = if (sistemaPeso.contains("Imperial")) "oz" else "g"
        val valorMostrado = convertirDesdeGramos(pesoPorcionOriginal, unidadPorcion)
        pesoPorcionMostrado = String.format("%.2f", valorMostrado)
    }

    // Inicializar el valor según la unidad original
    LaunchedEffect(Unit) {
        if (sistemaPeso.contains("Imperial")) {
            // Si está en sistema imperial, verificar si la unidad guardada es imperial
            unidadPorcion = when (miAlimento.unidadPorcion) {
                "oz", "fl oz", "lb" -> miAlimento.unidadPorcion
                else -> "oz" // Por defecto si viene en métrico
            }
        } else {
            // Si está en sistema métrico, verificar si la unidad guardada es métrica
            unidadPorcion = when (miAlimento.unidadPorcion) {
                "g", "kg", "ml", "L" -> miAlimento.unidadPorcion
                else -> "g" // Por defecto si viene en imperial
            }
        }

        // Convertir el valor según la unidad original
        val valorMostrado = convertirDesdeGramos(miAlimento.pesoPorcion, unidadPorcion)
        pesoPorcionMostrado = String.format("%.2f", valorMostrado)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 56.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Editar Alimento",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    placeholder = { Text("Selecciona una categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showCategoriasSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar categoría"
                            )
                        }
                    }
                )

                if (showCategoriasSheet) {
                    SelectorCategoriasBottomSheet(
                        categoriaSeleccionada = categoriaSeleccionada,
                        onCategoriaSelected = { categoria ->
                            categoriaSeleccionada = categoria
                        },
                        onDismiss = { showCategoriasSheet = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombrePorcion,
                    onValueChange = { nombrePorcion = it },
                    label = { Text("Nombre de Porción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = pesoPorcionMostrado,
                        onValueChange = { nuevoValor ->
                            pesoPorcionMostrado = nuevoValor
                            nuevoValor.toFloatOrNull()?.let { valor ->
                                pesoPorcionOriginal = convertirAGramos(valor, unidadPorcion)
                            }
                        },
                        label = { Text("Peso de Porción") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedUnidades,
                        onExpandedChange = { expandedUnidades = it }
                    ) {
                        OutlinedTextField(
                            value = unidadPorcion,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad") },
                            modifier = Modifier
                                .menuAnchor()
                                .width(100.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnidades)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expandedUnidades,
                            onDismissRequest = { expandedUnidades = false }
                        ) {
                            unidadesPeso.forEach { unidad ->
                                DropdownMenuItem(
                                    text = { Text(unidad) },
                                    onClick = {
                                        unidadPorcion = unidad
                                        expandedUnidades = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = calorias,
                    onValueChange = { calorias = it },
                    label = { Text("Calorías") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = proteinas,
                    onValueChange = { proteinas = it },
                    label = { Text("Proteínas (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = grasas,
                    onValueChange = { grasas = it },
                    label = { Text("Grasas (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            val alimentoActualizado = miAlimento.copy(
                                nombre = nombre.lowercase(),
                                marca = marca,
                                categoria = categoriaSeleccionada,
                                nombrePorcion = nombrePorcion,
                                pesoPorcion = pesoPorcionOriginal,
                                unidadPorcion = unidadPorcion,
                                calorias = calorias.toIntOrNull() ?: 0,
                                proteinas = proteinas.toFloatOrNull() ?: 0f,
                                grasas = grasas.toFloatOrNull() ?: 0f,
                                carbohidratos = carbohidratos.toFloatOrNull() ?: 0f
                            )
                            onConfirm(alimentoActualizado)
                        }
                    ) {
                        Text("Guardar")
                    }
                }
                if (showCategoriasSheet) {
                    SelectorCategoriasBottomSheet(
                        categoriaSeleccionada = categoriaSeleccionada,
                        onCategoriaSelected = { categoria ->
                            categoriaSeleccionada = categoria
                        },
                        onDismiss = { showCategoriasSheet = false }
                    )
                }
            }
        }
    }
}

// Funciones de conversión mejoradas
fun convertirAGramos(valor: Float, unidad: String): Float {
    return when (unidad) {
        "kg" -> valor * 1000
        "oz" -> valor * 28.3495f
        "fl oz" -> valor * 29.5735f
        "lb" -> valor * 453.592f
        "ml" -> valor
        "L" -> valor * 1000
        else -> valor // gramos
    }
}

fun convertirDesdeGramos(valorEnGramos: Float, unidadDestino: String): Float {
    return when (unidadDestino) {
        "kg" -> valorEnGramos / 1000
        "oz" -> valorEnGramos / 28.3495f
        "fl oz" -> valorEnGramos / 29.5735f
        "lb" -> valorEnGramos / 453.592f
        "ml" -> valorEnGramos
        "L" -> valorEnGramos / 1000
        else -> valorEnGramos
    }
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
        Column(modifier = Modifier.padding(16.dp)) {
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

                Row {
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