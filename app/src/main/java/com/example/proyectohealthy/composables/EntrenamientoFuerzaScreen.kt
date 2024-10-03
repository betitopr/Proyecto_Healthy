/*package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.screen.questionnaire.RadioButtonGroup
import com.example.proyectohealthy.viewmodels.UserSelectionsViewModel

@Composable
fun EntrenamientoFuerzaScreen(
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
            text = "¿Realizas entrenamiento de fuerza?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí se usa RadioButtonGroup para seleccionar una opción
        RadioButtonGroup(
            options = listOf("Sí", "No"),
            selectedOption = userSelectionsViewModel.entrenamientoFuerza,
            onOptionSelected = { selectedOption ->
                userSelectionsViewModel.updateEntrenamientoFuerza(selectedOption)
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
fun EntrenamientoFuerzaScreenPreview() {
    EntrenamientoFuerzaScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}
*/