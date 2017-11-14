package com.example.my.trackr.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.Listing
import com.example.my.trackr.data.SubscribeResponse
import com.example.my.trackr.data.UserSessionManager
import com.example.my.trackr.data.WebApiService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_movie_details.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class MovieDetailsActivity : AppCompatActivity() {
    @Inject lateinit var gson: Gson
    @Inject lateinit var webApi: WebApiService
    @Inject lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        (application as MainApplication).component.inject(this)
        setSupportActionBar(toolbar)

        val json = intent.getStringExtra(EXTRA_MESSAGE)
        val listing = gson.fromJson<Listing>(json, Listing::class.java)

        title = listing.title

        fab.setOnClickListener { view ->
            doAsync {
                val message = try {
                    val resp : SubscribeResponse = webApi.subscribe(listing.listing_id, sessionManager.getToken())

                    when(resp.status_code) {
                        "200" -> "Added ${listing.title} to ${sessionManager.username}'s subscriptions!"
                        else -> "Error: ${resp.description}"
                    }
                } catch (e: Exception) {
                    e.message!!
                }

                uiThread {
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }

        }
    }
}
