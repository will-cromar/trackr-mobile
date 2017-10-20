package com.example.my.trackr.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.Movie
import com.example.my.trackr.data.UserSessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_movie_details.*
import javax.inject.Inject

class MovieDetailsActivity : AppCompatActivity() {
    @Inject lateinit var gson: Gson
    @Inject lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        (application as MainApplication).component.inject(this)
        setSupportActionBar(toolbar)

        val json = intent.getStringExtra(EXTRA_MESSAGE)
        val movie : Movie = gson.fromJson<Movie>(json, Movie::class.java)

        title = movie.name

        // TODO: Actually subscribe
        fab.setOnClickListener { view ->
            val message = "Added ${movie.name} to ${sessionManager.username}'s subscriptions!"
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
