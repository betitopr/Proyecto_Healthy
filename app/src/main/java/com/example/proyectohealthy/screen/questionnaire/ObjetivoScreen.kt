package com.example.proyectohealthy.screen.questionnaire

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjetivoScreen(
    perfilViewModel: PerfilViewModel,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val currentPerfil by perfilViewModel.currentPerfil.collectAsState()
    var selectedObjetivo by remember { mutableStateOf(currentPerfil?.Objetivo ?: "") }

    LaunchedEffect(currentPerfil?.Objetivo) {
        selectedObjetivo = currentPerfil?.Objetivo ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Objetivo") },
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
            Text("¿Cuál es tu objetivo principal?", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            RadioButtonGroup(
                options = listOf("Perder peso", "Mantener peso", "Ganar peso"),
                selectedOption = selectedObjetivo,
                onOptionSelected = {
                    selectedObjetivo = it
                    perfilViewModel.updateObjetivo(it)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // El botón "Anterior" está oculto en la primera pantalla
                Box(modifier = Modifier.width(100.dp)) // Placeholder para mantener el layout

                Button(
                    onClick = onNextClick,
                    enabled = selectedObjetivo.isNotEmpty()
                ) {
                    Text("Siguiente")
                }
            }
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
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 8.dp)
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

//@Preview(showBackground = true)
//@Composable
//fun ObjetivoScreenPreview() {
//    val viewModel = PerfilViewModel() // Utiliza un ViewModel de ejemplo para la vista previa
//    ObjetivoScreen(PerfilViewModel = viewModel, onContinueClick = {})
//}
