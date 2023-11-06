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
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateAllWidgetsUseCase

class HomeViewModel(
    contributionCalendarRepository: ContributionCalendarRepository,
    widgetConfigurationRepository: WidgetConfigurationRepository,
    private val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    private val appAnalytics: AppAnalytics
) : ViewModel() {
    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<UiState> = combine(
        widgetConfigurationRepository.configurations(),
        contributionCalendarRepository.allContributions()
    ) { configurations, contributions ->
        val items = mutableListOf<UiState.User>()

        if (contributions.isNotEmpty()) {
            repeat(configurations.size) { index ->
                val (widgetIdAndUserName, config) = configurations[index]
                val (widgetId, userName) = widgetIdAndUserName
                val result = contributions.firstOrNull { (user, _) -> user == userName }
                val colors = result?.second

                if (colors != null) {
                    items.add(
                        UiState.User(
                            name = userName,
                            widgetId = widgetId,
                            config = config,
                            colors = colors.toIntArray()
                        )
                    )
                }
            }
        }

        UiState(
            items = items,
            loading = false,
            refreshing = false
        )
    }.combine(_refreshing) { state, refreshing -> state.copy(refreshing = refreshing) }
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default().copy(loading = true))

    init {
        appAnalytics.trackHomeView()
    }

    fun refreshUsersContributions() {
        viewModelScope.launch {
            _refreshing.value = true

            val size = updateAllWidgetsUseCase(Unit)

            appAnalytics.trackHomeRefresh(size)

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
        )

        companion object {
            fun default() = UiState(
                items = emptyList(),
                loading = false,
                refreshing = false
            )
        }
    }
}