package pl.deniotokiari.githubcontributioncalendar.data.repository

import kotlinx.coroutines.flow.Flow
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubLocalDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.ContributionsError

class ContributionsRepository(
    private val gitHubRemoteDataSource: GitHubRemoteDataSource,
    private val gitHubLocalDataSource: GitHubLocalDataSource,
    private val years: () -> Int
) {
    fun allContributions(): Flow<Result<List<Pair<String, Contributions>>, ContributionsError>> =
        gitHubLocalDataSource.allContributions()

    fun contributions(userName: String): Flow<Result<Contributions, ContributionsError>> =
        gitHubLocalDataSource.contributions(userName)

    suspend fun removeContributions(userName: String): Result<Unit, ContributionsError> =
        gitHubLocalDataSource.removeContributions(userName)

    suspend fun updateContributions(userName: String): Result<Contributions, ContributionsError> =
        gitHubRemoteDataSource.getDateRangesFor(years()).flatMap {
            gitHubRemoteDataSource.getUserContributions(userName, it)
        }.mapSuccess { contributions ->
            if (contributions.colors.isNotEmpty()) {
                gitHubLocalDataSource.addContributions(userName, contributions)
            }

            contributions
        }
}