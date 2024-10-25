package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
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
import com.example.proyectohealthy.data.local.entity.AlimentoFiltros
import com.example.proyectohealthy.data.local.entity.NutrientRange
import com.example.proyectohealthy.data.local.entity.OrderType

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
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    "Filtros y Ordenamiento",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
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
            .padding(vertical = 16.dp),
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
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentOrder == type,
                    onClick = { onOrderChanged(type, isAscending) }
                )
                Text(name, modifier = Modifier.weight(1f))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Text("Categorías", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

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
                    label = { Text(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NutrientsSection(
    currentRanges: Map<String, NutrientRange>,
    onRangeChanged: (String, NutrientRange) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Rangos de nutrientes", style = MaterialTheme.typography.titleMedium)

        currentRanges.forEach { (nutrient, range) ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(nutrient)
                    Switch(
                        checked = range.isEnabled,
                        onCheckedChange = { enabled ->
                            onRangeChanged(nutrient, range.copy(isEnabled = enabled))
                        }
                    )
                }

                if (range.isEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = range.min.toString(),
                            onValueChange = { value ->
                                value.toFloatOrNull()?.let { min ->
                                    onRangeChanged(nutrient, range.copy(min = min))
                                }
                            },
                            label = { Text("Mínimo") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = range.max.toString(),
                            onValueChange = { value ->
                                value.toFloatOrNull()?.let { max ->
                                    onRangeChanged(nutrient, range.copy(max = max))
                                }
                            },
                            label = { Text("Máximo") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}