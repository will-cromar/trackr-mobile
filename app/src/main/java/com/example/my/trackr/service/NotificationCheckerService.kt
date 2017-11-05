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
import com.example.my.trackr.ui.SplashPageActivity
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
                                    .setContentText("${n.listing_id} ${n.time}")
                    val targetIntent = Intent(context, SplashPageActivity::class.java)
                    val contentIntent = PendingIntent.getActivity(
                            context,
                            0,
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
