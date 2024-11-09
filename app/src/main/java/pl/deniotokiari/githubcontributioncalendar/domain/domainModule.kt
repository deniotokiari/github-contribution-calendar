package pl.deniotokiari.githubcontributioncalendar.domain

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllWidgetsConfigurationsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllWidgetsConfigurationsWithContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetSupportEmailUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetWidgetsConfigurationsWithContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.RemoveWidgetDataUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.SetUpWidgetUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateAllWidgetsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateWidgetConfigurationUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateWidgetContributionUseCase

val domainModule = module {
    factoryOf(::SetUpWidgetUseCase)
    factoryOf(::UpdateAllWidgetsUseCase)
    factoryOf(::GetAllWidgetsConfigurationsWithContributionsUseCase)
    factoryOf(::GetSupportEmailUseCase)
    factoryOf(::GetWidgetsConfigurationsWithContributionsUseCase)
    factoryOf(::UpdateWidgetConfigurationUseCase)
    factoryOf(::UpdateWidgetContributionUseCase)
    factoryOf(::RemoveWidgetDataUseCase)
    factoryOf(::GetAllWidgetsConfigurationsUseCase)
    factoryOf(::GetAllContributionsUseCase)
}
