package com.example.proyectohealthy.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarAlimentoManualScreen(
    navController: NavController,
    viewModel: AlimentoViewModel = hiltViewModel(),
    registroComidaViewModel: RegistroComidaViewModel,
    tipoComida: String
) {
    var categoria by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcion by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Alimento Manualmente") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Alimento") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = nombrePorcion,
                    onValueChange = { nombrePorcion = it },
                    label = { Text("Nombre de la Porción") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = pesoPorcion,
                    onValueChange = { pesoPorcion = it },
                    label = { Text("Peso de la Porción (g)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = calorias,
                    onValueChange = { calorias = it },
                    label = { Text("Calorías") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = proteinas,
                    onValueChange = { proteinas = it },
                    label = { Text("Proteínas (g)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = carbohidratos,
                    onValueChange = { carbohidratos = it },
                    label = { Text("Carbohidratos (g)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = grasas,
                    onValueChange = { grasas = it },
                    label = { Text("Grasas (g)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                Button(
                    onClick = {
                        val nuevoAlimento = Alimento(
                            id = UUID.randomUUID().toString(),
                            categoria = categoria,
                            nombre = nombre,
                            nombrePorcion = nombrePorcion,
                            pesoPorcion = pesoPorcion.toFloatOrNull() ?: 0f,
                            calorias = calorias.toFloatOrNull() ?: 0f,
                            proteinas = proteinas.toFloatOrNull() ?: 0f,
                            carbohidratos = carbohidratos.toFloatOrNull() ?: 0f,
                            grasas = grasas.toFloatOrNull() ?: 0f
                        )
                        viewModel.createOrUpdateAlimento(nuevoAlimento)
                        registroComidaViewModel.agregarAlimento(nuevoAlimento, 1f, tipoComida)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text("Guardar Alimento")
                }
            }
            item {
                if (error != null) {
                    Text(error!!, color = Color.Red)
                }
            }
        }
    }
}