package com.mohamedbenzraidi.proconnect.data.repository

import com.mohamedbenzraidi.proconnect.data.remote.api.MessageApi
import com.mohamedbenzraidi.proconnect.data.remote.dto.ConversationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.MessageDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SendMessageRequest
import com.mohamedbenzraidi.proconnect.domain.repository.MessageRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class MessageRepositoryImpl(
    private val messageApi: MessageApi
) : MessageRepository {

    override suspend fun getConversations(): Result<List<ConversationDto>> {
        return runCatching {
            messageApi.getConversations()
        }.mapFailure { parseException(it) }
    }

    override suspend fun getMessages(conversationId: String): Result<List<MessageDto>> {
        return runCatching {
            messageApi.getMessages(conversationId)
        }.mapFailure { parseException(it) }
    }

    override suspend fun sendMessage(recipientId: String, content: String): Result<MessageDto> {
        return runCatching {
            messageApi.sendMessage(SendMessageRequest(recipientId, content))
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
