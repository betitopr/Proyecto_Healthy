package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
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
    var busquedaCategoria by remember { mutableStateOf("") }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcion by remember { mutableStateOf("") }
    var unidadPorcion by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedUnidades by remember { mutableStateOf(false) }

    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    val sistemaPeso = currentPerfil?.unidadesPreferences?.sistemaPeso ?: "Métrico (kg)"

    // Unidades de peso disponibles
    val unidadesPeso = listOf("g", "kg", "oz", "fl oz", "ml", "L")

    // Establecer unidad predeterminada según preferencias
    LaunchedEffect(sistemaPeso) {
        unidadPorcion = if (sistemaPeso.contains("Imperial")) "oz" else "g"
    }

    // Función para convertir a gramos
    fun convertirAGramos(valor: Float, unidad: String): Float {
        return when (unidad) {
            "kg" -> valor * 1000
            "oz" -> valor * 28.3495f
            "fl oz" -> valor * 29.5735f
            "ml" -> valor
            "L" -> valor * 1000
            else -> valor // gramos
        }
    }

    // Filtrar categorías según búsqueda
    val categoriasFiltradas = Constants.CATEGORIAS_ALIMENTOS.filter {
        it.lowercase().contains(busquedaCategoria.lowercase())
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

                Spacer(modifier = Modifier.height(8.dp))

                // Categoría con búsqueda
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = it }
                ) {
                    OutlinedTextField(
                        value = busquedaCategoria,
                        onValueChange = {
                            busquedaCategoria = it
                            expandedCategoria = true
                        },
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
                        categoriasFiltradas.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    busquedaCategoria = categoria
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre de porción
                OutlinedTextField(
                    value = nombrePorcion,
                    onValueChange = { nombrePorcion = it },
                    label = { Text("Nombre de Porción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Peso de porción con selector de unidades
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically  // Centra verticalmente los componentes
                ) {
                    OutlinedTextField(
                        value = pesoPorcion,
                        onValueChange = { pesoPorcion = it },
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
                            label = { Text("Unidad") },  // Añade una etiqueta para alinear visualmente
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
                                    modifier = Modifier.padding(vertical = 2.dp),
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

                // Campos nutricionales
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

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val pesoEnGramos = pesoPorcion.toFloatOrNull()?.let { peso ->
                                convertirAGramos(peso, unidadPorcion)
                            } ?: 0f

                            val nuevoAlimento = MisAlimentos(
                                nombre = nombre.lowercase(),
                                marca = marca,
                                categoria = categoriaSeleccionada,
                                nombrePorcion = nombrePorcion,
                                pesoPorcion = pesoEnGramos,
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

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

