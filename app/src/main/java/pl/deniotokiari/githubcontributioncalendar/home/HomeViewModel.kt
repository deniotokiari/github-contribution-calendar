package pl.deniotokiari.githubcontributioncalendar.home

import androidx.compose.ui.tooling.data.EmptyGroup.data
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository

class HomeViewModel(
    contributionCalendarRepository: ContributionCalendarRepository,
    widgetConfigurationRepository: WidgetConfigurationRepository
) : ViewModel() {
    val uiState: StateFlow<UiState> = combine(
        contributionCalendarRepository.allContributions(),
        widgetConfigurationRepository.configurations()
    ) { contributions, configurations ->
        UiState(
            items = 
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default().copy(loading = true))

    /*contributionCalendarRepository.allContributions().map { map ->
        UiState(
            items = map.map { (user, items) -> UiState.User(name = user, colors = items.toIntArray()) },
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default().copy(loading = true))*/

    data class UiState(
        val items: List<User>,
        val loading: Boolean
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
                loading = false
            )
        }
    }
}