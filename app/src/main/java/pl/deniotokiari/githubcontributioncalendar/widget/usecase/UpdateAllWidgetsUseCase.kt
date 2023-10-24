package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.data.encode
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget

class UpdateAllWidgetsUseCase(
    private val context: Context,
    private val contributionCalendarRepository: ContributionCalendarRepository
) : UseCase<Unit, Int> {
    override suspend fun invoke(params: Unit): Int {
        val manager = GlanceAppWidgetManager(context)
        val appWidget = AppWidget()
        var count = 0

        manager.getGlanceIds(AppWidget::class.java).forEach {
            val preferences = appWidget.getAppWidgetState<Preferences>(context, it)
            val userName = preferences[AppWidget.USER_NAME_KEY]

            if (userName != null) {
                val colors = contributionCalendarRepository.updateContributionsForUser(userName)

                if (colors.isNotEmpty()) {
                    count++
                }

                updateAppWidgetState(context, it) { prefs ->
                    prefs[AppWidget.COLORS_KEY] = colors.encode()
                }
            }
        }

        AppWidget().updateAll(context)

        return count
    }
}