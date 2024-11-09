package com.ftthreign.storyapp.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ftthreign.storyapp.R
import com.ftthreign.storyapp.data.response.ListStoryItem
import com.ftthreign.storyapp.databinding.ItemStoryBinding
import com.squareup.picasso.Picasso

class StoryListAdapter(
    private val onItemClickedCallback: OnItemClickedCallback
) : ListAdapter<ListStoryItem, StoryListAdapter.StoryViewHolder>(DIFF_CALLBACK) {
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
            binding.timeCreated.text = context.getString(R.string.created_at, data.createdAt)
            binding.root.setOnClickListener {
                onItemClickedCallback.onItemClicked(data)
            }
        }
    }

    interface OnItemClickedCallback {
        fun onItemClicked(data : ListStoryItem)
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

