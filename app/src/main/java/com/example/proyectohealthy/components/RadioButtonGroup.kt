package com.example.proyectohealthy.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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