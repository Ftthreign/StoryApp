package com.ftthreign.storyapp.di

import android.content.Context
import com.ftthreign.storyapp.data.local.pref.UserPreference
import com.ftthreign.storyapp.data.local.pref.dataStore
import com.ftthreign.storyapp.data.remote.AuthRepository
import com.ftthreign.storyapp.data.remote.StoryRepository
import com.ftthreign.storyapp.data.remote.api.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return StoryRepository.getInstance(apiService)
    }
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return AuthRepository.getInstance(apiService, pref)
    }
}