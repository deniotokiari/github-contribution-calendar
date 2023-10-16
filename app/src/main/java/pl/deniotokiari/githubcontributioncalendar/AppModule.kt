package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.analytics.Analytics
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.analytics.FirebaseAnalyticsImpl
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import pl.deniotokiari.githubcontributioncalendar.network.apolloClient

val appModule = module {
    single { AppDispatchers.IO }
    single(qualifier = named("app")) { get<Context>().appDataStore }
    single(qualifier = named("contribution")) { get<Context>().contributionDataStore }
    single(qualifier = named("widgetConfiguration")) { get<Context>().widgetConfigurationDataStore }
    single(qualifier = named("dev")) { get<Context>().devDataStore }
    single { apolloClient }
    singleOf(::BlocksBitmapCreator)
    single { DevRepository(get(qualifier = named("dev"))) }
    singleOf(::FirebaseAnalyticsImpl) bind Analytics::class
    singleOf(::AppAnalytics)
}

sealed class AppDispatchers(val dispatcher: CoroutineDispatcher) {
    object IO : AppDispatchers(Dispatchers.IO)
}

// for general use
private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

// for user contributions
private val Context.contributionDataStore: DataStore<Preferences> by preferencesDataStore(name = "contribution")

// for widget configuration
private val Context.widgetConfigurationDataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetConfiguration")

// for dev purpose
private val Context.devDataStore: DataStore<Preferences> by preferencesDataStore(name = "dev")
