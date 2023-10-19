package pl.deniotokiari.githubcontributioncalendar

import android.app.Application
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import pl.deniotokiari.githubcontributioncalendar.about.aboutModule
import pl.deniotokiari.githubcontributioncalendar.data.dataModule
import pl.deniotokiari.githubcontributioncalendar.home.homeModule
import pl.deniotokiari.githubcontributioncalendar.prefs.prefsModule
import pl.deniotokiari.githubcontributioncalendar.user.userModule
import pl.deniotokiari.githubcontributioncalendar.widget.widgetModule

class GitHubContributionCalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        Firebase.analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        startKoin {
            androidContext(this@GitHubContributionCalendarApplication)
            workManagerFactory()

            modules(
                appModule,
                prefsModule,
                dataModule,
                widgetModule,
                homeModule,
                userModule,
                aboutModule
            )
        }
    }
}