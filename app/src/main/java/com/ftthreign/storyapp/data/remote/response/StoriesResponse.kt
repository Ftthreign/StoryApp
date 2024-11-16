package com.ftthreign.storyapp.data.remote.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoriesResponse(

    @field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
	val error: Boolean? = null,

    @field:SerializedName("message")
	val message: String? = null
)

@Parcelize
@Entity(tableName = "story")
data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("id")
	@PrimaryKey
	val id: String,

	@field:SerializedName("lat")
	val lat: Double? = null
) : Parcelable
