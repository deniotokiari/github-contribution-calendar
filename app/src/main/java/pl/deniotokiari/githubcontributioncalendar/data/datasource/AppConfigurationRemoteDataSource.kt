package pl.deniotokiari.githubcontributioncalendar.data.datasource

import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class AppConfigurationRemoteDataSource(
    private val remoteConfig: FirebaseRemoteConfig
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

        remoteConfig.fetchAndActivate()
    }

    fun getRepeatInterval(): Long = remoteConfig.getLong("repeat_interval")

    fun getSupportEmail(): String = remoteConfig.getString("support_email")

    fun getYears(): Int = remoteConfig.getLong("years").toInt()
}