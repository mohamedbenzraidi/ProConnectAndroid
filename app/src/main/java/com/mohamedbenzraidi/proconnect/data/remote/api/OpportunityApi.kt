package com.mohamedbenzraidi.proconnect.data.remote.api

import com.mohamedbenzraidi.proconnect.data.remote.dto.CreateOpportunityRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OpportunityApi {

    @GET("api/v1/opportunities")
    suspend fun getOpportunities(): List<OpportunityDto>

    @POST("api/v1/opportunities")
    suspend fun createOpportunity(@Body request: CreateOpportunityRequest): OpportunityDto

    @POST("api/v1/opportunities/{id}/apply")
    suspend fun applyToOpportunity(@Path("id") id: String)
}
