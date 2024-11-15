package pl.deniotokiari.githubcontributioncalendar.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class UserRoute(
    val user: String,
    val widgetId: Int,
)
