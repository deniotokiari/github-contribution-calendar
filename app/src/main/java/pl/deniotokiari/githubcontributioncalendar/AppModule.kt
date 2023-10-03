package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import pl.deniotokiari.githubcontributioncalendar.network.apolloClient
import pl.deniotokiari.githubcontributioncalendar.widget.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.GitHubLocalDataSource
import pl.deniotokiari.githubcontributioncalendar.widget.GitHubRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.widget.UpdateAppWidgetWorker
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetBitmapCreator

val appModule = module {
    single { AppDispatchers.IO }
    single { get<Context>().dataStore }
    single { apolloClient }
    singleOf(::WidgetBitmapCreator)
    singleOf(::GitHubService)
    singleOf(::GitHubLocalDataSource)
    singleOf(::GitHubRemoteDataSource)
    singleOf(::ContributionCalendarRepository)
    workerOf(::UpdateAppWidgetWorker)
}

sealed class AppDispatchers(val dispatcher: CoroutineDispatcher) {
    object IO : AppDispatchers(Dispatchers.IO)
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")