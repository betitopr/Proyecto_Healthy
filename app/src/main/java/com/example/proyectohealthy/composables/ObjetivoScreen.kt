package com.example.proyectohealthy.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.R
import viewmodels.UserSelectionsViewModel

@Composable
fun ObjetivoScreen(userSelectionsViewModel: UserSelectionsViewModel, onContinueClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE8F5E9)), // Color de fondo claro
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen en la parte superior
        Image(
            painter = painterResource(id = R.drawable.ic_health), // Reemplaza con tu icono de salud
            contentDescription = "Icono de salud",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp) // Espacio inferior de la imagen
        )

        Text(
            text = "¿Qué objetivo tienes en mente?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF2E7D32) // Color verde oscuro para el texto
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grupo de RadioButtons
        RadioButtonGroup(
            options = listOf("Perder peso", "Mantener el peso", "Aumentar peso"),
            selectedOption = userSelectionsViewModel.objetivo,
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.updateObjetivo(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF388E3C)), // Color verde para el botón
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)) // Botón verde
        ) {
            Text(text = "Continuar", color = Color.White) // Texto blanco para contrastar
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
                    .clickable { onOptionSelected(option) }
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
    val viewModel = UserSelectionsViewModel()
    ObjetivoScreen(userSelectionsViewModel = viewModel, onContinueClick = {})
}
