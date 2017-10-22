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

    // TODO: make this more like the other tests
    @Test
    fun subscriptionsTest() {
        val authorization = AuthResponse("DEADBEEF")
        val responseJson =
                "{\n" +
                        "  \"subscriptions\": [\n" +
                        "    {\n" +
                        "      \"title\": \"wlyUPieAGrQNIlkbmySd\",\n" +
                        "      \"description\": \"vGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxPvGkKzmIaNOfwjXAcmSxP\",\n" +
                        "      \"releaseDate\": 1511271457,\n" +
                        "      \"actors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 19,\n" +
                        "          \"name\": \"LpqvupgvWJUGpCaowEIy\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 11,\n" +
                        "          \"name\": \"YECMcEASiXejEJmgUmEG\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 19,\n" +
                        "          \"name\": \"LpqvupgvWJUGpCaowEIy\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 17,\n" +
                        "          \"name\": \"PlbkrSYoHuZBWfYjYnfw\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 12,\n" +
                        "          \"name\": \"vEBWkLmuIpqNdxZZdlnW\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"writers\": [\n" +
                        "        {\n" +
                        "          \"personId\": 16,\n" +
                        "          \"name\": \"SYQTbMrWPSQnOfTSlQxp\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"directors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 13,\n" +
                        "          \"name\": \"TTtiRKFZIaQpIWggfCoF\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"genres\": [\n" +
                        "        {\n" +
                        "          \"genreId\": 1,\n" +
                        "          \"genre\": \"fake\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"RCUMynmHNBGoeooqChmK\",\n" +
                        "      \"description\": \"KdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvMKdvCvkvVEKSNtasNSXvM\",\n" +
                        "      \"releaseDate\": 1511397289,\n" +
                        "      \"actors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 15,\n" +
                        "          \"name\": \"VCREhgqUPSUkmfOTvGiW\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 17,\n" +
                        "          \"name\": \"PlbkrSYoHuZBWfYjYnfw\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 9,\n" +
                        "          \"name\": \"LJZfurSmjxvomWxSCcZR\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 5,\n" +
                        "          \"name\": \"aLJBnHfwxXTnAjVTpHFh\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 9,\n" +
                        "          \"name\": \"LJZfurSmjxvomWxSCcZR\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"writers\": [\n" +
                        "        {\n" +
                        "          \"personId\": 12,\n" +
                        "          \"name\": \"vEBWkLmuIpqNdxZZdlnW\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"directors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 10,\n" +
                        "          \"name\": \"YWSizludtZnOxvXZCLip\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"genres\": [\n" +
                        "        {\n" +
                        "          \"genreId\": 1,\n" +
                        "          \"genre\": \"fake\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"title\": \"IPLRVYBVEIewTjnrxrTo\",\n" +
                        "      \"description\": \"XvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXyXvRyMcXldXcbmRGmyfXy\",\n" +
                        "      \"releaseDate\": 1508965527,\n" +
                        "      \"actors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 15,\n" +
                        "          \"name\": \"VCREhgqUPSUkmfOTvGiW\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 3,\n" +
                        "          \"name\": \"ZHCJROlbqnkXTqIuVxnm\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 12,\n" +
                        "          \"name\": \"vEBWkLmuIpqNdxZZdlnW\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 14,\n" +
                        "          \"name\": \"LkGnzVRewoaOHnMCwadT\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"personId\": 2,\n" +
                        "          \"name\": \"KclpemfoHstknWHFiLit\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"writers\": [\n" +
                        "        {\n" +
                        "          \"personId\": 5,\n" +
                        "          \"name\": \"aLJBnHfwxXTnAjVTpHFh\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"directors\": [\n" +
                        "        {\n" +
                        "          \"personId\": 9,\n" +
                        "          \"name\": \"LJZfurSmjxvomWxSCcZR\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"genres\": [\n" +
                        "        {\n" +
                        "          \"genreId\": 1,\n" +
                        "          \"genre\": \"fake\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"

        val mockHttpClient = mock(HttpClient::class.java)
        `when`(mockHttpClient.get("api/subscriptions", authHeader = authorization.jwtToken))
                .thenReturn(responseJson)

        val webApi = WebApiService(mockHttpClient, gson)
        val resp = webApi.subscriptions(authorization)

        assertThat(resp.subscriptions.size).isEqualTo(3)
        assertThat(resp.subscriptions[2].title).isEqualTo("IPLRVYBVEIewTjnrxrTo")
        assertThat(resp.subscriptions[0].actors[0].name).isEqualTo("LpqvupgvWJUGpCaowEIy")
    }
}