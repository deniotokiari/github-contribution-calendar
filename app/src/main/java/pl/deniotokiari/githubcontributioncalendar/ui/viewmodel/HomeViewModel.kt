package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.Logger
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetConfiguration
import pl.deniotokiari.githubcontributioncalendar.data.model.WidgetId
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllContributionsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetAllWidgetsConfigurationsUseCase
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.UpdateAllWidgetsUseCase

class HomeViewModel(
    private val getAllWidgetsConfigurationsUseCase: GetAllWidgetsConfigurationsUseCase,
    private val getAllContributionsUseCase: GetAllContributionsUseCase,
    private val updateAllWidgetsUseCase: UpdateAllWidgetsUseCase,
    private val appAnalytics: AppAnalytics,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState>
        get() = _uiState

    init {
        appAnalytics.trackHomeView()

        viewModelScope.launch(Dispatchers.Default) {
            getAllWidgetsConfigurationsUseCase(Unit).collect { result ->
                result.fold(
                    success = { items ->
                        val configurations = items.associate { (userName, widgetId, config) ->
                            (widgetId to userName) to config
                        }

                        if (configurations.isEmpty()) {
                            _uiState.update { UiState.Empty }
                        } else {
                            _uiState.update { state ->
                                (state as? UiState.Content)?.copy(
                                    configurations = configurations,
                                ) ?: UiState.Content(
                                    configurations = configurations,
                                    contributions = emptyMap(),
                                    refreshing = false,
                                )
                            }
                        }
                    },
                    failed = { error ->
                        logger.error(error.throwable)

                        _uiState.update { UiState.Empty }
                    },
                )
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            getAllContributionsUseCase(Unit).collect { result ->
                result.fold(
                    success = { items ->
                        val contributions = items.associate { (userName, contributions) ->
                            userName to contributions
                        }

                        if (contributions.isEmpty()) {
                            _uiState.update { UiState.Empty }
                        } else {
                            _uiState.update { state ->
                                (state as? UiState.Content)?.copy(
                                    contributions = contributions,
                                ) ?: UiState.Content(
                                    contributions = contributions,
                                    configurations = emptyMap(),
                                    refreshing = false,
                                )
                            }
                        }
                    },
                    failed = { error ->
                        logger.error(error.throwable)

                        _uiState.update { UiState.Empty }
                    },
                )
            }
        }
    }

    fun refreshUsersContributions() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { state ->
                (state as? UiState.Content)?.copy(refreshing = true) ?: state
            }

            val size = updateAllWidgetsUseCase(Unit).successOrNull()?.value

            appAnalytics.trackHomeRefresh(size ?: 0)

            _uiState.update { state ->
                (state as? UiState.Content)?.copy(refreshing = false) ?: state
            }

        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data object Empty : UiState
        data class Content(
            val configurations: Map<Pair<WidgetId, UserName>, WidgetConfiguration>,
            val contributions: Map<UserName, Contributions>,
            val refreshing: Boolean,
        ) : UiState

        data class Widget(
            val name: String,
            val widgetId: Int,
            val config: WidgetConfiguration,
            val contributions: Contributions
        )

        val Content.items: List<Widget>
            get() = configurations.map { (key, value) ->
                val (widgetId, userName) = key
                val contributions = contributions[userName]

                Widget(
                    name = userName.value,
                    widgetId = widgetId.value,
                    config = value,
                    contributions = contributions ?: Contributions(emptyList()),
                )
            }

    }
}
