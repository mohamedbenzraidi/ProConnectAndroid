package com.mohamedbenzraidi.proconnect.data.remote.api

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConversationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.MessageDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {

    @GET("api/v1/conversations")
    suspend fun getConversations(): List<ConversationDto>

    @GET("api/v1/conversations/{id}/messages")
    suspend fun getMessages(@Path("id") conversationId: String): List<MessageDto>

    @POST("api/v1/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): MessageDto
}
