package com.mohamedbenzraidi.proconnect.presentation.opportunities

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunitiesScreen(
    viewModel: OpportunitiesViewModel,
    isRecruiter: Boolean,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
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
                title = { Text("💼 Opportunités & Offres") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    if (isRecruiter) {
                        IconButton(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Publier une offre")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isRecruiter) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter une offre")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barre de recherche
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Rechercher par titre, entreprise, lieu...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            // Filtres par type d'emploi
            PrimaryScrollableTabRow(
                selectedTabIndex = when (selectedFilter) {
                    "TOUS" -> 0
                    "STAGE" -> 1
                    "CDI" -> 2
                    "FREELANCE" -> 3
                    else -> 0
                },
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("TOUS", "STAGE", "CDI", "FREELANCE").forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        text = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is OpportunitiesUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is OpportunitiesUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadOpportunities() }) {
                                Text("Réessayer")
                            }
                        }
                    }
                    is OpportunitiesUiState.Success -> {
                        if (state.opportunities.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Aucune offre trouvée.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.opportunities) { opportunity ->
                                    OpportunityCard(
                                        opportunity = opportunity,
                                        isRecruiter = isRecruiter
                                    )
                                }
                            }
                        }
                    }
                }

                if (showCreateDialog) {
                    CreateOpportunityDialog(
                        onDismiss = { showCreateDialog = false },
                        onConfirm = { title, org, desc, loc, type, skills, url ->
                            viewModel.createOpportunity(title, org, desc, loc, type, skills, url) { success, msg ->
                                snackbarMessage = msg
                                if (success) showCreateDialog = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OpportunityCard(
    opportunity: OpportunityDto,
    isRecruiter: Boolean
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opportunity.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = opportunity.organization, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(text = opportunity.employmentType, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = opportunity.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = opportunity.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )

            if (!opportunity.requiredSkills.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Compétences requises : ${opportunity.requiredSkills}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!isRecruiter) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val url = opportunity.applyUrl?.takeIf { it.isNotBlank() } ?: "https://www.google.com/search?q=${Uri.encode(opportunity.title + " " + opportunity.organization)}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Postuler 🚀")
                }
            }
        }
    }
}

@Composable
private fun CreateOpportunityDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var employmentType by remember { mutableStateOf("STAGE") }
    var requiredSkills by remember { mutableStateOf("") }
    var applyUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publier une offre d'emploi") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre du poste *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Entreprise / Organisation *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lieu *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = employmentType, onValueChange = { employmentType = it }, label = { Text("Type (STAGE, CDI, FREELANCE) *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = requiredSkills, onValueChange = { requiredSkills = it }, label = { Text("Compétences requises") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = applyUrl, onValueChange = { applyUrl = it }, label = { Text("Lien de postulation (URL)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(title, organization, description, location, employmentType, requiredSkills.ifBlank { null }, applyUrl.ifBlank { null })
            }) {
                Text("Publier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
