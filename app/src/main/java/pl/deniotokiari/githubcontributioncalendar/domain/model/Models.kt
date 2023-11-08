package pl.deniotokiari.githubcontributioncalendar.domain.model

import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.failed
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId

@JvmInline
value class DomainError(val throwable: Throwable)

@JvmInline
value class Count(val value: Int)

@JvmInline
value class Email(val value: String)

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

fun fromDataError(error: DataError): Failed<DomainError> = DomainError(error.throwable).failed()