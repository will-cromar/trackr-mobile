package com.example.my.trackr.ui

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
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_results_page.*
import org.jetbrains.anko.*
import javax.inject.Inject


const val EXTRA_QUERY = "query"

class SearchResultsActivity : AppCompatActivity() {
    @Inject lateinit var webApi: WebApiService
    @Inject lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results_page)
        (application as MainApplication).component.inject(this)

        val query : String = intent.getStringExtra(EXTRA_QUERY)

        title = "${resources.getString(R.string.title_prefix_search)} $query"
        searchErrorMessage.visibility = View.GONE

        // Asynchronously query the web API. Loading circle will be shown until success or error.
        val context = this
        doAsync {
            try {
                val results = webApi.dataDump()

                uiThread {
                    moviesList.adapter = MoviesRecyclerViewAdapter(results)
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

    inner class MoviesRecyclerViewAdapter(private val movies: List<SearchResult>) : RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder>() {
        val context: Context = this@SearchResultsActivity

        override fun getItemCount() = movies.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val rowMain = LayoutInflater
                    .from(parent!!.context)
                    .inflate(R.layout.row_search, parent, false)
            return ViewHolder(rowMain)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val feedItem = movies[position]

            holder!!.apply {
                title.text = feedItem.title
                subtitle.text = feedItem.subtitle
                itemView.setOnClickListener {
                    val json = gson.toJson(feedItem)
                    context.startActivity<MovieDetailsActivity>(EXTRA_MESSAGE to json)
                }
            }
        }

        inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView!!.findViewById(R.id.movieName)
            val subtitle: TextView = itemView!!.findViewById(R.id.movieSubtitle)
        }
    }
}
