package com.mohamedbenzraidi.proconnect.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mohamedbenzraidi.proconnect.data.local.TokenManager
import com.mohamedbenzraidi.proconnect.data.remote.api.AuthApi
import com.mohamedbenzraidi.proconnect.data.remote.api.ConnectionApi
import com.mohamedbenzraidi.proconnect.data.remote.api.MessageApi
import com.mohamedbenzraidi.proconnect.data.remote.api.OpportunityApi
import com.mohamedbenzraidi.proconnect.data.remote.api.ProfileApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private fun createOkHttpClient(tokenManager: TokenManager? = null): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)

        if (tokenManager != null) {
            val tokenAuthenticator = TokenAuthenticator(tokenManager)
            builder.addInterceptor(tokenAuthenticator)
                .authenticator(tokenAuthenticator)
        }

        return builder.build()
    }

    fun provideAuthApi(tokenManager: TokenManager): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApi::class.java)
    }

    fun provideProfileApi(tokenManager: TokenManager): ProfileApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProfileApi::class.java)
    }

    fun provideOpportunityApi(tokenManager: TokenManager): OpportunityApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpportunityApi::class.java)
    }

    fun provideConnectionApi(tokenManager: TokenManager): ConnectionApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ConnectionApi::class.java)
    }

    fun provideMessageApi(tokenManager: TokenManager): MessageApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MessageApi::class.java)
    }
}
