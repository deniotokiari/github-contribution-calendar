package pl.deniotokiari.githubcontributioncalendar.widget

import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import pl.deniotokiari.githubcontributioncalendar.widget.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.data.GitHubLocalDataSource
import pl.deniotokiari.githubcontributioncalendar.widget.data.GitHubRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.SetUserNameToWidgetUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateWidgetByIdUseCase

val widgetModule = module {
    singleOf(::GitHubService)
    singleOf(::GitHubLocalDataSource)
    singleOf(::GitHubRemoteDataSource)
    singleOf(::ContributionCalendarRepository)
    workerOf(::UpdateAppWidgetWorker)
    factoryOf(::SetUserNameToWidgetUseCase)
    factoryOf(::UpdateWidgetByIdUseCase)
}