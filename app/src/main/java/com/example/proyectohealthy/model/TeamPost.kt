package com.example.proyectohealthy.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.database.ServerValue

data class TeamPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    var likes: Map<String, Boolean> = emptyMap(),
    var comments: MutableMap<String, Comment> = mutableMapOf(),
    //val timestamp: Any = ServerValue.TIMESTAMP
    val timestamp: Long = System.currentTimeMillis()
) {
    val likeCount: Int
        get() = likes.count { it.value }

    val commentCount: Int
        get() = comments.size

    //constructor sin argumentos
    constructor() : this(
        id = "",
        authorId = "",
        authorName = "",
        content = "",
        imageUrl = null,
        likes = emptyMap(),
        comments = mutableMapOf(),
        //timestamp = ServerValue.TIMESTAMP
        timestamp = System.currentTimeMillis(),
    )
}