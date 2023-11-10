package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers

class RemoveWidgetDataUseCase(
    private val contributionsRepository: ContributionsRepository,
    private val configurationDataStore: WidgetConfigurationDataStore
) : UseCase<WidgetIdentifiers, Result<Unit, DomainError>> {
    override suspend fun invoke(params: WidgetIdentifiers): Result<Unit, DomainError> =
        configurationDataStore.removeConfiguration(
            userName = params.userName,
            widgetId = params.widgetId
        ).flatMap {
            contributionsRepository.removeContributions(params.userName)
        }.mapFailure { DomainError(it.throwable) }
}