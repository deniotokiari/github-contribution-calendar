package pl.deniotokiari.githubcontributioncalendar.data

import androidx.core.graphics.toColorInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.AppDispatchers
import pl.deniotokiari.githubcontributioncalendar.prefs.GetYearsUseCase

class ContributionCalendarRepository(
    private val gitHubLocalDataSource: GitHubLocalDataSource,
    private val gitHubRemoteDataSource: GitHubRemoteDataSource,
    private val getYearsUseCase: GetYearsUseCase,
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

    suspend fun updateContributionsForUser(userName: String): List<Int> = withContext(io.dispatcher) {
        mutex.withLock {
            val remoteItems = gitHubRemoteDataSource.getUserContribution(
                userName,
                getYearsUseCase(Unit)
            ).map { it.toColorInt() }

            if (remoteItems.isNotEmpty()) {
                gitHubLocalDataSource.addContributionsForUser(userName, remoteItems)
            }

            remoteItems
        }
    }
}