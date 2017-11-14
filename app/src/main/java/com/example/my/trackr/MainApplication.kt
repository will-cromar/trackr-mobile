package com.example.my.trackr

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.example.my.trackr.service.NotificationCheckerService
import kotlinx.coroutines.experimental.Job

class MainApplication : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }
    val jobScheduler: JobScheduler by lazy {
        getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(getString(R.string.notification_channel_name),
                    "Everything",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        jobScheduler.schedule(
                JobInfo.Builder(12344,
                        ComponentName(this, NotificationCheckerService::class.java))
                        .setPeriodic(60000)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build())
    }
}
