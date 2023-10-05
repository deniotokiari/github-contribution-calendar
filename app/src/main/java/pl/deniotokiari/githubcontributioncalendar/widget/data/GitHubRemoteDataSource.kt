package pl.deniotokiari.githubcontributioncalendar.widget.data

import android.util.Log
import kotlinx.coroutines.delay
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private val formatter = DateTimeFormatter.ISO_DATE_TIME
private const val YEARS = 3

interface GitHubRemoteDataSource {
    suspend fun getUserContribution(username: String): List<String>
}

class GitHubRemoteDataSourceImpl(
    private val gitHubService: GitHubService
) : GitHubRemoteDataSource {
    override suspend fun getUserContribution(username: String): List<String> {
        Log.d("LOG", "getUserContribution remote for $username")
        val years = YEARS
        val now = LocalDateTime.now()
        val fromItems = Array<Pair<LocalDateTime, LocalDateTime>>(years) {
            val from = now.plusYears(-(years - it - 1).toLong()).withDayOfYear(1).with(LocalTime.MIN)
            val to = if (it + 1 == years) {
                now
            } else {
                from.plusYears(1).with(LocalTime.MAX).plusDays(-1)
            }

            from and to
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

class RandomBlocksDataSource : GitHubRemoteDataSource {
    override suspend fun getUserContribution(username: String): List<String> {
        val delay = Random.nextLong(from = 10 * 1000L, until = 30 * 1000L)
        Log.d("LOG", "getUserContribution random for $username with delay ${delay / 1000} seconds")

        val result = mutableListOf<String>()

        // to fit minimal widget size
        repeat(6) {
            val value = if (Random.nextBoolean()) {
                "red"
            } else {
                "green"
            }

            result.add(value)
        }

        delay(delay)

        return result
    }
}

private infix fun <A, B> A.and(that: B): Pair<A, B> = Pair(this, that)