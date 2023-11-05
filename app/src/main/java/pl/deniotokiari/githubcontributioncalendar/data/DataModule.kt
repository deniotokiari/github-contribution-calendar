package pl.deniotokiari.githubcontributioncalendar.data

import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AndroidBitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AppConfigurationRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.BitmapDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubLocalDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.GitHubRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetConfigurationDataStore
import pl.deniotokiari.githubcontributioncalendar.data.datasource.WidgetDataSource
import pl.deniotokiari.githubcontributioncalendar.data.repository.AndroidBitmapRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.BitmapRepository
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository

val dataModule = module {
    // dataSource
    factoryOf(::AppConfigurationRemoteDataSource)
    factoryOf(::AndroidBitmapDataSource) bind BitmapDataSource::class
    factoryOf(::GitHubRemoteDataSource)
    factory { GitHubLocalDataSource(get(named("contribution"))) }
    factory { WidgetConfigurationDataStore(get(named("widgetConfiguration"))) }
    factoryOf(::WidgetDataSource)

    // repository
    factoryOf(::AppConfigurationRepository)
    factoryOf(::AndroidBitmapRepository) bind BitmapRepository::class
    factoryOf(::ContributionsRepository)
}