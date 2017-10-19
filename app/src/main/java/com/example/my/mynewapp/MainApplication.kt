package com.example.my.mynewapp

import android.app.Application

class MainApplication : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }
}
