package com.example.proyectohealthy.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectohealthy.ui.viewmodel.TeamsViewModel
import androidx.compose.ui.Alignment
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectohealthy.components.CustomBottomBar
import com.example.proyectohealthy.components.CustomTopBar
import com.example.proyectohealthy.components.TeamPostItem
import com.example.proyectohealthy.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    perfilViewModel: PerfilViewModel,
    viewModel: TeamsViewModel = hiltViewModel(),
    navController: NavController
) {
    val posts by viewModel.posts.collectAsState()
    val currentPerfil by viewModel.currentPerfil.collectAsState()
    var showNewPostDialog by remember { mutableStateOf(false) }
    val perfilState by perfilViewModel.currentPerfil.collectAsState()


    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Teams",
                userPhotoUrl = perfilState?.perfilImagen
            )
        },
        bottomBar = {
            CustomBottomBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewPostDialog = true }) {
                Icon(Icons.Default.Add, "Nueva publicación")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(posts) { post ->
                TeamPostItem(
                    post = post,
                    currentPerfil = currentPerfil,
                    navController = navController,
                    perfilRepository = viewModel.perfilRepository,
                    onLike = { viewModel.likePost(post.id) },
                    onComment = { content -> viewModel.addComment(post.id, content) },
                    onDelete = { viewModel.deletePost(post.id) },
                    onUpdate = { newContent -> viewModel.updatePost(post.id, newContent) }
                )
            }
        }

        // Dialog para nuevo post
        if (showNewPostDialog) {
            NewPostDialog(
                onDismiss = { showNewPostDialog = false },
                onPost = { content, imageUri ->
                    viewModel.createPost(content, imageUri)
                    showNewPostDialog = false
                }
            )
        }
    }
}

@Composable
private fun NewPostDialog(
    onDismiss: () -> Unit,
    onPost: (String, Uri?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva publicación") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedImageUri != null) "Cambiar imagen" else "Añadir imagen")
                }

                selectedImageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onPost(content, selectedImageUri)
                    }
                }
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}