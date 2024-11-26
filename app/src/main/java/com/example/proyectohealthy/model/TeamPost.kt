package com.example.proyectohealthy.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.database.ServerValue

data class TeamPost(
    val id: String = "",
    val autorId: String = "",
    // Ya no necesitamos autorName porque lo obtendremos del Perfil
    val content: String = "",
    val imageUrl: String? = null,
    val categoria: PostCategory = PostCategory.TIPS_NUTRICION,
    var likes: Map<String, Boolean> = emptyMap(),
    var comments: MutableMap<String, Comment> = mutableMapOf(),
    val timestamp: Long = System.currentTimeMillis()
) {
    // Propiedades calculadas
    val likeCount: Int
        get() = likes.count { it.value }

    val commentCount: Int
        get() = comments.size

    enum class PostCategory {
        PROGRESO,
        RECETAS,
        TIPS_NUTRICION,
        DUDAS_DIETA,
        MOTIVACION
    }
}