package com.example.proyectohealthy.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel

@Composable
fun GeneroScreen(
    userSelectionsViewModel: UserSelectionsViewModel,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE8F5E9)), // Fondo verde
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Cuál es tu género?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black // Color de texto blanco para contraste
        )

        Spacer(modifier = Modifier.height(16.dp))

        RadioButtonGroup(
            options = listOf("Femenino", "Masculino", "Otro"),
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.updateGenero(selectedOption)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinueClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)) // Botón verde
        ) {
            Text(text = "Continuar", color = Color.White) // Color de texto blanco
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneroScreenPreview() {
    GeneroScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}
