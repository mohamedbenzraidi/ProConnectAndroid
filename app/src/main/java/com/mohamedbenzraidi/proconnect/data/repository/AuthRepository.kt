package com.mohamedbenzraidi.proconnect.domain.repository

import com.mohamedbenzraidi.proconnect.data.remote.dto.AuthResponse
import com.mohamedbenzraidi.proconnect.data.remote.dto.Role
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val token: Flow<String?>
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun register(firstName: String, lastName: String, email: String, password: String, role: Role): Result<AuthResponse>
    suspend fun logout()
}
