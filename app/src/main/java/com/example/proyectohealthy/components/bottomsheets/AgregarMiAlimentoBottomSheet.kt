package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.util.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgregarMiAlimentoBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (MisAlimentos) -> Unit,
    viewModel: MisAlimentosViewModel,
    perfilViewModel: PerfilViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcionOriginal by remember { mutableStateOf(0f) }
    var pesoPorcionMostrado by remember { mutableStateOf("") }
    var unidadPorcion by remember { mutableStateOf("g") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var expandedUnidades by remember { mutableStateOf(false) }

    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"
    var showCategoriasSheet by remember { mutableStateOf(false) }

    LaunchedEffect(sistemaPeso) {
        // Establecer unidad por defecto según el sistema
        unidadPorcion = if (sistemaPeso.contains("Imperial")) "oz" else "g"

        // Si hay un valor existente, convertirlo a la unidad correspondiente
        if (pesoPorcionOriginal > 0) {
            val valorMostrado = convertirDesdeGramos(pesoPorcionOriginal, unidadPorcion)
            pesoPorcionMostrado = String.format("%.2f", valorMostrado)
        }
    }

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

    // Efecto para actualizar el valor mostrado cuando cambia la unidad
    LaunchedEffect(unidadPorcion) {
        val nuevoValorMostrado = convertirDesdeGramos(pesoPorcionOriginal, unidadPorcion)
        pesoPorcionMostrado = String.format("%.2f", nuevoValorMostrado)
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
                    "Agregar Nuevo Alimento",
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

                Text(
                    "Categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))


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
                            val nuevoAlimento = MisAlimentos(
                                nombre = nombre.lowercase(),
                                marca = marca,
                                categoria = categoriaSeleccionada,
                                nombrePorcion = nombrePorcion,
                                pesoPorcion = pesoPorcionOriginal, // Siempre guardamos en gramos
                                unidadPorcion = unidadPorcion,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCategoriasBottomSheet(
    categoriaSeleccionada: String,
    onCategoriaSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f),
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Cabecera fija con título y botón cerrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Seleccionar Categoría",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }

                HorizontalDivider()

                // Lista scrolleable de categorías
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(Constants.CATEGORIAS_ALIMENTOS) { categoria ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (categoria == categoriaSeleccionada)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategoriaSelected(categoria)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 16.dp,
                                        vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = categoria == categoriaSeleccionada,
                                    onClick = {
                                        onCategoriaSelected(categoria)
                                        onDismiss()
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = categoria,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (categoria == categoriaSeleccionada)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Espacio adicional al final para evitar que el último ítem quede oculto
                    item {
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
            }
        }
    }
}
