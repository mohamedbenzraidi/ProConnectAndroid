package com.mohamedbenzraidi.proconnect.data.remote.api

import com.mohamedbenzraidi.proconnect.data.remote.dto.EducationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ExperienceDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.ProfileDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SkillDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileApi {

    @GET("api/v1/profile/me")
    suspend fun getMyProfile(): ProfileDto

    @PUT("api/v1/profile/me")
    suspend fun updateProfile(@Body profile: ProfileDto): ProfileDto

    @POST("api/v1/profile/skills")
    suspend fun addSkill(@Body skill: SkillDto): ProfileDto

    @POST("api/v1/profile/experiences")
    suspend fun addExperience(@Body experience: ExperienceDto): ProfileDto

    @POST("api/v1/profile/education")
    suspend fun addEducation(@Body education: EducationDto): ProfileDto
}
