package com.example.my.trackr

import android.app.Application
import com.example.my.trackr.service.NotificationCheckerService
import com.example.my.trackr.ui.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(app: Application)
    fun inject(splashPageActivity: SplashPageActivity)
    fun inject(splashBrowseFragment: SplashBrowseFragment)
    fun inject(splashNotificationsFragment: SplashNotificationsFragment)
    fun inject(searchResultsActivity: SearchResultsActivity)
    fun inject(movieDetailsActivity: MovieDetailsActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(notificationCheckerService: NotificationCheckerService)
}
