package pl.deniotokiari.githubcontributioncalendar.about

import androidx.lifecycle.ViewModel
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics

class AboutViewModel(
    private val appAnalytics: AppAnalytics
) : ViewModel() {
    init {
        appAnalytics.trackAboutView()
    }
}