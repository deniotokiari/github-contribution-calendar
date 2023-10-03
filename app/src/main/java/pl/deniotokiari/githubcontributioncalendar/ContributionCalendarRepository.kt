package pl.deniotokiari.githubcontributioncalendar

import androidx.core.graphics.toColorInt
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pl.deniotokiari.githubcontributioncalendar.service.github.QueryUserContributionQuery

class ContributionCalendarRepository(
    private val apolloClient: ApolloClient
) {
    fun getBlocks(user: String, size: Int, defaultColor: Int): Flow<IntArray> = flow {
        if (size != 0) {
            // TODO calculate from and to for request

            val result = withContext(Dispatchers.IO) {
                val response = apolloClient.query(QueryUserContributionQuery(user)).execute()

                response
                    .data
                    ?.user
                    ?.contributionsCollection
                    ?.contributionCalendar
                    ?.weeks
                    ?.flatMap { it.contributionDays }
                    ?.map { it.color.toColorInt() }

            } ?: emptyList()

            emit(
                IntArray(size) {
                    result.getOrNull(it + result.size - size) ?: defaultColor
                }
            )
        } else {
            emit(IntArray(0))
        }
    }
}