package pl.deniotokiari.githubcontributioncalendar.data.datasource

import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.data.model.ContributionError
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
    ): Result<List<Pair<LocalDateTime, LocalDateTime>>, ContributionError> =
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
            onFailure = { Failed(ContributionError(it)) }
        )

    suspend fun getUserContributions(
        userName: String,
        dateRanges: List<Pair<LocalDateTime, LocalDateTime>>
    ): Result<List<String>, ContributionError> = runCatching {
        dateRanges.map { (from, to) ->
            val result = gitHubService.queryUserContribution(
                username = userName,
                from = formatter.format(from),
                to = formatter.format(to)
            )

            result ?: throw Exception("Null response from git hub service $from - $to")
        }.flatten()
    }.fold(
        onSuccess = { Success(it) },
        onFailure = { Failed(ContributionError(it)) }
    )
}

class GitHubLocalDataSource {
}