package pl.deniotokiari.githubcontributioncalendar

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.data.dataModule
import pl.deniotokiari.githubcontributioncalendar.home.homeModule
import pl.deniotokiari.githubcontributioncalendar.user.userModule
import pl.deniotokiari.githubcontributioncalendar.widget.UpdateAppWidgetWorker
import pl.deniotokiari.githubcontributioncalendar.widget.widgetModule

class GitHubContributionCalendarApplication : Application() {
    private val applicationCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GitHubContributionCalendarApplication)
            workManagerFactory()

            modules(
                module {
                    single(qualifier = named("app")) { applicationCoroutineScope }
                },
                appModule,
                dataModule,
                widgetModule,
                homeModule,
                userModule
            )
        }

        UpdateAppWidgetWorker.start(this)
    }
}