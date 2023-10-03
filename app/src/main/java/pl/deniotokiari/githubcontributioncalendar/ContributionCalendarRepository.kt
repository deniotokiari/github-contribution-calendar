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
    fun getBlocks(user: String, size: Int): Flow<IntArray> = flow {
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

        val trimSize = result.size - size

        emit(result.drop(trimSize).toIntArray())
    }
}