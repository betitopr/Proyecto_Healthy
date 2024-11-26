package com.example.proyectohealthy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.proyectohealthy.model.Comment
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.example.proyectohealthy.data.local.entity.Perfil
import com.example.proyectohealthy.data.repository.PerfilRepository
import com.example.proyectohealthy.model.TeamPost
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    val perfilRepository: PerfilRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<TeamPost>>(emptyList())
    val posts: StateFlow<List<TeamPost>> = _posts

    private val _currentPerfil = MutableStateFlow<Perfil?>(null)
    val currentPerfil = _currentPerfil.asStateFlow()

    init {
        loadCurrentPerfil()
        loadPosts()
    }

    private fun loadCurrentPerfil() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { uid ->
                perfilRepository.getPerfilFlow(uid).collect { perfil ->
                    _currentPerfil.value = perfil
                }
            }
        }
    }

    private fun loadPosts() {
        val postsRef = database.getReference("team_posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postsList = mutableListOf<TeamPost>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(TeamPost::class.java)
                    post?.let {
                        //Cargar los comentarios de cada publicación
                        loadCommentsForPost(postSnapshot.child("comments"), it)
                        postsList.add(it)
                    }
                }
                _posts.value = postsList.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                //manejo de errores
            }
        })
    }

    private fun loadCommentsForPost(commentsSnapshot: DataSnapshot, post: TeamPost) {
        val commentsMap = mutableMapOf<String, Comment>()
        for (commentSnapshot in commentsSnapshot.children) {
            val comment = commentSnapshot.getValue(Comment::class.java)
            comment?.let {
                commentsMap[comment.id] = it
            }
        }
        post.comments = commentsMap.toMutableMap()
    }


    fun createPost(content: String, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                val currentPerfil = _currentPerfil.value ?: return@launch

                val imageUrl = imageUri?.let { uploadImage(it) }
                val post = TeamPost(
                    id = UUID.randomUUID().toString(),
                    autorId = currentPerfil.uid,
                    content = content,
                    imageUrl = imageUrl,
                    likes = emptyMap()
                )
                database.getReference("team_posts")
                    .child(post.id)
                    .setValue(post)
                    .await()
            } catch (e: Exception) {
                Log.e("TeamsViewModel", "Error creating post", e)
            }
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String {
        val ref = storage.reference.child("team_posts/${UUID.randomUUID()}")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val postRef = database.getReference("team_posts").child(postId)
                postRef.runTransaction(object : com.google.firebase.database.Transaction.Handler {
                    override fun doTransaction(mutableData: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                        val post = mutableData.getValue(TeamPost::class.java)
                        if (post != null) {
                            val likes = post.likes.toMutableMap()
                            if (likes[currentUser.uid] == true) {
                                likes.remove(currentUser.uid)
                            } else {
                                likes[currentUser.uid] = true
                            }
                            post.likes = likes
                            mutableData.value = post
                        }
                        return com.google.firebase.database.Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        //manejo de la finalización de la transacción porsiacaso
                    }
                })
            }
        }
    }

    fun addComment(postId: String, content: String) {
        viewModelScope.launch {
            try {
                val currentPerfil = _currentPerfil.value ?: return@launch
                val commentId = UUID.randomUUID().toString()

                val comment = Comment(
                    id = commentId,
                    postId = postId,
                    autorId = currentPerfil.uid,  // Usamos el ID del perfil actual
                    content = content,
                    timestamp = System.currentTimeMillis()
                )

                database.getReference("team_posts")
                    .child(postId)
                    .child("comments")
                    .child(commentId)
                    .setValue(comment)
                    .await()
            } catch (e: Exception) {
                Log.e("TeamsViewModel", "Error adding comment", e)
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                database.getReference("team_posts").child(postId).removeValue().await()
            } catch (e: Exception) {
                //otro error
            }
        }
    }

    // Nuevo método para obtener perfil por ID
    private suspend fun getPerfilById(perfilId: String): Perfil? {
        return perfilRepository.getPerfil(perfilId)
    }

    // Nuevo método para navegar al perfil
    fun navigateToProfile(navController: NavController, perfilId: String) {
        navController.navigate("profile/$perfilId")
    }

    fun updatePost(postId: String, newContent: String) {
        viewModelScope.launch {
            try {
                database.getReference("team_posts").child(postId).child("content").setValue(newContent).await()
            } catch (e: Exception) {
                //error de manejos
            }
        }
    }
}