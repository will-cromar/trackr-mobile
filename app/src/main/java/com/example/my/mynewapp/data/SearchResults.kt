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
    private val webApiService = DatadumpWebApiService()

    override fun search(query: Query): List<SearchResult> {
        val json = webApiService.search(query)

        // HACK: This is a really hideous block, but it is necessary according to the official docs
        // ref: https://github.com/google/gson/blob/master/UserGuide.md#collections-examples
        val listType = object : TypeToken<List<Movie>>() {}.type
        val result: List<Movie> = gson.fromJson(json, listType)

        return result
    }
}