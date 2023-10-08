package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository

class RemoveWidgetByUserNameAndWidgetIdUseCase(
    private val contributionCalendarRepository: ContributionCalendarRepository,
    private val configurationRepository: WidgetConfigurationRepository
) : UseCase<RemoveWidgetByUserNameAndWidgetIdUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) {
        contributionCalendarRepository.removeContributionsForUser(params.userName)
        configurationRepository.removeConfiguration(
            widgetId = params.widgetId,
            userName = params.userName
        )
    }

    class Params(
        val userName: String,
        val widgetId: Int
    )
}
