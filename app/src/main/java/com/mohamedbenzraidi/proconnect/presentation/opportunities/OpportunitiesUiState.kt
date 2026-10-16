package com.mohamedbenzraidi.proconnect.presentation.opportunities

import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto

sealed interface OpportunitiesUiState {
    data object Loading : OpportunitiesUiState
    data class Success(val opportunities: List<OpportunityDto>) : OpportunitiesUiState
    data class Error(val message: String) : OpportunitiesUiState
}
