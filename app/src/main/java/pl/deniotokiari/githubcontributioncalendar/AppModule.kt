package pl.deniotokiari.githubcontributioncalendar

import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.WorkManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.analytics.Analytics
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.analytics.FirebaseAnalyticsImpl
import pl.deniotokiari.githubcontributioncalendar.core.misc.Logger
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import pl.deniotokiari.githubcontributioncalendar.network.apolloClient
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

@JvmInline
value class PackageName(val value: String)

val appModule = module {
    single { apolloClient }
    single { Firebase.analytics }
    single { FirebaseRemoteConfig.getInstance() }
    singleOf(::FirebaseAnalyticsImpl) bind Analytics::class
    singleOf(::AppAnalytics)
    singleOf(::GitHubService)
    single(named("contribution")) { get<Context>().contributionsDataStore }
    single(named("widgetConfiguration")) { get<Context>().widgetConfigurationDataStore }
    single { GlanceAppWidgetManager(get()) }
    factory { AppWidget() }
    single { WorkManager.getInstance(get()) }
    single { PackageName(get<Context>().packageName) }
    single<PowerManager?> { get<Context>().getSystemService<PowerManager>() }

    single<Logger> {
        if (BuildConfig.DEBUG) {
            LogLogger()
        } else {
            FirebaseLogger()
        }
    }
}

// for user contributions
private val Context.contributionsDataStore: DataStore<Preferences> by preferencesDataStore(name = "contribution")

// for widget configuration
private val Context.widgetConfigurationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "widgetConfiguration"
)

private class FirebaseLogger : Logger {
    override fun message(message: String) {
        Firebase.crashlytics.log(message)
    }

    override fun error(error: Throwable) {
        Firebase.crashlytics.recordException(error)
    }
}

private class LogLogger : Logger {
    override fun message(message: String) {
        Log.d("githubcontributioncalendar", message)
    }

    override fun error(error: Throwable) {
        Log.e("githubcontributioncalendar", error.message, error)
    }
}
