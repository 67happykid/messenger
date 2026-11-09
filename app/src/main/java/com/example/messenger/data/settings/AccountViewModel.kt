package com.example.messenger.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.messenger.data.user.DemoUserRepo
import com.example.messenger.ui.screens.AccountUIState
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns
import kotlinx.coroutines.Job

class AccountViewModel(
    private val userRepo: DemoUserRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AccountUIState(
            username = "",
            isLoading = true
        )
    )

    val uiState = _uiState.asStateFlow()

    private var profileJob: Job? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        profileJob?.cancel()

        _uiState.update {
            it.copy(
                isLoading = true,
                loadError = null
            )
        }

        profileJob = viewModelScope.launch {
            try {
                userRepo.profile.collect { profile ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            username = profile.username,
                            email = profile.email,
                            phone = profile.phone,
                            isLoading = false,
                            loadError = null
                        )
                    }
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "Не удалось загрузить профиль"
                    )
                }
            }
        }
    }

    fun openUsernameEditor() {
        _uiState.update { currentState ->
            currentState.copy(
                showUsernameDialog = true,
                usernameDraft = currentState.username,
                usernameError = null
            )
        }
    }

    fun changeUsernameDraft(value: String) {
        if (_uiState.value.isSavingUsername) return

        _uiState.update { currentState ->
            currentState.copy(
                usernameDraft = value,
                usernameError = null
            )
        }
    }

    fun dismissUsernameEditor() {
        if (_uiState.value.isSavingUsername) return

        _uiState.update { currentState ->
            currentState.copy(
                showUsernameDialog = false,
                usernameError = null
            )
        }
    }

    fun saveUsername() {
        val currentState = _uiState.value

        if (currentState.isSavingUsername) return

        val username = currentState.usernameDraft

        if (username.length !in 3..32) {
            _uiState.update {
                it.copy(
                    usernameError = "Введите от 3 до 32 символов"
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isSavingUsername = true,
                usernameError = null
            )
        }

        viewModelScope.launch {
            try {
                userRepo.updateUsername(username)

                _uiState.update {
                    it.copy(
                        username = username,
                        showUsernameDialog = false,
                        isSavingUsername = false
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isSavingUsername = false,
                        usernameError = "Не удалось сохранить имя. Попробуйте ещё раз"
                    )
                }
            }
        }
    }

    fun openEmailEditor() {
        _uiState.update { currentState ->
            currentState.copy(
                showEmailDialog = true,
                emailDraft = currentState.email.orEmpty(),
                emailError = null
            )
        }
    }

    fun changeEmailDraft(value: String) {
        if (_uiState.value.isSavingEmail) return

        _uiState.update { currentState ->
            currentState.copy(
                emailDraft = value,
                emailError = null
            )
        }
    }

    fun dismissEmailEditor() {
        if (_uiState.value.isSavingEmail) return

        _uiState.update { currentState ->
            currentState.copy(
                showEmailDialog = false,
                emailError = null
            )
        }
    }

    fun saveEmail() {
        val currentState = _uiState.value

        if (currentState.isSavingEmail) return

        val email = currentState.emailDraft.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update {
                it.copy(
                    emailError = "Введите корректный адрес электронной почты"
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isSavingEmail = true,
                emailError = null
            )
        }

        viewModelScope.launch {
            try {
                userRepo.updateEmail(email)

                _uiState.update {
                    it.copy(
                        email = email,
                        showEmailDialog = false,
                        isSavingEmail = false
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isSavingEmail = false,
                        emailError = "Не удалось сохранить почту. Попробуйте ещё раз"
                    )
                }
            }
        }
    }

    fun openPhoneEditor() {
        _uiState.update { currentState ->
            currentState.copy(
                showPhoneDialog = true,
                phoneDraft = currentState.phone.orEmpty(),
                phoneError = null
            )
        }
    }

    fun changePhoneDraft(value: String) {
        if (_uiState.value.isSavingPhone) return

        _uiState.update { currentState ->
            currentState.copy(
                phoneDraft = value,
                phoneError = null
            )
        }
    }

    fun dismissPhoneEditor() {
        if (_uiState.value.isSavingPhone) return

        _uiState.update { currentState ->
            currentState.copy(
                showPhoneDialog = false,
                phoneError = null
            )
        }
    }

    fun savePhone() {
        val currentState = _uiState.value

        if (currentState.isSavingPhone) return

        val phone = currentState.phoneDraft.filterNot { character ->
            character.isWhitespace() ||
                    character == '-' ||
                    character == '(' ||
                    character == ')'
        }

        val phonePattern = Regex("""\+[1-9][0-9]{6,14}""")

        if (!phonePattern.matches(phone)) {
            _uiState.update {
                it.copy(
                    phoneError = "Введите номер с + и кодом страны: " +
                            "от 7 до 15 цифр"
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isSavingPhone = true,
                phoneError = null
            )
        }

        viewModelScope.launch {
            try {
                userRepo.updatePhone(phone)

                _uiState.update {
                    it.copy(
                        phone = phone,
                        showPhoneDialog = false,
                        isSavingPhone = false
                    )
                }
            } catch (exception: IOException) {
                _uiState.update {
                    it.copy(
                        isSavingPhone = false,
                        phoneError = "Не удалось сохранить телефон. " +
                                "Попробуйте ещё раз"
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            userRepository: DemoUserRepo
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AccountViewModel(userRepository)
            }
        }
    }
}