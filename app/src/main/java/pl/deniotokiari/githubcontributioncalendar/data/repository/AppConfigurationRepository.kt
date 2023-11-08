package pl.deniotokiari.githubcontributioncalendar.data.repository

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.asFailed
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.datasource.AppConfigurationRemoteDataSource
import pl.deniotokiari.githubcontributioncalendar.data.model.DataError
import pl.deniotokiari.githubcontributioncalendar.data.model.Year

class AppConfigurationRepository(
    private val appConfigurationRemoteDataSource: AppConfigurationRemoteDataSource
) {
    fun getYears(): Result<Year, DataError> = runCatching {
        Year(value = appConfigurationRemoteDataSource.getYears())
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    fun getUpdateInterval(): Result<Long, DataError> = runCatching {
        appConfigurationRemoteDataSource.getRepeatInterval()
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )

    fun getSupportEmail(): Result<String, DataError> = runCatching {
        appConfigurationRemoteDataSource.getSupportEmail()
    }.fold(
        onSuccess = { it.success() },
        onFailure = { it.asFailed(::DataError) }
    )
}