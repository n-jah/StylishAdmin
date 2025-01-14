package com.example.stylishadmin.viewModel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylishadmin.repository.auth.AuthRepoInterface
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepoInterface) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun signIn(email: String, password: String) {
        _loading.value = true // Start loading

        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            _loading.postValue(false) // Stop loading
            if (result.isSuccess) {
                _loginStatus.postValue(true)
                _errorMessage.postValue(null)
            } else {
                _loginStatus.postValue(false)
                _errorMessage.postValue(result.exceptionOrNull()?.message)
            }
        }
    }
}