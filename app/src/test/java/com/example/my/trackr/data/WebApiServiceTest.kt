package com.example.my.trackr.data

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


class WebApiServiceTest {
    private val gson = Gson()

    @Test
    fun jwtTokenTest() {
        val authorization = AuthResponse("DEADBEEF")

        assertThat(authorization.jwtToken).isEqualTo("JWT ${authorization.access_token}")
    }

    @Test
    fun dataDumpTest() {
        val moviesList = listOf(
                Movie("movie 1", 1234567890),
                Movie("movie 2", 2345678901),
                Movie("movie 2", 2345678901))

        val mockHttpClient = mock(HttpClient::class.java)
        `when`(mockHttpClient.get("datadump"))
                .thenReturn(gson.toJson(moviesList))

        val webApi = WebApiService(mockHttpClient, gson)
        val resp = webApi.dataDump()

        assertThat(resp).isEqualTo(moviesList)
    }

    @Test
    fun authenticateTest() {
        val credentials = AuthCredentials("user", "pass")
        val authorization = AuthResponse("DEADBEEF")

        val mockHttpClient = mock(HttpClient::class.java)
        `when`(mockHttpClient.post("auth", gson.toJson(credentials)))
                .thenReturn(gson.toJson(authorization))

        val webApi = WebApiService(mockHttpClient, gson)
        val resp = webApi.authenticate(credentials)

        assertThat(resp).isEqualTo(authorization)
    }

    @Test
    fun whoAmITest() {
        val authorization = AuthResponse("DEADBEEF")
        val username = WhoAmIResponse("user")

        val mockHttpClient = mock(HttpClient::class.java)
        `when`(mockHttpClient.get("api/whoami", authHeader = authorization.jwtToken))
                .thenReturn(gson.toJson(username))

        val webApi = WebApiService(mockHttpClient, gson)
        val resp = webApi.whoAmI(authorization)

        assertThat(resp).isEqualTo(username)
    }
}