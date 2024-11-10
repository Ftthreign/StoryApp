package com.ftthreign.storyapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ftthreign.storyapp.data.remote.api.ApiService
import com.ftthreign.storyapp.data.remote.response.AddStoryResponse
import com.ftthreign.storyapp.data.remote.response.StoriesResponse
import com.ftthreign.storyapp.helpers.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.HttpException
import java.io.File

class StoryRepository(
    private val apiService : ApiService,
) {

    fun getAllStories(
    ) : LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val res = apiService.getStories()

            if(!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }
        } catch (e : HttpException) {
            Log.e(StoriesResponse::class.java.simpleName, e.message.toString())
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val parseError = Gson().fromJson(errorRes, StoriesResponse::class.java)
                emit(Result.Success(parseError))
            } catch (e : Exception) {
                emit(Result.Error("Error : ${e.message}"))
            }
        }
    }

    fun addStory(
        file : File,
        description : String
    ) : LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        val reqBody = description.toRequestBody("text/plain".toMediaType())
        val reqImageData = file.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImageData
        )
        try {
            val res = apiService.addStory( multipartBody, reqBody)
            if (!res.error!!) {
                emit(Result.Success(res))
            } else {
                emit(Result.Error(res.message ?: "Unknown Error"))
            }
        } catch (e : HttpException) {
            try {
                val errorRes = e.response()?.errorBody()?.string()
                val parseError = Gson().fromJson(errorRes, AddStoryResponse::class.java)
                emit(Result.Success(parseError))
            } catch (e : Exception) {
                emit(Result.Error(e.message.toString()))
            }
            Log.e("Fail to add story", e.message().toString())
        } catch (e : IOException) {
            emit(Result.Error(e.message.toString()))
            Log.e("IOException", e.message.toString())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE : StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ) : StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService)
            }.also { INSTANCE = it }
    }
}