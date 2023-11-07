package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.deniotokiari.githubcontributioncalendar.core.FlowUseCase
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.combineResult
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.GetAllWidgetsConfigurationsWithContributionsError
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetConfigurationWithContributions

class GetAllWidgetsConfigurationsWithContributionsUseCase(
    private val widgetConfigurationDataStore: WidgetConfigurationDataStore,
    private val contributionsRepository: ContributionsRepository
) :
    FlowUseCase<Unit, Result<List<WidgetConfigurationWithContributions>, GetAllWidgetsConfigurationsWithContributionsError>> {
    override fun invoke(params: Unit): Flow<Result<List<WidgetConfigurationWithContributions>, GetAllWidgetsConfigurationsWithContributionsError>> =
        combine(
            widgetConfigurationDataStore.allConfigurations(),
            contributionsRepository.allContributions()
        ) { configurationsResult, contributionsResult ->
            combineResult(
                configurationsResult.mapFailure { it.throwable },
                contributionsResult.mapFailure { it.throwable }) { configurations, contributions ->
                configurations.mapNotNull { (widgetIdentifiers, configuration) ->
                    val (widgetUserName, widgetId) = widgetIdentifiers
                    val contribution = contributions.firstOrNull { (userName, _) ->
                        userName == widgetUserName.value
                    }?.second

                    contribution?.let {
                        WidgetConfigurationWithContributions(
                            configuration = configuration,
                            contributions = it,
                            userName = widgetUserName,
                            widgetId = widgetId
                        )
                    }
                }
            }.mapFailure { GetAllWidgetsConfigurationsWithContributionsError(it) }
        }
}