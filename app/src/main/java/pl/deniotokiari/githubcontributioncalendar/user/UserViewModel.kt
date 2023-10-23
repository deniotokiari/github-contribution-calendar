package pl.deniotokiari.githubcontributioncalendar.user

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
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.SetWidgetConfigUseCase
import pl.deniotokiari.githubcontributioncalendar.widget.usecase.UpdateWidgetByUserNameAndWidgetIdUseCase

class UserViewModel(
    private val user: String,
    private val widgetId: Int,
    private val contributionCalendarRepository: ContributionCalendarRepository,
    configurationRepository: WidgetConfigurationRepository,
    private val setWidgetConfigUseCase: SetWidgetConfigUseCase,
    private val updateWidgetByUserNameAndWidgetIdUseCase: UpdateWidgetByUserNameAndWidgetIdUseCase,
    private val appAnalytics: AppAnalytics
) : ViewModel() {
    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
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
                config = config,
                refreshing = false
            )
        } else {
            UiState.default(user = user)
        }
    }.combine(_refreshing) { state, refreshing ->
        state.copy(refreshing = refreshing)
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = UiState.default(user).copy(loading = true))

    init {
        appAnalytics.trackUserView(user)
    }

    fun refreshUserContribution() {
        viewModelScope.launch {
            _refreshing.value = true

            contributionCalendarRepository.updateContributionsForUser(user)
            updateWidgetByUserNameAndWidgetIdUseCase(
                UpdateWidgetByUserNameAndWidgetIdUseCase.Params(
                    widgetId = widgetId,
                    userName = user
                )
            )

            appAnalytics.trackUserRefresh()

            _refreshing.value = false
        }
    }

    fun updateOpacity(value: Int) {
        viewModelScope.launch {
            val config = uiState.value.config.copy(opacity = value)

            setWidgetConfigUseCase(
                SetWidgetConfigUseCase.Params(
                    widgetId = widgetId,
                    userName = user,
                    config = config
                )
            )
        }
    }

    fun updatePadding(value: Int) {
        viewModelScope.launch {
            val config = uiState.value.config.copy(padding = value)

            setWidgetConfigUseCase(
                SetWidgetConfigUseCase.Params(
                    widgetId = widgetId,
                    userName = user,
                    config = config
                )
            )

            appAnalytics.trackWidgetConfigUpdate(user, config)
        }
    }

    fun updateBlockSize(value: Int) {
        viewModelScope.launch {
            val config = uiState.value.config.copy(blockSize = value)

            setWidgetConfigUseCase(
                SetWidgetConfigUseCase.Params(
                    widgetId = widgetId,
                    userName = user,
                    config = config
                )
            )

            appAnalytics.trackWidgetConfigUpdate(user, config)
        }
    }

    data class UiState(
        val user: User,
        val loading: Boolean,
        val config: WidgetConfiguration,
        val refreshing: Boolean
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
                config = WidgetConfiguration.default(),
                refreshing = false
            )
        }
    }
}