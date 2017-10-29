package com.example.my.trackr.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.my.trackr.MainApplication
import com.example.my.trackr.R
import com.example.my.trackr.data.UserSessionManager
import com.example.my.trackr.data.WebApiService
import com.example.my.trackr.ui.SplashPageActivity
import javax.inject.Inject

class NotificationCheckerService : JobService() {
    @Inject lateinit var webApi: WebApiService
    @Inject lateinit var sessionManager: UserSessionManager

    override fun onStartJob(params: JobParameters?): Boolean {
        (application as MainApplication).component.inject(this)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelName,
                    "Everything",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder =
                NotificationCompat.Builder(this, channelName)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Very important message from Trackr")
                        .setContentText("You have notifications waiting for you!")
        val notificationId = 12345

        val targetIntent = Intent(this, SplashPageActivity::class.java)
        val contentIntent = PendingIntent.getActivity(
                this,
                0,
                targetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)

        notificationManager.notify(notificationId, builder.build())

        return false
    }

    override fun onStopJob(params: JobParameters?) = false
}
