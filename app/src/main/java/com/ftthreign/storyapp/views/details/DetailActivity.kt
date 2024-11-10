package com.ftthreign.storyapp.views.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ftthreign.storyapp.R
import com.ftthreign.storyapp.data.remote.response.ListStoryItem
import com.ftthreign.storyapp.databinding.ActivityDetailBinding
import com.ftthreign.storyapp.helpers.formatDate
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val storyDetail = intent.getParcelableExtra<ListStoryItem>("STORY_DATA") as ListStoryItem

        Picasso
            .get()
            .load(storyDetail.photoUrl)
            .placeholder(R.drawable.ic_broken_image)
            .into(binding.storyImage)
        binding.username.text = storyDetail.name
        binding.description.text = storyDetail.description
        binding.createdAt.text = formatDate(storyDetail.createdAt as String)
    }
}