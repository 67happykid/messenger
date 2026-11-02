package com.example.messenger.ui.screens

import androidx.lifecycle.ViewModel
import com.example.messenger.ui.state.PrivateMessagesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrivateMessagesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PrivateMessagesUiState())
    val uiState = _uiState.asStateFlow()

    fun updateMessageQuery(value: String) {
        _uiState.update { currentState ->
            currentState.copy(messageQuery = value)
        }
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = value, matchingMessageIds = emptySet())
        }
    }

    fun searchMessage() {
        _uiState.update { currentState ->
            currentState.copy(
                matchingMessageIds = if (currentState.searchQuery.isEmpty())
                    emptySet()
                else
                    currentState.messages
                    .filter { it.text.contains(currentState.searchQuery, ignoreCase = true) }
                    .map { it.id }
                    .toSet()
            )
        }
    }

    fun sendMessage() {
        val text = _uiState.value.messageQuery.trim()

        if (text.isBlank()) return

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + ChatMessage(
                    text = text,
                    isFromMe = true
                ),
                messageQuery = ""
            )
        }
    }
}
