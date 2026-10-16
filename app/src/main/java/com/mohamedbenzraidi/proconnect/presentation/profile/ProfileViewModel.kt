package com.mohamedbenzraidi.proconnect.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedbenzraidi.proconnect.data.remote.dto.EducationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ExperienceDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SkillDto
import com.mohamedbenzraidi.proconnect.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadMyProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            profileRepository.getMyProfile()
                .onSuccess { profile ->
                    _uiState.value = ProfileUiState.Success(profile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Erreur de chargement du profil")
                }
        }
    }

    fun updateProfileInfo(headline: String, bio: String, location: String) {
        val currentProfile = (_uiState.value as? ProfileUiState.Success)?.profile ?: return
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val updated = currentProfile.copy(
                headline = headline.ifBlank { null },
                bio = bio.ifBlank { null },
                location = location.ifBlank { null },
            )
            profileRepository.updateProfile(updated)
                .onSuccess { newProfile ->
                    _uiState.value = ProfileUiState.Success(newProfile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Erreur de mise à jour du profil")
                }
        }
    }

    fun addSkill(skillName: String) {
        if (skillName.isBlank()) return
        viewModelScope.launch {
            val skill = SkillDto(id = "", name = skillName.trim())
            profileRepository.addSkill(skill)
                .onSuccess { updatedProfile ->
                    _uiState.value = ProfileUiState.Success(updatedProfile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Erreur lors de l'ajout de la compétence")
                }
        }
    }

    fun addExperience(
        company: String,
        position: String,
        startDate: String,
        endDate: String?,
        isCurrent: Boolean,
        description: String?,
    ) {
        if (company.isBlank() || position.isBlank() || startDate.isBlank()) return
        viewModelScope.launch {
            val exp = ExperienceDto(
                id = "",
                company = company.trim(),
                position = position.trim(),
                startDate = startDate.trim(),
                endDate = if (isCurrent) null else endDate?.trim(),
                isCurrent = isCurrent,
                description = description?.trim()
            )
            profileRepository.addExperience(exp)
                .onSuccess { updatedProfile ->
                    _uiState.value = ProfileUiState.Success(updatedProfile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Erreur lors de l'ajout de l'expérience")
                }
        }
    }

    fun addEducation(
        institution: String,
        degree: String,
        fieldOfStudy: String,
        startDate: String,
        endDate: String?,
    ) {
        if (institution.isBlank() || degree.isBlank() || startDate.isBlank()) return
        viewModelScope.launch {
            val edu = EducationDto(
                id = "",
                institution = institution.trim(),
                degree = degree.trim(),
                fieldOfStudy = fieldOfStudy.trim(),
                startDate = startDate.trim(),
                endDate = endDate?.trim()
            )
            profileRepository.addEducation(edu)
                .onSuccess { updatedProfile ->
                    _uiState.value = ProfileUiState.Success(updatedProfile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.localizedMessage ?: "Erreur lors de l'ajout de la formation")
                }
        }
    }
}
