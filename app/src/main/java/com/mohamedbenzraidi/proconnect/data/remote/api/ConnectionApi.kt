package com.mohamedbenzraidi.proconnect.data.remote.api

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConnectionDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SendConnectionRequest
import com.mohamedbenzraidi.proconnect.data.remote.dto.UserSearchDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ConnectionApi {

    @GET("api/v1/users/search")
    suspend fun searchUsers(@Query("query") query: String): List<UserSearchDto>

    @GET("api/v1/connections")
    suspend fun getConnections(): List<ConnectionDto>

    @POST("api/v1/connections/request")
    suspend fun sendConnectionRequest(@Body request: SendConnectionRequest): ConnectionDto

    @POST("api/v1/connections/{id}/accept")
    suspend fun acceptConnection(@Path("id") connectionId: String): ConnectionDto

    @DELETE("api/v1/connections/{id}")
    suspend fun removeConnection(@Path("id") connectionId: String)
}
