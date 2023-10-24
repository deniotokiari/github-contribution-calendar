package pl.deniotokiari.githubcontributioncalendar.prefs

import pl.deniotokiari.githubcontributioncalendar.core.UseCase

class GetYearsUseCase(
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<Unit, Int> {
    override suspend fun invoke(params: Unit): Int = appConfigurationRepository.getYears()
}