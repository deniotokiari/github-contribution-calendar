package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.data.repository.AppConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError
import pl.deniotokiari.githubcontributioncalendar.domain.model.Email

class GetSupportEmailUseCase(
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<Unit, Result<Email, DomainError>> {
    override suspend fun invoke(params: Unit): Result<Email, DomainError> =
        appConfigurationRepository.getSupportEmail()
            .mapFailure { DomainError(it.throwable) }
            .mapSuccess(::Email)
}