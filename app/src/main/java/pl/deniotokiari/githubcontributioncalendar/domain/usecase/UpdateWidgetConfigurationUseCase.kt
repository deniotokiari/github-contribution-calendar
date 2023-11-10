package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers

class UpdateWidgetConfigurationUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val configurationDataStore: WidgetConfigurationDataStore
) : UseCase<UpdateWidgetConfigurationUseCase.Params, Result<Unit, DomainError>> {
    override suspend fun invoke(params: Params): Result<Unit, DomainError> =
        widgetDataSource.getGlanceId(params.widgetIdentifiers.widgetId)
            .flatMap { id ->
                widgetDataSource.updateConfiguration(id, params.widgetConfiguration)
                    .mapSuccess { id }
            }.flatMap { id ->
                configurationDataStore.addConfiguration(
                    params.widgetIdentifiers.userName,
                    params.widgetIdentifiers.widgetId,
                    params.widgetConfiguration
                ).mapSuccess { id }
            }.flatMap {
                widgetDataSource.updateWidget(it)
            }.mapFailure {
                DomainError(it.throwable)
            }

    data class Params(
        val widgetIdentifiers: WidgetIdentifiers,
        val widgetConfiguration: WidgetConfiguration
    )
}