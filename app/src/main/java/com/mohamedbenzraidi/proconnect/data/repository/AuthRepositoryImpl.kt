package com.mohamedbenzraidi.proconnect.data.repository

import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.data.remote.api.AuthApi
import com.mohamedbenzraidi.proconnect.data.remote.dto.AuthResponse
import com.mohamedbenzraidi.proconnect.data.remote.dto.LoginRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.RegisterRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.Role
import com.mohamedbenzraidi.proconnect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override val token: Flow<String?> = tokenManager.token

    override suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            val response = authApi.login(LoginRequest(email, password))
            tokenManager.saveAuthData(response.token, response.userId)
            response
        }.mapFailure { parseException(it) }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: Role,
    ): Result<AuthResponse> {
        return runCatching {
            val response = authApi.register(RegisterRequest(firstName, lastName, email, password, role))
            tokenManager.saveAuthData(response.token, response.userId)
            response
        }.mapFailure { parseException(it) }
    }

    override suspend fun logout() {
        tokenManager.clearSession()
    }

    private fun parseException(throwable: Throwable): Throwable {
        return when (throwable) {
            is ConnectException -> Exception("Impossible de se connecter au serveur (http://10.0.2.2:8080). Assurez-vous que le serveur backend est démarré.")
            is SocketTimeoutException -> Exception("Délai d'attente dépassé (Timeout). Le serveur ne répond pas.")
            is HttpException -> {
                val errorBody = try { throwable.response()?.errorBody()?.string() } catch (_: Exception) { null }
                val message = if (!errorBody.isNullOrBlank()) {
                    errorBody
                } else {
                    when (throwable.code()) {
                        400 -> "Requête invalide (données incorrectes)"
                        401 -> "Identifiants incorrects"
                        409 -> "Cet email est déjà utilisé"
                        500 -> "Erreur interne du serveur"
                        else -> "Erreur serveur (${throwable.code()})"
                    }
                }
                Exception(message)
            }
            is IOException -> Exception("Erreur réseau. Vérifiez votre connexion internet.")
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
