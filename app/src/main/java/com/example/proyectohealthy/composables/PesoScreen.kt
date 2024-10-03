/*package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Slider
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel

@Composable
fun PesoScreen(userSelectionsViewModel: UserSelectionsViewModel, onContinueClick: () -> Unit) {
    val peso by userSelectionsViewModel.peso.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cuál es tu peso actual en este momento?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        SliderPicker(
            min = 40f,
            max = 150f,
            unit = "Kg",
            currentValue = peso,
            onValueChange = { newValue ->
                userSelectionsViewModel.updatePeso(newValue)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}

@Composable
fun SliderPicker(min: Float, max: Float, unit: String, currentValue: Float, onValueChange: (Float) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp) // Padding para el SliderPicker
    ) {
        Text(
            text = "${currentValue.toInt()} $unit",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = currentValue,
            onValueChange = {
                onValueChange(it) // Notificar el cambio de valor
            },
            valueRange = min..max
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PesoScreenPreview() {
    val viewModel = UserSelectionsViewModel() // Utiliza un ViewModel de ejemplo para la vista previa
    PesoScreen(userSelectionsViewModel = viewModel, onContinueClick = {})
}
*/