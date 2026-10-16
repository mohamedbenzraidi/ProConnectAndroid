package com.mohamedbenzraidi.proconnect.domain.repository

import com.mohamedbenzraidi.proconnect.data.remote.dto.CreateOpportunityRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto

interface OpportunityRepository {
    suspend fun getOpportunities(): Result<List<OpportunityDto>>
    suspend fun createOpportunity(request: CreateOpportunityRequest): Result<OpportunityDto>
    suspend fun applyToOpportunity(id: String): Result<Unit>
}
