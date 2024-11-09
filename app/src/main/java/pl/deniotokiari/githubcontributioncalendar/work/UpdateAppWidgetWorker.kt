package pl.deniotokiari.githubcontributioncalendar.work

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.Logger
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateAllWidgetsUseCase
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UpdateAppWidgetWorker(
    context: Context,
    private val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    private val appAnalytics: AppAnalytics,
    private val logger: Logger,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        Log.d("LOG", "update all widget worker start")

        val start = System.currentTimeMillis()
        val updatedCount = updateAllWidgetsUseCase(Unit).fold(
            success = { it.value },
            failed = { error ->
                logger.error(error.throwable)

                0
            }
        )

        val time = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)

        Log.d(
            "LOG",
            "${updatedCount}: update all widget worker end $time"
        )

        appAnalytics.trackAllWidgetsUpdate(
            count = updatedCount,
            time = time.inWholeMilliseconds
        )

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "UpdateAppWidgetWorker"

        fun start(workManager: WorkManager, repeatInterval: Long) {
            val request = PeriodicWorkRequestBuilder<UpdateAppWidgetWorker>(
                repeatInterval = repeatInterval,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
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
                )
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
