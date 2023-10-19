package pl.deniotokiari.githubcontributioncalendar.prefs

import pl.deniotokiari.githubcontributioncalendar.core.UseCase

class GetRepeatIntervalUseCase(
    private val appConfigurationRepository: AppConfigurationRepository
) : UseCase<Unit, Long> {
    override suspend fun invoke(params: Unit): Long = appConfigurationRepository.getRepeatInterval()
}