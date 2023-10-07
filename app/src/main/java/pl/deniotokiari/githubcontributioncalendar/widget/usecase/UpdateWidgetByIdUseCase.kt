package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget

class UpdateWidgetByIdUseCase(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository
) : UseCase<UpdateWidgetByIdUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        contributionCalendarRepository.updateContributionsForUser(params.userName)

        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(params.widgetId)

        AppWidget().update(context, glanceId)
    }

    data class Params(
        val widgetId: Int,
        val userName: String
    )
}