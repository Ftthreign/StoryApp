package com.ftthreign.storyapp.views.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ftthreign.storyapp.data.remote.AuthRepository
import com.ftthreign.storyapp.data.remote.StoryRepository
import com.ftthreign.storyapp.di.Injection

class ViewModelFactory(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> {
                DetailsViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> {
                AuthenticationViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(storyRepository) as T
            }
            else -> {
                Log.e(ViewModelFactory::class.java.simpleName, "Unknown ViewModel") as T
                throw IllegalArgumentException("Unknown ViewModel ${modelClass.name}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance : ViewModelFactory? = null
        fun getInstance(context: Context) : ViewModelFactory =
            instance ?: synchronized(this) {
                val auth = Injection.provideAuthRepository(context)
                val story = Injection.provideStoryRepository(context)
                instance ?: ViewModelFactory(story, auth)
            }
    }
}