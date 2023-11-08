package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.Count
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError

class UpdateAllWidgetsUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val appConfigurationRepository: AppConfigurationRepository,
    private val contributionsRepository: ContributionsRepository
) : UseCase<Unit, Result<Count, DomainError>> {
    override suspend fun invoke(params: Unit): Result<Count, DomainError> =
        widgetDataSource.getAllGlanceIds()
            .flatMap { ids ->
                appConfigurationRepository.getYears()
                    .mapSuccess { years -> ids to years }
            }
            .flatMap { (ids, years) ->
                var count = 0

                ids.forEach { id ->
                    widgetDataSource.getUserName(id)
                        .flatMap { userName ->
                            contributionsRepository.updateContributions(userName, years)
                        }
                        .flatMap { contributions ->
                            widgetDataSource.updateContributions(id, contributions)
                                .mapSuccess { contributions.colors.isNotEmpty() }
                        }.mapSuccess {
                            if (it) {
                                count++
                            }
                        }
                }

                Count(count).success()
            }.flatMap { count -> widgetDataSource.updateAllWidgets().mapSuccess { count } }
            .mapFailure { DomainError(it.throwable) }

}