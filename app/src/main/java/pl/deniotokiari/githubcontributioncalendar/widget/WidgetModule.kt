package pl.deniotokiari.githubcontributioncalendar.widget

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.RemoveWidgetByUserNameAndWidgetIdUseCase

val widgetModule = module {
    workerOf(::UpdateAppWidgetWorker)
    workerOf(::SetUpAppWidgetWorker)
    single { WidgetConfigurationRepository(get(qualifier = named("widgetConfiguration")), get<AppDispatchers.IO>()) }
    factoryOf(::RemoveWidgetByUserNameAndWidgetIdUseCase)
}