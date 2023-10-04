package pl.deniotokiari.githubcontributioncalendar.widget.data

import pl.deniotokiari.githubcontributioncalendar.network.GitHubService

class GitHubRemoteDataSource(
    private val gitHubService: GitHubService
) {
    suspend fun getUserContribution(username: String): List<String>? = gitHubService.queryUserContribution(username)
}