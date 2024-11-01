package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.NutrientRange
import com.example.proyectohealthy.data.local.entity.OrderType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.focus.onFocusChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosBottomSheet(
    mostrar: Boolean,
    filtrosActuales: AlimentoFiltros,
    categoriasDisponibles: List<String>,
    onDismiss: () -> Unit,
    onFiltrosChanged: (AlimentoFiltros) -> Unit
) {
    var filtrosTemporales by remember(filtrosActuales) {
        mutableStateOf(filtrosActuales)
    }
    var selectedTab by remember { mutableStateOf(0) }

    if (mostrar) {
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
                        .padding(bottom = 56.dp)
                ) {
                    // Título
                    Text(
                        "Filtros y Ordenamiento",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(10.dp)
                    )

                    // Pestañas
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        ) { Text("Ordenar") }
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        ) { Text("Categorías") }
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        ) { Text("Nutrientes") }
                    }

                    // Contenido según la pestaña seleccionada
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            0 -> OrderingSection(
                                currentOrder = filtrosTemporales.orderType,
                                isAscending = filtrosTemporales.isAscending,
                                onOrderChanged = { orderType, ascending ->
                                    filtrosTemporales = filtrosTemporales.copy(
                                        orderType = orderType,
                                        isAscending = ascending
                                    )
                                }
                            )

                            1 -> CategoriesSection(
                                selectedCategories = filtrosTemporales.categories,
                                availableCategories = categoriasDisponibles,
                                onCategoriesChanged = { categories ->
                                    filtrosTemporales = filtrosTemporales.copy(
                                        categories = categories
                                    )
                                }
                            )

                            2 -> NutrientsSection(
                                currentRanges = mapOf(
                                    "Calorías" to filtrosTemporales.caloriesRange,
                                    "Proteínas" to filtrosTemporales.proteinsRange,
                                    "Carbohidratos" to filtrosTemporales.carbsRange,
                                    "Grasas" to filtrosTemporales.fatsRange
                                ),
                                onRangeChanged = { nutrient, range ->
                                    filtrosTemporales = when (nutrient) {
                                        "Calorías" -> filtrosTemporales.copy(caloriesRange = range)
                                        "Proteínas" -> filtrosTemporales.copy(proteinsRange = range)
                                        "Carbohidratos" -> filtrosTemporales.copy(carbsRange = range)
                                        "Grasas" -> filtrosTemporales.copy(fatsRange = range)
                                        else -> filtrosTemporales
                                    }
                                }
                            )
                        }
                    }

                    // Botones de acción
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                filtrosTemporales = AlimentoFiltros()
                            }
                        ) {
                            Text("Limpiar")
                        }
                        Row {
                            TextButton(onClick = onDismiss) {
                                Text("Cancelar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onFiltrosChanged(filtrosTemporales)
                                    onDismiss()
                                }
                            ) {
                                Text("Aplicar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderingSection(
    currentOrder: OrderType,
    isAscending: Boolean,
    onOrderChanged: (OrderType, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ordenar por:", style = MaterialTheme.typography.titleMedium)

        val orderTypes = listOf(
            OrderType.Name to "Nombre",
            OrderType.Calories to "Calorías",
            OrderType.Proteins to "Proteínas",
            OrderType.Carbs to "Carbohidratos",
            OrderType.Fats to "Grasas"
        )

        orderTypes.forEach { (type, name) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOrderChanged(type, isAscending) }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentOrder == type,
                    onClick = { onOrderChanged(type, isAscending) }
                )
                Text(name, modifier = Modifier.weight(1f))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Orden ascendente")
            Switch(
                checked = isAscending,
                onCheckedChange = { onOrderChanged(currentOrder, it) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoriesSection(
    selectedCategories: Set<String>,
    availableCategories: List<String>,
    onCategoriesChanged: (Set<String>) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Si prefieres mostrar las categorías en un grid (fluido)
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableCategories.forEach { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = {
                            val newCategories = if (category in selectedCategories) {
                                selectedCategories - category
                            } else {
                                selectedCategories + category
                            }
                            onCategoriesChanged(newCategories)
                        },
                        label = { Text(category) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Espacio al final para evitar que el último item quede oculto
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutrientsSection(
    currentRanges: Map<String, NutrientRange>,
    onRangeChanged: (String, NutrientRange) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                "Rangos de nutrientes",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(currentRanges.toList()) { (nutrient, range) ->
            NutrientRangeSelector(
                nutrient = nutrient,
                range = range,
                onRangeChanged = { onRangeChanged(nutrient, it) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutrientRangeSelector(
    nutrient: String,
    range: NutrientRange,
    onRangeChanged: (NutrientRange) -> Unit
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var minText by remember(range.min) {
        mutableStateOf(if (range.min == 0f) "" else range.min.toString())
    }
    var maxText by remember(range.max) {
        mutableStateOf(if (range.max == Float.MAX_VALUE) "" else range.max.toString())
    }

    // Estado para el foco de los campos
    var minFieldFocus by remember { mutableStateOf(false) }
    var maxFieldFocus by remember { mutableStateOf(false) }

    fun validateAndUpdateValue(
        value: String,
        isMin: Boolean,
        onValidValue: (Float) -> Unit,
        onInvalidValue: () -> Unit
    ) {
        if (value.isEmpty()) {
            if (isMin) {
                onValidValue(0f)
            } else {
                onValidValue(Float.MAX_VALUE)
            }
            return
        }

        try {
            val numericValue = value.toFloat()
            if (numericValue >= 0) {
                onValidValue(numericValue)
            } else {
                onInvalidValue()
                errorMessage = "El valor debe ser positivo"
                showErrorDialog = true
            }
        } catch (e: NumberFormatException) {
            onInvalidValue()
            errorMessage = "Ingrese un número válido (ejemplo: 4 o 4.5)"
            showErrorDialog = true
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior con título y switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nutrient,
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = range.isEnabled,
                    onCheckedChange = { enabled ->
                        onRangeChanged(range.copy(isEnabled = enabled))
                    }
                )
            }

            // Campos de entrada visibles solo si está habilitado
            AnimatedVisibility(visible = range.isEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minText,
                            onValueChange = { minText = it },
                            label = { Text("Mín") },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { focusState ->
                                    if (minFieldFocus && !focusState.isFocused) {
                                        validateAndUpdateValue(minText, true,
                                            onValidValue = { validValue ->
                                                onRangeChanged(range.copy(min = validValue))
                                            },
                                            onInvalidValue = {
                                                minText = if (range.min == 0f) "" else range.min.toString()
                                            }
                                        )
                                    }
                                    minFieldFocus = focusState.isFocused
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = maxText,
                            onValueChange = { maxText = it },
                            label = { Text("Máx") },
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { focusState ->
                                    if (maxFieldFocus && !focusState.isFocused) {
                                        validateAndUpdateValue(maxText, false,
                                            onValidValue = { validValue ->
                                                onRangeChanged(range.copy(max = validValue))
                                            },
                                            onInvalidValue = {
                                                maxText = if (range.max == Float.MAX_VALUE) "" else range.max.toString()
                                            }
                                        )
                                    }
                                    maxFieldFocus = focusState.isFocused
                                },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    // Mostrar el rango actual
                    Text(
                        text = when (nutrient) {
                            "Calorías" -> "Rango: ${if (range.min == 0f) "0" else "%.1f".format(range.min)} - ${if (range.max == Float.MAX_VALUE) "∞" else "%.1f".format(range.max)} kcal"
                            else -> "Rango: ${if (range.min == 0f) "0" else "%.1f".format(range.min)} - ${if (range.max == Float.MAX_VALUE) "∞" else "%.1f".format(range.max)} g"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Valor inválido") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}