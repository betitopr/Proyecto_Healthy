package com.example.proyectohealthy.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.model.TeamPost
import com.example.proyectohealthy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectohealthy.data.repository.PerfilRepository

@Composable
fun TeamPostItem(
    post: TeamPost,
    currentPerfil: Perfil?,
    navController: NavController,
    perfilRepository: PerfilRepository,
    onLike: () -> Unit,
    onComment: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var autorPerfil by remember { mutableStateOf<Perfil?>(null) }
    var showComments by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    // Cargar perfil del autor
    LaunchedEffect(post.autorId) {
        autorPerfil = perfilRepository.getPerfil(post.autorId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con foto y nombre clickeables
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de perfil
                AsyncImage(
                    model = autorPerfil?.perfilImagen ?: "/api/placeholder/40/40",
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("profile/${post.autorId}")
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Información del autor
                Column {
                    Text(
                        text = autorPerfil?.username ?: "Usuario",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            navController.navigate("profile/${post.autorId}")
                        }
                    )
                    Text(
                        text = "Publicado hace ${getTimeAgo(post.timestamp)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Si el post es del usuario actual, mostrar opciones de editar/eliminar
                if (currentPerfil?.uid == post.autorId) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onUpdate(post.content) }) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, "Eliminar")
                        }
                    }
                }
            }

            // Contenido del post
            Text(
                text = post.content,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Imagen si existe
            post.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de likes con contador
                Button(
                    onClick = onLike,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (post.likes[currentPerfil?.uid] == true)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (post.likes[currentPerfil?.uid] == true)
                            Color.White
                        else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${post.likeCount} Me gusta",
                        color = if (post.likes[currentPerfil?.uid] == true)
                            Color.White
                        else MaterialTheme.colorScheme.primary
                    )
                }

                // Botón de comentarios con contador
                TextButton(
                    onClick = { showComments = !showComments },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Outlined.Comment,
                        contentDescription = "Comentarios"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.comments.size} Comentarios")
                }
            }

// Y reemplaza la sección de comentarios por esta:
// Sección de comentarios
            if (showComments) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        LazyColumn(
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(post.comments.values.toList().sortedByDescending { it.timestamp }) { comment ->
                                CommentItem(
                                    comment = comment,
                                    perfilRepository = perfilRepository,
                                    navController = navController
                                )
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newComment,
                                onValueChange = { newComment = it },
                                placeholder = { Text("Añade un comentario...") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )
                            IconButton(
                                onClick = {
                                    if (newComment.isNotBlank()) {
                                        onComment(newComment)
                                        newComment = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, "Enviar comentario")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    perfilRepository: PerfilRepository,
    navController: NavController
) {
    var autorPerfil by remember { mutableStateOf<Perfil?>(null) }

    LaunchedEffect(comment.autorId) {
        autorPerfil = perfilRepository.getPerfil(comment.autorId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = autorPerfil?.perfilImagen,
            contentDescription = "Foto del autor",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable {
                    navController.navigate("profile/${comment.autorId}")
                }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = autorPerfil?.username ?: "Usuario",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    navController.navigate("profile/${comment.autorId}")
                }
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 1000 * 60 -> "hace un momento"
        diff < 1000 * 60 * 60 -> "${diff / (1000 * 60)} minutos"
        diff < 1000 * 60 * 60 * 24 -> "${diff / (1000 * 60 * 60)} horas"
        else -> "${diff / (1000 * 60 * 60 * 24)} días"
    }
}