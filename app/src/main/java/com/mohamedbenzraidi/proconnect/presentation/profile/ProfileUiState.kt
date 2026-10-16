package com.mohamedbenzraidi.proconnect.presentation.profile

import com.mohamedbenzraidi.proconnect.data.remote.dto.ProfileDto

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: ProfileDto) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
