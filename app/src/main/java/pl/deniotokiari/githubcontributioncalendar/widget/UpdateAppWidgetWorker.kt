package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.awaitCancellation
import pl.deniotokiari.githubcontributioncalendar.DevRepository
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UpdateAppWidgetWorker(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository,
    private val devRepository: DevRepository,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        devRepository.incrementWidgetUpdateCount()
        val start = System.currentTimeMillis()
        Log.d("LOG", "update all widget worker start")

        val updatedCount = contributionCalendarRepository.updateAllContributions()

        AppWidget().updateAll(context)

        Log.d(
            "LOG",
            "update all widget worker end ${(System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)}"
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

        fun start(context: Context) {
            val request = PeriodicWorkRequestBuilder<UpdateAppWidgetWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setInitialDelay(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}