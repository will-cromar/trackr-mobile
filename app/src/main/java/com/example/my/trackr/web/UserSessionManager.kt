package com.example.my.trackr.web

import javax.inject.Inject
import javax.inject.Singleton


// TODO: Make this class less nullable
@Singleton
class UserSessionManager @Inject constructor(private val webApi: TrackrWebApi) {
    var username: String? = null
    private var password: String? = null
    private var authorization: AuthResponse? = null

    val hasActiveSession: Boolean
        get() = username != null && password != null

    // Attempt to authenticate with the given credentials
    fun attemptLogin(username: String, password: String) {
        val credentials = AuthCredentials(username, password)
        val authResponse = webApi.authenticate(credentials)

        if (authResponse.error != null) {
            throw IllegalArgumentException(authResponse.description)
        } else {
            this.username = username
            this.password = password
            this.authorization = authResponse
        }
    }

    // Attempt to sign up and log in with given credentials
    fun attempSignUp(username: String, password: String) {
        val credentials = AuthCredentials(username, password)
        val response = webApi.createAccount(credentials)

        when (response.status_code) {
            "200" -> attemptLogin(credentials.username, credentials.password)
            else -> throw IllegalArgumentException(response.description)
        }
    }

    // Release current user session information
    fun logout() {
        username = null
        password = null
        authorization = null
    }

    // Get current authorization token or refresh
    fun getToken(): AuthResponse {
        if (username == null || password == null) {
            throw IllegalStateException("No credentials available.")
        }

        if (authorization != null) {
            val response = webApi.whoAmI(authorization!!)

            if (response.error == null) {
                return authorization!!
            }
        }

        attemptLogin(username!!, password!!)
        return authorization!!
    }
}