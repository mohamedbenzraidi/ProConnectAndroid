package com.mohamedbenzraidi.proconnect.presentation.auth

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val userId: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}