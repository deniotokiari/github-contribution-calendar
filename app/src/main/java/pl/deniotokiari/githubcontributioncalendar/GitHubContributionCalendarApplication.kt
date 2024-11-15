package pl.deniotokiari.githubcontributioncalendar

import android.app.Application
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import pl.deniotokiari.githubcontributioncalendar.data.dataModule
import pl.deniotokiari.githubcontributioncalendar.domain.domainModule
import pl.deniotokiari.githubcontributioncalendar.ui.uiModule
import pl.deniotokiari.githubcontributioncalendar.work.workModule

class GitHubContributionCalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        Firebase.analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        startKoin {
            androidContext(this@GitHubContributionCalendarApplication)
            workManagerFactory()

            modules(
                appModule,
                dataModule,
                domainModule,
                workModule,
                uiModule
            )
        }
    }
}
