package com.example.proyectohealthy.data.local.entity

data class RecetaApi(
    val title: String = "",
    val ingredients: List<String> = emptyList(),
    val servings: String = "",
    val instructions: String = ""
)