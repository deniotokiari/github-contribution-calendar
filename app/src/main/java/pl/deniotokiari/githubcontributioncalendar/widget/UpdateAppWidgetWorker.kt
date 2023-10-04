package pl.deniotokiari.githubcontributioncalendar.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import pl.deniotokiari.githubcontributioncalendar.BuildConfig
import pl.deniotokiari.githubcontributioncalendar.widget.data.ContributionCalendarRepository
import java.util.concurrent.TimeUnit

class UpdateAppWidgetWorker(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        contributionCalendarRepository.updateAll()
        AppWidget().updateAll(context)

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "UpdateAppWidgetWorker"

        fun start(context: Context) {
            val request = PeriodicWorkRequestBuilder<UpdateAppWidgetWorker>(
                repeatInterval = if (BuildConfig.DEBUG) {
                    15
                } else {
                    12
                },
                repeatIntervalTimeUnit = if (BuildConfig.DEBUG) {
                    TimeUnit.MINUTES
                } else {
                    TimeUnit.HOURS
                }
            )
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