package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Ejercicio
import com.example.proyectohealthy.data.local.entity.RegistroEjercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioBottomSheet(
    ejercicios: List<Ejercicio>,
    onDismiss: () -> Unit,
    onEjercicioSelected: (idEjercicio: String, duracion: Int) -> Unit
) {
    var selectedEjercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var duracion by remember { mutableStateOf("") }
    val windowInsets = WindowInsets.navigationBars

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f),
        windowInsets = windowInsets
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Seleccionar Ejercicio", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(ejercicios) { ejercicio ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEjercicio = ejercicio }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedEjercicio == ejercicio,
                            onClick = { selectedEjercicio = ejercicio }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(ejercicio.nombre)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (minutos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedEjercicio?.let { ejercicio ->
                        if (duracion.isNotEmpty()) {
                            onEjercicioSelected(ejercicio.id, duracion.toInt())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedEjercicio != null && duracion.isNotEmpty()
            ) {
                Text("Agregar Ejercicio")
            }
        }
    }
}

@Composable
fun EjercicioItem(
    registroEjercicio: RegistroEjercicio,
    ejercicio: Ejercicio,
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
                Text(text = ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Duración: ${registroEjercicio.duracionMinutos} minutos")
                Text(text = "Calorías quemadas: ${registroEjercicio.duracionMinutos * ejercicio.caloriasPorMinuto}")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
    }
}
