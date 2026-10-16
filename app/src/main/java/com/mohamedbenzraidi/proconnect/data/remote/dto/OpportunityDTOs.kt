package com.mohamedbenzraidi.proconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpportunityDto(
    val id: String,
    val title: String,
    val organization: String,
    val description: String,
    val location: String,
    @SerialName("employmentType")
    val employmentType: String = "STAGE",
    @SerialName("requiredSkills")
    val requiredSkills: String? = null,
    @SerialName("applyUrl")
    val applyUrl: String? = null,
    @SerialName("createdAt")
    val createdAt: JsonElement? = null
)

@Serializable
data class CreateOpportunityRequest(
    val title: String,
    val organization: String,
    val description: String,
    val location: String,
    @SerialName("employmentType")
    val employmentType: String,
    @SerialName("requiredSkills")
    val requiredSkills: String? = null,
    @SerialName("applyUrl")
    val applyUrl: String? = null
)
