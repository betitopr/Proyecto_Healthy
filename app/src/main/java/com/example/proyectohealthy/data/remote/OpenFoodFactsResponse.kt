package com.example.proyectohealthy.data.remote

data class OpenFoodFactsResponse(
    val product: Product
)

data class Product(
    val product_name: String?,
    val brands: String?,
    val categories: String?,
    val nutriments: Nutriments?
)

data class Nutriments(
    val energy_100g: Float?,
    val fat_100g: Float?,
    val saturated_fat_100g: Float?,
    val carbohydrates_100g: Float?,
    val sugars_100g: Float?,
    val fiber_100g: Float?,
    val proteins_100g: Float?,
    val salt_100g: Float?
)