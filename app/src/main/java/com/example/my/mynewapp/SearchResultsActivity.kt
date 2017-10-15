package com.example.my.mynewapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.my.mynewapp.data.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_results_page.*
import org.jetbrains.anko.*


const val EXTRA_QUERY = "query"

class SearchResultsActivity : AppCompatActivity() {
    private val webApi = WebApiService("https://limitless-dusk-74218.herokuapp.com/")
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results_page)

        val queryJson = intent.getStringExtra(EXTRA_QUERY)
        val query = gson.fromJson<Query>(queryJson, Query::class.java)

        title = "${resources.getString(R.string.title_prefix_search)} ${query.search}"
        searchErrorMessage.visibility = View.GONE

        // Asynchronously query the web API. Loading circle will be shown until success or error.
        val context = this
        doAsync {
            try {
                val results = webApi.dataDump()

                uiThread {
                    moviesList.adapter = MoviesRecyclerViewAdapter(context, results)
                    moviesList.layoutManager = LinearLayoutManager(context)
                }
            } catch (e: Exception) {
                Log.e("searchresults", "Query failed", e)

                uiThread {
                    searchErrorMessage.visibility = View.VISIBLE
                }
            } finally {
                uiThread {
                    searchProgressBar.visibility = View.GONE
                }
            }
        }
    }

    class MoviesRecyclerViewAdapter(val context: Context, val movies: List<SearchResult>) : RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder>() {
        override fun getItemCount() = movies.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val rowMain = LayoutInflater
                    .from(parent!!.context)
                    .inflate(R.layout.row_search, parent, false)
            val viewHolder = ViewHolder(rowMain)
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val feedItem = movies[position]

            holder!!.apply {
                title.text = feedItem.title
                subtitle.text = feedItem.subtitle
                itemView.setOnClickListener {
                    val json = Gson().toJson(feedItem)
                    context.startActivity<MovieDetailsActivity>(EXTRA_MESSAGE to json)
                }
            }
        }

        class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView!!.findViewById(R.id.movieName)
            val subtitle: TextView = itemView!!.findViewById(R.id.movieSubtitle)
        }
    }
}
