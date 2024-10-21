package com.example.proyectohealthy.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectohealthy.model.TeamPost
//import java.util.UUID//UUID
import com.example.proyectohealthy.model.Comment
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.outlined.*


//no es recomendable definirlo acá pero me da hueva crear otro archivojuasjuas
/*data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val authorId: String = "",
    val authorName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)*/

@Composable
fun TeamPostItem(
    post: TeamPost,
    onLike: () -> Unit,
    onComment: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var showComments by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf(post.content) }

    val currentUser = FirebaseAuth.getInstance().currentUser

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //info del autor del post
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://api.dicebear.com/6.x/initials/svg?seed=${post.authorName}",
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Publicado hace ${getTimeAgo(post.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                if (currentUser?.uid == post.authorId) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //contenido de la publicacion
            if (isEditing) {
                TextField(
                    value = editedContent,
                    onValueChange = { editedContent = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                    TextButton(
                        onClick = {
                            onUpdate(editedContent)
                            isEditing = false
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            } else {
                Text(text = post.content)
            }

            Spacer(modifier = Modifier.height(12.dp))

            //imagen
            post.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            //botones del like y el comentartio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onLike,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (post.likes[currentUser?.uid] == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        tint = if (post.likes[currentUser?.uid] == true) Color.White else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${post.likeCount} Me gusta",
                        color = if (post.likes[currentUser?.uid] == true) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
                TextButton(
                    onClick = { showComments = !showComments },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Outlined.Comment,
                        contentDescription = "Toggle comments"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.commentCount} Comentarios")
                    Icon(
                        if (showComments) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand comments"
                    )
                }
            }

            //seccion comentarios
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
                            items(post.comments.values.toList()) { comment ->
                                CommentItem(comment)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
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
                                Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        AsyncImage(
            model = "https://api.dicebear.com/6.x/initials/svg?seed=${comment.authorName}",
            contentDescription = "Commenter Avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = comment.authorName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
//porfinme salio
fun getTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 1000 * 60 -> "justo ahora"
        diff < 1000 * 60 * 60 -> "${diff / (1000 * 60)} minutos"
        diff < 1000 * 60 * 60 * 24 -> "${diff / (1000 * 60 * 60)} horas"
        else -> "${diff / (1000 * 60 * 60 * 24)} días"
    }
}