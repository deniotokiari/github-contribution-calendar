package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository

class UpdateWidgetByIdUseCase(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository,
    private val widgetConfigurationRepository: WidgetConfigurationRepository
) : UseCase<UpdateWidgetByIdUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        runCatching {
            contributionCalendarRepository.updateContributionsForUser(params.userName)
            widgetConfigurationRepository.addConfiguration(
                widgetId = params.widgetId,
                userName = params.userName,
                config = WidgetConfiguration.default()
            )

            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(params.widgetId)

            AppWidget().update(context, glanceId)
        }.onFailure {
            Log.d("LOG", "UpdateWidgetByIdUseCase => ${it.message}")
        }
    }

    data class Params(
        val widgetId: Int,
        val userName: String
    )
}