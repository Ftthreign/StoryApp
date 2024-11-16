package com.ftthreign.storyapp.views.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ftthreign.storyapp.data.local.pref.UserModel
import com.ftthreign.storyapp.data.remote.AuthRepository
import com.ftthreign.storyapp.data.remote.StoryRepository
import com.ftthreign.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch


class MainViewModel(
    storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val story : LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStory().cachedIn(viewModelScope)

    fun getSession() : LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
    }
}