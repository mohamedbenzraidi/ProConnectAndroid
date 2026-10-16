package com.mohamedbenzraidi.proconnect.data.repository

import com.mohamedbenzraidi.proconnect.data.remote.api.ProfileApi
import com.mohamedbenzraidi.proconnect.data.remote.dto.EducationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ExperienceDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ProfileDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SkillDto
import com.mohamedbenzraidi.proconnect.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
) : ProfileRepository {
    override suspend fun getMyProfile(): Result<ProfileDto> {
        return runCatching { profileApi.getMyProfile() }
    }

    override suspend fun updateProfile(profile: ProfileDto): Result<ProfileDto> {
        return runCatching { profileApi.updateProfile(profile) }
    }

    override suspend fun addSkill(skill: SkillDto): Result<ProfileDto> {
        return runCatching { profileApi.addSkill(skill) }
    }

    override suspend fun addExperience(experience: ExperienceDto): Result<ProfileDto> {
        return runCatching { profileApi.addExperience(experience) }
    }

    override suspend fun addEducation(education: EducationDto): Result<ProfileDto> {
        return runCatching { profileApi.addEducation(education) }
    }
}
