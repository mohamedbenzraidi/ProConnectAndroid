package com.mohamedbenzraidi.proconnect.presentation.connections

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.UserSearchDto

sealed interface NetworkUiState {
    data object Loading : NetworkUiState
    data class Success(
        val searchResults: List<UserSearchDto> = emptyList(),
        val connections: List<ConnectionDto> = emptyList()
    ) : NetworkUiState
    data class Error(val message: String) : NetworkUiState
}
