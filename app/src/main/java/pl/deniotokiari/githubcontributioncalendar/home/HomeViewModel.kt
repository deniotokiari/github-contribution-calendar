package pl.deniotokiari.githubcontributioncalendar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.deniotokiari.githubcontributioncalendar.widget.data.ContributionCalendarRepository

class HomeViewModel(
    contributionCalendarRepository: ContributionCalendarRepository
) : ViewModel() {
    val uiState: StateFlow<UiState> = contributionCalendarRepository.getUsersWithContributions().map { map ->
        UiState(
            items = map.map { (user, items) -> UiState.User(name = user, colors = items.toIntArray()) },
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default().copy(loading = true))

    data class UiState(
        val items: List<User>,
        val loading: Boolean
    ) {
        data class User(
            val name: String,
            val colors: IntArray
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as User

                if (name != other.name) return false
                if (!colors.contentEquals(other.colors)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
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