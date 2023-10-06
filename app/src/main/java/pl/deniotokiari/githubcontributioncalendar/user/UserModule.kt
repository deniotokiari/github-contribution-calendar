package pl.deniotokiari.githubcontributioncalendar.user

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val userModule = module {
    viewModelOf(::UserViewModel)
}