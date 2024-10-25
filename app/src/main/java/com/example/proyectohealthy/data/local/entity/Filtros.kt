package com.example.proyectohealthy.data.local.entity

// Estados y modelos para los filtros
sealed class OrderType {
    object Calories : OrderType()
    object Proteins : OrderType()
    object Carbs : OrderType()
    object Fats : OrderType()
    object Name : OrderType()
}

data class NutrientRange(
    val min: Float = 0f,
    val max: Float = Float.MAX_VALUE,
    val isEnabled: Boolean = false
)

data class AlimentoFiltros(
    val categories: Set<String> = emptySet(),
    val orderType: OrderType = OrderType.Name,
    val isAscending: Boolean = true,
    val caloriesRange: NutrientRange = NutrientRange(),
    val proteinsRange: NutrientRange = NutrientRange(),
    val carbsRange: NutrientRange = NutrientRange(),
    val fatsRange: NutrientRange = NutrientRange()
) {
    fun hasActiveFilters(): Boolean {
        return categories.isNotEmpty() ||
                orderType != OrderType.Name ||
                !isAscending ||
                caloriesRange.isEnabled ||
                proteinsRange.isEnabled ||
                carbsRange.isEnabled ||
                fatsRange.isEnabled
    }

    fun countActiveFilters(): Int {
        var count = 0
        if (categories.isNotEmpty()) count++
        if (orderType != OrderType.Name) count++
        if (!isAscending) count++
        if (caloriesRange.isEnabled) count++
        if (proteinsRange.isEnabled) count++
        if (carbsRange.isEnabled) count++
        if (fatsRange.isEnabled) count++
        return count
    }
}