package com.example.proyectohealthy.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val communityId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),//pinches furros
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val commentCount: Int = 0
)