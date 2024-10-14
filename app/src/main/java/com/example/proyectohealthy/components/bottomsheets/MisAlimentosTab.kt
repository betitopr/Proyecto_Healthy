package com.example.proyectohealthy.components.bottomsheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.data.local.entity.Alimento
import com.example.proyectohealthy.data.local.entity.MisAlimentos
import com.example.proyectohealthy.ui.viewmodel.AlimentoViewModel
import com.example.proyectohealthy.ui.viewmodel.MisAlimentosViewModel

@Composable
fun MisAlimentosTab(
    viewModel: MisAlimentosViewModel,
    onMiAlimentoSelected: (MisAlimentos) -> Unit,
    onAddMiAlimentoClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val misAlimentos by viewModel.misAlimentos.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchMisAlimentosByNombre(it)
            },
            label = { Text("Buscar mi alimento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAddMiAlimentoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Alimento Manualmente")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(misAlimentos) { miAlimento ->
                MiAlimentoItem(
                    miAlimento = miAlimento,
                    onClick = { onMiAlimentoSelected(miAlimento) }
                )
            }
        }
    }
}

@Composable
fun MiAlimentoItem(
    miAlimento: MisAlimentos,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = miAlimento.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${miAlimento.calorias} kcal por ${miAlimento.nombrePorcion}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}