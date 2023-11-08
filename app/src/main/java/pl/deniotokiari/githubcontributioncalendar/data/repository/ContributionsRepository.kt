package pl.deniotokiari.githubcontributioncalendar.data.repository

import kotlinx.coroutines.flow.Flow
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.flatMap
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubLocalDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.Year

class ContributionsRepository(
    private val gitHubRemoteDataSource: GitHubRemoteDataSource,
    private val gitHubLocalDataSource: GitHubLocalDataSource
) {
    fun allContributions(): Flow<Result<List<Pair<String, Contributions>>, DataError>> =
        gitHubLocalDataSource.allContributions()

    fun contributions(userName: UserName): Flow<Result<Contributions, DataError>> =
        gitHubLocalDataSource.contributions(userName)

    suspend fun removeContributions(userName: UserName): Result<Unit, DataError> =
        gitHubLocalDataSource.removeContributions(userName)

    suspend fun updateContributions(userName: UserName, years: Year): Result<Contributions, DataError> =
        gitHubRemoteDataSource.getDateRangesFor(years).flatMap {
            gitHubRemoteDataSource.getUserContributions(userName, it)
        }.mapSuccess { contributions ->
            if (contributions.colors.isNotEmpty()) {
                gitHubLocalDataSource.addContributions(userName, contributions)
            }

            contributions
        }
}