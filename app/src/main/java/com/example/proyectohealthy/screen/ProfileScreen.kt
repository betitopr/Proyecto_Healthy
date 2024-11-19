package com.example.proyectohealthy.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.ProfileImage
import com.example.proyectohealthy.data.local.entity.AuthType
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.local.entity.UnidadesPreferences
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
    val isLoading by perfilViewModel.isLoading.collectAsState()

    var showUnidadesDialog by remember { mutableStateOf(false) }
    var editedPerfil by remember { mutableStateOf(perfil) }
    val context = LocalContext.current
    var showPasswordDialog by remember { mutableStateOf(false) }


    val authState by authViewModel.authState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { perfilViewModel.updateProfileImage(it) }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.NotAuthenticated -> {
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(perfil) {
        editedPerfil = perfil
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    IconButton(onClick = { showUnidadesDialog = true }) {
                        Icon(Icons.Default.Settings, "Configurar unidades")
                    }
                    if (isEditing) {
                        TextButton(onClick = {
                            editedPerfil?.let { perfilViewModel.updatePerfil(it) }
                            perfilViewModel.setEditing(false)
                        }) {
                            Text("Guardar")
                        }
                    } else {
                        IconButton(onClick = { perfilViewModel.setEditing(true) }) {
                            Icon(Icons.Default.Edit, "Editar perfil")
                        }
                    }
                }
            )

        },
        bottomBar = {
            CustomBottomBar(navController = navController)
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
            ProfileImage(
                currentImageUrl = perfil?.perfilImagen,
                isGmailAccount = perfil?.authType == AuthType.GMAIL,
                isEditing = isEditing,
                onChangeImage = { launcher.launch("image/*") },
                onRemoveImage = { perfilViewModel.updateProfileImage(null) },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            editedPerfil?.let { perfil ->
                if (isEditing) {
                    EditableProfileContent(
                        perfil = perfil,
                        onPerfilChanged = { editedPerfil = it }
                    )
                } else {
                    ProfileInfoCard(perfil)
                }
            }

            AccountSettingsSection(
                perfil = editedPerfil ?: return@Column,
                isEditing = isEditing,
                onPasswordChangeRequest = {
                    showPasswordDialog = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("questionnaire") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar cuestionario")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { authViewModel.signOut() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }

    if (showUnidadesDialog) {
        UnidadesPreferencesDialog(
            currentPreferences = editedPerfil?.unidadesPreferences,
            onDismiss = { showUnidadesDialog = false },
            onConfirm = { sistemaPeso, sistemaAltura, sistemaVolumen ->
                perfilViewModel.updateUnidadesPreferencias(sistemaPeso, sistemaAltura, sistemaVolumen)
                showUnidadesDialog = false
            }
        )
    }
    if (showPasswordDialog) {
        PasswordChangeDialog(
            viewModel = authViewModel,
            onDismiss = { showPasswordDialog = false }
        )
    }

    errorState?.let { error ->
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(error)
        }
    }
}

@Composable
private fun EditableProfileContent(perfil: Perfil, onPerfilChanged: (Perfil) -> Unit) {
    val sistemaPeso = perfil.unidadesPreferences.sistemaPeso
    val sistemaAltura = perfil.unidadesPreferences.sistemaAltura

    // Conversiones según el sistema seleccionado
    val pesoActualMostrado = if (sistemaPeso == "Imperial (lb)") {
        perfil.pesoActual * 2.20462f
    } else perfil.pesoActual

    val pesoObjetivoMostrado = if (sistemaPeso == "Imperial (lb)") {
        perfil.pesoObjetivo * 2.20462f
    } else perfil.pesoObjetivo

    val alturaMostrada = if (sistemaAltura == "Imperial (ft/in)") {
        val totalPulgadas = perfil.altura / 2.54f
        val pies = (totalPulgadas / 12).toInt()
        val pulgadas = (totalPulgadas % 12).toInt()
        "$pies'$pulgadas\""
    } else {
        perfil.altura.toString()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Datos personales básicos
            EditableField(
                label = "Nombre",
                value = perfil.nombre,
                onValueChange = { onPerfilChanged(perfil.copy(nombre = it)) }
            )

            EditableField(
                label = "Apellido",
                value = perfil.apellido,
                onValueChange = { onPerfilChanged(perfil.copy(apellido = it)) }
            )

            EditableField(
                label = "Username",
                value = perfil.username,
                onValueChange = { onPerfilChanged(perfil.copy(username = it)) }
            )

            EditableFieldType(
                label = "Correo Electronico",
                value = perfil.email,
                onValueChange = { onPerfilChanged(perfil.copy(email = it)) },
                type = perfil.authType == AuthType.APP,
                )

            // Género (Dropdown)
            EditableDropdownField(
                label = "Género",
                value = perfil.genero,
                options = listOf("Masculino", "Femenino", "Otro"),
                onValueChange = { onPerfilChanged(perfil.copy(genero = it)) }
            )

            // Medidas físicas con unidades
            EditableNumberField(
                label = "Altura ${if (sistemaAltura == "Imperial (ft/in)") "(ft'in\")" else "(cm)"}",
                value = alturaMostrada,
                onValueChange = { newValue ->
                    val nuevaAltura = when (sistemaAltura) {
                        "Imperial (ft/in)" -> {
                            // Convertir ft'in" a cm
                            try {
                                val partes = newValue.split("'")
                                val pies = partes[0].toInt()
                                val pulgadas = partes[1].replace("\"", "").toInt()
                                ((pies * 12 + pulgadas) * 2.54f)
                            } catch (e: Exception) {
                                perfil.altura
                            }
                        }
                        else -> newValue.toFloatOrNull() ?: perfil.altura
                    }
                    onPerfilChanged(perfil.copy(altura = nuevaAltura))
                }
            )

            EditableNumberField(
                label = "Peso Actual ${if (sistemaPeso == "Imperial (lb)") "(lb)" else "(kg)"}",
                value = String.format("%.1f", pesoActualMostrado),
                onValueChange = { newValue ->
                    val nuevoPeso = newValue.toFloatOrNull()?.let { peso ->
                        if (sistemaPeso == "Imperial (lb)") peso / 2.20462f else peso
                    } ?: perfil.pesoActual
                    onPerfilChanged(perfil.copy(pesoActual = nuevoPeso))
                }
            )

            EditableNumberField(
                label = "Peso Objetivo ${if (sistemaPeso == "Imperial (lb)") "(lb)" else "(kg)"}",
                value = String.format("%.1f", pesoObjetivoMostrado),
                onValueChange = { newValue ->
                    val nuevoPeso = newValue.toFloatOrNull()?.let { peso ->
                        if (sistemaPeso == "Imperial (lb)") peso / 2.20462f else peso
                    } ?: perfil.pesoObjetivo
                    onPerfilChanged(perfil.copy(pesoObjetivo = nuevoPeso))
                }
            )

            EditableNumberField(
                label = "Edad",
                value = perfil.edad.toString(),
                onValueChange = { newValue ->
                    onPerfilChanged(perfil.copy(edad = newValue.toIntOrNull() ?: perfil.edad))
                }
            )

            // Dropdowns para selecciones
            EditableDropdownField(
                label = "Nivel de Actividad",
                value = perfil.nivelActividad,
                options = listOf(
                    "Sedentario",
                    "Ligeramente activo",
                    "Moderadamente activo",
                    "Muy activo"
                ),
                onValueChange = { onPerfilChanged(perfil.copy(nivelActividad = it)) }
            )

            EditableDropdownField(
                label = "Objetivo",
                value = perfil.objetivo,
                options = listOf("Perder peso", "Mantener peso", "Ganar peso"),
                onValueChange = { onPerfilChanged(perfil.copy(objetivo = it)) }
            )

            EditableDropdownField(
                label = "Cómo conseguirlo",
                value = perfil.comoConseguirlo,
                options = listOf("Plan nutricional", "Contar calorías"),
                onValueChange = { onPerfilChanged(perfil.copy(comoConseguirlo = it)) }
            )

            EditableDropdownField(
                label = "Entrenamiento de fuerza",
                value = perfil.entrenamientoFuerza,
                options = listOf("Sí", "No"),
                onValueChange = { onPerfilChanged(perfil.copy(entrenamientoFuerza = it)) }
            )

            // Biografía
            EditableField(
                label = "Biografía",
                value = perfil.biografia ?: "",
                onValueChange = { onPerfilChanged(perfil.copy(biografia = it)) },
                singleLine = false
            )
        }
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = singleLine
    )
}

@Composable
private fun EditableFieldType(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    type:Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = type,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = singleLine
    )
}

@Composable
private fun EditableNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                .padding(vertical = 4.dp)
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
}

@Composable
fun ProfileInfoCard(perfil: Perfil) {
    val sistemaPeso = perfil.unidadesPreferences.sistemaPeso
    val sistemaAltura = perfil.unidadesPreferences.sistemaAltura

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoItem("Nombre", "${perfil.nombre} ${perfil.apellido}")
            ProfileInfoItem("Username", perfil.username)
            ProfileInfoItem("Correo Electronico", perfil.email)
            ProfileInfoItem("Género", perfil.genero)

            // Mostrar altura según el sistema seleccionado
            val alturaTexto = if (sistemaAltura == "Imperial (ft/in)") {
                val totalPulgadas = (perfil.altura / 2.54).toInt()
                val pies = totalPulgadas / 12
                val pulgadas = totalPulgadas % 12
                "$pies' $pulgadas\""
            } else {
                "${perfil.altura.toInt()} cm"
            }
            ProfileInfoItem("Altura", alturaTexto)

            // Mostrar pesos según el sistema seleccionado
            val pesoActualTexto = if (sistemaPeso == "Imperial (lb)") {
                val pesoLb = perfil.pesoActual * 2.20462f
                "${"%.1f".format(pesoLb)} lb"
            } else {
                "${"%.1f".format(perfil.pesoActual)} kg"
            }

            val pesoObjetivoTexto = if (sistemaPeso == "Imperial (lb)") {
                val pesoLb = perfil.pesoObjetivo * 2.20462f
                "${"%.1f".format(pesoLb)} lb"
            } else {
                "${"%.1f".format(perfil.pesoObjetivo)} kg"
            }

            ProfileInfoItem("Edad", "${perfil.edad} años")
            ProfileInfoItem("Peso actual", pesoActualTexto)
            ProfileInfoItem("Peso objetivo", pesoObjetivoTexto)

            // Calcular y mostrar IMC
            val alturaMetros = perfil.altura / 100
            val imc = perfil.pesoActual / (alturaMetros * alturaMetros)
            ProfileInfoItem("IMC", "${"%.1f".format(imc)}")

            // Nivel de actividad y objetivos
            ProfileInfoItem("Nivel de actividad", perfil.nivelActividad)
            ProfileInfoItem("Objetivo", perfil.objetivo)
            ProfileInfoItem("Método", perfil.comoConseguirlo)
            ProfileInfoItem("Entrenamiento de fuerza", perfil.entrenamientoFuerza)

            // Biografía (si existe)
            perfil.biografia?.let {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Biografía",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnidadesPreferencesDialog(
    currentPreferences: UnidadesPreferences?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var sistemaPeso by remember { mutableStateOf(currentPreferences?.sistemaPeso ?: "Métrico (kg)") }
    var sistemaAltura by remember { mutableStateOf(currentPreferences?.sistemaAltura ?: "Métrico (cm)") }
    var sistemaVolumen by remember { mutableStateOf(currentPreferences?.sistemaVolumen ?: "Métrico (ml)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar unidades de medida") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sistema de peso
                Text(
                    text = "Sistema de peso",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaPeso == "Métrico (kg)",
                        onClick = { sistemaPeso = "Métrico (kg)" }
                    )
                    Text("Kilogramos (kg)", modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaPeso == "Imperial (lb)",
                        onClick = { sistemaPeso = "Imperial (lb)" }
                    )
                    Text("Libras (lb)", modifier = Modifier.padding(start = 8.dp))
                }

                HorizontalDivider()

                // Sistema de altura
                Text(
                    text = "Sistema de altura",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaAltura == "Métrico (cm)",
                        onClick = { sistemaAltura = "Métrico (cm)" }
                    )
                    Text("Centímetros (cm)", modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaAltura == "Imperial (ft/in)",
                        onClick = { sistemaAltura = "Imperial (ft/in)" }
                    )
                    Text("Pies y pulgadas (ft/in)", modifier = Modifier.padding(start = 8.dp))
                }

                HorizontalDivider()

                // Sistema de volumen
                Text(
                    text = "Sistema de volumen",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaVolumen == "Métrico (ml)",
                        onClick = { sistemaVolumen = "Métrico (ml)" }
                    )
                    Text("Mililitros (ml)", modifier = Modifier.padding(start = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = sistemaVolumen == "Imperial (fl oz)",
                        onClick = { sistemaVolumen = "Imperial (fl oz)" }
                    )
                    Text("Onzas fluidas (fl oz)", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(sistemaPeso, sistemaAltura, sistemaVolumen)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AccountSettingsSection(
    perfil: Perfil,
    isEditing: Boolean,
    onPasswordChangeRequest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Texto indicativo para cuentas Gmail
            if (perfil.authType == AuthType.GMAIL) {
                Text(
                    text = "Cuenta vinculada a Gmail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Botón de cambio de contraseña solo para cuentas APP
            if (perfil.authType == AuthType.APP && isEditing) {
                Button(
                    onClick = onPasswordChangeRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cambiar contraseña")
                }
            }
        }
    }
}

@Composable
fun PasswordChangeDialog(
    viewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val passwordUpdateState by viewModel.passwordUpdateState.collectAsState()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }

    // Efecto para manejar el estado de actualización
    LaunchedEffect(passwordUpdateState) {
        when (passwordUpdateState) {
            is AuthViewModel.PasswordUpdateState.Success -> {
                // Mostrar snackbar o mensaje de éxito
                onDismiss()
            }
            is AuthViewModel.PasswordUpdateState.Error -> {
                // El error se muestra en el diálogo
            }
            else -> {}
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar contraseña") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campos de contraseña...
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña actual") },
                    visualTransformation = if (showPasswords)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordUpdateState is AuthViewModel.PasswordUpdateState.Error,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = if (showPasswords)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = if (showPasswords)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Checkbox para mostrar contraseñas
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showPasswords,
                        onCheckedChange = { showPasswords = it }
                    )
                    Text("Mostrar contraseñas")
                }

                // Mostrar error si existe
                if (passwordUpdateState is AuthViewModel.PasswordUpdateState.Error) {
                    Text(
                        text = (passwordUpdateState as AuthViewModel.PasswordUpdateState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Mostrar loading si está procesando
                if (passwordUpdateState is AuthViewModel.PasswordUpdateState.Loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword == confirmPassword) {
                        viewModel.updatePassword(currentPassword, newPassword)
                    }
                },
                enabled = currentPassword.isNotBlank() &&
                        newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        passwordUpdateState !is AuthViewModel.PasswordUpdateState.Loading
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}