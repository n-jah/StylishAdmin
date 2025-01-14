package com.example.stylishadmin.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

class AuthRepository : AuthRepoInterface {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit) // Sign-in successful
        } catch (e: Exception) {
            // Handle specific error messages
            val message = when (e) {
                is FirebaseAuthInvalidUserException -> "No account found with this email."
                is FirebaseAuthInvalidCredentialsException -> "Incorrect password. Please try again."
                else -> e.message ?: "Authentication failed."
            }
            Result.failure(Exception(message))
        }
    }
}