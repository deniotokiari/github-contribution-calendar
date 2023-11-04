package pl.deniotokiari.githubcontributioncalendar.domain.model

import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId

@JvmInline
value class SetUpWidgetError(val throwable: Throwable)

@JvmInline
value class UpdateAllWidgetsError(val throwable: Throwable)

@JvmInline
value class Count(val value: Int)

data class WidgetIdentifiers(
    val userName: UserName,
    val widgetId: WidgetId
)