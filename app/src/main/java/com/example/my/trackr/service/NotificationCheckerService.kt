package com.example.my.trackr.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.UserSessionManager
import com.example.my.trackr.data.WebApiService
import com.example.my.trackr.ui.MovieDetailsActivity
import com.example.my.trackr.ui.extraListingId
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class NotificationCheckerService : JobService() {
    @Inject lateinit var webApi: WebApiService
    @Inject lateinit var sessionManager: UserSessionManager

    override fun onStartJob(params: JobParameters?): Boolean {
        (application as MainApplication).component.inject(this)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (sessionManager.hasActiveSession) {
            val context = this
            doAsync {
                val authorization = sessionManager.getToken()
                val notifications = webApi.notifications(authorization)

                notifications.notifications.forEachIndexed { i, n ->
                    val builder =
                            NotificationCompat.Builder(context, getString(R.string.notification_channel_name))
                                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                    .setContentTitle(n.message)
                                    .setContentText(n.submessage ?: n.timePretty)
                    val targetIntent = Intent(context, MovieDetailsActivity::class.java)
                    targetIntent.putExtra(extraListingId, n.listing_id.toString())
                    targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    val contentIntent = PendingIntent.getActivity(
                            context,
                            i,
                            targetIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)
                    builder.setContentIntent(contentIntent)

                    notificationManager.notify(i, builder.build())
                }
            }
        }

        return false
    }

    override fun onStopJob(params: JobParameters?) = false
}
