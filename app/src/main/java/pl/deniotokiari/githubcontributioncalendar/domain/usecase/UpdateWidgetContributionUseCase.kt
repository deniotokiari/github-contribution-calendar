package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError

class UpdateWidgetContributionUseCase(
    private val widgetDataSource: WidgetDataSource,
    private val contributionsRepository: ContributionsRepository,
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<UserName, Result<Unit, DomainError>> {
    override suspend fun invoke(params: UserName): Result<Unit, DomainError> =
        appConfigurationRepository.getYears()
            .flatMap { year ->
                contributionsRepository.updateContributions(userName = params, year)
            }.flatMap { contributions ->
                widgetDataSource.getAllGlanceIds().mapSuccess { ids ->
                    ids.forEach { id ->
                        val userName = widgetDataSource.getUserName(id).successOrNull()

                        if (userName == params) {
                            widgetDataSource.updateContributions(id, contributions).flatMap {
                                widgetDataSource.updateWidget(id)
                            }
                        }
                    }
                }

                Success(Unit)
            }.mapFailure { DomainError(it.throwable) }
}
