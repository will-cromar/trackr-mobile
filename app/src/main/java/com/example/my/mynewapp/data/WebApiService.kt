package com.example.my.mynewapp.data

import android.util.Log
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

// Any JSON response from the server may have error information defined
open class JsonResponse(val status_code: String? = null,
                        val error: String? = null,
                        val description: String? = null)

// Contains a JWT auth token
data class AuthResponse(val access_token: String?) : JsonResponse()

// Contains the username associated with a "Who am I?" request
data class WhoAmIResponse(val username: String?) : JsonResponse()

// Login credentials for authorization
data class AuthCredentials(val username: String, val password: String)

interface WebApiService {
    fun search(query: Query): String
}

/**
 * Simple dummy web service that does not require an internet connection.
 *
 * For testing purposes only.
 */
class DummyWebApiService : WebApiService {
    override fun search(query: Query) = "[{'releaseDate': 1436241600, 'name': 'Carpet Stain: The Cleaner Man'}, {'releaseDate': 1489982400, 'name': 'Is your Ceiling Fan a Soviet Spy? (A Documentary)'}, {'releaseDate': 1420261200, 'name': 'COP 4331C: Processes of Object Oriented Software Development'}, {'releaseDate': 1456981200, 'name': 'How to Make a Bad Language: The Story of Java'}, {'releaseDate': 1430971200, 'name': 'Inner Life of the Cell'}, {'releaseDate': 1583211600, 'name': 'Mitochondria: Powerhouse of the Cell'}, {'releaseDate': 1494043200, 'name': 'Linearly Independent'}, {'releaseDate': 1533355200, 'name': 'Physics 2: Box Diagrams Reloaded'}, {'releaseDate': 1567310400, 'name': 'Do Better 2: Doing Better'}, {'releaseDate': 1572235200, 'name': 'Group 7: A Tale of Two Binaries'}, {'releaseDate': 1550206800, 'name': 'Mission Impossible 16: Finding Parking at UCF'}, {'releaseDate': 1451192400, 'name': 'Of Thee I Pain: Design Hell'}, {'releaseDate': 1586836800, 'name': 'Oviedo: The City of Chickens'}]"
}

/**
 * Simple web service implementation that queries the /datadump endpoint.
 *
 * For testing purposes only.
 */
class DatadumpWebApiService : WebApiService {
    val url = URL("https://limitless-dusk-74218.herokuapp.com/datadump");

    override fun search(query: Query): String {
        val urlConnection = url.openConnection() as HttpURLConnection
        val res : String;

        try {
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val builder = StringBuilder()
            inputStream.bufferedReader()
                    .readLines()
                    .forEach { builder.appendln(it) }
            res = builder.toString()
            Log.d("webapiservice", res)
        } finally {
            urlConnection.disconnect()
        }

        return res
    }
}

// TODO: Create an actual implementation of the WebApiService interface