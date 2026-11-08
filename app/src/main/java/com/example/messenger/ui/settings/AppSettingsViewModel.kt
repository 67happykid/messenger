package com.example.messenger.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.messenger.data.settings.AppSettings
import com.example.messenger.data.settings.SettingsRepo
import com.example.messenger.ui.theme.ThemeMod
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppSettingsUiState(
    val settings: AppSettings? = null,
    val errorMessage: String? = null
)

class AppSettingsViewModel(
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppSettingsUiState())

    val uiState: StateFlow<AppSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.settings.collect { settings ->
                _uiState.update { currentState ->
                    currentState.copy(settings = settings)
                }
            }
        }
    }

    fun setThemeMod(themeMod: ThemeMod) {
        saveSettings {
            settingsRepo.setThemeMod(themeMod)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        saveSettings {
            settingsRepo.setDynamicColor(enabled)
        }
    }

    fun onErrorShown() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    private fun saveSettings(
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                action()
            } catch (exception: IOException) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Не удалось сохранить настройки"
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            settingsRepo: SettingsRepo
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AppSettingsViewModel(settingsRepo)
            }
        }
    }
}