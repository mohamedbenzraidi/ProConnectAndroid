package com.mohamedbenzraidi.proconnect.presentation.opportunities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.remote.dto.CreateOpportunityRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto
import com.mohamedbenzraidi.proconnect.domain.repository.OpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OpportunitiesViewModel(
    private val opportunityRepository: OpportunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OpportunitiesUiState>(OpportunitiesUiState.Loading)
    val uiState: StateFlow<OpportunitiesUiState> = _uiState.asStateFlow()

    private var allOpportunities: List<OpportunityDto> = emptyList()

    var searchQuery = MutableStateFlow("")
        private set

    var selectedFilter = MutableStateFlow("TOUS")
        private set

    init {
        loadOpportunities()
    }

    fun loadOpportunities() {
        viewModelScope.launch {
            _uiState.value = OpportunitiesUiState.Loading
            opportunityRepository.getOpportunities()
                .onSuccess { list ->
                    allOpportunities = list.sortedByDescending { it.createdAt?.toString() ?: "" }
                    updateFilteredList()
                }
                .onFailure { error ->
                    _uiState.value = OpportunitiesUiState.Error(error.localizedMessage ?: "Erreur de chargement des offres")
                }
        }
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
        updateFilteredList()
    }

    fun setFilter(filter: String) {
        selectedFilter.value = filter
        updateFilteredList()
    }

    private fun updateFilteredList() {
        val query = searchQuery.value.trim().lowercase()
        val filter = selectedFilter.value

        val filtered = allOpportunities.filter { opp ->
            val matchesQuery = query.isBlank() ||
                    opp.title.lowercase().contains(query) ||
                    opp.organization.lowercase().contains(query) ||
                    opp.location.lowercase().contains(query) ||
                    (opp.requiredSkills?.lowercase()?.contains(query) == true)

            val matchesFilter = filter == "TOUS" || opp.employmentType.uppercase() == filter

            matchesQuery && matchesFilter
        }

        _uiState.value = OpportunitiesUiState.Success(filtered)
    }

    fun applyToOpportunity(id: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            opportunityRepository.applyToOpportunity(id)
                .onSuccess {
                    onResult(true, "Candidature envoyée avec succès ! 🚀")
                }
                .onFailure { error ->
                    onResult(false, error.localizedMessage ?: "Erreur lors de la candidature")
                }
        }
    }

    fun createOpportunity(
        title: String,
        organization: String,
        description: String,
        location: String,
        employmentType: String,
        requiredSkills: String?,
        applyUrl: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (title.isBlank() || organization.isBlank() || description.isBlank() || location.isBlank()) {
            onResult(false, "Veuillez remplir tous les champs obligatoires")
            return
        }

        viewModelScope.launch {
            val req = CreateOpportunityRequest(
                title = title.trim(),
                organization = organization.trim(),
                description = description.trim(),
                location = location.trim(),
                employmentType = employmentType.trim(),
                requiredSkills = requiredSkills?.trim(),
                applyUrl = applyUrl?.trim()
            )
            opportunityRepository.createOpportunity(req)
                .onSuccess {
                    loadOpportunities()
                    onResult(true, "Offre publiée avec succès !")
                }
                .onFailure { error ->
                    onResult(false, error.localizedMessage ?: "Erreur lors de la publication")
                }
        }
    }
}
