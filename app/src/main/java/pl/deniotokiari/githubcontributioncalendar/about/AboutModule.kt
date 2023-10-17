package pl.deniotokiari.githubcontributioncalendar.about

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val aboutModule = module {
    viewModelOf(::AboutViewModel)
}