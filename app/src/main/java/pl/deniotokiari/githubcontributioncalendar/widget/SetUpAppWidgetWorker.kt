package pl.deniotokiari.githubcontributioncalendar.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.data.encode
import java.util.concurrent.TimeUnit

class SetUpAppWidgetWorker(
    private val context: Context,
    private val parameters: WorkerParameters,
    private val contributionCalendarRepository: ContributionCalendarRepository,
    private val widgetConfigurationRepository: WidgetConfigurationRepository
) : CoroutineWorker(
    context, parameters
) {
    override suspend fun doWork(): Result = runCatching {
        val widgetId = parameters.inputData.getInt(WIDGET_ID_KEY, AppWidgetManager.INVALID_APPWIDGET_ID).also {
            require(it != AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        val userName = requireNotNull(parameters.inputData.getString(USER_NAME_KEY))

        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)

        val config = WidgetConfiguration.default()
        val colors = contributionCalendarRepository.updateContributionsForUser(userName)

        widgetConfigurationRepository.addConfiguration(widgetId, userName, config)

        updateAppWidgetState(context, glanceId) {
            it[AppWidget.USER_NAME_KEY] = userName
            it[AppWidget.WIDGET_ID_KEY] = widgetId
            it[AppWidget.CONFIG_KEY] = config.encode()
            it[AppWidget.COLORS_KEY] = colors.encode()
        }

        AppWidget().update(context, glanceId)
    }.fold(
        onSuccess = { Result.success() },
        onFailure = {
            Log.d("LOG", "SetUpAppWidgetWorker ${it.message}")

            Result.failure()
        }
    )

    companion object {
        private const val WORK_NAME = "SetUpAppWidgetWorker"
        private const val USER_NAME_KEY = "USER_NAME_KEY"
        private const val WIDGET_ID_KEY = "WIDGET_ID_KEY"

        fun start(context: Context, widgetId: Int, userName: String) {
            val request = OneTimeWorkRequestBuilder<SetUpAppWidgetWorker>()
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiresDeviceIdle(false)
                        .setRequiresBatteryNotLow(false)
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                ).setInputData(
                    Data
                        .Builder()
                        .putInt(WIDGET_ID_KEY, widgetId)
                        .putString(USER_NAME_KEY, userName)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND, request)
        }
    }
}