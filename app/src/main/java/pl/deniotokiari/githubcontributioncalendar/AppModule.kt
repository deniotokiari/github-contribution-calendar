package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.WorkManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.analytics.Analytics
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.analytics.FirebaseAnalyticsImpl
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import pl.deniotokiari.githubcontributioncalendar.network.apolloClient
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

@JvmInline
value class PackageName(val value: String)

val appModule = module {
    factory { apolloClient }
    factory { Firebase.analytics }
    factory { FirebaseRemoteConfig.getInstance() }
    factoryOf(::FirebaseAnalyticsImpl) bind Analytics::class
    factoryOf(::AppAnalytics)
    factoryOf(::GitHubService)
    factory(named("contribution")) { get<Context>().contributionsDataStore }
    factory(named("widgetConfiguration")) { get<Context>().widgetConfigurationDataStore }
    factory { GlanceAppWidgetManager(get()) }
    factory { AppWidget() }
    factory { WorkManager.getInstance(get()) }
    factory { PackageName(get<Context>().packageName) }
    factory { get<Context>().getSystemService(ComponentActivity.POWER_SERVICE) as? PowerManager }
}

// for user contributions
private val Context.contributionsDataStore: DataStore<Preferences> by preferencesDataStore(name = "contribution")

// for widget configuration
private val Context.widgetConfigurationDataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetConfiguration")