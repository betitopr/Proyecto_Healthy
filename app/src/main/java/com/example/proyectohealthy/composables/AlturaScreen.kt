package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import viewmodels.UserSelectionsViewModel


@Composable
fun AlturaScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit
) {
    // Utiliza collectAsState para observar cambios en el estado de altura
    val altura by userSelectionsViewModel.altura.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cuál es tu altura?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el valor actual del slider
        Text(
            text = "${altura.toInt()} cm",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = altura,
            onValueChange = { newValue ->
                userSelectionsViewModel.updateAltura(newValue)
            },
            valueRange = 140f..200f,
            onValueChangeFinished = {
                // Acciones opcionales cuando el cambio de valor se ha completado
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
fun AlturaScreenPreview() {
    // Creamos un ViewModel simulado para la vista previa
    val mockViewModel = UserSelectionsViewModel().apply {
        // Establecemos un valor inicial para la altura
        updateAltura(170f)
    }
    AlturaScreen(
        userSelectionsViewModel = mockViewModel,
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}