package pl.deniotokiari.githubcontributioncalendar.widget.data

import android.util.Log
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

class GitHubRemoteDataSource(
    private val gitHubService: GitHubService
) {
    suspend fun getUserContribution(username: String): List<String> {
        Log.d("LOG", "getUserContribution remote")
        val years = 3
        val now = LocalDateTime.now()
        val fromItems = Array<Pair<LocalDateTime, LocalDateTime>>(years) {
            val from = now.plusYears(-(years - it - 1).toLong()).withDayOfYear(1).with(LocalTime.MIN)
            val to = if (it + 1 == years) {
                now
            } else {
                from.plusYears(1).with(LocalTime.MAX).plusDays(-1)
            }

            from to to
        }

        return fromItems.mapNotNull { (from, to) ->
            gitHubService.queryUserContribution(
                username = username,
                from = formatter.format(from),
                to = formatter.format(to)
            )
        }.flatten()
    }
}