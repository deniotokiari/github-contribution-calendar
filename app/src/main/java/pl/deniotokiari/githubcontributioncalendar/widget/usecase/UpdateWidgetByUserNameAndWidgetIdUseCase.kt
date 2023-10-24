package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.data.encode
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget

class UpdateWidgetByUserNameAndWidgetIdUseCase(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository
) : UseCase<UpdateWidgetByUserNameAndWidgetIdUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        val manager = GlanceAppWidgetManager(context)
        val appWidget = AppWidget()

        manager.getGlanceIds(AppWidget::class.java).forEach {
            val preferences = appWidget.getAppWidgetState<Preferences>(context, it)
            val widgetId = preferences[AppWidget.WIDGET_ID_KEY]
            val userName: String? = preferences[AppWidget.USER_NAME_KEY]

            if (widgetId == params.widgetId && userName == params.userName) {
                val colors = contributionCalendarRepository.updateContributionsForUser(userName)

                updateAppWidgetState(context, it) { prefs ->
                    prefs[AppWidget.COLORS_KEY] = colors.encode()
                }

                appWidget.update(context, it)

                return
            }
        }
    }

    data class Params(
        val widgetId: Int,
        val userName: String
    )
}