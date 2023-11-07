package pl.deniotokiari.githubcontributioncalendar.domain.model

import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId

@JvmInline
value class SetUpWidgetError(val throwable: Throwable)

@JvmInline
value class UpdateAllWidgetsError(val throwable: Throwable)

@JvmInline
value class GetAllWidgetsConfigurationsWithContributionsError(val throwable: Throwable)

@JvmInline
value class Count(val value: Int)

data class WidgetIdentifiers(
    val userName: UserName,
    val widgetId: WidgetId
)

data class WidgetConfigurationWithContributions(
    val configuration: WidgetConfiguration,
    val contributions: Contributions,
    val userName: UserName,
    val widgetId: WidgetId
)