package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.domain.model.WidgetConfigurationWithContributions
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllWidgetsConfigurationsWithContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateAllWidgetsUseCase

class HomeViewModel(
    getAllWidgetsConfigurationsWithContributionsUseCase: GetAllWidgetsConfigurationsWithContributionsUseCase,
    private val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    private val appAnalytics: AppAnalytics
) : ViewModel() {
    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<UiState> =
        getAllWidgetsConfigurationsWithContributionsUseCase(Unit).combine(_refreshing) { contributionsResult, refreshing ->
            contributionsResult.fold(
                success = {
                    UiState(
                        items = it.map(UiState.User::fromWidgetConfigurationWithContributions),
                        loading = false,
                        refreshing = refreshing
                    )
                },
                failed = {
                    UiState.default().copy(refreshing = refreshing)
                }
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default().copy(loading = true))

    init {
        appAnalytics.trackHomeView()
    }

    fun refreshUsersContributions() {
        viewModelScope.launch {
            _refreshing.value = true

            val size = updateAllWidgetsUseCase(Unit).successOrNull()?.value

            appAnalytics.trackHomeRefresh(size ?: 0)

            _refreshing.value = false
        }
    }

    data class UiState(
        val items: List<User>,
        val loading: Boolean,
        val refreshing: Boolean,
    ) {
        data class User(
            val name: String,
            val widgetId: Int,
            val config: WidgetConfiguration,
            val contributions: Contributions
        ) {
            companion object {
                fun fromWidgetConfigurationWithContributions(item: WidgetConfigurationWithContributions): User = User(
                    name = item.userName.value,
                    widgetId = item.widgetId.value,
                    config = item.configuration,
                    contributions = item.contributions
                )
            }
        }

        companion object {
            fun default() = UiState(
                items = emptyList(),
                loading = false,
                refreshing = false
            )
        }
    }
}