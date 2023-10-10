package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository

class UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase(
    private val context: Context,
    private val widgetConfigurationRepository: WidgetConfigurationRepository
) : UseCase<UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        runCatching {
            widgetConfigurationRepository.addConfiguration(
                widgetId = params.widgetId,
                userName = params.userName,
                config = params.config
            )

            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(params.widgetId)

            AppWidget().update(context, glanceId)
        }.onFailure {
            Log.d("LOG", "UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase => ${it.message}")
        }
    }

    data class Params(
        val widgetId: Int,
        val userName: String,
        val config: WidgetConfiguration
    )
}