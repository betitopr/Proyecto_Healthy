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
                Text(text = alimento.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "$cantidad ${alimento.nombrePorcion}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${(alimento.calorias * cantidad).toInt()} kcal",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
    }
}