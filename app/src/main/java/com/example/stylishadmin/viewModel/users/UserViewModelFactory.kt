package com.example.stylishadmin.viewModel.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylishadmin.repository.users.UserRepoInterface

class UserViewModelFactory(private val userRepo: UserRepoInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(userRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}