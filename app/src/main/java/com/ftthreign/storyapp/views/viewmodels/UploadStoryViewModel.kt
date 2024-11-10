package com.ftthreign.storyapp.views.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ftthreign.storyapp.data.remote.StoryRepository
import java.io.File

class UploadStoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel(){
    private var _curImage = MutableLiveData<Uri?>()
    val curImage : MutableLiveData<Uri?> = _curImage

    fun setCurImage(uri : Uri?) { _curImage.value = uri }

    fun uploadStory(file : File, description : String) = storyRepository.addStory(file, description)
}