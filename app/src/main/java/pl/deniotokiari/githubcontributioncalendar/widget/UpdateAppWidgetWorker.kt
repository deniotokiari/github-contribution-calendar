package pl.deniotokiari.githubcontributioncalendar.widget

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
import kotlinx.coroutines.awaitCancellation
import pl.deniotokiari.githubcontributioncalendar.DevRepository
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateAllWidgetsUseCase
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UpdateAppWidgetWorker(
    private val context: Context,
    private val devRepository: DevRepository,
    private val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    private val appAnalytics: AppAnalytics,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        devRepository.incrementWidgetUpdateCount()
        val start = System.currentTimeMillis()

        Log.d("LOG", "update all widget worker start")

        val updatedCount =
            try {
                updateAllWidgetsUseCase(Unit)
            } catch (e: Exception) {
                Log.d("LOG", e.message.toString())

                -1
            }

        val time = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)

        Log.d(
            "LOG",
            "${updatedCount}: update all widget worker end $time"
        )

        appAnalytics.trackWidgetUpdate(
            count = updatedCount,
            time = time.inWholeMilliseconds
        )

        if (updatedCount == 0) {
            cancel(context)

            awaitCancellation()
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "UpdateAppWidgetWorker"

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun start(context: Context, repeatInterval: Long) {
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

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}