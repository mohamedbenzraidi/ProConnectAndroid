package com.mohamedbenzraidi.proconnect.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.domain.repository.ConnectionRepository
import com.mohamedbenzraidi.proconnect.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ConversationsViewModel(
    private val messageRepository: MessageRepository,
    private val connectionRepository: ConnectionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationsUiState>(ConversationsUiState.Loading)
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    private val _contacts = MutableStateFlow<List<ConnectionDto>>(emptyList())
    val contacts: StateFlow<List<ConnectionDto>> = _contacts.asStateFlow()

    val currentUserId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            currentUserId.value = tokenManager.userId.firstOrNull()
        }
        loadConversations()
        loadContacts()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = ConversationsUiState.Loading
            messageRepository.getConversations()
                .onSuccess { list ->
                    _uiState.value = ConversationsUiState.Success(list)
                }
                .onFailure { error ->
                    _uiState.value = ConversationsUiState.Error(error.localizedMessage ?: "Erreur de chargement des conversations")
                }
        }
    }

    fun loadContacts() {
        viewModelScope.launch {
            if (currentUserId.value == null) {
                currentUserId.value = tokenManager.userId.firstOrNull()
            }
            connectionRepository.getConnections()
                .onSuccess { list ->
                    // Garder les connexions acceptées
                    _contacts.value = list.filter { it.status == "ACCEPTED" }
                }
        }
    }
}
