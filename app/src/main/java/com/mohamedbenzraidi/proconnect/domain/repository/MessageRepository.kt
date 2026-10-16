package com.mohamedbenzraidi.proconnect.domain.repository

import com.mohamedbenzraidi.proconnect.data.remote.dto.ConversationDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.MessageDto
import com.mohamedbenzraidi.proconnect.data.remote.dto.SendMessageRequest

interface MessageRepository {
    suspend fun getConversations(): Result<List<ConversationDto>>
    suspend fun getMessages(conversationId: String): Result<List<MessageDto>>
    suspend fun sendMessage(recipientId: String, content: String): Result<MessageDto>
}
