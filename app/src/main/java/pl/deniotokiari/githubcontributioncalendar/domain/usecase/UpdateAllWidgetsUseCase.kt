package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.Count
import pl.deniotokiari.githubcontributioncalendar.domain.model.UpdateAllWidgetsError

class UpdateAllWidgetsUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val appConfigurationRepository: AppConfigurationRepository,
    private val contributionsRepository: ContributionsRepository
) : UseCase<Unit, Result<Count, UpdateAllWidgetsError>> {
    override suspend fun invoke(params: Unit): Result<Count, UpdateAllWidgetsError> =
        widgetDataSource.getAllGlanceIds().mapFailure { it.throwable }
            .flatMap { ids ->
                appConfigurationRepository.getYears()
                    .mapSuccess { years -> ids to years }
                    .mapFailure { it.throwable }
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

                Success(Count(count))
            }
            .mapFailure { UpdateAllWidgetsError(it) }

}