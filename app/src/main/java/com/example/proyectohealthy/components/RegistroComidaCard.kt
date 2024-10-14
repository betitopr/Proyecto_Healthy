package com.example.proyectohealthy.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.data.local.entity.RegistroComida

@Composable
fun RegistroComidaCard(
    registroComida: RegistroComida,
    alimento: Any,
    cantidad: Float,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                when (alimento) {
                    is Alimento -> {
                        Text(text = alimento.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Cantidad: $cantidad ${alimento.nombrePorcion}")
                        Text(text = "Calorías: ${(alimento.calorias * cantidad).toInt()}")
                        Text(
                            text = "P: ${(alimento.proteinas * cantidad).toInt()}g | " +
                                    "C: ${(alimento.carbohidratos * cantidad).toInt()}g | " +
                                    "G: ${(alimento.grasas * cantidad).toInt()}g"
                        )
                    }
                    is MisAlimentos -> {
                        Text(text = alimento.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Cantidad: $cantidad ${alimento.nombrePorcion}")
                        Text(text = "Calorías: ${(alimento.calorias * cantidad).toInt()}")
                        Text(
                            text = "P: ${(alimento.proteinas * cantidad).toInt()}g | " +
                                    "C: ${(alimento.carbohidratos * cantidad).toInt()}g | " +
                                    "G: ${(alimento.grasas * cantidad).toInt()}g"
                        )
                    }
                    else -> {
                        Text("Alimento desconocido", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
    }
}