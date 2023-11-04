package pl.deniotokiari.githubcontributioncalendar.data.repository

import pl.deniotokiari.githubcontributioncalendar.core.Failed
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.Success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AppConfigurationRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.AppConfigurationError
import pl.deniotokiari.githubcontributioncalendar.data.model.Year

class AppConfigurationRepository(
    private val appConfigurationRemoteDataSource: AppConfigurationRemoteDataSource
) {
    fun getYears(): Result<Year, AppConfigurationError> = runCatching {
        Year(value = appConfigurationRemoteDataSource.getYears())
    }.fold(
        onSuccess = { Success(it) },
        onFailure = { Failed(AppConfigurationError(it)) }
    )
}