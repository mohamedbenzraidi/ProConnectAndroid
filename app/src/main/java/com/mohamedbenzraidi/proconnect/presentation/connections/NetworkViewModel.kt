package com.mohamedbenzraidi.proconnect.presentation.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NetworkViewModel(
    private val connectionRepository: ConnectionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkUiState>(NetworkUiState.Loading)
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    var searchQuery = MutableStateFlow("")
        private set

    val currentUserId = MutableStateFlow<String?>(null)

    private val _sentRequestIds = mutableSetOf<String>()
    val sentRequestIds = MutableStateFlow<Set<String>>(emptySet())
    val sentIdsState: StateFlow<Set<String>> = sentRequestIds.asStateFlow()

    init {
        viewModelScope.launch {
            currentUserId.value = tokenManager.userId.firstOrNull()
            loadData()
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = NetworkUiState.Loading

            val myUserId = currentUserId.value ?: tokenManager.userId.firstOrNull()
            if (myUserId != null) {
                currentUserId.value = myUserId
            }

            val connectionsResult = connectionRepository.getConnections()

            val searchResult = if (searchQuery.value.isNotBlank()) {
                connectionRepository.searchUsers(searchQuery.value)
            } else {
                Result.success(emptyList())
            }

            if (connectionsResult.isSuccess && searchResult.isSuccess) {
                val rawConnections = connectionsResult.getOrDefault(emptyList())
                // Filtrer les auto-invitations (où l'utilisateur s'est envoyé une inv à lui-même)
                val cleanConnections = rawConnections.filter { connection ->
                    val isSelf = connection.senderId == connection.receiverId
                    !isSelf
                }

                val rawSearch = searchResult.getOrDefault(emptyList())
                // Filtrer le profil de l'utilisateur connecté dans la recherche
                val cleanSearch = rawSearch.filter { user ->
                    val isSelf = user.effectiveUserId == myUserId || user.id == myUserId
                    !isSelf
                }

                _uiState.value = NetworkUiState.Success(
                    searchResults = cleanSearch,
                    connections = cleanConnections
                )
            } else {
                val errorMsg = connectionsResult.exceptionOrNull()?.localizedMessage
                    ?: searchResult.exceptionOrNull()?.localizedMessage
                    ?: "Erreur de chargement du réseau"
                _uiState.value = NetworkUiState.Error(errorMsg)
            }
        }
    }

    fun searchUsers(query: String) {
        searchQuery.value = query
        if (query.isBlank()) {
            val currentSuccess = _uiState.value as? NetworkUiState.Success ?: return
            _uiState.value = currentSuccess.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            val currentSuccess = _uiState.value as? NetworkUiState.Success ?: return@launch
            val myUserId = currentUserId.value
            connectionRepository.searchUsers(query)
                .onSuccess { users ->
                    val cleanSearch = users.filter { user ->
                        val isSelf = user.effectiveUserId == myUserId || user.id == myUserId
                        !isSelf
                    }
                    _uiState.value = currentSuccess.copy(searchResults = cleanSearch)
                }
        }
    }

    fun sendConnectionRequest(receiverId: String, onResult: (Boolean, String?) -> Unit) {
        if (_sentRequestIds.contains(receiverId)) return

        _sentRequestIds.add(receiverId)
        sentRequestIds.value = _sentRequestIds.toSet()

        viewModelScope.launch {
            connectionRepository.sendConnectionRequest(receiverId)
                .onSuccess {
                    onResult(true, "Demande de connexion envoyée ! 🤝")
                    loadData()
                }
                .onFailure { error ->
                    _sentRequestIds.remove(receiverId)
                    sentRequestIds.value = _sentRequestIds.toSet()
                    onResult(false, error.localizedMessage ?: "Erreur lors de l'envoi de la demande")
                }
        }
    }

    fun acceptConnection(connectionId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            connectionRepository.acceptConnection(connectionId)
                .onSuccess {
                    onResult(true, "Connexion acceptée !")
                    loadData()
                }
                .onFailure { error ->
                    onResult(false, error.localizedMessage ?: "Erreur")
                }
        }
    }

    fun removeConnection(connectionId: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            connectionRepository.removeConnection(connectionId)
                .onSuccess {
                    onResult(true, "Connexion supprimée")
                    loadData()
                }
                .onFailure { error ->
                    onResult(false, error.localizedMessage ?: "Erreur")
                }
        }
    }
}
