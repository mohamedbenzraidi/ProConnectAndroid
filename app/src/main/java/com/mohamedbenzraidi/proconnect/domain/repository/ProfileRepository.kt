package com.mohamedbenzraidi.proconnect.domain.repository

import com.mohamedbenzraidi.proconnect.data.remote.dto.EducationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ExperienceDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ProfileDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SkillDto

interface ProfileRepository {
    suspend fun getMyProfile(): Result<ProfileDto>
    suspend fun updateProfile(profile: ProfileDto): Result<ProfileDto>
    suspend fun addSkill(skill: SkillDto): Result<ProfileDto>
    suspend fun addExperience(experience: ExperienceDto): Result<ProfileDto>
    suspend fun addEducation(education: EducationDto): Result<ProfileDto>
}
