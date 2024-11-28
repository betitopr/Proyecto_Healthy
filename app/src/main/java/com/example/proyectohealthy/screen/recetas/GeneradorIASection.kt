package com.example.proyectohealthy.ui.screen.recetas.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GeneradorIASection(
    onGenerar: (String, String, String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var descripcion by remember { mutableStateOf("") }
    var tipoComida by remember { mutableStateOf("") }
    var restricciones by remember { mutableStateOf("") }
    var showLimitDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Generar Receta con IA",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Describe la receta que deseas") },
                placeholder = { Text("Ej: Una ensalada fresca con pollo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tipoComida,
                onValueChange = { tipoComida = it },
                label = { Text("Tipo de comida") },
                placeholder = { Text("Ej: Almuerzo, Cena, Desayuno") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = restricciones,
                onValueChange = { restricciones = it },
                label = { Text("Restricciones o preferencias (opcional)") },
                placeholder = { Text("Ej: Sin gluten, vegetariano") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onGenerar(descripcion, tipoComida, restricciones)
                    showLimitDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && descripcion.isNotBlank() && tipoComida.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Restaurant, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar Receta")
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Generando receta... Esto puede tardar unos segundos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Límite de Generaciones") },
            text = {
                Text(
                    "Ten en cuenta que el servicio de IA tiene un límite diario de generaciones. " +
                            "Si alcanzas el límite, podrás seguir usando las otras funciones de la app.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}