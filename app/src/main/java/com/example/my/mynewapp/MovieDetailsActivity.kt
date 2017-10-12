package com.example.my.mynewapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.my.mynewapp.data.Movie
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        setSupportActionBar(toolbar)

        val json = intent.getStringExtra(EXTRA_MESSAGE)
        val movie : Movie = Gson().fromJson<Movie>(json, Movie::class.java)

        title = movie.name

        // TODO: Actually subscribe
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Subscribed! (Not really)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
