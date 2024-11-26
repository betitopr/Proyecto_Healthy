package com.example.proyectohealthy.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.database.ServerValue

data class Comment(
    val id: String = "",
    val postId: String = "",
    val autorId: String = "", // ID del Perfil
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = "",
        postId = "",
        autorId = "",
        content = "",
        timestamp = System.currentTimeMillis()
    )
}