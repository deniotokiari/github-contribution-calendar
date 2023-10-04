package pl.deniotokiari.githubcontributioncalendar.widget.data

import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers

private const val SEPARATOR = ","
private const val USERS_KEY = "usersKey"

class ContributionCalendarRepository(
    private val gitHubLocalDataSource: GitHubLocalDataSource,
    private val gitHubRemoteDataSource: GitHubRemoteDataSource,
    private val dataStore: DataStore<Preferences>,
    private val io: AppDispatchers.IO
) {
    private val usersKey = stringPreferencesKey(USERS_KEY)

    fun getBlocks(user: String): Flow<List<Int>> = flow {
        val result = withContext(io.dispatcher) {
            addUser(user)

            var items = gitHubLocalDataSource.getUserContribution(user)

            if (items.isEmpty()) {
                items = gitHubRemoteDataSource.getUserContribution(user)?.map { it.toColorInt() } ?: emptyList()

                gitHubLocalDataSource.setUserContribution(user, items)
            }

            items
        }

        emit(result)
    }
        .catch {
            Log.d("LOG", "error => $it")
            emit(emptyList())
        }

    suspend fun removeBlocksForUser(user: String) {
        removeUser(user)
        gitHubLocalDataSource.removeUserContribution(user)
    }

    suspend fun updateAll() = runCatching {
        withContext(io.dispatcher) {
            getAllUsers().forEach { user ->
                val result = gitHubRemoteDataSource.getUserContribution(user)?.map { it.toColorInt() } ?: emptyList()

                if (result.isNotEmpty()) {
                    gitHubLocalDataSource.setUserContribution(user, result)
                }
            }
        }
    }

    private suspend fun addUser(user: String) {
        val users = getAllUsers().toMutableList()

        if (!users.contains(user)) {
            users.add(user)

            dataStore.edit {
                it[usersKey] = users.joinToString(separator = SEPARATOR)
            }
        }
    }

    private suspend fun removeUser(user: String) {
        val users = getAllUsers().toMutableList()

        users.remove(user)

        dataStore.edit {
            it[usersKey] = users.joinToString(separator = SEPARATOR)
        }
    }

    private suspend fun getAllUsers(): List<String> {
        return dataStore.data.first()[usersKey]?.split(SEPARATOR) ?: emptyList()
    }
}