package com.ftthreign.storyapp.views.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ftthreign.storyapp.data.local.pref.UserModel
import com.ftthreign.storyapp.data.remote.AuthRepository
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun login(email : String, password : String) = authRepository.loginUser(email, password)

    fun register(name : String, email: String, password: String) = authRepository.registerUser(name, email, password)

    fun saveSessionData(userData : UserModel) = viewModelScope.launch {
        authRepository.saveSession(userData)
    }
}