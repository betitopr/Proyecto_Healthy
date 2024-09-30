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
fun ComoConseguirloScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit // Acción para retroceder
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE8F5E9)), // Color de fondo claro
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de retroceso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onBackClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de retroceso
            Image(
                painter = painterResource(id = R.drawable.ic_r), // Reemplaza con tu icono de retroceso
                contentDescription = "Retroceder",
                modifier = Modifier.size(24.dp)
            )
        }

        // Imagen centrada
        Image(
            painter = painterResource(id = R.drawable.ic_back), // Reemplaza con tu imagen
            contentDescription = "Descripción de la imagen",
            modifier = Modifier
                .size(100.dp) // Ajusta el tamaño según sea necesario
                .padding(bottom = 16.dp) // Espacio debajo de la imagen
        )

        Text(
            text = "¿Cómo deseas conseguirlo?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF2E7D32) // Color verde oscuro para el texto
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grupo de RadioButtons
        RadioButtonGroup(
            options = listOf("Necesito un plan nutricional", "Necesito contar mis calorías"),
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.addSelection(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)) // Botón verde
        ) {
            Text(text = "Continuar", color = Color.White) // Texto blanco para contrastar
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
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ },
        onBackClick = { /* Acción a realizar cuando se haga clic en "Retroceder" */ }
    )
}
