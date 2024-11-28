package com.example.proyectohealthy.util

import com.example.proyectohealthy.data.local.entity.RecetaApi

class IAPromptBuilder {
    companion object {
        fun buildTraduccionPrompt(receta: RecetaApi): String {
            return """
                Traduce la siguiente receta del inglés al español y calcula sus valores nutricionales aproximados por porción.
                Receta:
                
                Título: ${receta.title}
                Porciones: ${receta.servings}
                Ingredientes:
                ${receta.ingredients.joinToString("\n")}
                
                Instrucciones:
                ${receta.instructions}
                
                Proporciona la respuesta en formato JSON con esta estructura exacta:
                {
                    "nombre": "nombre traducido",
                    "ingredientes": ["ingrediente1", "ingrediente2"],
                    "instrucciones": "instrucciones traducidas",
                    "porciones": "número de porciones",
                    "tiempoPreparacion": "tiempo estimado",
                    "valoresNutricionales": {
                        "porcion": 100,
                        "unidadPorcion": "g",
                        "calorias": 0,
                        "proteinas": 0,
                        "carbohidratos": 0,
                        "grasas": 0,
                        "fibra": 0,
                        "azucares": 0
                    }
                }
            """.trimIndent()
        }

        fun buildGeneracionPrompt(descripcion: String, tipo: String, restricciones: String): String {
            return """
                Genera una receta detallada basada en los siguientes parámetros:
                
                Descripción: $descripcion
                Tipo de comida: $tipo
                Restricciones: $restricciones
                
                Proporciona la receta en formato JSON con esta estructura exacta:
                {
                    "nombre": "",
                    "ingredientes": [""],
                    "instrucciones": "",
                    "porciones": "",
                    "tiempoPreparacion": "",
                    "valoresNutricionales": {
                        "porcion": 100,
                        "unidadPorcion": "g",
                        "calorias": 0,
                        "proteinas": 0,
                        "carbohidratos": 0,
                        "grasas": 0,
                        "fibra": 0,
                        "azucares": 0
                    }
                }
            """.trimIndent()
        }
    }
}