package pl.deniotokiari.githubcontributioncalendar.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SEPARATOR = ","

class GitHubLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    fun allContributions(): Flow<List<Pair<String, List<Int>>>> = dataStore.data
        .map { prefs ->
            prefs.asMap().map { (key, item) ->
                val userName = key.name
                val colors = (item as? String).decode()

                userName to colors
            }
        }

    fun contributionsByUser(user: String): Flow<List<Int>> = dataStore.data
        .map { prefs ->
            val key = stringPreferencesKey(user)
            val items = prefs[key].decode()

            items
        }

    suspend fun addContributionsForUser(userName: String, items: List<Int>) {
        dataStore.edit {
            val key = stringPreferencesKey(userName)

            it[key] = items.encode()
        }
    }

    suspend fun removeContributionsForUser(userName: String) {
        dataStore.edit {
            val key = stringPreferencesKey(userName)

            it.remove(key)
        }
    }
}

fun String?.decode(): List<Int> {
    return if (isNullOrEmpty()) {
        emptyList()
    } else {
        this.split(SEPARATOR).map { it.toInt() }
    }
}

fun List<Int>.encode(): String = this.joinToString(SEPARATOR)