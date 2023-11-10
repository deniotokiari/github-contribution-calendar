package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers
import pl.deniotokiari.githubcontributioncalendar.domain.model.fromDataError

class SetUpWidgetUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val widgetConfigurationDataStore: WidgetConfigurationDataStore,
    private val contributionsRepository: ContributionsRepository,
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<WidgetIdentifiers, Result<Unit, DomainError>> {
    override suspend fun invoke(params: WidgetIdentifiers): Result<Unit, DomainError> =
        widgetDataSource.getGlanceId(params.widgetId)
            .flatMap { glanceId ->
                appConfigurationRepository.getYears()
                    .mapSuccess { years -> glanceId to years }
            }
            .flatMap { (glanceId, years) ->
                contributionsRepository.updateContributions(params.userName, years)
                    .mapSuccess { contributions -> glanceId to contributions }
            }
            .flatMap { (glanceId, contributions) ->
                val configuration = widgetConfigurationDataStore.defaultConfiguration()

                widgetDataSource.setWidgetData(
                    glanceId = glanceId,
                    userName = params.userName,
                    widgetId = params.widgetId,
                    widgetConfiguration = configuration,
                    contributions = contributions
                ).flatMap {
                    widgetConfigurationDataStore.addConfiguration(params.userName, params.widgetId, configuration)
                }.mapSuccess { glanceId }
            }
            .flatMap { glanceId -> widgetDataSource.updateWidget(glanceId) }
            .fold(
                success = { Unit.success() },
                failed = ::fromDataError
            )
}