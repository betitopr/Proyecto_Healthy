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

fun PesoObjetivoScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit
) {
    // Obtener el valor mutable del peso objetivo desde el ViewModel
    val pesoObjetivo by userSelectionsViewModel.pesoObjetivo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cuál es tu peso objetivo?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        SliderPicker(
            min = 40f,
            max = 150f,
            unit = "Kg",
            currentValue = pesoObjetivo,
            onValueChange = { newValue ->
                userSelectionsViewModel.updatePesoObjetivo(newValue) // Actualizar el peso objetivo en el ViewModel
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PesoObjetivoScreenPreview() {
    val viewModel = UserSelectionsViewModel().apply {
        // Establece un valor inicial para la vista previa
        updatePesoObjetivo(70f)
    }
    PesoObjetivoScreen(userSelectionsViewModel = viewModel, onContinueClick = {})
}*/