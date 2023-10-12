package pl.deniotokiari.githubcontributioncalendar.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.random.Random

class GitHubRemoteDataSource {
    suspend fun getUserContribution(username: String): List<String> {
        val delay = Random.nextLong(from = 1 * 1000L, until = 2 * 1000L)
        Log.d("LOG", "getUserContribution random for $username with delay ${delay / 1000} seconds")

        val result = mutableListOf<String>()

        // to fit minimal widget size
        repeat(400) {
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