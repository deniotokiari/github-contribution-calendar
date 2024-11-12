package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.PackageName
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.SetUpWidgetUseCase
import pl.deniotokiari.githubcontributioncalendar.work.UpdateAppWidgetWorker

class AppWidgetConfigurationViewModel(
    private val powerManager: PowerManager?,
    private val packageName: PackageName,
    private val appAnalytics: AppAnalytics,
    private val workManager: WorkManager,
    private val appConfigurationRepository: AppConfigurationRepository,
    private val setUpWidgetUseCase: SetUpWidgetUseCase,
) : ViewModel() {
    fun isIgnoringBatteryOptimizations(): Boolean =
        powerManager?.isIgnoringBatteryOptimizations(packageName.value) == true

    @OptIn(DelicateCoroutinesApi::class)
    fun setUpWidget(widgetId: Int, userName: String) {
        GlobalScope.launch(Dispatchers.Default) {
            setUpWidgetUseCase(
                WidgetIdentifiers(
                    widgetId = WidgetId(widgetId),
                    userName = UserName(userName),
                ),
            )

            appConfigurationRepository.getUpdateInterval().mapSuccess {
                UpdateAppWidgetWorker.start(workManager, it)
            }

            appAnalytics.trackWidgetAdd(userName)
        }
    }

    fun trackIsIgnoringBatteryOptimizations() {
        appAnalytics.trackIsIgnoringBatteryOptimizations(isIgnoringBatteryOptimizations())
    }
}
