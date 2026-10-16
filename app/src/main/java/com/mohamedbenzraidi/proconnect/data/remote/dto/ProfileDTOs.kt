package com.mohamedbenzraidi.proconnect.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: Role = Role.STUDENT,
    val headline: String? = null,        // Ex: "Étudiant en Génie Logiciel | Développeur Android"
    val bio: String? = null,             // Biographie / Présentation
    val location: String? = null,        // Ex: "Casablanca, Maroc"
    val avatarUrl: String? = null,
    val skills: List<SkillDto> = emptyList(),
    val experiences: List<ExperienceDto> = emptyList(),
    val education: List<EducationDto> = emptyList()
)

@Serializable
data class SkillDto(
    val id: String,
    val name: String
)

@Serializable
data class ExperienceDto(
    val id: String,
    val company: String,
    val position: String,
    val startDate: String,
    val endDate: String? = null,
    val isCurrent: Boolean = false,
    val description: String? = null
)

@Serializable
data class EducationDto(
    val id: String,
    val institution: String,
    val degree: String,
    val fieldOfStudy: String,
    val startDate: String,
    val endDate: String? = null
)