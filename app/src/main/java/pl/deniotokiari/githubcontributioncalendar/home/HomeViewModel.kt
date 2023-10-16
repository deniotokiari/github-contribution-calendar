package pl.deniotokiari.githubcontributioncalendar.home

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
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateAllWidgetsUseCase

class HomeViewModel(
    private val contributionCalendarRepository: ContributionCalendarRepository,
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

        repeat(configurations.size) { index ->
            val (widgetIdAndUserName, config) = configurations[index]
            val (widgetId, userName) = widgetIdAndUserName
            val (_, colors) = contributions.first { (user, _) -> user == userName }

            items.add(
                UiState.User(
                    name = userName,
                    widgetId = widgetId,
                    config = config,
                    colors = colors.toIntArray()
                )
            )
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

            val size = contributionCalendarRepository.updateAllContributions()

            updateAllWidgetsUseCase(Unit)

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
            val colors: IntArray
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as User

                if (name != other.name) return false
                if (widgetId != other.widgetId) return false
                if (config != other.config) return false
                if (!colors.contentEquals(other.colors)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + widgetId
                result = 31 * result + config.hashCode()
                result = 31 * result + colors.contentHashCode()

                return result
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