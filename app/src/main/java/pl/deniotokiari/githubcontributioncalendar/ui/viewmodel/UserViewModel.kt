package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.fold
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
    getWidgetsConfigurationsWithContributionsUseCase: GetWidgetsConfigurationsWithContributionsUseCase
) : ViewModel() {
    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<UiState> = getWidgetsConfigurationsWithContributionsUseCase(
        WidgetIdentifiers(
            userName = UserName(user),
            widgetId = WidgetId(widgetId)
        )
    ).map {
        it.fold(
            success = { model ->
                UiState(
                    userName = UserName(user),
                    contributions = model.contributions,
                    loading = false,
                    config = model.configuration,
                    refreshing = false
                )
            },
            failed = {
                UiState.default(user)
            }
        )
    }.combine(_refreshing) { state, refreshing ->
        state.copy(refreshing = refreshing)
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default(user).copy(loading = true))

    init {
        appAnalytics.trackUserView(user)
    }

    fun refreshUserContribution() {
        viewModelScope.launch {
            _refreshing.value = true

            updateWidgetContributionUseCase(UserName(user))

            appAnalytics.trackUserRefresh(user)

            _refreshing.value = false
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
        viewModelScope.launch {
            updateWidgetConfigurationUseCase(
                UpdateWidgetConfigurationUseCase.Params(
                    widgetIdentifiers = WidgetIdentifiers(userName = UserName(user), widgetId = WidgetId(widgetId)),
                    widgetConfiguration = configuration
                )
            )

            appAnalytics.trackWidgetConfigUpdate(user, configuration)
        }
    }

    data class UiState(
        val userName: UserName,
        val contributions: Contributions,
        val loading: Boolean,
        val config: WidgetConfiguration,
        val refreshing: Boolean
    ) {


        companion object {
            fun default(user: String) = UiState(
                userName = UserName(user),
                contributions = Contributions(emptyList()),
                loading = false,
                config = WidgetConfiguration.default(),
                refreshing = false
            )
        }
    }
}