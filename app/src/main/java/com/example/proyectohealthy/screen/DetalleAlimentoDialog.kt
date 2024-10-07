package com.example.proyectohealthy.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento

@Composable
fun DetalleAlimentoDialog(
    alimento: Alimento,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var cantidad by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(alimento.nombre) },
        text = {
            Column {
                Text("Calorías: ${alimento.calorias}")
                Text("Proteínas: ${alimento.proteinas}g")
                Text("Carbohidratos: ${alimento.carbohidratos}g")
                Text("Grasas: ${alimento.grasas}g")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad (${alimento.nombrePorcion})") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                cantidad.toFloatOrNull()?.let { onConfirm(it) }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}