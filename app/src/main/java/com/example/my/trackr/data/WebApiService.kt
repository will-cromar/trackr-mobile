package com.example.my.trackr.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

// Any JSON response from the server may have error information defined
open class JsonResponse(val status_code: String? = null,
                        val error: String? = null,
                        val description: String? = null)

// Contains a JWT auth token
data class AuthResponse(val access_token: String?) : JsonResponse() {
    val jwtToken: String
        get() = "JWT $access_token"
}

// Contains the username associated with a "Who am I?" request
data class WhoAmIResponse(val username: String?) : JsonResponse()

// Contains a list of all content in the database
typealias DataDumpResponse = List<Movie>

// Login credentials for authorization
data class AuthCredentials(val username: String, val password: String)

class WebApiService @Inject constructor(private val requestInterface: HttpClient,
                                        private val gson: Gson) {
//    private val gson = Gson()

    companion object {
        // List of endpoints for all services
        val DATA_DUMP_ENDPOINT = "datadump"
        val AUTH_ENDPOINT = "auth"
        val WHO_ENDPOINT = "api/whoami"
    }

    fun dataDump(): DataDumpResponse {
        val json = requestInterface.get(DATA_DUMP_ENDPOINT)

        // HACK: This is a really hideous block, but it is necessary according to the official docs
        // ref: https://github.com/google/gson/blob/master/UserGuide.md#collections-examples
        val listType = object : TypeToken<DataDumpResponse>() {}.type
        return gson.fromJson<DataDumpResponse>(json, listType)
    }

    fun authenticate(credentials: AuthCredentials): AuthResponse {
        val credentialsJson = gson.toJson(credentials)
        val responseJson = requestInterface.post(AUTH_ENDPOINT, credentialsJson)

        return gson.fromJson<AuthResponse>(responseJson, AuthResponse::class.java)
    }

    fun whoAmI(authorization: AuthResponse) : WhoAmIResponse {
        val token = authorization.jwtToken
        val responseJson = requestInterface.get(WHO_ENDPOINT, authHeader = token)

        return gson.fromJson<WhoAmIResponse>(responseJson, WhoAmIResponse::class.java)
    }
}