package com.example.my.trackr.web

import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson

class UserSessionManagerTest {
    val username = "user"
    val password = "pass"
    val token = "DEADBEEF"
    val credentials = AuthCredentials(username, password)
    val authorization = AuthResponse(token)
    val failedAuthorization = Gson().fromJson<AuthResponse>(
            "{'status_code': 401, 'error': 'Unauthorized', 'description': 'Do better.'}",
            AuthResponse::class.java)

    @Test
    fun attemptLogin_CorrectTest() {
        val mockWebApi = mock(TrackrWebApi::class.java)
        `when`(mockWebApi.authenticate(credentials))
                .thenReturn(authorization)

        val sessionManager = UserSessionManager(mockWebApi)
        sessionManager.attemptLogin(username, password)
        assertThat(sessionManager.username).isEqualTo(username)
    }

    @Test
    fun attemptLogin_IncorrectTest() {
        val mockWebApi = mock(TrackrWebApi::class.java)
        `when`(mockWebApi.authenticate(credentials))
                .thenReturn(failedAuthorization)

        val sessionManager = UserSessionManager(mockWebApi)
        try {
            sessionManager.attemptLogin(username, password)
            throw AssertionError("Failed login should have thrown exception.")
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo(failedAuthorization.description)
        }
    }

    @Test
    fun getToken_CorrectTest() {
        val mockWebApi = mock(TrackrWebApi::class.java)
        `when`(mockWebApi.authenticate(credentials))
                .thenReturn(authorization)
        `when`(mockWebApi.whoAmI(authorization))
                .thenReturn(WhoAmIResponse(username))

        val sessionManager = UserSessionManager(mockWebApi)
        sessionManager.attemptLogin(username, password)
        val authResponse = sessionManager.getToken()
        assertThat(authResponse).isEqualTo(authorization)
    }

    @Test
    fun getToken_WithoutLoginTest() {
        val mockWebApi = mock(TrackrWebApi::class.java)
        val sessionManager = UserSessionManager(mockWebApi)
        try {
            sessionManager.getToken()
            throw AssertionError("Failed login should have thrown exception.")
        } catch (e: IllegalStateException) {
            // It threw an exception like it should have. Yay!
        }
    }
}