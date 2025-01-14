package com.example.stylishadmin.repository.users

import com.example.stylishadmin.model.user.User

interface UserRepoInterface {
    suspend fun getUser(userId: String): Result<User>
    suspend fun getTotalUsers(): Result<List<User>>

}