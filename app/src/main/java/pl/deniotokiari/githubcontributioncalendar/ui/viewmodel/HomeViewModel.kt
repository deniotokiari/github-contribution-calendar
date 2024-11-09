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
    private val _refreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(UiState.default().copy(loading = true))
    val uiState: StateFlow<UiState>
        get() = _uiState

    init {
        appAnalytics.trackHomeView()

        viewModelScope.launch(Dispatchers.Default) {
            getAllWidgetsConfigurationsUseCase(Unit).collect { result ->
                result.fold(
                    success = { items ->
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                configurations = items.associate { (userName, widgetId, config) ->
                                    (widgetId to userName) to config
                                },
                            )
                        }
                    },
                    failed = { error ->
                        logger.error(error.throwable)

                        _uiState.update { UiState.default() }
                    },
                )
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            getAllContributionsUseCase(Unit).collect { result ->
                result.fold(
                    success = { items ->
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                contributions = items.associate { (userName, contributions) ->
                                    userName to contributions
                                }
                            )
                        }
                    },
                    failed = { error ->
                        logger.error(error.throwable)

                        _uiState.update { state -> state.copy(loading = false) }
                    },
                )
            }
        }
    }

    fun refreshUsersContributions() {
        viewModelScope.launch(Dispatchers.Default) {
            _refreshing.value = true

            val size = updateAllWidgetsUseCase(Unit).successOrNull()?.value

            appAnalytics.trackHomeRefresh(size ?: 0)

            _refreshing.value = false
        }
    }

    data class UiState(
        val configurations: Map<Pair<WidgetId, UserName>, WidgetConfiguration>,
        val contributions: Map<UserName, Contributions>,
        val loading: Boolean,
        val refreshing: Boolean,
    ) {
        data class Widget(
            val name: String,
            val widgetId: Int,
            val config: WidgetConfiguration,
            val contributions: Contributions
        )

        val items: List<Widget>
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

        companion object {
            fun default() = UiState(
                configurations = emptyMap(),
                contributions = emptyMap(),
                loading = false,
                refreshing = false
            )
        }
    }
}
