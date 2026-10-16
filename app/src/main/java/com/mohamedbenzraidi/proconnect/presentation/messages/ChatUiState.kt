package com.mohamedbenzraidi.proconnect.presentation.messages

import com.mohamedbenzraidi.proconnect.data.remote.dto.MessageDto

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val messages: List<MessageDto>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}
