package com.mohamedbenzraidi.proconnect.data.repository

import com.mohamedbenzraidi.proconnect.data.remote.api.ConnectionApi
import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SendConnectionRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.UserSearchDto
import com.mohamedbenzraidi.proconnect.domain.repository.ConnectionRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class ConnectionRepositoryImpl(
    private val connectionApi: ConnectionApi
) : ConnectionRepository {

    override suspend fun searchUsers(query: String): Result<List<UserSearchDto>> {
        return runCatching {
            connectionApi.searchUsers(query)
        }.mapFailure { parseException(it) }
    }

    override suspend fun getConnections(): Result<List<ConnectionDto>> {
        return runCatching {
            connectionApi.getConnections()
        }.mapFailure { parseException(it) }
    }

    override suspend fun sendConnectionRequest(receiverId: String): Result<ConnectionDto> {
        return runCatching {
            connectionApi.sendConnectionRequest(SendConnectionRequest(receiverId))
        }.mapFailure { parseException(it) }
    }

    override suspend fun acceptConnection(connectionId: String): Result<ConnectionDto> {
        return runCatching {
            connectionApi.acceptConnection(connectionId)
        }.mapFailure { parseException(it) }
    }

    override suspend fun removeConnection(connectionId: String): Result<Unit> {
        return runCatching {
            connectionApi.removeConnection(connectionId)
        }.mapFailure { parseException(it) }
    }

    private fun parseException(throwable: Throwable): Throwable {
        return when (throwable) {
            is ConnectException -> Exception("Impossible de se connecter au serveur.")
            is SocketTimeoutException -> Exception("Délai d'attente dépassé.")
            is HttpException -> {
                val errorBody = try { throwable.response()?.errorBody()?.string() } catch (_: Exception) { null }
                Exception(errorBody?.takeIf { !it.isBlank() } ?: "Erreur serveur (${throwable.code()})")
            }
            is IOException -> Exception("Erreur réseau.")
            else -> throwable
        }
    }

    private inline fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> {
        val exception = exceptionOrNull()
        return if (exception != null) {
            Result.failure(transform(exception))
        } else {
            this
        }
    }
}
