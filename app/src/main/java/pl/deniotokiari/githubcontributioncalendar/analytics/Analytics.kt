package pl.deniotokiari.githubcontributioncalendar.analytics

import com.google.firebase.analytics.FirebaseAnalytics

interface Analytics {
    suspend fun track(name: String, values: Map<String, Any>)
}

class FirebaseAnalyticsImpl(
    private val analytics: FirebaseAnalytics
) : Analytics {
    override suspend fun track(name: String, values: Map<String, Any>) {

    }
}