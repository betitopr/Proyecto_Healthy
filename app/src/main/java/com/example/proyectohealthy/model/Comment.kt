package com.example.proyectohealthy.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.database.ServerValue

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val content: String = "",
    val authorName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    //val upvotes: Int = 0,
    //val downvotes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
    ) {
    //Constructor sin argumentos requerido por Firebase
    constructor() : this(
        id = "",
        postId = "",
        authorId = "",
        content = "",
        authorName = "",
        createdAt = System.currentTimeMillis(),
        //upvotes = 0,
        //downvotes = 0,
        timestamp = System.currentTimeMillis()
    )
}
