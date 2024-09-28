package com.example.proyectohealthy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentosViewModel
import java.util.*
import com.example.proyectohealthy.data.repository.AlimentoRepository

@Composable
fun AlimentosScreen(
    repository: AlimentoRepository, // Pasa el repositorio como parámetro
    viewModel: AlimentosViewModel = viewModel(
        factory = AlimentosViewModel.Factory(repository)
    )
) {
    val alimentos by viewModel.alimentos.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Formulario para agregar alimentos
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del alimento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = calorias,
            onValueChange = { calorias = it },
            label = { Text("Calorías por 100g") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = proteinas,
            onValueChange = { proteinas = it },
            label = { Text("Proteínas por 100g") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val nuevoAlimento = Alimento(
                    id_Alimento = 0, // Room generará el ID automáticamente
                    Nombre = nombre,
                    CaloriasPor100g = calorias.toFloatOrNull() ?: 0f,
                    ProteinasPor100g = proteinas.toFloatOrNull() ?: 0f,
                    CarbohidratosPor100g = 0f,
                    GrasasPor100g = 0f,
                    CodigoQR = null,
                    Porcion = "100g",
                    unidad_de_medida = "g",
                    Fibra = 0f,
                    Sodio = 0f,
                    Azucares = 0f,
                    Grasas_Saturadas = 0f,
                    Dia_creado = Date(),
                    Clasificacion = "Sin clasificar"
                )
                viewModel.insertAlimento(nuevoAlimento)
                nombre = ""
                calorias = ""
                proteinas = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Agregar Alimento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de alimentos
        LazyColumn {
            items(alimentos) { alimento ->
                AlimentoItem(alimento)
            }
        }
    }
}

@Composable
fun AlimentoItem(alimento: Alimento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = alimento.Nombre, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Calorías: ${alimento.CaloriasPor100g} por 100g", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Proteínas: ${alimento.ProteinasPor100g} por 100g", style = MaterialTheme.typography.bodyMedium)
        }
    }
}