package com.example.my.trackr.data

import java.text.SimpleDateFormat
import java.util.*


interface SearchResult {
    val title: String
    val subtitle: String
}

data class Movie(val name: String, val releaseDate: Long) : SearchResult {
    companion object {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    }
    override val title: String
            get() = name
    override val subtitle: String
            // Scale seconds to milliseconds
            get() = dateFormat.format(releaseDate * 1000L)
}

// Represents a cast or crew member
data class Person(val personId: Long, val name: String)

// Represents a genre
data class Genre(val genreId: Long, val genre: String)

// Represent a movie or TV show
data class Listing(val title: String, val description: String, val releaseDate: Long,
                   val actors: List<Person>, val writers: List<Person>, val directors: List<Person>,
                   val genres: List<Genre>)