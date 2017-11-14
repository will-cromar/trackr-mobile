package com.example.my.trackr.data

import java.text.SimpleDateFormat
import java.util.*

// Used for classes that are often displayed in list views
interface Listable {
    val listTitle: String
    val listSubtitle: String
}

// Represents a cast or crew member
data class Person(val person_id: Long, val name: String): Listable {
    override val listTitle: String
        get() = name
    override val listSubtitle: String
        get() = ""
}

// Represents a genre
data class Genre(val genre_id: Long, val genre: String): Listable {
    override val listTitle: String
        get() = genre
    override val listSubtitle: String
        get() = ""
}

// Represent a movie or TV show
data class Listing(val listing_id: Long, val title: String, val description: String,
                   val release_date: Long, val actors: List<Person>, val writers: List<Person>,
                   val directors: List<Person>, val genres: List<Genre>): Listable {
    companion object {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy @ HH:mm", Locale.US)
    }

    val releaseDatePretty: String
        get() = dateFormat.format(release_date * 1000L)

    override val listTitle: String
        get() = title
    override val listSubtitle: String
        get() = releaseDatePretty


}