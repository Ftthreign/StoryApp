package com.ftthreign.storyapp.helper

import com.ftthreign.storyapp.data.remote.response.ListStoryItem
import kotlin.random.Random
import java.util.UUID

object DataDummy {
    fun generateDummyStoryResponse() : List<ListStoryItem> {
        val items : MutableList<ListStoryItem> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItem(
                id = UUID.randomUUID().toString(),
                createdAt = "2024-11-16T12:34:56Z",
                description = "This is a description for Story $i",
                name = "Story by $i",
                lon = Random.nextDouble(100.0, 140.0),
                lat = Random.nextDouble(-10.0, 10.0),
                photoUrl = "https://picsum.photos/200/300?random=$i"
            )
            items.add(story)
        }
        return items
    }
}