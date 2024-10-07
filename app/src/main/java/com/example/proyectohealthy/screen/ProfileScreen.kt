package com.example.proyectohealthy.screen

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
import coil.compose.AsyncImage
import com.example.proyectohealthy.R
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    authViewModel: AuthViewModel
) {
    val errorState by perfilViewModel.error.collectAsState()
    val perfil by perfilViewModel.currentPerfil.collectAsState()
    val isEditing by perfilViewModel.isEditing.collectAsState()
    var editedPerfil by remember { mutableStateOf(perfil) }

    LaunchedEffect(perfil) {
        editedPerfil = perfil
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                actions = {
                    if (isEditing) {
                        TextButton(onClick = {
                            editedPerfil?.let { perfilViewModel.updatePerfil(it) }
                            perfilViewModel.setEditing(false)
                        }) {
                            Text("Guardar")
                        }
                    } else {
                        TextButton(onClick = { perfilViewModel.setEditing(true) }) {
                            Text("Editar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil circular grande
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                when {
                    perfil?.perfilImagen != null && perfil?.perfilImagen?.startsWith("http") == true -> {
                        AsyncImage(
                            model = perfil?.perfilImagen,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.gojowin),
                            contentDescription = "Icono de perfil predeterminado",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            editedPerfil?.let { perfil ->
                if (isEditing) {
                    EditableProfileContent(perfil, onPerfilChanged = { editedPerfil = it })
                } else {
                    ProfileInfoCard(perfil)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("questionnaire") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Realizar cuestionario nuevamente")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.signOut() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }

            errorState?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun EditableProfileContent(perfil: Perfil, onPerfilChanged: (Perfil) -> Unit) {
    ProfileField("Nombre", perfil.nombre, true) { onPerfilChanged(perfil.copy(nombre = it)) }
    ProfileField("Apellido", perfil.apellido, true) { onPerfilChanged(perfil.copy(apellido = it)) }
    ProfileField("Género", perfil.genero, true) { onPerfilChanged(perfil.copy(genero = it)) }
    ProfileField("Altura", perfil.altura.toString(), true) { onPerfilChanged(perfil.copy(altura = it.toFloatOrNull() ?: 0f)) }
    ProfileField("Edad", perfil.edad.toString(), true) { onPerfilChanged(perfil.copy(edad = it.toIntOrNull() ?: 0)) }
    ProfileField("Peso Actual", perfil.pesoActual.toString(), true) { onPerfilChanged(perfil.copy(pesoActual = it.toFloatOrNull() ?: 0f)) }
    ProfileField("Peso Objetivo", perfil.pesoObjetivo.toString(), true) { onPerfilChanged(perfil.copy(pesoObjetivo = it.toFloatOrNull() ?: 0f)) }
    ProfileDropdownField("Nivel de Actividad", perfil.nivelActividad, true, listOf("Sedentario", "Ligeramente activo", "Moderadamente activo", "Muy activo")) { onPerfilChanged(perfil.copy(nivelActividad = it)) }
    ProfileDropdownField("Objetivo", perfil.objetivo, true, listOf("Perder peso", "Mantener peso", "Ganar peso")) { onPerfilChanged(perfil.copy(objetivo = it)) }
    ProfileDropdownField("Cómo conseguirlo", perfil.comoConseguirlo, true, listOf("Plan nutricional", "Contar calorías")) { onPerfilChanged(perfil.copy(comoConseguirlo = it)) }
    ProfileDropdownField("Entrenamiento de fuerza", perfil.entrenamientoFuerza, true, listOf("Sí", "No")) { onPerfilChanged(perfil.copy(entrenamientoFuerza = it)) }
    ProfileField("Biografía", perfil.biografia ?: "", true) { onPerfilChanged(perfil.copy(biografia = it)) }
}

@Composable
fun ProfileInfoCard(perfil: Perfil) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoItem("Nombre", "${perfil.nombre} ${perfil.apellido}")
            ProfileInfoItem("Género", perfil.genero)
            ProfileInfoItem("Altura", "${perfil.altura} cm")
            ProfileInfoItem("Edad", "${perfil.edad} años")
            ProfileInfoItem("Peso actual", "${perfil.pesoActual} kg")
            ProfileInfoItem("Peso objetivo", "${perfil.pesoObjetivo} kg")
            ProfileInfoItem("Nivel de actividad", perfil.nivelActividad)
            ProfileInfoItem("Objetivo de salud", perfil.objetivo)
            ProfileInfoItem("Cómo conseguirlo", perfil.comoConseguirlo)
            ProfileInfoItem("Entrenamiento de fuerza", perfil.entrenamientoFuerza)
            perfil.biografia?.let { ProfileInfoItem("Biografía", it) }
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

@Composable
fun ProfileField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    if (isEditing) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        Text("$label: $value", modifier = Modifier.padding(vertical = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdownField(label: String, value: String, isEditing: Boolean, options: List<String>, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    if (isEditing) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .padding(vertical = 8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    } else {
        Text("$label: $value", modifier = Modifier.padding(vertical = 8.dp))
    }
}