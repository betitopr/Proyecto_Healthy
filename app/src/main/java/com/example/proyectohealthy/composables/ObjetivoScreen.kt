package com.example.proyectohealthy.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import viewmodels.UserSelectionsViewModel


@Composable
fun ObjetivoScreen(userSelectionsViewModel: UserSelectionsViewModel, onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Qué objetivo tienes en mente?",
            style = MaterialTheme.typography.headlineSmall // Usando Material 3 style
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos

        // Grupo de RadioButtons
        RadioButtonGroup(
            options = listOf("Perder peso", "Mantener el peso", "Aumentar peso"),
            selectedOption = userSelectionsViewModel.objetivo,
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.updateObjetivo(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos

        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onOptionSelected(option) } // Manejar clic en el RadioButton
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ObjetivoScreenPreview() {
    val viewModel = UserSelectionsViewModel() // Utiliza un ViewModel de ejemplo para la vista previa
    ObjetivoScreen(userSelectionsViewModel = viewModel, onContinueClick = {})
}
