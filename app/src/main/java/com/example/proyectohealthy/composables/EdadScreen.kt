package com.example.proyectohealthy.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
fun EdadScreen(
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

        Text(
            text = "¿Cuál es tu edad?",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF2E7D32) // Color verde oscuro para el texto
        )

        Spacer(modifier = Modifier.height(16.dp))

        NumberPicker(
            start = 17,
            end = 50,
            onNumberSelected = { selectedNumber ->
                userSelectionsViewModel.updateEdad(selectedNumber)
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
fun NumberPicker(start: Int, end: Int, onNumberSelected: (Int) -> Unit) {
    val selectedNumber = remember { mutableStateOf(start) }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        for (i in start..end) {
            Text(
                text = i.toString(),
                modifier = Modifier
                    .clickable {
                        selectedNumber.value = i
                        onNumberSelected(i) // Notificar el número seleccionado
                    }
                    .padding(horizontal = 4.dp) // Espacio entre los números
                    .background(if (i == selectedNumber.value) Color(0xFF81C784) else Color.Transparent) // Fondo dinámico
                    .padding(8.dp), // Espacio interno
                color = if (i == selectedNumber.value) Color.White else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EdadScreenPreview() {
    EdadScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ },
        onBackClick = { /* Acción a realizar cuando se haga clic en "Retroceder" */ }
    )
}
