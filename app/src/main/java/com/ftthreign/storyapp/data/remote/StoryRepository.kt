package com.ftthreign.storyapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ftthreign.storyapp.data.StoryRemoteMediator
import com.ftthreign.storyapp.data.local.database.StoryDatabase
import com.ftthreign.storyapp.data.remote.api.ApiService
import com.ftthreign.storyapp.data.remote.response.AddStoryResponse
import com.ftthreign.storyapp.data.remote.response.ListStoryItem
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
    private val storyDatabase: StoryDatabase,
    private val apiService : ApiService,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory() : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getListStory() : List<ListStoryItem> = apiService.getStories().listStory

    fun getStoriesWithLocation(location : Int = 1) : LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val res = apiService.getStoriesWithLocation(location)

            emit(Result.Success(res))
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
        description : String,
        lat : Float? = null,
        lon : Float? = null
    ) : LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        val reqBody = description.toRequestBody("text/plain".toMediaType())
        val reqImageData = file.asRequestBody("image/jpeg".toMediaType())
        val latReqData = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonReqData = lon?.toString()?.toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            reqImageData,

        )
        try {
            val res = apiService.addStory( multipartBody, reqBody, latReqData, lonReqData)
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
            storyDatabase: StoryDatabase,
            apiService: ApiService,
        ) : StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(storyDatabase, apiService)
            }.also { INSTANCE = it }
    }
}