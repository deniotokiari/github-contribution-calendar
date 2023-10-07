package pl.deniotokiari.githubcontributioncalendar.widget

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.SetUserNameToWidgetUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateWidgetByIdUseCase

val widgetModule = module {
    workerOf(::UpdateAppWidgetWorker)
    factoryOf(::SetUserNameToWidgetUseCase)
    factoryOf(::UpdateWidgetByIdUseCase)
}