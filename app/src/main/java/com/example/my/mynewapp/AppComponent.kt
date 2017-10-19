package com.example.my.mynewapp

import android.app.Application
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(app: Application)
    fun inject(splashPageActivity: SplashPageActivity)
    fun inject(searchResultsActivity: SearchResultsActivity)
    fun inject(movieDetailsActivity: MovieDetailsActivity)
    fun inject(loginActivity: LoginActivity)
}
