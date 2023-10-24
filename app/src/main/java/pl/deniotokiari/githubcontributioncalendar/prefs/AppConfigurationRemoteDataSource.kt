package pl.deniotokiari.githubcontributioncalendar.prefs

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import pl.deniotokiari.githubcontributioncalendar.BuildConfig

class AppConfigurationRemoteDataSource(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) {
    init {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings
                .Builder()
                .setFetchTimeoutInSeconds(
                    if (BuildConfig.DEBUG) {
                        0
                    } else {
                        3600
                    }
                )
                .build()
        )
    }

    fun init() {
        remoteConfig.fetchAndActivate()
    }

    fun getRepeatInterval(): Long = remoteConfig.getLong("repeat_interval")

    fun getSupportEmail(): String = remoteConfig.getString("support_email")

    fun getYears(): Int = remoteConfig.getLong("years").toInt()
}