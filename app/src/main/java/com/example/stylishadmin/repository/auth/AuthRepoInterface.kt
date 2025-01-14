package com.example.stylishadmin.repository.auth

interface AuthRepoInterface {
    suspend fun signIn(email: String, password: String): Result<Unit>
}