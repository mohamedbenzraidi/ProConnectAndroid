package com.mohamedbenzraidi.proconnect.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedbenzraidi.proconnect.data.remote.dto.EducationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ExperienceDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ProfileDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.Role
import androidx.compose.material.icons.filled.Business

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditInfoDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showAddExpDialog by remember { mutableStateOf(false) }
    var showAddEduDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMyProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ProfileUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMyProfile() }) {
                            Text("Réessayer")
                        }
                    }
                }

                is ProfileUiState.Success -> {
                    val profile = state.profile
                    ProfileContent(
                        profile = profile,
                        onEditInfoClick = { showEditInfoDialog = true },
                        onAddSkillClick = { showAddSkillDialog = true },
                        onAddExpClick = { showAddExpDialog = true },
                        onAddEduClick = { showAddEduDialog = true }
                    )

                    // Dialogues d'édition et d'ajout
                    if (showEditInfoDialog) {
                        EditInfoDialog(
                            currentProfile = profile,
                            onDismiss = { showEditInfoDialog = false },
                            onConfirm = { headline, bio, location ->
                                viewModel.updateProfileInfo(headline, bio, location)
                                showEditInfoDialog = false
                            }
                        )
                    }

                    if (showAddSkillDialog) {
                        AddSkillDialog(
                            onDismiss = { showAddSkillDialog = false },
                            onConfirm = { name ->
                                viewModel.addSkill(name)
                                showAddSkillDialog = false
                            }
                        )
                    }

                    if (showAddExpDialog) {
                        AddExperienceDialog(
                            onDismiss = { showAddExpDialog = false },
                            onConfirm = { company, position, startDate, endDate, isCurrent, desc ->
                                viewModel.addExperience(company, position, startDate, endDate, isCurrent, desc)
                                showAddExpDialog = false
                            }
                        )
                    }

                    if (showAddEduDialog) {
                        AddEducationDialog(
                            onDismiss = { showAddEduDialog = false },
                            onConfirm = { institution, degree, field, startDate, endDate ->
                                viewModel.addEducation(institution, degree, field, startDate, endDate)
                                showAddEduDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileContent(
    profile: ProfileDto,
    onEditInfoClick: () -> Unit,
    onAddSkillClick: () -> Unit,
    onAddExpClick: () -> Unit,
    onAddEduClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val isRecruiter = profile.role == Role.RECRUITER

        // En-tête : Photo, Nom, Titre, Localisation + Bouton Éditer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecruiter) Icons.Default.Business else Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${profile.firstName} ${profile.lastName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Badge(
                        containerColor = if (isRecruiter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (isRecruiter) "👔 Recruteur / Entreprise" else "🎓 Étudiant / Candidat",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    if (!profile.headline.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile.headline,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!profile.location.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(onClick = onEditInfoClick) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Éditer le profil")
                    }
                }
            }
        }

        // Section "À propos"
        if (!profile.bio.isNullOrBlank()) {
            item {
                SectionHeader("À propos", onAddClick = null)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = profile.bio,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (isRecruiter) {
            // === SECTION RECRUTEUR ===
            item {
                SectionHeader("Gestion des Offres d'Emploi", onAddClick = null)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Publiez et gérez vos offres d'emploi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Trouvez les meilleurs talents pour votre entreprise.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            // Action pour publier une offre (Phase 5)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publier une offre d'emploi")
                        }
                    }
                }
            }
        } else {
            // === SECTION ÉTUDIANT ===
            // Section "Compétences"
            item {
                SectionHeader("Compétences", onAddClick = onAddSkillClick)
                if (profile.skills.isEmpty()) {
                    Text(
                        text = "Aucune compétence ajoutée.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profile.skills.forEach { skill ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(skill.name) }
                            )
                        }
                    }
                }
            }

            // Section "Expériences"
            item {
                SectionHeader("Expériences Professionnelles", onAddClick = onAddExpClick)
            }

            if (profile.experiences.isEmpty()) {
                item {
                    Text(
                        text = "Aucune expérience ajoutée.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(profile.experiences) { exp ->
                    ExperienceItem(exp)
                }
            }

            // Section "Formations"
            item {
                SectionHeader("Formations", onAddClick = onAddEduClick)
            }

            if (profile.education.isEmpty()) {
                item {
                    Text(
                        text = "Aucune formation ajoutée.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(profile.education) { edu ->
                    EducationItem(edu)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onAddClick: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (onAddClick != null) {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ExperienceItem(experience: ExperienceDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = experience.position,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = experience.company,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${experience.startDate} - ${if (experience.isCurrent) "Présent" else experience.endDate ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!experience.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = experience.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun EducationItem(education: EducationDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = education.institution,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${education.degree} (${education.fieldOfStudy})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${education.startDate} - ${education.endDate ?: "Présent"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Dialogues d'édition et d'ajout

@Composable
private fun EditInfoDialog(
    currentProfile: ProfileDto,
    onDismiss: () -> Unit,
    onConfirm: (headline: String, bio: String, location: String) -> Unit,
) {
    var headline by remember { mutableStateOf(currentProfile.headline ?: "") }
    var bio by remember { mutableStateOf(currentProfile.bio ?: "") }
    var location by remember { mutableStateOf(currentProfile.location ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éditer les informations") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Titre / Headline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Localisation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biographie / À propos") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(headline, bio, location) }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun AddSkillDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var skillName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une compétence") },
        text = {
            OutlinedTextField(
                value = skillName,
                onValueChange = { skillName = it },
                label = { Text("Nom de la compétence (ex: Kotlin, Spring Boot)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(skillName) }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun AddExperienceDialog(
    onDismiss: () -> Unit,
    onConfirm: (company: String, position: String, startDate: String, endDate: String?, isCurrent: Boolean, description: String?) -> Unit,
) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isCurrent by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une expérience") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Poste (ex: Développeur Android)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Entreprise / Organisation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Date de début (ex: 2023-01)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCurrent, onCheckedChange = { isCurrent = it })
                    Text("Poste actuel")
                }
                if (!isCurrent) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Date de fin (ex: 2024-06)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(company, position, startDate, endDate, isCurrent, description) }
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun AddEducationDialog(
    onDismiss: () -> Unit,
    onConfirm: (institution: String, degree: String, fieldOfStudy: String, startDate: String, endDate: String?) -> Unit,
) {
    var institution by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une formation") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Établissement / Université") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = degree,
                    onValueChange = { degree = it },
                    label = { Text("Diplôme (ex: Master, Licence)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fieldOfStudy,
                    onValueChange = { fieldOfStudy = it },
                    label = { Text("Domaine d'études (ex: Informatique)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Date de début (ex: 2021)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Date de fin (ex: 2024)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(institution, degree, fieldOfStudy, startDate, endDate) }
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
