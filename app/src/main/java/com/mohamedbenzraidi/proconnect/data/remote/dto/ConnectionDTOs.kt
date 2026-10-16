package com.mohamedbenzraidi.proconnect.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSearchDto(
    val id: String = "",
    @SerialName("userId")
    val userId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val headline: String? = null,
    val location: String? = null,
    val role: Role = Role.STUDENT,
    @SerialName("connectionStatus")
    val connectionStatus: String? = null
) {
    val effectiveUserId: String
        get() = userId?.takeIf { it.isNotBlank() } ?: id
}

@Serializable
data class ConnectionDto(
    val id: String = "",
    @SerialName("senderId")
    val senderId: String = "",
    @SerialName("senderUserId")
    val senderUserId: String? = null,
    @SerialName("receiverId")
    val receiverId: String = "",
    @SerialName("receiverUserId")
    val receiverUserId: String? = null,
    val status: String = "PENDING",
    @SerialName("senderName")
    val senderName: String? = null,
    @SerialName("receiverName")
    val receiverName: String? = null
) {
    fun getOtherPartyId(myUserId: String?): String {
        val candidates = listOfNotNull(
            receiverUserId,
            receiverId,
            senderUserId,
            senderId
        ).filter { it.isNotBlank() }

        if (!myUserId.isNullOrBlank()) {
            val other = candidates.firstOrNull { it != myUserId }
            if (other != null) return other
        }

        return candidates.firstOrNull() ?: ""
    }

    fun getOtherPartyName(myUserId: String?): String {
        val isSender = !myUserId.isNullOrBlank() && (senderId == myUserId || senderUserId == myUserId)
        return if (isSender) {
            receiverName ?: senderName ?: "Contact"
        } else {
            senderName ?: receiverName ?: "Contact"
        }
    }
}

@Serializable
data class SendConnectionRequest(
    @SerialName("receiverId")
    val receiverId: String
)
