package com.mohamedbenzraidi.proconnect.core.network

import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
) : Interceptor, Authenticator {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.token.firstOrNull() }
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            runBlocking { tokenManager.clearSession() }
        }
        return null
    }
}
