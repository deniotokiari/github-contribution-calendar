package pl.deniotokiari.githubcontributioncalendar

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import pl.deniotokiari.githubcontributioncalendar.data.dataModule
import pl.deniotokiari.githubcontributioncalendar.home.homeModule
import pl.deniotokiari.githubcontributioncalendar.user.userModule
import pl.deniotokiari.githubcontributioncalendar.widget.widgetModule

class GitHubContributionCalendarApplication : Application() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        firebaseAnalytics = Firebase.analytics

        startKoin {
            androidContext(this@GitHubContributionCalendarApplication)
            workManagerFactory()

            modules(
                appModule,
                dataModule,
                widgetModule,
                homeModule,
                userModule
            )
        }
    }
}