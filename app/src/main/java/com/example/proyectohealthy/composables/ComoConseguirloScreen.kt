/*package com.example.proyectohealthy.composables

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
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel

@Composable
fun ComoConseguirloScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cómo deseas conseguirlo?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        RadioButtonGroup(
            options = listOf("Necesito un plan nutricional", "Necesito contar mis calorías"),
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.addSelection(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    onOptionSelected: (String) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf(options[0]) } // Estado para la opción seleccionada
    Column {
        options.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp) // Padding entre los elementos
            ) {
                RadioButton(
                    selected = text == selectedOption,
                    onClick = {
                        selectedOption = text
                        onOptionSelected(text) // Notificar la opción seleccionada
                    }
                )
                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el RadioButton y el texto
                Text(text = text)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComoConseguirloScreenPreview() {
    ComoConseguirloScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}

 */