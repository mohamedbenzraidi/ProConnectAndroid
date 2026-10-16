package com.mohamedbenzraidi.proconnect.presentation.connections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.UserSearchDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel,
    onNavigateToChat: (String, String) -> Unit, // participantId, participantName
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sentIds by viewModel.sentIdsState.collectAsState()
    val myUserId by viewModel.currentUserId.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Découvrir / Recherche, 1: Mes Connexions
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("🤝 Mon Réseau & Connexions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Découvrir / Rechercher") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Mes Connexions") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Recherche et Découverte de profils
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchUsers(it) },
                            placeholder = { Text("Rechercher un professionnel par nom...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            singleLine = true
                        )

                        when (val state = uiState) {
                            is NetworkUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is NetworkUiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                                }
                            }
                            is NetworkUiState.Success -> {
                                if (state.searchResults.isEmpty()) {
                                    InitialSearchState(searchQuery = searchQuery)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.searchResults) { user ->
                                            UserSearchItem(
                                                user = user,
                                                isLocallySent = sentIds.contains(user.effectiveUserId),
                                                onConnectClick = {
                                                    viewModel.sendConnectionRequest(user.effectiveUserId) { _, msg ->
                                                        snackbarMessage = msg
                                                    }
                                                },
                                                onMessageClick = {
                                                    val userName = "${user.firstName} ${user.lastName}".trim()
                                                    onNavigateToChat(user.effectiveUserId, userName)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Gestion des connexions
                    when (val state = uiState) {
                        is NetworkUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is NetworkUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is NetworkUiState.Success -> {
                            if (state.connections.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Aucune connexion pour le moment.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.connections) { connection ->
                                        ConnectionItem(
                                            connection = connection,
                                            myUserId = myUserId,
                                            onAccept = {
                                                viewModel.acceptConnection(connection.id) { _, msg ->
                                                    snackbarMessage = msg
                                                }
                                            },
                                            onRemove = {
                                                viewModel.removeConnection(connection.id) { _, msg ->
                                                    snackbarMessage = msg
                                                }
                                            },
                                            onNavigateToChat = onNavigateToChat
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InitialSearchState(searchQuery: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isBlank()) "Recherchez un professionnel" else "Aucun utilisateur trouvé",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (searchQuery.isBlank()) "Tapez un nom ou un prénom pour commencer à élargir votre réseau." else "Essayez de chercher avec un autre mot-clé.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UserSearchItem(
    user: UserSearchDto,
    isLocallySent: Boolean,
    onConnectClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    val isConnected = user.connectionStatus == "ACCEPTED"
    val isPending = user.connectionStatus == "PENDING" || isLocallySent

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!user.headline.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = user.headline, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (!user.location.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = user.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "Connectés 🤝",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onMessageClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Discuter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else if (isPending) {
                Text(
                    text = "En attente...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                IconButton(onClick = onConnectClick) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Se connecter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionItem(
    connection: ConnectionDto,
    myUserId: String?,
    onAccept: () -> Unit,
    onRemove: () -> Unit,
    onNavigateToChat: (String, String) -> Unit
) {
    val isPending = connection.status == "PENDING"
    val isSender = myUserId != null && connection.senderId == myUserId

    val otherPartyName = if (isSender) {
        connection.receiverName ?: "Utilisateur"
    } else {
        connection.senderName ?: "Utilisateur"
    }

    val otherPartyId = if (isSender) {
        connection.receiverId
    } else {
        connection.senderId
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = otherPartyName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isPending) {
                        if (isSender) "Demande envoyée (en attente...)" else "Demande de connexion reçue"
                    } else "Connecté 🤝",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPending) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isPending) {
                    IconButton(onClick = { onNavigateToChat(otherPartyId, otherPartyName) }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Envoyer un message", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (isPending && !isSender) {
                    IconButton(onClick = onAccept) {
                        Icon(Icons.Default.Check, contentDescription = "Accepter", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                TextButton(onClick = onRemove) {
                    Text(
                        text = if (isPending) {
                            if (isSender) "Annuler" else "Refuser"
                        } else "Retirer",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
