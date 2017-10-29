package com.example.my.trackr.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.UserSessionManager
import com.example.my.trackr.data.WebApiService
import kotlinx.android.synthetic.main.activity_splash_page.*
import kotlinx.android.synthetic.main.fragment_splash_reminders.view.*
import kotlinx.android.synthetic.main.fragment_splash_search.view.*
import kotlinx.android.synthetic.main.row_browse.view.*
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class SplashPageActivity : AppCompatActivity() {

    private companion object {
        val NUM_PAGES = 3
        val HOME_PAGE_INDEX = 0
        val BROWSE_PAGE_INDEX = 1
        val NOTIFICATIONS_PAGE_INDEX = 2
    }

    /**
     * Controls main layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)
        (application as MainApplication).component.inject(this)

        splashFragmentPager.adapter = SplashFragmentPagerAdapter(supportFragmentManager)

        setSupportActionBar(splashToolbar)

        // Listener for bottom navigation bar
        navigation.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> {
                            splashFragmentPager.currentItem = HOME_PAGE_INDEX
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.navigation_dashboard -> {
                            splashFragmentPager.currentItem = BROWSE_PAGE_INDEX
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.navigation_notifications -> {
                            splashFragmentPager.currentItem = NOTIFICATIONS_PAGE_INDEX
                            return@OnNavigationItemSelectedListener true
                        }
                    }
                    false
                })
    }

    /**
     * Controls top tool bar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)

        val searchView = menu?.findItem(R.id.action_search)?.actionView!! as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // query has to be non-null
                startActivity<SearchResultsActivity>(EXTRA_QUERY to query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        val menuItem = menu.findItem(R.id.action_profile)!!
        menuItem.setOnMenuItemClickListener {
            startActivity<LoginActivity>()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Adapter to display fragments in main display area.
     */
    class SplashFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = NUM_PAGES

        override fun getItem(position: Int): Fragment? =
                when(position) {
                    HOME_PAGE_INDEX -> SplashHomeFragment()
                    BROWSE_PAGE_INDEX -> SplashBrowseFragment()
                    NOTIFICATIONS_PAGE_INDEX -> SplashNotificationsFragment()
                    else -> Fragment()
                }
    }
}

class SplashHomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_splash_home, container, false)
    }
}

class SplashBrowseFragment : Fragment() {
    private val genres = listOf("List", "Of", "Genres", "To", "Browse")

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflater!!.inflate(R.layout.fragment_splash_search, container, false)

        viewGroup.genresList.adapter = BrowseListAdapter(genres)

        return viewGroup
    }

    private class BrowseListAdapter(val rows : List<String>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
                when (convertView) {
                    null -> LayoutInflater.from(parent!!.context).inflate(R.layout.row_browse, parent, false)
                    else -> convertView
                }.apply { genreText.text = rows[position] }

        override fun getItem(position: Int) = rows[position]

        override fun getItemId(position: Int) = position + 0L

        override fun getCount() = rows.size
    }
}

class SplashNotificationsFragment : Fragment() {
    @Inject lateinit var sessionManager: UserSessionManager
    @Inject lateinit var webApi: WebApiService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflater!!.inflate(R.layout.fragment_splash_reminders, container, false)
        (activity.application as MainApplication).component.inject(this)

        // Bad for 2 reasons:
        // 1. It's just dumping the parsed JSON response from the server
        // 2. Blindly getting the future from doAsyncResult blocks the main thread
        // TODO: Do better.
        viewGroup.textView3.text = when(sessionManager.hasActiveSession) {
            false -> "Log in for notifications!"
            true -> {
                doAsyncResult {
                    "Notifications for will: ${webApi.subscriptions(sessionManager.getToken())}"
                }.get()
            }
        }

        return viewGroup
    }
}
