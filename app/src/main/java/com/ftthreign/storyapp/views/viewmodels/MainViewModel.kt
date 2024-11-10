package com.ftthreign.storyapp.views.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ftthreign.storyapp.data.local.pref.UserModel
import com.ftthreign.storyapp.data.remote.AuthRepository
import com.ftthreign.storyapp.data.remote.StoryRepository
import kotlinx.coroutines.launch


class MainViewModel(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getSession() : LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }

    fun getStory() = storyRepository.getAllStories()
}