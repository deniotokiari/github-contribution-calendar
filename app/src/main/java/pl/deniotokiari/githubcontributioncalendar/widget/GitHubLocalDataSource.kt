package pl.deniotokiari.githubcontributioncalendar.widget

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

private const val SEPARATOR = ","

class GitHubLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun getUserContribution(username: String): List<Int> {
        val key = stringPreferencesKey(username)

        val result = dataStore.data.first()[key]

        return result?.split(SEPARATOR)?.map { it.toInt() } ?: emptyList()
    }

    suspend fun setUserContribution(username: String, items: List<Int>) {
        val key = stringPreferencesKey(username)

        dataStore.edit {
            it[key] = items.joinToString(separator = SEPARATOR)
        }
    }

    suspend fun removeUserContribution(user: String) {
        val key = stringPreferencesKey(user)

        dataStore.edit {
            it.remove(key)
        }
    }

    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }
}