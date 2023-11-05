package pl.deniotokiari.githubcontributioncalendar.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetError
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

class WidgetDataSource(
    private val context: Context,
    private val glanceAppWidgetManager: GlanceAppWidgetManager,
    private val widget: AppWidget
) {
    fun getGlanceId(widgetId: WidgetId): Result<GlanceId, WidgetError> = runCatching {
        glanceAppWidgetManager.getGlanceIdBy(widgetId.value)
    }.fold(
        onSuccess = { Success(it) },
        onFailure = { Failed(WidgetError(it)) }
    )

    suspend fun setWidgetData(
        glanceId: GlanceId,
        userName: UserName,
        widgetId: WidgetId,
        widgetConfiguration: WidgetConfiguration,
        contributions: Contributions
    ): Result<Unit, WidgetError> = runCatching {
        updateAppWidgetState(context, glanceId) {
            it[AppWidget.USER_NAME_KEY] = userName.value
            it[AppWidget.WIDGET_ID_KEY] = widgetId.value
            it[AppWidget.CONFIG_KEY] = widgetConfiguration.toLocalModel()
            it[AppWidget.COLORS_KEY] = contributions.toLocalModel()
        }
    }.fold(
        onSuccess = { Success(Unit) },
        onFailure = { Failed(WidgetError(it)) }
    )

    suspend fun updateWidget(glanceId: GlanceId): Result<Unit, WidgetError> = runCatching {
        widget.update(context, glanceId)
    }.fold(
        onSuccess = { Success(Unit) },
        onFailure = { Failed(WidgetError(it)) }
    )

    suspend fun getAllGlanceIds(): Result<List<GlanceId>, WidgetError> = runCatching {
        glanceAppWidgetManager.getGlanceIds(widget::class.java)
    }.fold(
        onSuccess = { Success(it) },
        onFailure = { Failed(WidgetError(it)) }
    )

    suspend fun getUserName(glanceId: GlanceId): Result<UserName, WidgetError> = runCatching {
        val preferences = widget.getAppWidgetState<Preferences>(context, glanceId)
        val userName = preferences[AppWidget.USER_NAME_KEY]

        userName ?: throw Exception("Null user name for $glanceId")
    }.fold(
        onSuccess = { Success(UserName(it)) },
        onFailure = { Failed(WidgetError(it)) }
    )

    suspend fun updateContributions(glanceId: GlanceId, contributions: Contributions): Result<Unit, WidgetError> =
        runCatching {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[AppWidget.COLORS_KEY] = contributions.toLocalModel()
            }
        }.fold(
            onSuccess = { Success(Unit) },
            onFailure = { Failed(WidgetError(it)) }
        )
}