package pl.deniotokiari.githubcontributioncalendar.data

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AndroidBitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.BitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AndroidBitmapRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.BitmapRepository
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService

val dataModule = module {
    singleOf(::GitHubService)
    single { GitHubLocalDataSource(get(qualifier = named("contribution"))) }
    singleOf(::GitHubRemoteDataSource)
    singleOf(::ContributionCalendarRepository)

    //////
    factoryOf(::AndroidBitmapDataSource) bind BitmapDataSource::class
    factoryOf(::AndroidBitmapRepository) bind BitmapRepository::class
    //factoryOf(::GitHubRemoteDataSource)
}