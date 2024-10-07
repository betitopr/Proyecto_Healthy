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
import com.example.proyectohealthy.data.local.entity.RegistroComida

@Composable
fun RegistroComidaCard(
    registroComida: RegistroComida,
    alimento: Alimento,
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
                Text(text = alimento.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Porción: ${registroComida.alimentos[alimento.id]} ${alimento.nombrePorcion}")
                Text(text = "Calorías: ${(alimento.calorias * (registroComida.alimentos[alimento.id] ?: 0f)).toInt()}")
                Text(text = "P: ${(alimento.proteinas * (registroComida.alimentos[alimento.id] ?: 0f)).toInt()}g | " +
                        "C: ${(alimento.carbohidratos * (registroComida.alimentos[alimento.id] ?: 0f)).toInt()}g | " +
                        "G: ${(alimento.grasas * (registroComida.alimentos[alimento.id] ?: 0f)).toInt()}g")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
    }
}