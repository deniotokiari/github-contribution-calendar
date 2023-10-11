package pl.deniotokiari.githubcontributioncalendar.data

import androidx.core.graphics.toColorInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers

class ContributionCalendarRepository(
    private val gitHubLocalDataSource: GitHubLocalDataSource,
    private val gitHubRemoteDataSource: GitHubRemoteDataSource,
    private val io: AppDispatchers.IO
) {
    private val mutex: Mutex by lazy { Mutex() }

    fun allContributions(): Flow<List<Pair<String, List<Int>>>> =
        gitHubLocalDataSource.allContributions().flowOn(io.dispatcher)

    fun contributionsByUser(user: String): Flow<List<Int>> =
        gitHubLocalDataSource.contributionsByUser(user)

    suspend fun removeContributionsForUser(userName: String) = withContext(io.dispatcher) {
        gitHubLocalDataSource.removeContributionsForUser(userName)
    }

    suspend fun updateContributionsForUser(userName: String) = withContext(io.dispatcher) {
        mutex.withLock {
            val remoteItems = gitHubRemoteDataSource.getUserContribution(userName).map { it.toColorInt() }

            if (remoteItems.isNotEmpty()) {
                gitHubLocalDataSource.addContributionsForUser(userName, remoteItems)
            }
        }
    }

    suspend fun updateAllContributions(): Int = withContext(io.dispatcher) {
        mutex.withLock {
            val users = gitHubLocalDataSource.getAllUsers()

            users.forEach {
                launch {
                    val items = gitHubRemoteDataSource.getUserContribution(it).map { it.toColorInt() }

                    if (items.isNotEmpty()) {
                        gitHubLocalDataSource.addContributionsForUser(it, items)
                    }
                }
            }

            users.size
        }
    }
}