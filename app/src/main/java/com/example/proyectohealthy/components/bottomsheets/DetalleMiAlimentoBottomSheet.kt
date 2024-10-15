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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleMiAlimentoBottomSheet(
    miAlimento: MisAlimentos,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var cantidad by remember { mutableStateOf("1") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.78f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(miAlimento.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Calorías: ${miAlimento.calorias}")
            Text("Proteínas: ${miAlimento.proteinas}g")
            Text("Carbohidratos: ${miAlimento.carbohidratos}g")
            Text("Grasas: ${miAlimento.grasas}g")
            //Text("Grasas Saturadas: ${miAlimento.grasasSaturadas}g")
            //Text("Grasas Trans: ${miAlimento.grasasTrans}g")
            Text("Sodio: ${miAlimento.sodio}mg")
            Text("Fibra: ${miAlimento.fibra}g")
            Text("Azúcares: ${miAlimento.azucares}g")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad (${miAlimento.nombrePorcion})") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        cantidad.toFloatOrNull()?.let { onConfirm(it) }
                    }
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}