package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.mapFailure
import pl.deniotokiari.githubcontributioncalendar.data.model.BlockSize
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.Opacity
import pl.deniotokiari.githubcontributioncalendar.data.model.Padding
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetIdentifiers
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetWidgetsConfigurationsWithContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateWidgetConfigurationUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateWidgetContributionUseCase

class UserViewModel(
    private val user: String,
    private val widgetId: Int,
    private val appAnalytics: AppAnalytics,
    private val updateWidgetConfigurationUseCase: UpdateWidgetConfigurationUseCase,
    private val updateWidgetContributionUseCase: UpdateWidgetContributionUseCase,
    getWidgetsConfigurationsWithContributionsUseCase: GetWidgetsConfigurationsWithContributionsUseCase,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState.default(user = user))
    val uiState: StateFlow<UiState>
        get() = _uiState
    private var updateWidgetConfigurationJob: Job? = null

    init {
        appAnalytics.trackUserView(user)

        viewModelScope.launch(Dispatchers.Default) {
            getWidgetsConfigurationsWithContributionsUseCase(
                WidgetIdentifiers(
                    userName = UserName(user),
                    widgetId = WidgetId(widgetId),
                ),
            ).collect { result ->
                result.fold(
                    success = { configurationWithContributions ->
                        _uiState.update { state ->
                            state.copy(
                                contributions = configurationWithContributions.contributions,
                                config = configurationWithContributions.configuration,
                            )
                        }
                    },
                    failed = { error ->
                        logger.error(error.throwable)

                        _uiState.update { it.copy(refreshing = false) }
                    },
                )
            }
        }
    }

    fun refreshUserContribution() {
        viewModelScope.launch(Dispatchers.Default) {
           _uiState.update { it.copy(refreshing = true) }

            updateWidgetContributionUseCase(UserName(user)).mapFailure { logger.error(it.throwable) }

            appAnalytics.trackUserRefresh(user)

            _uiState.update { it.copy(refreshing = false) }
        }
    }

    fun updateOpacity(value: Int) {
        updateWidgetConfiguration(uiState.value.config.copy(opacity = Opacity(value)))
    }

    fun updatePadding(value: Int) {
        updateWidgetConfiguration(uiState.value.config.copy(padding = Padding(value)))
    }

    fun updateBlockSize(value: Int) {
        updateWidgetConfiguration(uiState.value.config.copy(blockSize = BlockSize(value)))
    }

    private fun updateWidgetConfiguration(configuration: WidgetConfiguration) {
        _uiState.value = _uiState.value.copy(config = configuration)

        updateWidgetConfigurationJob?.cancel()

        updateWidgetConfigurationJob = viewModelScope.launch(Dispatchers.Default) {
            delay(300L)

            updateWidgetConfigurationUseCase(
                UpdateWidgetConfigurationUseCase.Params(
                    widgetIdentifiers = WidgetIdentifiers(
                        userName = UserName(user),
                        widgetId = WidgetId(widgetId)
                    ),
                    widgetConfiguration = _uiState.value.config
                )
            ).mapFailure { logger.error(it.throwable) }

            appAnalytics.trackWidgetConfigUpdate(user, _uiState.value.config)
        }
    }

    data class UiState(
        val userName: UserName,
        val contributions: Contributions,
        val config: WidgetConfiguration,
        val refreshing: Boolean
    ) {


        companion object {
            fun default(user: String) = UiState(
                userName = UserName(user),
                contributions = Contributions(emptyList()),
                config = WidgetConfiguration.default(),
                refreshing = false
            )
        }
    }
}
