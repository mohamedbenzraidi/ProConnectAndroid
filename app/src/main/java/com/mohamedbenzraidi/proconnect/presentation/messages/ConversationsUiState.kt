package com.mohamedbenzraidi.proconnect.presentation.messages

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConversationDto

sealed interface ConversationsUiState {
    data object Loading : ConversationsUiState
    data class Success(val conversations: List<ConversationDto>) : ConversationsUiState
    data class Error(val message: String) : ConversationsUiState
}
