package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator
import pl.deniotokiari.githubcontributioncalendar.network.apolloClient

val appModule = module {
    single { AppDispatchers.IO }
    single(qualifier = named("app")) { get<Context>().appDataStore }
    single(qualifier = named("contribution")) { get<Context>().contributionDataStore }
    single { apolloClient }
    singleOf(::BlocksBitmapCreator)
}

sealed class AppDispatchers(val dispatcher: CoroutineDispatcher) {
    object IO : AppDispatchers(Dispatchers.IO)
}

// for general use
private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

// for user contributions
private val Context.contributionDataStore: DataStore<Preferences> by preferencesDataStore(name = "contribution")
