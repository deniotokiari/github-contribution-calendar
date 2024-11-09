package pl.deniotokiari.githubcontributioncalendar.work

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
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
import pl.deniotokiari.githubcontributioncalendar.core.Logger
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.SetUpWidgetUseCase
import java.util.concurrent.TimeUnit

class SetUpAppWidgetWorker(
    context: Context,
    private val parameters: WorkerParameters,
    private val setUpWidgetUseCase: SetUpWidgetUseCase,
    private val logger: Logger,
) : CoroutineWorker(
    context, parameters
) {
    override suspend fun doWork(): Result = runCatching {
        val widgetId = parameters
            .inputData
            .getInt(WIDGET_ID_KEY, AppWidgetManager.INVALID_APPWIDGET_ID)
            .also { require(it != AppWidgetManager.INVALID_APPWIDGET_ID) }
        val userName = requireNotNull(parameters.inputData.getString(USER_NAME_KEY))

        setUpWidgetUseCase(
            WidgetIdentifiers(
                widgetId = WidgetId(widgetId),
                userName = UserName(userName)
            )
        ).mapFailure { throw it.throwable }
    }.fold(
        onSuccess = {
            Result.success()
        },
        onFailure = {
            Log.d("LOG", "SetUpAppWidgetWorker ${it.message}")
            logger.error(it)

            Result.failure()
        }
    )

    companion object {
        private const val WORK_NAME = "SetUpAppWidgetWorker"
        private const val USER_NAME_KEY = "USER_NAME_KEY"
        private const val WIDGET_ID_KEY = "WIDGET_ID_KEY"

        fun start(workManager: WorkManager, widgetId: Int, userName: String) {
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

            workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND, request)
        }
    }
}
