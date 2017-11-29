package com.example.my.trackr.ui

import android.app.Activity
import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Context
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
import android.widget.*
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.web.Genre
import com.example.my.trackr.web.UserSessionManager
import com.example.my.trackr.web.TrackrWebApi
import com.example.my.trackr.service.NotificationCheckerService
import kotlinx.android.synthetic.main.activity_splash_page.*
import kotlinx.android.synthetic.main.fragment_splash_browse.view.*
import kotlinx.android.synthetic.main.fragment_splash_subscriptions.view.*
import kotlinx.android.synthetic.main.list_header.view.*
import kotlinx.android.synthetic.main.list_children.view.*
import kotlinx.android.synthetic.main.row_browse.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.util.*
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
        menu!!

        val searchView = menu.findItem(R.id.action_search)?.actionView!! as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // query has to be non-null
                startActivity<SearchResultsActivity>(EXTRA_QUERY to query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        val profileItem = menu.findItem(R.id.action_profile)!!
        profileItem.setOnMenuItemClickListener {
            startActivity<LoginActivity>()
            true
        }

        val refreshItem = menu.findItem(R.id.action_refresh)!!
        refreshItem.setOnMenuItemClickListener {
            (application as MainApplication).jobScheduler.schedule(JobInfo.Builder(
                    12345,
                    ComponentName(this, NotificationCheckerService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build())
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
    @Inject lateinit var webApi: TrackrWebApi

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflater!!.inflate(R.layout.fragment_splash_browse, container, false)
        (activity.application as MainApplication).component.inject(this)

        doAsync {
            val genres = webApi.genreList()

            uiThread {
                viewGroup.genresList.adapter = BrowseListAdapter(genres.genres)
                viewGroup.genresList.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
                    startActivity<SearchResultsActivity>(EXTRA_QUERY to view!!.genreText.text)
                }
            }
        }

        return viewGroup
    }

    private class BrowseListAdapter(val rows : List<Genre>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
                when (convertView) {
                    null -> LayoutInflater.from(parent!!.context).inflate(R.layout.row_browse, parent, false)
                    else -> convertView
                }.apply { genreText.text = rows[position].genre }

        override fun getItem(position: Int) = rows[position]

        override fun getItemId(position: Int) = position + 0L

        override fun getCount() = rows.size
    }
}

class SplashNotificationsFragment : Fragment() {
    @Inject lateinit var sessionManager: UserSessionManager
    @Inject lateinit var webApi: TrackrWebApi

    fun convertTime(epochTime: Long): Date {
        val localDate = Date(epochTime*1000L)
        return localDate
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewGroup = inflater!!.inflate(R.layout.fragment_splash_subscriptions, container, false)
        (activity.application as MainApplication).component.inject(this)

        val nameTable = mutableMapOf<String, List<String>>()
        if(sessionManager.hasActiveSession){
        val movies = doAsyncResult{webApi.subscriptions(sessionManager.getToken())}.get()

        for (Listings in movies.subscriptions){
            val listTitle = mutableListOf<String>()

            // listTitle.add(Listings.title)
            listTitle.add(Listings.description)
            if (Listings.schedules != null) {
                for(Schedules in Listings.schedules) {
                    listTitle.add("Episode: " + Schedules.episode.toString() +" Air Date:  "+ convertTime(Schedules.date).toString())
                }
            }
            for(Actors in Listings.actors){
                listTitle.add("Actor: " + Actors.name)
            }
            for(Genres in Listings.genres){

                listTitle.add("Genres: " + Genres.genre)
            }

            listTitle.add("Release Date: " + Listings.releaseDatePretty)

            nameTable.put(Listings.title, listTitle)


        }

        val expandableListTitel = nameTable.keys.toList()


        viewGroup.expand.setAdapter(ExpandableAdapter(viewGroup.context,expandableListTitel, nameTable))
        return viewGroup}
        else{
            toast("Log in for your Personalized Notifications!!!")
        }
        return viewGroup
    }

    private class ExpandableAdapter(val context: Context, val list: List<String>, val map : Map<String,List<String>>): BaseExpandableListAdapter() {
        val expandableListTitle = list
        val expandableListDetail = map

        override fun getGroupCount(): Int {
            return this.expandableListTitle.size
        }

        override fun getChildId(p0: Int, p1: Int): Long {
            return p1.toLong()
        }

        override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
            val text = getChild(p0, p1)
            var view = p3
            if (view == null) {
                val newInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = newInflater.inflate(R.layout.list_children, null)
            }

            view!!.expandableChildren.text = text.toString()

            return view

        }

        override fun getGroupId(p0: Int): Long {
           return p0.toLong()
        }

        override fun getChild(p0: Int, p1: Int): Any {
            return this.expandableListDetail.get(this.expandableListTitle.get(p0))!!.get(p1)
        }

        override fun getChildrenCount(p0: Int): Int {
            return this.expandableListDetail.get(this.expandableListTitle.get(p0))!!.size

        }

        override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
            val listTitel = getGroup(p0)
            var view = p2

            if(p2 == null) {
                val newInflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                 view = newInflater.inflate(R.layout.list_header, null)
            }
            view!!.expandableHeader.text = listTitel.toString()

            return view
        }

        override fun hasStableIds(): Boolean {
           return false
        }

        override fun isChildSelectable(p0: Int, p1: Int): Boolean {
            return true
        }

        override fun getGroup(p0: Int): Any {
            return this.expandableListTitle.get(p0)
        }

    }

}
