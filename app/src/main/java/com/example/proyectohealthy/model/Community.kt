package com.example.proyectohealthy.model

import com.google.firebase.Timestamp

data class Community(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val creatorId: String = "",
    val memberCount: Int = 0
)