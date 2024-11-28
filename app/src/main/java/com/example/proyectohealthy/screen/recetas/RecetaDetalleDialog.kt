package com.example.proyectohealthy.screen.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.RecetaGuardada

@Composable
fun RecetaDetalleDialog(
    receta: RecetaGuardada,
    onDismiss: () -> Unit,
    onAgregarAMisAlimentos: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                item {
                    InfoNutricional(receta)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(receta.ingredientes) { ingrediente ->
                    Text(
                        text = "• $ingrediente",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Instrucciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = receta.instrucciones,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAgregarAMisAlimentos,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.LocalDining, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar a Mis Alimentos")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun InfoNutricional(receta: RecetaGuardada) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información Nutricional",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))

            with(receta.valoresNutricionales) {
                NutrienteRow("Calorías", "$calorias kcal")
                NutrienteRow("Proteínas", "${proteinas}g")
                NutrienteRow("Carbohidratos", "${carbohidratos}g")
                NutrienteRow("Grasas", "${grasas}g")
                if (fibra > 0) NutrienteRow("Fibra", "${fibra}g")
                if (azucares > 0) NutrienteRow("Azúcares", "${azucares}g")
            }
        }
    }
}

@Composable
private fun NutrienteRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}