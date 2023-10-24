package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository

class SetWidgetConfigUseCase(
    private val context: Context,
    private val widgetConfigurationRepository: WidgetConfigurationRepository
) : UseCase<SetWidgetConfigUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        widgetConfigurationRepository.addConfiguration(params.widgetId, params.userName, params.config)

        val manager = GlanceAppWidgetManager(context)
        val appWidget = AppWidget()

        manager.getGlanceIds(AppWidget::class.java).forEach {
            val preferences = appWidget.getAppWidgetState<Preferences>(context, it)
            val widgetId = preferences[AppWidget.WIDGET_ID_KEY]
            val userName: String? = preferences[AppWidget.USER_NAME_KEY]

            if (widgetId == params.widgetId && userName == params.userName) {
                updateAppWidgetState(context, it) { prefs ->
                    prefs[AppWidget.CONFIG_KEY] = params.config.encode()
                }

                appWidget.update(context, it)

                return
            }
        }
    }

    data class Params(
        val widgetId: Int,
        val userName: String,
        val config: WidgetConfiguration
    )
}