package com.ftthreign.storyapp.views.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ftthreign.storyapp.data.remote.response.AddStoryResponse
import com.ftthreign.storyapp.databinding.ActivityUploadStoryBinding
import com.ftthreign.storyapp.helpers.Result
import com.ftthreign.storyapp.helpers.getImageUri
import com.ftthreign.storyapp.helpers.reduceFileImage
import com.ftthreign.storyapp.helpers.showMaterialDialog
import com.ftthreign.storyapp.helpers.showToast
import com.ftthreign.storyapp.helpers.uriToFile
import com.ftthreign.storyapp.views.MainActivity
import com.ftthreign.storyapp.views.viewmodels.UploadStoryViewModel
import com.ftthreign.storyapp.views.viewmodels.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUploadStoryBinding
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var lat : Float? = null
    private var lon : Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupAction()
        getCurrentLocation()
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
        val uri = viewModel.curImage.value ?: return
        lifecycleScope.launch {
            try {
                showProgress(true)

                val image = withContext(Dispatchers.IO) {
                    uriToFile(uri, this@UploadStoryActivity).reduceFileImage()
                }
                Log.d("path", image.path)

                val desc = binding.editTextDescription.text.toString()


                viewModel.uploadStory(image, desc, lat, lon).observe(this@UploadStoryActivity) { res ->
                    handleUploadResult(res)
                }
            } catch (e: Exception) {
                showProgress(false)
                Log.e("UploadStory", "Error during image upload", e)
                showMaterialDialog(this@UploadStoryActivity, "Error", "Unexpected error occurred", "Retry")
            }
        }
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressUpload.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun handleUploadResult(res: Result<AddStoryResponse>) {
        when (res) {
            is Result.Loading -> showProgress(true)
            is Result.Error -> {
                showProgress(false)
                showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.error}", "Retry")
                Log.d("UploadStoryError", res.error)
            }
            is Result.Success -> {
                showProgress(false)
                if (res.data.error == true) {
                    showMaterialDialog(this@UploadStoryActivity, "Error", "Error : ${res.data.message}", "Retry")
                } else {
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@UploadStoryActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun getCurrentLocation() {
        if(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    if(loc != null) {
                        lat = loc.latitude.toFloat()
                        lon = loc.longitude.toFloat()
                        showToast(this@UploadStoryActivity, "Added Location for Story")
                    } else {
                        showToast(this@UploadStoryActivity, "Location not found or disabled")
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted : Boolean ->
        if(isGranted) {
            getCurrentLocation()
        }
    }
}