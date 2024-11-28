package com.example.proyectohealthy.data.remote

import com.example.proyectohealthy.data.local.entity.RecetaApi

data class RecetaResponse(
    val title: String = "",
    val ingredients: String = "",
    val servings: String = "",
    val instructions: String = ""
) {
    fun toRecetaApi(): RecetaApi {
        return RecetaApi(
            title = title,
            ingredients = ingredients.split("|").map { it.trim() },
            servings = servings,
            instructions = instructions
        )
    }
}