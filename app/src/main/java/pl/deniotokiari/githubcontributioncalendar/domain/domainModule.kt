package pl.deniotokiari.githubcontributioncalendar.domain

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.SetUpWidgetUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateAllWidgetsUseCase

val domainModule = module {
    factoryOf(::SetUpWidgetUseCase)
    factoryOf(::UpdateAllWidgetsUseCase)
}