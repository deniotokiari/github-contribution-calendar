package pl.deniotokiari.githubcontributioncalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.deniotokiari.githubcontributioncalendar.analytics.AppAnalytics
import pl.deniotokiari.githubcontributioncalendar.core.mapSuccess
import pl.deniotokiari.githubcontributioncalendar.domain.usecase.GetSupportEmailUseCase

class AboutViewModel(
    private val appAnalytics: AppAnalytics,
    private val getSupportEmailUseCase: GetSupportEmailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        appAnalytics.trackAboutView()

        viewModelScope.launch {
            getSupportEmailUseCase(Unit).mapSuccess { email ->
                _uiState.update {
                    UiState.Idle(email = email.value)
                }
            }
        }
    }

    fun onSupportEmailClicked() {
        appAnalytics.trackOpenSupportEmail()
    }

    fun onSupportEmailClickFailed() {
        appAnalytics.trackOpenSupportEmailFailed()
    }

    sealed class UiState {
        object Loading : UiState()
        data class Idle(
            val email: String
        ) : UiState()
    }
}