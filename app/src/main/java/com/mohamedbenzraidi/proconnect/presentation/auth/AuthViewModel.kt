package com.mohamedbenzraidi.proconnect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.remote.dto.Role
import com.mohamedbenzraidi.proconnect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> = authRepository.token
        .map { token -> !token.isNullOrEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Veuillez remplir tous les champs")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess { response ->
                    _uiState.value = AuthUiState.Success(response.userId)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Erreur de connexion")
                }
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String, role: Role) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Veuillez remplir tous les champs")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(firstName, lastName, email, password, role)
                .onSuccess { response ->
                    _uiState.value = AuthUiState.Success(response.userId)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Erreur d'inscription")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }
}
