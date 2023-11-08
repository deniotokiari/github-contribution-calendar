package pl.deniotokiari.githubcontributioncalendar.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.asFailed
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.ui.widget.AppWidget

class WidgetDataSource(
    private val context: Context,
    private val glanceAppWidgetManager: GlanceAppWidgetManager,
    private val widget: AppWidget
) {
    suspend fun getGlanceId(widgetId: WidgetId): Result<GlanceId, DataError> = runCatching {
        glanceAppWidgetManager.getGlanceIds(widget::class.java).forEach { id ->
            val preferences = widget.getAppWidgetState<Preferences>(context, id)

            if (preferences[AppWidget.WIDGET_ID_KEY] == widgetId.value) {
                return@runCatching id
            }
        }

        glanceAppWidgetManager.getGlanceIdBy(widgetId.value)
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun setWidgetData(
        glanceId: GlanceId,
        userName: UserName,
        widgetId: WidgetId,
        widgetConfiguration: WidgetConfiguration,
        contributions: Contributions
    ): Result<Unit, DataError> = runCatching {
        updateAppWidgetState(context, glanceId) {
            it[AppWidget.USER_NAME_KEY] = userName.value
            it[AppWidget.WIDGET_ID_KEY] = widgetId.value
            it[AppWidget.CONFIG_KEY] = widgetConfiguration.toLocalModel()
            it[AppWidget.COLORS_KEY] = contributions.toLocalModel()
        }
    }.fold(
        onSuccess = { Unit.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun updateAllWidgets(): Result<Unit, DataError> = runCatching {
        widget.updateAll(context)
    }.fold(
        onSuccess = { Unit.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun updateWidget(glanceId: GlanceId): Result<Unit, DataError> = runCatching {
        widget.update(context, glanceId)
    }.fold(
        onSuccess = { Unit.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun getAllGlanceIds(): Result<List<GlanceId>, DataError> = runCatching {
        glanceAppWidgetManager.getGlanceIds(widget::class.java)
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun getUserName(glanceId: GlanceId): Result<UserName, DataError> = runCatching {
        val preferences = widget.getAppWidgetState<Preferences>(context, glanceId)
        val userName = preferences[AppWidget.USER_NAME_KEY]

        userName ?: throw Exception("Null user name for $glanceId")
    }.fold(
        onSuccess = { UserName(it).success() },
        onFailure = { it.asFailed(::DataError) }
    )

    suspend fun updateContributions(glanceId: GlanceId, contributions: Contributions): Result<Unit, DataError> =
        runCatching {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[AppWidget.COLORS_KEY] = contributions.toLocalModel()
            }
        }.fold(
            onSuccess = { Unit.success() },
            onFailure = { it.asFailed(::DataError) }
        )

    suspend fun updateConfiguration(glanceId: GlanceId, configuration: WidgetConfiguration): Result<Unit, DataError> =
        runCatching {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[AppWidget.CONFIG_KEY] = configuration.toLocalModel()
            }
        }.fold(
            onSuccess = { Unit.success() },
            onFailure = { it.asFailed(::DataError) }
        )
}