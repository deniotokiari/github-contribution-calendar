package pl.deniotokiari.githubcontributioncalendar.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

interface Analytics {
    fun track(name: String, values: Map<String, Any>)
}

class FirebaseAnalyticsImpl(
    private val analytics: FirebaseAnalytics
) : Analytics {
    override fun track(name: String, values: Map<String, Any>) {
        analytics.logEvent(
            name,
            Bundle().apply {
                values.forEach { (key, value) ->
                    when (value) {
                        is Int -> putInt(key, value)
                        is String -> putString(key, value)
                        is Float -> putFloat(key, value)
                    }
                }
            }
        )
    }
}