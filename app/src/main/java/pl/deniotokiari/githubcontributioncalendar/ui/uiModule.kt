package pl.deniotokiari.githubcontributioncalendar.ui

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.AppWidgetConfigurationViewModel
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.HomeViewModel
import pl.deniotokiari.githubcontributioncalendar.ui.viewmodel.UserViewModel

val uiModule = module {
    viewModelOf(::AppWidgetConfigurationViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::UserViewModel)
}