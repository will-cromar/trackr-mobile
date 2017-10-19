package com.example.my.mynewapp

import android.app.Application
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides @Singleton
    fun provideApplication() = app

    @Provides
    fun provideGson() = Gson()

    @Provides @Named("urlRoot")
    fun provideUrlRoot() = "https://limitless-dusk-74218.herokuapp.com/"
}