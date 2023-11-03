package pl.deniotokiari.githubcontributioncalendar.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfigurationError
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId

class WidgetConfigurationDataStore(
    private val dataStore: DataStore<Preferences>
) {
    fun allConfigurations(): Flow<Result<List<Pair<Pair<UserName, WidgetId>, WidgetConfiguration>>, WidgetConfigurationError>> =
        dataStore.data.map {
            runCatching {
                it.asMap().map { (key, value) ->
                    val (userName, widgetId) = key.name.split(":")

                    (UserName(userName) to WidgetId(widgetId.toInt())) to WidgetConfiguration.fromLocalModel(value)
                }
            }.fold(
                onSuccess = { Success(it) },
                onFailure = { Failed(WidgetConfigurationError(it)) }
            )
        }

    fun configuration(
        userName: UserName,
        widgetId: WidgetId
    ): Flow<Result<WidgetConfiguration, WidgetConfigurationError>> = dataStore.data.map {
        runCatching {
            it[configurationKey(userName, widgetId)].let {
                WidgetConfiguration.fromLocalModel(it)
            }
        }.fold(
            onSuccess = { Success(it) },
            onFailure = { Failed(WidgetConfigurationError(it)) }
        )
    }

    suspend fun addConfiguration(
        userName: UserName,
        widgetId: WidgetId,
        configuration: WidgetConfiguration
    ): Result<Unit, WidgetConfigurationError> =
        runCatching {
            dataStore.edit {
                it[configurationKey(userName, widgetId)] = configuration.toLocalModel()
            }
        }.fold(
            onSuccess = { Success(Unit) },
            onFailure = { Failed(WidgetConfigurationError(it)) }
        )

    suspend fun removeConfiguration(userName: UserName, widgetId: WidgetId): Result<Unit, WidgetConfigurationError> =
        runCatching {
            dataStore.edit {
                it.remove(configurationKey(userName, widgetId))
            }
        }.fold(
            onSuccess = { Success(Unit) },
            onFailure = { Failed(WidgetConfigurationError(it)) }
        )


    private fun configurationKey(userName: UserName, widgetId: WidgetId) =
        stringPreferencesKey("${userName.value}:${widgetId.value}")
}