package com.example.my.trackr

import android.app.Application
import com.example.my.trackr.ui.LoginActivity
import com.example.my.trackr.ui.MovieDetailsActivity
import com.example.my.trackr.ui.SearchResultsActivity
import com.example.my.trackr.ui.SplashPageActivity
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
