package pl.deniotokiari.githubcontributioncalendar

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DevRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val widgetUpdateCountKey = intPreferencesKey("widgetUpdateCountKey")

    suspend fun incrementWidgetUpdateCount() {
        dataStore.edit {
            it[widgetUpdateCountKey] = widgetUpdateCount().first() + 1
        }
    }

    fun widgetUpdateCount(): Flow<Int> = dataStore.data.map { it[widgetUpdateCountKey] ?: 0 }
}