package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget

class SetUserNameToWidgetUseCase(
    private val context: Context
) : UseCase<SetUserNameToWidgetUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(params.id)

        updateAppWidgetState(
            context = context,
            glanceId = glanceId
        ) {
            it[AppWidget.USER_NAME_KEY] = params.userName
        }
    }

    data class Params(
        val userName: String,
        val id: Int
    )
}