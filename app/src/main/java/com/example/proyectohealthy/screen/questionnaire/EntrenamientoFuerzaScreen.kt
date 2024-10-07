package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrenamientoFuerzaScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    var entrenamientoFuerza by remember { mutableStateOf(currentPerfil?.entrenamientoFuerza ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrenamiento de Fuerza") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Realizas entrenamiento de fuerza?", style = MaterialTheme.typography.headlineSmall)

            RadioButtonGroup(
                options = listOf("Sí", "No"),
                selectedOption = entrenamientoFuerza,
                onOptionSelected = {
                    entrenamientoFuerza = it
                    perfilViewModel.updateEntrenamientoFuerza(it)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onPreviousClick) {
                    Text("Anterior")
                }
                Button(onClick = onNextClick) {
                    Text("Siguiente")
                }
            }
        }
    }
}