package com.example.my.mynewapp.data

import android.net.Uri
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


// Extension function that makes it easier to read an entire multi-line stream
fun InputStream.readAll() : String {
    val builder = StringBuilder()
    this.bufferedReader()
            .readLines()
            .forEach { builder.appendln(it) }
    Log.i("http", builder.toString())
    return builder.toString()
}

// Extension function that makes it easier to write an entire string and close
fun OutputStream.writeAllData(data: String) {
    val dataOutput = DataOutputStream(this)
    dataOutput.writeBytes(data)
    dataOutput.flush()
    dataOutput.close()
}

/**
 * Extension property to deal with the quirk that when the HTTP response code is 200 (OK), body data
 * comes in through the input stream. Otherwise, it comes in through the error stream.
 */
val HttpURLConnection.responseBodyStream: InputStream
    get() = when(this.responseCode) {
            200 -> inputStream
            else -> errorStream
    }

// Parameter to an HTTP GET request
typealias GetParam = Pair<String, String>

// Construct the URL string from a root, an endpoint, and an optional list of query parameters.
fun constructUrlString(root: String, endpoint: String, vararg params: GetParam) : String {
    val uri = Uri.Builder().encodedPath(root).appendEncodedPath(endpoint)
    params.forEach { uri.appendQueryParameter(it.first, it.second) }

    return uri.toString()
}


// MIME type for JSON
val jsonType = "application/json"

/**
 * Provides common HTTP methods for remote web server.
 */
class HttpRequestInterface(private val root : String) {

    fun get(endpoint: String, vararg params: GetParam, authHeader: String? = null) : String {
        val url = constructUrlString(root, endpoint, *params)
        Log.i("http", url)

        val client = URL(url).openConnection() as HttpURLConnection
        try {
            client.apply {
                setRequestProperty("Authorization", authHeader)
            }

            val responseBody = client.responseBodyStream.readAll()
            Log.i("http", responseBody)
            return responseBody
        } finally {
            client.disconnect()
        }
    }

    fun post(endpoint: String, body: String, contentHeader: String = jsonType, authHeader: String? = null) : String {
        val url = constructUrlString(root, endpoint)
        Log.i("http", url)

        val client = URL(url).openConnection() as HttpURLConnection
        try {
            client.apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", authHeader)
                setRequestProperty("Content-Type", contentHeader)
                setRequestProperty("Accept", jsonType)
                doOutput = true
                doInput = true
            }

            client.outputStream.writeAllData(body)

            val responseCode = client.responseCode
            Log.i("http", "STATUS: $responseCode")

            val responseBody = client.responseBodyStream.readAll()
            Log.i("http", responseBody)
            return responseBody
        } finally {
            client.disconnect()
        }
    }
}