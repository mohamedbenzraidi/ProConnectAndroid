package com.mohamedbenzraidi.proconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ConversationDto(
    val id: String = "",
    @SerialName("participantName")
    val participantName: String = "",
    @SerialName("participantId")
    val participantId: String? = null,
    @SerialName("participantUserId")
    val participantUserId: String? = null,
    @SerialName("recipientId")
    val recipientId: String? = null,
    @SerialName("receiverId")
    val receiverId: String? = null,
    @SerialName("otherUserId")
    val otherUserId: String? = null,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("lastMessage")
    val lastMessage: String? = null,
    @SerialName("unreadCount")
    val unreadCount: Int = 0,
    @SerialName("updatedAt")
    val updatedAt: JsonElement? = null
) {
    fun getOtherParticipantId(myUserId: String?): String {
        val candidates = listOfNotNull(
            participantUserId,
            participantId,
            otherUserId,
            recipientId,
            receiverId,
            userId
        ).filter { it.isNotBlank() }

        if (!myUserId.isNullOrBlank()) {
            val other = candidates.firstOrNull { it != myUserId }
            if (other != null) return other
        }

        return candidates.firstOrNull() ?: ""
    }
}

@Serializable
data class MessageDto(
    val id: String = "",
    @SerialName("conversationId")
    val conversationId: String = "",
    @SerialName("senderId")
    val senderId: String = "",
    @SerialName("senderName")
    val senderName: String? = null,
    val content: String = "",
    val timestamp: JsonElement? = null
)

@Serializable
data class SendMessageRequest(
    @SerialName("recipientId")
    val recipientId: String,
    val content: String,
    @SerialName("receiverId")
    val receiverId: String = recipientId,
    @SerialName("toUserId")
    val toUserId: String = recipientId
)
