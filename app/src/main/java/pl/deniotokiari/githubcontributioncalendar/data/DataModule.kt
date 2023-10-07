package pl.deniotokiari.githubcontributioncalendar.data

import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService

val dataModule = module {
    singleOf(::GitHubService)
    single { GitHubLocalDataSource(get(qualifier = named("contribution"))) }
    singleOf(::GitHubRemoteDataSource)
    singleOf(::ContributionCalendarRepository)
}