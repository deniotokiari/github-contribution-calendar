package pl.deniotokiari.githubcontributioncalendar.widget

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.RemoveWidgetByUserNameAndWidgetIdUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.SetWidgetConfigUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateAllWidgetsUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateWidgetByUserNameAndWidgetIdUseCase

val widgetModule = module {
    workerOf(::UpdateAppWidgetWorker)
    workerOf(::SetUpAppWidgetWorker)
    single { WidgetConfigurationRepository(get(qualifier = named("widgetConfiguration")), get<AppDispatchers.IO>()) }
    factoryOf(::RemoveWidgetByUserNameAndWidgetIdUseCase)
    singleOf(::SetWidgetConfigUseCase)
    factoryOf(::UpdateAllWidgetsUseCase)
    factoryOf(::UpdateWidgetByUserNameAndWidgetIdUseCase)
}