/*package com.example.proyectohealthy.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}*/