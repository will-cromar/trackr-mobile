package com.example.my.trackr.data

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


// Login credentials for authorization
data class AuthCredentials(val username: String, val password: String)

// Request for user to subscribe to some content item
data class SubscribeRequest(val listing_id: Long)

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

// Server gives generic status code response for create account
typealias CreateAccountResponse = JsonResponse

// Listings for shows the user has subscribed to
data class QueryResponse(val results: List<Listing>)

// Listings for shows the user has subscribed to
data class GenreListResponse(val genres: List<Genre>)

// Response when new subscription is attempted
typealias SubscribeResponse = JsonResponse

// Listings for shows the user has subscribed to
data class SubscriptionsResponse(val subscriptions: List<Listing>): JsonResponse()

// Represents notifications from the user's inbox on the server
data class Notification(val listing_id: Long, val message: String, val time: Long,
                        val submessage: String?) {
    companion object {
        val dateFormat = SimpleDateFormat("MMMM dd @ HH:mm z", Locale.getDefault())
    }

    val timePretty: String
        get() = dateFormat.format(time * 1000L)
}
data class NotificationsResponse(val notifications: List<Notification>)

class WebApiService @Inject constructor(private val requestInterface: HttpClient,
                                        private val gson: Gson) {

    companion object {
        // List of endpoints for all services
        val QUERY_ENDPOINT = "api/query"
        val GENRE_LIST_ENDPOINT = "api/genrelist"
        val AUTH_ENDPOINT = "auth"
        val CREATE_ACCOUNT_ENDPOINT = "api/createaccount"
        val WHO_ENDPOINT = "api/whoami"
        val SUBSCRIBE_ENDPOINT = "api/addsubscription"
        val SUBSCRIPTIONS_ENDPOINT = "api/subscriptions"
        val NOTIFICATIONS_ENDPOINT = "api/notifications"
    }

    fun query(q: String): QueryResponse {
        val responseJson = requestInterface.get(QUERY_ENDPOINT, "query" to q)
        return gson.fromJson<QueryResponse>(responseJson, QueryResponse::class.java)
    }

    fun subscribe(id: Long, credentials: AuthResponse): SubscribeResponse {
        val request = SubscribeRequest(id)
        val responseJson = requestInterface.post(SUBSCRIBE_ENDPOINT, gson.toJson(request), authHeader = credentials.jwtToken)

        return gson.fromJson<SubscribeResponse>(responseJson, SubscribeResponse::class.java)
    }

    fun genreList(): GenreListResponse {
        val responseJson = requestInterface.get(GENRE_LIST_ENDPOINT)
        return gson.fromJson<GenreListResponse>(responseJson, GenreListResponse::class.java)
    }

    fun authenticate(credentials: AuthCredentials) : AuthResponse {
        val credentialsJson = gson.toJson(credentials)
        val responseJson = requestInterface.post(AUTH_ENDPOINT, credentialsJson)

        return gson.fromJson<AuthResponse>(responseJson, AuthResponse::class.java)
    }

    fun createAccount(credentials: AuthCredentials) : CreateAccountResponse {
        val credentialsJson = gson.toJson(credentials)
        val responseJson = requestInterface.post(CREATE_ACCOUNT_ENDPOINT, credentialsJson)

        return gson.fromJson<AuthResponse>(responseJson, CreateAccountResponse::class.java)
    }

    fun whoAmI(authorization: AuthResponse) : WhoAmIResponse {
        val token = authorization.jwtToken
        val responseJson = requestInterface.get(WHO_ENDPOINT, authHeader = token)

        return gson.fromJson<WhoAmIResponse>(responseJson, WhoAmIResponse::class.java)
    }

    fun subscriptions(authorization: AuthResponse) : SubscriptionsResponse {
        val token = authorization.jwtToken
        val responseJson = requestInterface.get(SUBSCRIPTIONS_ENDPOINT, authHeader = token)

        return gson.fromJson<SubscriptionsResponse>(responseJson, SubscriptionsResponse::class.java)
    }

    fun notifications(authorization: AuthResponse) : NotificationsResponse {
        val token = authorization.jwtToken
        val responseJson = requestInterface.get(NOTIFICATIONS_ENDPOINT, authHeader = token)

        return gson.fromJson<NotificationsResponse>(responseJson, NotificationsResponse::class.java)
    }
}