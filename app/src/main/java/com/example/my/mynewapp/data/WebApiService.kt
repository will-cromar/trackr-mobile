package com.example.my.mynewapp.data

import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

interface WebApiService {
    // TODO: Implement other services, such as user login
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
 * Simple web service implementation that queries the computer the emulator is running on.
 *
 * For testing purposes only.
 */
class LocalhostWebApiService : WebApiService {
    // 10.0.2.2 is the computer the emulator is running on; localhost loops back to the phone.
    // TODO: Host the web server somewhere
    val url = URL("http://10.0.2.2:5000/datadump");

    override fun search(query: Query): String {
        val urlConnection = url.openConnection() as HttpURLConnection
        val res : String;

        try {
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            res = inputStream.bufferedReader().readLine()
            Log.d("webapiservice", res)
        } finally {
            urlConnection.disconnect()
        }

        return res
    }
}

// TODO: Create an actual implementation of the WebApiService interface