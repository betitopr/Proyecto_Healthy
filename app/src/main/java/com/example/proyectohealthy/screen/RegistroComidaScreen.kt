package com.example.proyectohealthy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import com.example.proyectohealthy.ui.components.BusquedaAlimentosDialog

@Composable
fun RegistroComidaScreen(
    viewModel: RegistroComidaViewModel = hiltViewModel()
) {
    var showBusquedaDialog by remember { mutableStateOf(false) }
    var tipoComidaSeleccionado by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Registro de Comidas",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Botones para agregar comidas
        listOf("Desayuno", "Almuerzo", "Cena", "Snacks").forEach { tipoComida ->
            item {
                Text(
                    text = tipoComida,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Button(
                    onClick = {
                        // Al hacer clic en un botón, se guarda el tipo de comida y se muestra el diálogo de búsqueda
                        tipoComidaSeleccionado = tipoComida
                        showBusquedaDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Agregar $tipoComida")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }
    }

    // Mostrar el diálogo de búsqueda cuando showBusquedaDialog es true
    if (showBusquedaDialog) {
        BusquedaAlimentosDialog(
            onDismiss = { showBusquedaDialog = false },
            onAlimentoSelected = { alimento, cantidad ->
                viewModel.agregarAlimento(alimento, cantidad, tipoComidaSeleccionado)
                showBusquedaDialog = false
            },
            viewModel = viewModel
        )
    }
}