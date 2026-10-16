package com.mohamedbenzraidi.proconnect.domain.repository

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.UserSearchDto

interface ConnectionRepository {
    suspend fun searchUsers(query: String): Result<List<UserSearchDto>>
    suspend fun getConnections(): Result<List<ConnectionDto>>
    suspend fun sendConnectionRequest(receiverId: String): Result<ConnectionDto>
    suspend fun acceptConnection(connectionId: String): Result<ConnectionDto>
    suspend fun removeConnection(connectionId: String): Result<Unit>
}
