package com.mohamedbenzraidi.proconnect.data.repository

import com.mohamedbenzraidi.proconnect.data.remote.api.OpportunityApi
import com.mohamedbenzraidi.proconnect.data.remote.dto.CreateOpportunityRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.OpportunityDto
import com.mohamedbenzraidi.proconnect.domain.repository.OpportunityRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class OpportunityRepositoryImpl(
    private val opportunityApi: OpportunityApi
) : OpportunityRepository {

    override suspend fun getOpportunities(): Result<List<OpportunityDto>> {
        return runCatching {
            opportunityApi.getOpportunities()
        }.mapFailure { parseException(it) }
    }

    override suspend fun createOpportunity(request: CreateOpportunityRequest): Result<OpportunityDto> {
        return runCatching {
            opportunityApi.createOpportunity(request)
        }.mapFailure { parseException(it) }
    }

    override suspend fun applyToOpportunity(id: String): Result<Unit> {
        return runCatching {
            opportunityApi.applyToOpportunity(id)
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
