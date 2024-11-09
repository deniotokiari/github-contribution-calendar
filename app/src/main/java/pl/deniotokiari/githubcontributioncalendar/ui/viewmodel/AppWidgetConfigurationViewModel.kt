package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import pl.deniotokiari.githubcontributioncalendar.PackageName
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.work.SetUpAppWidgetWorker
import pl.deniotokiari.githubcontributioncalendar.work.UpdateAppWidgetWorker

class AppWidgetConfigurationViewModel(
    private val powerManager: PowerManager?,
    private val packageName: PackageName,
    private val appAnalytics: AppAnalytics,
    private val workManager: WorkManager,
    private val appConfigurationRepository: AppConfigurationRepository,
) : ViewModel() {
    fun isIgnoringBatteryOptimizations(): Boolean =
        powerManager?.isIgnoringBatteryOptimizations(packageName.value) == true

    fun setUpWidget(widgetId: Int, userName: String) {
        SetUpAppWidgetWorker.start(workManager, widgetId, userName)
    }

    fun startUpdateWidget() {
        appConfigurationRepository.getUpdateInterval().mapSuccess {
            UpdateAppWidgetWorker.start(workManager, it)
        }
    }

    fun trackIsIgnoringBatteryOptimizations() {
        appAnalytics.trackIsIgnoringBatteryOptimizations(isIgnoringBatteryOptimizations())
    }

    fun trackWidgetAdd(userName: String) {
        appAnalytics.trackWidgetAdd(userName)
    }
}
