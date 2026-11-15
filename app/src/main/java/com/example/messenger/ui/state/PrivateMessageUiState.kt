package com.example.messenger.ui.state

import com.example.messenger.ui.screens.ChatMessage
data class PrivateMessagesUiState(
    val messages: List<ChatMessage> = emptyList(),
    val matchingMessageIds: Set<String> = emptySet(),
    val messageQuery: String = "",
    val searchQuery: String = ""
)