package com.example.my.trackr.data

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class SearchResultsTest {
    @Test
    fun movieTitleTest() {
        val movie = Movie("movie", 1234567890)

        assertThat(movie.title).isEqualTo("movie")
    }

    @Test
    fun movieSubtitleTest() {
        val movie = Movie("movie", 1234567890)

        assertThat(movie.subtitle).isEqualTo("February 13, 2009")
    }
}
