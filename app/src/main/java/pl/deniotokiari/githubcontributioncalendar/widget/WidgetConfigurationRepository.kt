package pl.deniotokiari.githubcontributioncalendar.widget

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers
import pl.deniotokiari.githubcontributioncalendar.etc.BlocksBitmapCreator

class WidgetConfigurationRepository(
    private val dataStore: DataStore<Preferences>,
    private val io: AppDispatchers
) {
    suspend fun addConfiguration(widgetId: Int, userName: String, config: WidgetConfiguration) =
        withContext(io.dispatcher) {
            val key = widgetIdAndUserNameToStringPreferencesKey(widgetId, userName)
            dataStore.edit {
                it[key] = config.encode()
            }
        }

    suspend fun removeConfiguration(widgetId: Int, userName: String) = withContext(io.dispatcher) {
        val key = widgetIdAndUserNameToStringPreferencesKey(widgetId, userName)

        dataStore.edit {
            it.remove(key)
        }
    }

    fun configurations(): Flow<List<Pair<Pair<Int, String>, WidgetConfiguration>>> = dataStore.data.map {
        val map = it.asMap()
        val result = mutableListOf<Pair<Pair<Int, String>, WidgetConfiguration>>()

        map.forEach { (key, item) ->
            val keySplit = key.name.split(":")
            val widgetId: Int = keySplit[0].toInt()
            val userName: String = keySplit[1]
            val config = WidgetConfiguration.decode(item as String)

            result.add((widgetId to userName) to config)
        }

        result
    }.flowOn(io.dispatcher)

    fun configurationByWidgetIdAndUserName(
        widgetId: Int,
        userName: String
    ): Flow<WidgetConfiguration> = dataStore.data.map {
        val key = widgetIdAndUserNameToStringPreferencesKey(widgetId, userName)
        val value = it[key]

        if (value != null) {
            WidgetConfiguration.decode(value)
        } else {
            WidgetConfiguration.default()
        }
    }
        .catch {
            Log.d("LOG", "WidgetConfigurationRepository.configurationByWidgetIdAndUserName ${it.message}")
        }

    private fun widgetIdAndUserNameToStringPreferencesKey(widgetId: Int, userName: String) =
        stringPreferencesKey("$widgetId:$userName")
}

data class WidgetConfiguration(
    val blockSize: Int,
    val padding: Int,
    val opacity: Int
) {
    fun encode() = "$blockSize:$padding:$opacity"

    companion object {
        fun default() = WidgetConfiguration(
            blockSize = BlocksBitmapCreator.DEFAULT_BLOCK_SIZE,
            padding = BlocksBitmapCreator.DEFAULT_PADDING,
            opacity = BlocksBitmapCreator.DEFAULT_OPACITY
        )

        fun decode(value: String): WidgetConfiguration {
            val items = value.split(":")

            return WidgetConfiguration(
                blockSize = items[0].toInt(),
                padding = items[1].toInt(),
                opacity = items[2].toInt()
            )
        }
    }
}