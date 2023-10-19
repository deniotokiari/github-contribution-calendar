package pl.deniotokiari.githubcontributioncalendar.prefs

import pl.deniotokiari.githubcontributioncalendar.core.UseCase

class GetSupportEmailUseCase(
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<Unit, String> {
    override suspend fun invoke(params: Unit): String = appConfigurationRepository.getSupportEmail()
}