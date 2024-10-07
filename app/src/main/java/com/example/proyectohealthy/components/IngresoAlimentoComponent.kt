package com.example.proyectohealthy.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import java.util.*

@Composable
fun IngresoAlimentoComponent(viewModel: AlimentoViewModel) {
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var nombrePorcion by remember { mutableStateOf("") }
    var pesoPorcion by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ingresar Nuevo Alimento", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombrePorcion,
            onValueChange = { nombrePorcion = it },
            label = { Text("Nombre de la Porción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pesoPorcion,
            onValueChange = { pesoPorcion = it },
            label = { Text("Peso de la Porción (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = calorias,
            onValueChange = { calorias = it },
            label = { Text("Calorías") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = proteinas,
            onValueChange = { proteinas = it },
            label = { Text("Proteínas (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = carbohidratos,
            onValueChange = { carbohidratos = it },
            label = { Text("Carbohidratos (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = grasas,
            onValueChange = { grasas = it },
            label = { Text("Grasas (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val nuevoAlimento = Alimento(
                    nombre = nombre,
                    marca = marca,
                    categoria = categoria,
                    nombrePorcion = nombrePorcion,
                    pesoPorcion = pesoPorcion.toFloatOrNull() ?: 0f,
                    calorias = calorias.toIntOrNull() ?: 0,
                    proteinas = proteinas.toFloatOrNull() ?: 0f,
                    carbohidratos = carbohidratos.toFloatOrNull() ?: 0f,
                    grasas = grasas.toFloatOrNull() ?: 0f,
                    diaCreado = Date()
                )
                viewModel.createOrUpdateAlimento(nuevoAlimento)
                // Limpiar campos después de agregar
                nombre = ""
                marca = ""
                categoria = ""
                nombrePorcion = ""
                pesoPorcion = ""
                calorias = ""
                proteinas = ""
                carbohidratos = ""
                grasas = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Alimento")
        }
    }
}