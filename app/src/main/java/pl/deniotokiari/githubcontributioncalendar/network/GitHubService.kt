package pl.deniotokiari.githubcontributioncalendar.network

import com.apollographql.apollo3.ApolloClient
import pl.deniotokiari.githubcontributioncalendar.service.github.QueryUserContributionQuery

class GitHubService(
    private val apolloClient: ApolloClient
) {
    suspend fun queryUserContribution(username: String): List<String>? = apolloClient
        .query(QueryUserContributionQuery(username))
        .execute()
        .data
        ?.user
        ?.contributionsCollection
        ?.contributionCalendar
        ?.weeks
        ?.flatMap { it.contributionDays }
        ?.map { it.color }
}