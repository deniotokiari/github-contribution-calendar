package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.deniotokiari.githubcontributioncalendar.core.FlowUseCase
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetConfigurationWithContributions
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers

class GetWidgetsConfigurationsWithContributionsUseCase(
    private val widgetConfigurationDataStore: WidgetConfigurationDataStore,
    private val contributionsRepository: ContributionsRepository
) : FlowUseCase<WidgetIdentifiers, Result<WidgetConfigurationWithContributions, DomainError>> {
    override fun invoke(params: WidgetIdentifiers): Flow<Result<WidgetConfigurationWithContributions, DomainError>> =
        contributionsRepository.contributions(userName = params.userName).combine(
            widgetConfigurationDataStore.configuration(
                userName = params.userName,
                widgetId = params.widgetId
            )
        ) { contributionsResult, configurationResult ->
            contributionsResult.flatMap { contributions ->
                configurationResult.mapSuccess { configuration ->
                    WidgetConfigurationWithContributions(
                        configuration = configuration,
                        contributions = contributions,
                        userName = params.userName,
                        widgetId = params.widgetId
                    )
                }
            }.mapFailure { DomainError(it.throwable) }
        }
}