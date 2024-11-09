package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.deniotokiari.githubcontributioncalendar.core.FlowUseCase
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.failed
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError

class GetAllWidgetsConfigurationsUseCase(
    private val widgetConfigurationDataStore: WidgetConfigurationDataStore,
) :
    FlowUseCase<Unit, Result<List<Triple<UserName, WidgetId, WidgetConfiguration>>, DomainError>> {
    override fun invoke(params: Unit): Flow<Result<List<Triple<UserName, WidgetId, WidgetConfiguration>>, DomainError>> =
        widgetConfigurationDataStore
            .allConfigurations()
            .map { result ->
                result.fold(
                    success = { items ->
                        items.map { item ->
                            Triple(
                                first = item.first.first,
                                second = item.first.second,
                                third = item.second,
                            )
                        }.success()
                    },
                    failed = {
                        DomainError(it.throwable).failed()
                    },
                )
            }
}
