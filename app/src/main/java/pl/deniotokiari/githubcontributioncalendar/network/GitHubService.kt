package pl.deniotokiari.githubcontributioncalendar.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import pl.deniotokiari.githubcontributioncalendar.service.github.QueryUserContributionQuery

class GitHubService(
    private val apolloClient: ApolloClient
) {
    suspend fun queryUserContribution(
        username: String,
        from: String,
        to: String
    ): List<String>? = apolloClient
        .query(
            QueryUserContributionQuery(
                userName = username,
                from = Optional.present(from),
                to = Optional.present(to)
            )
        )
        .execute()
        .data
        ?.user
        ?.contributionsCollection
        ?.contributionCalendar
        ?.weeks
        ?.flatMap { it.contributionDays }
        ?.map { it.color }
}