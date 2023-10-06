package pl.deniotokiari.githubcontributioncalendar

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import pl.deniotokiari.githubcontributioncalendar.home.homeModule
import pl.deniotokiari.githubcontributioncalendar.user.userModule

class GitHubContributionCalendarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GitHubContributionCalendarApplication)
            workManagerFactory()

            modules(
                appModule,
                homeModule,
                userModule
            )
        }
    }
}