package com.ftthreign.storyapp.views.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.ftthreign.storyapp.data.remote.response.AddStoryResponse
import com.ftthreign.storyapp.databinding.ActivityUploadStoryBinding
import com.ftthreign.storyapp.helpers.Result
import com.ftthreign.storyapp.helpers.getImageUri
import com.ftthreign.storyapp.helpers.reduceFileImage
import com.ftthreign.storyapp.helpers.showMaterialDialog
import com.ftthreign.storyapp.helpers.uriToFile
import com.ftthreign.storyapp.views.MainActivity
import com.ftthreign.storyapp.views.viewmodels.UploadStoryViewModel
import com.ftthreign.storyapp.views.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUploadStoryBinding
    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        viewModel.curImage.observe(this) {image ->
            binding.imagePreview.setImageURI(image)
        }

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonUpload.setOnClickListener { uploadStory() }
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri : Uri? ->
        if(uri!= null) {
            binding.imagePreview.setImageURI(uri)
            viewModel.setCurImage(uri)
        }
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        if(it) {
            binding.imagePreview.setImageURI(viewModel.curImage.value)
        } else {
            viewModel.setCurImage(null)
        }
    }

    private fun startGallery() = launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    private fun startCamera() {
        viewModel.setCurImage(getImageUri(this))
        launchIntentCamera.launch(viewModel.curImage.value as Uri)
    }

    private fun uploadStory() {
        viewModel.curImage.value?.let { uri ->
            lifecycleScope.launch {
                binding.progressUpload.visibility = View.VISIBLE
                val image = withContext(Dispatchers.IO) {
                    uriToFile(uri, this@UploadStoryActivity).reduceFileImage()
                }
                Log.d("path", image.path)
                val desc = binding.editTextDescription.text.toString()
                viewModel.uploadStory(image, desc).observe(this@UploadStoryActivity) {res ->
                    when(res) {
                        is Result.Loading -> {
                            binding.progressUpload.visibility = View.VISIBLE
                        }
                        is Result.Error -> {
                            binding.progressUpload.visibility = View.GONE
                            showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.error}", "Retry")
                            Log.d("this@UploadStoryActivity::", res.error)
                        }
                        is Result.Success -> {
                            binding.progressUpload.visibility = View.GONE
                            if(res.data.error as Boolean) {
                                showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.data.error}", "Retry")
                            } else {
                                val intent = Intent(this@UploadStoryActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}