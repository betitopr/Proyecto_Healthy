package com.example.proyectohealthy.composables
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Calendario (Placeholder)
            Text(
                text = "Calendario",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            // Gráfico circular y nutrientes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray, shape = CircleShape)
                ) {
                    // Aquí va el gráfico circular
                    Text(
                        text = "Gráfico Circular",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Proteínas: 50g")
                    Text(text = "Carbohidratos: 100g")
                    Text(text = "Grasas: 30g")
                }
            }
        }

        item {
            // Buscador de comida
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar comida") }
            )
        }

        // Sección de alimentos
        item {
            Text(
                text = "Desayuno",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { /* Agregar desayuno */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Desayuno")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        item {
            Text(
                text = "Almuerzo",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { /* Agregar almuerzo */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Almuerzo")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        item {
            Text(
                text = "Cena",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { /* Agregar cena */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Cena")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        item {
            Text(
                text = "Snacks",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { /* Agregar snacks */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Snacks")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        // Sección de ejercicios
        item {
            Text(
                text = "Ejercicios",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Button(
                onClick = { /* Agregar ejercicios */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agregar Ejercicio")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        // Indicador de agua
        item {
            Text(
                text = "Consumo de Agua",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(8) {
                    Button(
                        onClick = { /* Marcar vaso */ },
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape
                    ) {
                        Text(text = "${it + 1}")
                    }
                }
            }
        }

        // Botones inferiores
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = { /* Acción de plan */ }) {
                    Icon(Icons.Filled.Info, contentDescription = "Plan")
                }
                IconButton(onClick = { /* Buscar en la base de datos */ }) {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar")
                }
                IconButton(onClick = { /* Ir a recetas */ }) {
                    Icon(Icons.Filled.AccountBox, contentDescription = "Recetas")
                }
                IconButton(onClick = { /* Ir a team */ }) {
                    Icon(Icons.Filled.Person, contentDescription = "Team")
                }
                IconButton(onClick = { /* Ir a progreso */ }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Progreso")
                }

            }
        }
    }
}
