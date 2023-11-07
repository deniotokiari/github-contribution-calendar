package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.failed
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.SetUpWidgetError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers

class SetUpWidgetUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val widgetConfigurationDataStore: WidgetConfigurationDataStore,
    private val contributionsRepository: ContributionsRepository,
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<WidgetIdentifiers, Result<Unit, SetUpWidgetError>> {
    override suspend fun invoke(params: WidgetIdentifiers): Result<Unit, SetUpWidgetError> =
        widgetDataSource.getGlanceId(params.widgetId).mapFailure { it.throwable }
            .flatMap { glanceId ->
                appConfigurationRepository.getYears()
                    .mapSuccess { years -> glanceId to years }
                    .mapFailure { it.throwable }
            }
            .flatMap { (glanceId, years) ->
                contributionsRepository.updateContributions(params.userName, years)
                    .mapSuccess { contributions -> glanceId to contributions }
                    .mapFailure { it.throwable }
            }
            .flatMap { (glanceId, contributions) ->
                widgetDataSource.setWidgetData(
                    glanceId = glanceId,
                    userName = params.userName,
                    widgetId = params.widgetId,
                    widgetConfiguration = widgetConfigurationDataStore.defaultConfiguration(),
                    contributions = contributions
                )
                    .mapSuccess { glanceId }
                    .mapFailure { it.throwable }
            }
            .flatMap { glanceId -> widgetDataSource.updateWidget(glanceId).mapFailure { it.throwable } }
            .fold(
                success = { Unit.success() },
                failed = { SetUpWidgetError(it).failed() }
            )
}