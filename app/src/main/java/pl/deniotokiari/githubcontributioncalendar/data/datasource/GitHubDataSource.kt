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
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.ContributionsError
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

class GitHubRemoteDataSource(
    private val gitHubService: GitHubService
) {
    fun getDateRangesFor(
        years: Int,
        now: LocalDateTime = LocalDateTime.now()
    ): Result<List<Pair<LocalDateTime, LocalDateTime>>, ContributionsError> =
        runCatching {
            Array<Pair<LocalDateTime, LocalDateTime>>(years) {
                val from = now.plusYears(-(years - it - 1).toLong()).withDayOfYear(1).with(LocalTime.MIN)
                val to = if (it + 1 == years) {
                    now
                } else {
                    from.plusYears(1).with(LocalTime.MAX).plusDays(-1)
                }

                Pair(from, to)
            }
        }.fold(
            onSuccess = { Success(it.toList()) },
            onFailure = { Failed(ContributionsError(it)) }
        )

    suspend fun getUserContributions(
        userName: String,
        dateRanges: List<Pair<LocalDateTime, LocalDateTime>>
    ): Result<Contributions, ContributionsError> = runCatching {
        dateRanges.map { (from, to) ->
            val result = gitHubService.queryUserContribution(
                username = userName,
                from = formatter.format(from),
                to = formatter.format(to)
            )

            result ?: throw Exception("Null response from git hub service $from - $to")
        }.flatten()
    }.fold(
        onSuccess = { Success(Contributions(it)) },
        onFailure = { Failed(ContributionsError(it)) }
    )
}

class GitHubLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    fun allContributions(): Flow<Result<List<Pair<String, Contributions>>, ContributionsError>> =
        dataStore.data.map { prefs ->
            runCatching {
                prefs.asMap().map { (key, item) ->
                    val userName = key.name
                    val contributions = Contributions.fromLocalModel(item)

                    userName to contributions
                }
            }.fold(
                onSuccess = { Success(it) },
                onFailure = { Failed(ContributionsError(it)) }
            )
        }

    fun contributions(userName: String): Flow<Result<Contributions, ContributionsError>> = dataStore.data.map {
        runCatching { Contributions.fromLocalModel(it[stringPreferencesKey(userName)]) }.fold(
            onSuccess = { Success(it) },
            onFailure = { Failed(ContributionsError(it)) }
        )
    }

    suspend fun addContributions(userName: String, contributions: Contributions): Result<Unit, ContributionsError> =
        runCatching {
            dataStore.edit {
                it[stringPreferencesKey(userName)] = contributions.toLocalModel()
            }
        }.fold(
            onSuccess = { Success(Unit) },
            onFailure = { Failed(ContributionsError(it)) }
        )

    suspend fun removeContributions(userName: String): Result<Unit, ContributionsError> = runCatching {
        dataStore.edit {
            it.remove(stringPreferencesKey(userName))
        }
    }.fold(
        onSuccess = { Success(Unit) },
        onFailure = { Failed(ContributionsError(it)) }
    )
}