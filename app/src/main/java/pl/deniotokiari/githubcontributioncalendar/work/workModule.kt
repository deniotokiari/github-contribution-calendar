package pl.deniotokiari.githubcontributioncalendar.work

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val workModule = module {
    workerOf(::SetUpAppWidgetWorker)
    workerOf(::UpdateAppWidgetWorker)
}