package com.example.stylishadmin.repository.users

import com.example.stylishadmin.model.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class  UserRepoImp: UserRepoInterface {
    private val db = Firebase.database
    private val usersRef = db.getReference("users")
    private val auth = FirebaseAuth.getInstance()
    override suspend fun getUser(userId: String): Result<User> {
        val userRef = usersRef.child(userId)
        try {

            val snapshot = userRef.get().await()
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                return Result.success(user) as Result<User>

            } else {
                return Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}