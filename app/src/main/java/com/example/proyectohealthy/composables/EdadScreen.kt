/*package com.example.proyectohealthy.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
fun EdadScreen(
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
            text = "¿Cuál es tu edad?",
            style = MaterialTheme.typography.headlineSmall
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

        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
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
                    .padding(horizontal = 4.dp), // Espacio entre los números
                color = if (i == selectedNumber.value) Color.Green else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EdadScreenPreview() {
    EdadScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}*/