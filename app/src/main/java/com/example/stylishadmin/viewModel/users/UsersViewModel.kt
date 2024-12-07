package com.example.stylishadmin.viewModel.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylishadmin.model.user.User
import com.example.stylishadmin.repository.users.UserRepoInterface
import kotlinx.coroutines.launch

class UsersViewModel (private val usersRepository: UserRepoInterface): ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading


    fun getUser(userId: String){
        _loading.value = true
        try {
            viewModelScope.launch {
                val result = usersRepository.getUser(userId)
                if (result.isSuccess) {
                    user.postValue(result.getOrNull())
                    _loading.postValue(false)
                } else {
                    user.postValue(null)
                    _loading.postValue(false)
                }

            }
        }catch (e: Exception){
            user.postValue(null)
            _loading.postValue(false)
            Log.d("UsersViewModel", "Error fetching user data: ${e.message}")
        }finally {
            _loading.postValue(false)
        }
    }

}