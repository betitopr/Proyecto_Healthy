package com.example.proyectohealthy.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectohealthy.R
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.data.local.entity.Perfil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: PerfilViewModel) {
    val perfilState by viewModel.currentPerfil.collectAsState()
    val errorState by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(navController = navController, title = "Perfil")
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            perfilState?.let { perfil ->
                ProfileContent(perfil, viewModel)
            } ?: run {
                CircularProgressIndicator()
            }

            errorState?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ProfileContent(perfil: Perfil, viewModel: PerfilViewModel) {
    Surface(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.primary
    ) {
        perfil.Perfil_Imagen?.let { imageUrl ->
            // Aquí deberías usar una librería de carga de imágenes como Coil o Glide
            // Por ahora, usamos una imagen de placeholder
            Image(
                painter = painterResource(id = R.drawable.gojowin),
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            // Placeholder si no hay imagen de perfil
            Icon(
                painter = painterResource(id = R.drawable.gojowin),
                contentDescription = "Icono de perfil",
                modifier = Modifier.fillMaxSize().padding(24.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "${perfil.Nombre} ${perfil.Apellido}",
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(24.dp))
    ProfileInfoCard(perfil)
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = {
        // Implementar lógica para editar perfil
    }) {
        Text("Editar Perfil")
    }
}

@Composable
fun ProfileInfoCard(perfil: Perfil) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoItem("Género", perfil.Genero)
            ProfileInfoItem("Altura", "${perfil.Altura} cm")
            ProfileInfoItem("Peso actual", "${perfil.Peso_Actual} kg")
            ProfileInfoItem("Peso objetivo", "${perfil.Peso_Objetivo} kg")
            ProfileInfoItem("Nivel de actividad", perfil.Nivel_Actividad)
            ProfileInfoItem("Objetivo de salud", perfil.Objetivo)
            perfil.Biografia?.let { ProfileInfoItem("Biografía", it) }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}