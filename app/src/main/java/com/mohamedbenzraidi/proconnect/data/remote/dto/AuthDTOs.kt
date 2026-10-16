package com.mohamedbenzraidi.proconnect.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    STUDENT,
    RECRUITER,
    ADMIN
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: Role
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: Role? = null
)