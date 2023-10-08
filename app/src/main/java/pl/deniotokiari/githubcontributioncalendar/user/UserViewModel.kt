package pl.deniotokiari.githubcontributioncalendar.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.data.ContributionCalendarRepository
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.widget.WidgetConfigurationRepository
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase

class UserViewModel(
    private val user: String,
    private val widgetId: Int,
    contributionCalendarRepository: ContributionCalendarRepository,
    private val configurationRepository: WidgetConfigurationRepository,
    private val updateWidgetConfigurationByWidgetIdAndUserNameUseCase: UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase
) : ViewModel() {
    val uiState: StateFlow<UiState> = combine(
        configurationRepository.configurationByWidgetIdAndUserName(widgetId = widgetId, userName = user),
        contributionCalendarRepository.contributionsByUser(user = user)
    ) { config, contributions ->
        if (contributions.isNotEmpty()) {
            UiState(
                user = UiState.User(
                    user = user,
                    colors = contributions.toIntArray()
                ),
                loading = false,
                config = config
            )
        } else {
            UiState.default(user = user)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default(user).copy(loading = true))

    fun updateBlockSize(value: Int) {
        viewModelScope.launch {
            updateWidgetConfigurationByWidgetIdAndUserNameUseCase(
                UpdateWidgetConfigurationByWidgetIdAndUserNameUseCase.Params(
                    widgetId = widgetId,
                    userName = user,
                    config = uiState.value.config.copy(blockSize = value)
                )
            )
        }
    }

    data class UiState(
        val user: User,
        val loading: Boolean,
        val config: WidgetConfiguration
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
                loading = false,
                config = WidgetConfiguration.default()
            )
        }
    }
}