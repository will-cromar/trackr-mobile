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
            get() = dateFormat.format(releaseDate * 1000L)
}