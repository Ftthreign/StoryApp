package com.ftthreign.storyapp.views.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ftthreign.storyapp.R
import com.ftthreign.storyapp.data.remote.response.ListStoryItem
import com.ftthreign.storyapp.databinding.ItemStoryBinding
import com.ftthreign.storyapp.helpers.formatDate
import com.ftthreign.storyapp.views.details.DetailActivity
import com.squareup.picasso.Picasso

class StoryListAdapter : ListAdapter<ListStoryItem, StoryListAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    inner class StoryViewHolder(private val binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : ListStoryItem) {
            val context = binding.root.context

            Picasso
                .get()
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_broken_image)
                .into(binding.storyImage)
            binding.storyAuthor.text = context.getString(R.string.story_author, data.name)
            binding.storyDescription.text = data.description
            binding.timeCreated.text = context.getString(R.string.created_at, formatDate(data.createdAt!!))
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("STORY_DATA", data)
                val actionCompat : ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyImage, "image"),
                        Pair(binding.storyAuthor, "Author"),
                        Pair(binding.storyDescription, "Description"),
                        Pair(binding.timeCreated, "CreatedAt")
                    )
                itemView.context.startActivity(intent, actionCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyData = getItem(position)
        holder.bind(storyData)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}

