package com.example.my.trackr.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.content_movie_details.*
import kotlinx.android.synthetic.main.row_details.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
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

        descriptionText.text = listing.description
        releaseDateText.text = listing.releaseDatePretty

        // Simply opens a search page for the text of the row
        val itemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            startActivity<SearchResultsActivity>(EXTRA_QUERY to view!!.detailsRowText.text)
        }

        genresListView.adapter = DetailsListAdapter(listing.genres)
        genresListView.onItemClickListener = itemClickListener
        directorsListView.adapter = DetailsListAdapter(listing.directors)
        directorsListView.onItemClickListener = itemClickListener
        writersListView.adapter = DetailsListAdapter(listing.writers)
        writersListView.onItemClickListener = itemClickListener
        actorsListView.adapter = DetailsListAdapter(listing.actors)
        actorsListView.onItemClickListener = itemClickListener

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

    private class DetailsListAdapter(val rows: List<Listable>): BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
                when (convertView) {
                    null -> LayoutInflater.from(parent!!.context).inflate(R.layout.row_details, parent, false)
                    else -> convertView
                }.apply { detailsRowText.text = rows[position].listTitle }

        override fun getItem(position: Int) = rows[position]

        override fun getItemId(position: Int) = position + 0L

        override fun getCount() = rows.size
    }
}
