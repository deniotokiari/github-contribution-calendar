package pl.deniotokiari.githubcontributioncalendar.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.asFailed
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.Year
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

class GitHubRemoteDataSource(
    private val gitHubService: GitHubService
) {
    fun getDateRangesFor(
        years: Year,
        now: LocalDateTime = LocalDateTime.now()
    ): Result<List<Pair<LocalDateTime, LocalDateTime>>, DataError> =
        runCatching {
            Array<Pair<LocalDateTime, LocalDateTime>>(years.value) {
                val from = now.plusYears(-(years.value - it - 1).toLong()).withDayOfYear(1).with(LocalTime.MIN)
                val to = if (it + 1 == years.value) {
                    now
                } else {
                    from.plusYears(1).with(LocalTime.MAX).plusDays(-1)
                }

                Pair(from, to)
            }
        }.fold(
            onSuccess = { it.toList().success() },
            onFailure = { it.asFailed(::DataError) }
        )

    suspend fun getUserContributions(
        userName: UserName,
        dateRanges: List<Pair<LocalDateTime, LocalDateTime>>
    ): Result<Contributions, DataError> = runCatching {
        dateRanges.map { (from, to) ->
            val result = gitHubService.queryUserContribution(
                username = userName.value,
                from = formatter.format(from),
                to = formatter.format(to)
            )

            result ?: throw Exception("Null response from git hub service $from - $to")
        }.flatten()
    }.fold(
        onSuccess = { Contributions(it).success() },
        onFailure = { it.asFailed(::DataError) }
    )
}

class GitHubLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    fun allContributions(): Flow<Result<List<Pair<String, Contributions>>, DataError>> =
        dataStore.data.map { prefs ->
            runCatching {
                prefs.asMap().map { (key, item) ->
                    val userName = key.name
                    val contributions = Contributions.fromLocalModel(item)

                    userName to contributions
                }
            }.fold(
                onSuccess = { it.success() },
                onFailure = { it.asFailed(::DataError) }
            )
        }

    fun contributions(userName: UserName): Flow<Result<Contributions, DataError>> = dataStore.data.map {
        runCatching { Contributions.fromLocalModel(it[stringPreferencesKey(userName.toString())]) }.fold(
            onSuccess = { it.success() },
            onFailure = { it.asFailed(::DataError) }
        )
    }

    suspend fun addContributions(userName: UserName, contributions: Contributions): Result<Unit, DataError> =
        runCatching {
            dataStore.edit {
                it[stringPreferencesKey(userName.value)] = contributions.toLocalModel()
            }
        }.fold(
            onSuccess = { Unit.success() },
            onFailure = { it.asFailed(::DataError) }
        )

    suspend fun removeContributions(userName: UserName): Result<Unit, DataError> = runCatching {
        dataStore.edit {
            it.remove(stringPreferencesKey(userName.value))
        }
    }.fold(
        onSuccess = { Unit.success() },
        onFailure = { it.asFailed(::DataError) }
    )
}