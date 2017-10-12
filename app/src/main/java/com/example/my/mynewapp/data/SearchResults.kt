package com.example.my.mynewapp.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


interface SearchResult {
    val title: String
    val subtitle: String
}

data class Movie(val name: String, val releaseDate: Long) : SearchResult {
    companion object {
        val dateFomat = SimpleDateFormat("MMMM dd, yyyy")
    }
    override val title: String
            get() = name
    override val subtitle: String
            get() = dateFomat.format(releaseDate * 1000L)
}

data class Query(val search: String)

interface SearchResultProvider {
    fun search(query: Query): List<SearchResult>
}

class DummySearchResultProvider : SearchResultProvider {
    private val gson = Gson()
    private val webApiService = LocalhostWebApiService()

    private val movieNames = listOf(
            "Carpet Stain: The Cleaner Man",
            "Is your Ceiling Fan a Soviet Spy? (A Documentary)",
            "COP 4331C: Processes of Object Oriented Software Development",
            "How to Make a Bad Language: The Story of Java",
            "Inner Life of the Cell",
            "Mitochondria: Powerhouse of the Cell",
            "Linearly Independent",
            "Physics 2: Box Diagrams Reloaded",
            "Do Better 2: Doing Better",
            "Group 7: A Tale of Two Binaries",
            "Mission Impossible 16: Finding Parking at UCF",
            "Of Thee I Pain: Design Hell",
            "Oviedo: The City of Chickens")

//    override fun search(query: Query): List<Movie> {
//        val len = movieNames.size - 1
//        val df = DateFormat.getDateInstance(DateFormat.MEDIUM)
//        return (0..len).map { Movie(movieNames[it], df.parse("Sep 18, 2017").time) }
//    }

    override fun search(query: Query): List<SearchResult> {
        val json = webApiService.search(query)

        // HACK: This is a really hideous block, but it is necessary according to the official docs
        // ref: https://github.com/google/gson/blob/master/UserGuide.md#collections-examples
        val listType = object : TypeToken<List<Movie>>() {}.type
        val result: List<Movie> = gson.fromJson(json, listType)

        return result
    }
}