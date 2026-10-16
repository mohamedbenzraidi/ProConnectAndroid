package com.mohamedbenzraidi.proconnect.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageRepository: MessageRepository,
    val recipientId: String,
    initialConversationId: String? = null,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private var currentConversationId: String? = initialConversationId.takeIf { !it.isNullOrBlank() && it != "none" }

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val currentUserId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            currentUserId.value = tokenManager?.userId?.firstOrNull()
        }
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            val convId = currentConversationId
            if (convId.isNullOrBlank()) {
                _uiState.value = ChatUiState.Success(emptyList())
                return@launch
            }

            _uiState.value = ChatUiState.Loading
            messageRepository.getMessages(convId)
                .onSuccess { list ->
                    _uiState.value = ChatUiState.Success(list)
                }
                .onFailure {
                    _uiState.value = ChatUiState.Success(emptyList())
                }
        }
    }

    fun sendMessage(content: String, onResult: (Boolean, String?) -> Unit) {
        if (content.isBlank()) return

        val myId = currentUserId.value
        val targetRecipient = recipientId.takeIf { it.isNotBlank() && it != "none" && it != myId }
            ?: currentConversationId?.takeIf { it.isNotBlank() && it != "none" && it != myId }
            ?: ""

        if (targetRecipient.isBlank()) {
            onResult(false, "Destinataire invalide. Vous ne pouvez pas envoyer un message à vous-même.")
            return
        }

        viewModelScope.launch {
            messageRepository.sendMessage(targetRecipient, content)
                .onSuccess { sentMessage ->
                    if (!sentMessage.conversationId.isNullOrBlank()) {
                        currentConversationId = sentMessage.conversationId
                    }

                    val currentList = (_uiState.value as? ChatUiState.Success)?.messages ?: emptyList()
                    val updatedList = currentList + sentMessage
                    _uiState.value = ChatUiState.Success(updatedList)

                    onResult(true, null)
                }
                .onFailure { error ->
                    onResult(false, error.localizedMessage ?: "Erreur lors de l'envoi du message")
                }
        }
    }
}
