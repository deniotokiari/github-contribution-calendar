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
    fun getBlocks(user: String): Flow<List<Int>> = flow {
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

        emit(result)
    }
}