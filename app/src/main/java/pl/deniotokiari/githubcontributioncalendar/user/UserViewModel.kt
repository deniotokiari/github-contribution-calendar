package pl.deniotokiari.githubcontributioncalendar.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.deniotokiari.githubcontributioncalendar.widget.data.ContributionCalendarRepository

class UserViewModel(
    private val user: String,
    contributionCalendarRepository: ContributionCalendarRepository
) : ViewModel() {
    val uiState: StateFlow<UiState> = contributionCalendarRepository.getUserContribution(user).map {
        UiState(
            user = UiState.User(
                user = user,
                colors = it.toIntArray()
            ),
            loading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default(user).copy(loading = true))

    data class UiState(
        val user: User,
        val loading: Boolean
    ) {
        data class User(
            val user: String,
            val colors: IntArray
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as User

                if (user != other.user) return false
                if (!colors.contentEquals(other.colors)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = user.hashCode()
                result = 31 * result + colors.contentHashCode()

                return result
            }
        }

        companion object {
            fun default(user: String) = UiState(
                user = User(
                    user = user,
                    colors = IntArray(0)
                ),
                loading = false
            )
        }
    }
}