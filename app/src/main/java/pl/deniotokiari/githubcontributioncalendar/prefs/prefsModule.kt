package pl.deniotokiari.githubcontributioncalendar.prefs

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val prefsModule = module {
    single { AppConfigurationRemoteDataSource().apply(AppConfigurationRemoteDataSource::init) }
    singleOf(::AppConfigurationRepository)
    factoryOf(::GetRepeatIntervalUseCase)
    factoryOf(::GetSupportEmailUseCase)
    factoryOf(::GetYearsUseCase)
}