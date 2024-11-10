package com.ftthreign.storyapp.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ftthreign.storyapp.R
import com.ftthreign.storyapp.databinding.ActivityMainBinding
import com.ftthreign.storyapp.helpers.Result
import com.ftthreign.storyapp.views.adapter.StoryListAdapter
import com.ftthreign.storyapp.views.auth.LoginActivity
import com.ftthreign.storyapp.views.upload.UploadStoryActivity
import com.ftthreign.storyapp.views.viewmodels.MainViewModel
import com.ftthreign.storyapp.views.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val storyAdapter : StoryListAdapter = StoryListAdapter()
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addStory.setOnClickListener {
            startActivity(Intent(this, UploadStoryActivity::class.java))
        }

        viewModel.getSession().observe(this) {user ->
            if(!user.isLoading) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        setupRv()
        setupData()
    }

    private fun setupRv() {
        binding.recyclerView.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupData() {
        viewModel.getStory().observe(this) {data ->
            when(data) {
                is Result.Loading -> {
                    binding.loadStory.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.loadStory.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.loadStory.visibility = View.GONE
                    storyAdapter.submitList(data.data.listStory)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}