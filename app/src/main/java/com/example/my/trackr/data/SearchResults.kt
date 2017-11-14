package com.example.my.trackr.data

import java.text.SimpleDateFormat
import java.util.*


// Represents a cast or crew member
data class Person(val person_id: Long, val name: String)

// Represents a genre
data class Genre(val genre_id: Long, val genre: String)

// Represent a movie or TV show
data class Listing(val listing_id: Long, val title: String, val description: String,
                   val release_date: Long, val actors: List<Person>, val writers: List<Person>,
                   val directors: List<Person>, val genres: List<Genre>) {
    companion object {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    }

    // Used when displaying listing in search results
    val listTitle: String
            get() = title
    val listSubtitle: String
            get() = dateFormat.format(release_date * 1000L)
}