package com.example.proyectohealthy.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import viewmodels.UserSelectionsViewModel
import com.example.proyectohealthy.R
@Composable
fun InicioScreen(
    userSelectionsViewModel: UserSelectionsViewModel, // Recibe el ViewModel como parámetro
    onContinueClick: () -> Unit // Acción para el botón
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ejemplo de imagen. Asegúrate de tener la imagen en res/drawable.
        Image(
            painter = painterResource(id = R.drawable.food_image),
            contentDescription = null
        )
        Text(
            text = "Come Mejor y obtén mejores resultados!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioScreenPreview() {
    InicioScreen(
        userSelectionsViewModel = UserSelectionsViewModel(), // Proporciona una instancia de UserSelectionsViewModel
        onContinueClick = { /* Acción a realizar cuando se haga clic en "Continuar" */ }
    )
}
