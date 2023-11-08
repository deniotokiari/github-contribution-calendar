package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError

class UpdateWidgetConfigurationUseCase(
    private val widgetDataSource: WidgetDataSource
) : UseCase<UpdateWidgetConfigurationUseCase.Params, Result<Unit, DomainError>> {
    override suspend fun invoke(params: Params): Result<Unit, DomainError> =
        widgetDataSource.getGlanceId(params.widgetId)
            .flatMap { id ->
                widgetDataSource.updateConfiguration(id, params.widgetConfiguration)
                    .mapSuccess { id }
            }.flatMap {
                widgetDataSource.updateWidget(it)
            }.mapFailure {
                DomainError(it.throwable)
            }

    data class Params(
        val widgetId: WidgetId,
        val widgetConfiguration: WidgetConfiguration
    )
}