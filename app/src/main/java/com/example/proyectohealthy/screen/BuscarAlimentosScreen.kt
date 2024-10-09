package com.example.proyectohealthy.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.RegistroComidaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarAlimentoScreen(
    navController: NavController,
    viewModel: AlimentoViewModel = hiltViewModel(),
    registroComidaViewModel: RegistroComidaViewModel = hiltViewModel(),
    tipoComida: String
) {
    var searchQuery by remember { mutableStateOf("") }
    val alimentos by viewModel.alimentos.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchAlimentosByNombre(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Alimento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar alimento") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(error!!, color = Color.Red)
            } else {
                LazyColumn {
                    items(alimentos) { alimento ->
                        AlimentoItem(
                            alimento = alimento,
                            onClick = {
                                registroComidaViewModel.agregarAlimento(alimento, 1f, tipoComida)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun AlimentoItem(alimento: Alimento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = alimento.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Calorías: ${alimento.calorias}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Proteínas: ${alimento.proteinas}g", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Carbohidratos: ${alimento.carbohidratos}g", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Grasas: ${alimento.grasas}g", style = MaterialTheme.typography.bodyMedium)
        }
    }
}